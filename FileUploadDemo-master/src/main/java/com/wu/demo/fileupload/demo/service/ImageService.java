package com.wu.demo.fileupload.demo.service;

import com.wu.demo.fileupload.demo.controller.request.AddImg;
import com.wu.demo.fileupload.demo.controller.request.ShowImg;
import com.wu.demo.fileupload.demo.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    /**
     * 根据homed中的海报信息初始化本地海报（用户未编辑海报时，用该方法初始化）
     * @return
     */
    Boolean initImgs();

    /**
     * 根据视频Id查询该视频下所有规格的海报
     * @param videoId
     * @return
     */
    List<Image> findByVideoId(Integer videoId);


    /**
     * 根据前端传来的参数，返回对应规格对应尺寸的海报base64字符串
     * @param showImg
     * @return
     */
    String showImgBySize(ShowImg showImg);

    /**
     * 根据传进来的参数更改或者添加海报
     * @param file
     * @param addImg
     * @return
     */
    Boolean changeImg(MultipartFile file,AddImg addImg)throws Exception;

    /**
     * 将所有海报图片提交到homed平台中存储
     * @param videoId
     * @return
     */
    Boolean submitAllImgs(Integer videoId);
}
