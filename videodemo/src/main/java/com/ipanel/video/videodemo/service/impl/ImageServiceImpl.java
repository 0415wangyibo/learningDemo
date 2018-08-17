package com.ipanel.video.videodemo.service.impl;

import com.ipanel.video.videodemo.controller.request.*;
import com.ipanel.video.videodemo.model.Image;
import com.ipanel.video.videodemo.dao.ImageMapper;
import com.ipanel.video.videodemo.dto.ImgSize;
import com.ipanel.video.videodemo.exception.RequestParamErrorException;
import com.ipanel.video.videodemo.service.ImageService;
import com.ipanel.video.videodemo.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageMapper imageMapper;

    @Override
    public Boolean initImgs() {
        return null;
    }

    @Override
    public List<Image> findByVideoId(Integer videoId) {
        return imageMapper.selectByVideoId(videoId);
    }

    @Override
    public String showImgBySize(ShowImg showImg) throws Exception{
        Integer videoId = showImg.getVideoId();
        Integer groupId = showImg.getGroupId();
        Integer width = showImg.getWidth();
        Integer height = showImg.getHeight();
        if (null==videoId||null==groupId||null==width||null==height||width<=0||height<=0){
            throw new RequestParamErrorException("传入的视频或者分组的参数为空，或者传入尺寸的长宽小于或者等于0");
        }
        List<Image> imageList = imageMapper.selectByVideoId(videoId);
        if (null==imageList||imageList.size()==0){
            throw new RequestParamErrorException("该视频海报未初始化或者初始化失败，没有找到对应的海报");
        }
        Image imageSize = new Image();
        List<Image> imageListGroup = new LinkedList<>();
        Image imageAll = new Image();
        Iterator iterator = imageList.iterator();
        while (iterator.hasNext()){
            Image image = (Image) iterator.next();
            String groupString = image.getGroupstring();
            //找到默认匹配所有规格尺寸海报的图片
            if (null==groupString||groupString.isEmpty()){
                imageAll=image;
            }else {
                List<Integer> groupIds = ImageUtil.stringToList(image.getGroupstring());
                if (null != groupIds && groupIds.contains(groupId)) {
                    String size = image.getSize();
                    //找到匹配特定规格下所有尺寸海报的图片
                    if (null==size||size.isEmpty()){
                        imageListGroup.add(image);
                        break;
                    }
                    List<ImgSize> imgSizes = ImageUtil.stringToSize(size);
                    if (null==imgSizes||imgSizes.size()==0){
                        break;
                    }else {
                        Iterator iterator1 = imgSizes.iterator();
                        while (iterator1.hasNext()){
                            ImgSize imgSize = (ImgSize) iterator1.next();
                            //找到匹配特定规格、特定尺寸海报的图片
                            if (imgSize.getWidth().equals(width)&&imgSize.getHeight().equals(height)){
                                imageSize = image;
                                break;
                            }
                        }
                    }
                }
            }
        }
        String base64String = null;
        if (null!=imageSize.getPath()&&!imageSize.getPath().isEmpty()){
            base64String = ImageUtil.scale(imageSize.getPath(), width, height, true);
        }else {
            if (imageListGroup.size()!=0){
                Image image = imageListGroup.get(0);
                base64String = ImageUtil.scale(image.getPath(), width, height, true);
            }else {
                if (null!=imageAll.getPath()||!imageAll.getPath().isEmpty()){
                    base64String = ImageUtil.scale(imageAll.getPath(), width, height, true);
                }
            }
        }
        return base64String;
    }

    @Override
    public Boolean changeImg(MultipartFile file, AddImg addImg) throws Exception {
        if (null == file) {
            throw new RequestParamErrorException("传进来的图片不存在");
        }
        if (null == addImg) {
            throw new RequestParamErrorException("addImg缺失");
        }
        Integer videoId = addImg.getVideoId();
        if (null == videoId) {
            throw new RequestParamErrorException("未传入视频Id，无法存储该海报");
        }
        List<Image> listImage = imageMapper.selectByVideoId(videoId);
        if (null == listImage || listImage.size() == 0) {
            throw new RequestParamErrorException("初始化该视频的海报失败，未从homed端导入该视频海报或者导入初始海报失败");
        }
        List<Integer> addImgListGroups = addImg.getGroupIds();
        List<Integer> addImgListWidths = addImg.getWidths();
        List<Integer> addImgListHeights = addImg.getHeights();
        if ((null != addImgListWidths && addImgListWidths.size() != 0) && (null != addImgListHeights && addImgListHeights.size() != 0) && (addImgListWidths.size() != addImgListHeights.size())) {
            throw new RequestParamErrorException("尺寸的长宽个数不匹配");
        }
        if ((null == addImgListWidths || addImgListWidths.size() == 0) && (null != addImgListHeights && addImgListHeights.size() != 0)) {
            throw new RequestParamErrorException("尺寸的长宽个数不匹配");
        }
        if ((null == addImgListHeights || addImgListHeights.size() == 0) && (null != addImgListWidths && addImgListWidths.size() != 0)) {
            throw new RequestParamErrorException("尺寸的长宽个数不匹配");
        }
        if ((null == addImgListGroups || addImgListGroups.size() == 0) && (null != addImgListWidths && addImgListWidths.size() != 0)) {
            throw new RequestParamErrorException("参数格式错误，需要groupIds字段，才能进一步将图片存放进指定尺寸");
        }
        //未填写图片所在规格分组，默认用传进来的图片适配所有规格、尺寸的海报
        if (null == addImgListGroups || addImgListGroups.size() == 0) {
            //获取图片的名字
            String name = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
            String localPath = "E:/Develop/Files/Photos/" + videoId + "/" + "all/" + name + "-all.png";
            //将图片保存到本地
            FileUtils.saveImg(file, localPath);
            Iterator iterator = listImage.iterator();
            while (iterator.hasNext()) {
                Image image = (Image) iterator.next();
                imageMapper.deleteByPrimaryKey(image.getId());
            }
            Image image = new Image();
            image.setVideoid(videoId);
            image.setPath(localPath);
            image.setGroupstring("");
            image.setSize("");
            imageMapper.insert(image);
        }
        //填写了海报的分组，但未填写尺寸，默认用该图片适配该规格分组中的所有海报
        if (null != addImgListGroups && addImgListGroups.size() != 0 && (null == addImgListWidths || addImgListWidths.size() == 0)) {
            Iterator iterator = addImgListGroups.iterator();
            while (iterator.hasNext()) {
                Integer groupId = (Integer) iterator.next();
                Iterator imgIterator = listImage.iterator();
                while (imgIterator.hasNext()) {
                    Image image = (Image) imgIterator.next();
                    String groupIdString = image.getGroupstring();
                    if (null != groupIdString && (!groupIdString.isEmpty())) {
                        List<Integer> groupIds = ImageUtil.stringToList(groupIdString);
                        if (null != groupIds && groupIds.size() != 0) {
                            if (groupIds.contains(groupId)) {
                                groupIds.remove(groupId);
                                listImage.remove(image);
                                if (groupIds.size() == 0) {
                                    imageMapper.deleteByPrimaryKey(image.getId());
                                } else {
                                    image.setGroupstring(ImageUtil.listToString(groupIds));
                                    imageMapper.updateByPrimaryKey(image);
                                    //更新listImage
                                    listImage.add(image);
                                }
                            }
                        }
                    }
                }
            }
            String name = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
            String name1 = FileNameUtils.listToString(addImgListGroups);//分组名字
            String localPath = "E:/Develop/Files/Photos/" + videoId + "/" +name1+ "/" + name  + "-all.png";
            //将图片保存到本地
            FileUtils.saveImg(file, localPath);
            Iterator iteratorNew = listImage.iterator();
            while (iteratorNew.hasNext()) {
                Image image = (Image) iteratorNew.next();
                imageMapper.deleteByPrimaryKey(image.getId());
            }
            Image image = new Image();
            image.setVideoid(videoId);
            image.setPath(localPath);
            image.setGroupstring(ImageUtil.listToString(addImgListGroups));
            image.setSize("");
            imageMapper.insert(image);
        }
        //填写了海报分组以及所需要适配的尺寸，则只用该海报适配指定规格、指定尺寸的海报
        if (null != addImgListGroups && addImgListGroups.size() != 0 && (null != addImgListWidths && addImgListWidths.size() != 0) && (null != addImgListHeights && addImgListHeights.size() != 0)) {
            Iterator iterator = addImgListGroups.iterator();
            while (iterator.hasNext()) {
                Integer groupId = (Integer) iterator.next();
                Iterator imgIterator = listImage.iterator();
                while (imgIterator.hasNext()) {
                    Image image = (Image) imgIterator.next();
                    String imageSize = image.getSize();
                    if (null != imageSize && !imageSize.isEmpty()) {
                        String groupIdString = image.getGroupstring();
                        if (null != groupIdString && (!groupIdString.isEmpty())) {
                            List<Integer> groupIds = ImageUtil.stringToList(groupIdString);
                            if (null != groupIds && groupIds.size() != 0) {
                                if (groupIds.contains(groupId)) {
                                    List<ImgSize> imgSizeList = ImageUtil.stringToSize(imageSize);
                                    List<Integer> listWidth = new LinkedList<>();
                                    List<Integer> listHeight = new LinkedList<>();
                                    Iterator iterator1 = imgSizeList.iterator();
                                    while (iterator1.hasNext()) {
                                        ImgSize imgSize = (ImgSize) iterator1.next();
                                        Integer width = imgSize.getWidth();
                                        listWidth.add(width);
                                        Integer height = imgSize.getHeight();
                                        listHeight.add(height);
                                        for (int i = 0; i < addImgListWidths.size(); i++) {
                                            if (width.equals(addImgListWidths.get(i)) && height.equals(addImgListHeights.get(i))) {
                                                imgSizeList.remove(imgSize);
                                                if (imgSizeList.size() == 0) {
                                                    listImage.remove(image);
                                                    imageMapper.deleteByPrimaryKey(image.getId());
                                                }
                                                listWidth.remove(width);
                                                listHeight.remove(height);
                                            }
                                        }
                                    }
                                    if (listWidth.size()!=0) {
                                        image.setSize(ImageUtil.sizeToString(listWidth, listHeight));
                                        imageMapper.updateByPrimaryKey(image);
                                        listImage = imageMapper.selectByVideoId(videoId);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            String name = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
            String name1 = FileNameUtils.listToString(addImgListGroups);//分组名字
            String name2 = FileNameUtils.listToString(addImgListWidths);
            String name3 = FileNameUtils.listToString(addImgListHeights);
            String localPath = "E:/Develop/Files/Photos/" + videoId + "/" + name1 + "/" + name + name2 + name3 + ".png";
            //将图片保存到本地
            FileUtils.saveImg(file, localPath);
            Iterator iteratorNew = listImage.iterator();
            while (iteratorNew.hasNext()) {
                Image image = (Image) iteratorNew.next();
                imageMapper.deleteByPrimaryKey(image.getId());
            }
            Image image = new Image();
            image.setVideoid(videoId);
            image.setPath(localPath);
            image.setGroupstring(ImageUtil.listToString(addImgListGroups));
            image.setSize(ImageUtil.sizeToString(addImgListWidths,addImgListHeights));
            imageMapper.insert(image);
        }
        return true;
    }

    @Override
    public Boolean submitAllImgs(Integer videoId) {
        return null;
    }
}
