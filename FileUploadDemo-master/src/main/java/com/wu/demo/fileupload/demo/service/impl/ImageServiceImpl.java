package com.wu.demo.fileupload.demo.service.impl;

import com.wu.demo.fileupload.demo.controller.request.AddImg;
import com.wu.demo.fileupload.demo.controller.request.ShowImg;
import com.wu.demo.fileupload.demo.dao.ImageDao;
import com.wu.demo.fileupload.demo.dto.ImgSize;
import com.wu.demo.fileupload.demo.entity.Image;
import com.wu.demo.fileupload.demo.exception.RequestParamErrorException;
import com.wu.demo.fileupload.demo.service.ImageService;
import com.wu.demo.fileupload.demo.util.FileNameUtils;
import com.wu.demo.fileupload.demo.util.FileUtils;
import com.wu.demo.fileupload.demo.util.ImageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    @Resource
    private ImageDao imageDao;

    @Override
    public Boolean initImgs() {
        return null;
    }

    @Override
    public List<Image> findByVideoId(Integer videoId) {
        return imageDao.findByVideoId(videoId);
    }

    @Override
    public String showImgBySize(ShowImg showImg) {
        return null;
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
        List<Image> listImage = imageDao.findByVideoId(videoId);
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
                imageDao.delete(image);
            }
            Image image = new Image();
            image.setVideoId(videoId);
            image.setPath(localPath);
            image.setGroupsString("");
            image.setSize("");
            List<Integer> stateList = new LinkedList<>();
            stateList.add(0);
            image.setState(ImageUtil.listToString(stateList));
            imageDao.save(image);
        }
        //填写了海报的分组，但未填写尺寸，默认用该图片适配该规格分组中的所有海报
        if (null != addImgListGroups && addImgListGroups.size() != 0 && (null == addImgListWidths || addImgListWidths.size() == 0)) {
            Iterator iterator = addImgListGroups.iterator();
            while (iterator.hasNext()) {
                Integer groupId = (Integer) iterator.next();
                Iterator imgIterator = listImage.iterator();
                while (imgIterator.hasNext()) {
                    Image image = (Image) imgIterator.next();
                    String groupIdString = image.getGroupsString();
                    if (null != groupIdString && (!groupIdString.isEmpty())) {
                        List<Integer> groupIds = ImageUtil.stringToList(groupIdString);
                        if (null != groupIds && groupIds.size() != 0) {
                            if (groupIds.contains(groupId)) {
                                groupIds.remove(groupId);
                                listImage.remove(image);
                                if (groupIds.size() == 0) {
                                    imageDao.delete(image);
                                } else {
                                    image.setGroupsString(ImageUtil.listToString(groupIds));
                                    imageDao.save(image);
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
                imageDao.delete(image);
            }
            Image image = new Image();
            image.setVideoId(videoId);
            image.setPath(localPath);
            image.setGroupsString(ImageUtil.listToString(addImgListGroups));
            image.setSize("");
            image.setState(ImageUtil.listToString(addImgListGroups));
            imageDao.save(image);
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
                        String groupIdString = image.getGroupsString();
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
                                                    imageDao.delete(image);
                                                }
                                                listWidth.remove(width);
                                                listHeight.remove(height);
                                            }
                                        }
                                    }
                                    if (listWidth.size()!=0) {
                                        image.setSize(ImageUtil.sizeToString(listWidth, listHeight));
                                        imageDao.save(image);
                                        listImage = imageDao.findByVideoId(videoId);
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
                imageDao.delete(image);
            }
            Image image = new Image();
            image.setVideoId(videoId);
            image.setPath(localPath);
            image.setGroupsString(ImageUtil.listToString(addImgListGroups));
            image.setSize(ImageUtil.sizeToString(addImgListWidths,addImgListHeights));
            List<Integer> stateList = new LinkedList<>();
            stateList.add(-1);
            image.setState(ImageUtil.listToString(stateList));
            imageDao.save(image);
        }
        return true;
    }

    @Override
    public Boolean submitAllImgs(Integer videoId) {
        return null;
    }
}
