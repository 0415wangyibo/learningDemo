package com.wu.demo.fileupload.demo.service;

import com.wu.demo.fileupload.demo.controller.response.ImgVO;
import com.wu.demo.fileupload.demo.entity.Img;
import org.springframework.web.multipart.MultipartFile;

public interface ImgService {
    /**
     * 通过视屏Id查找海报信息
     * @param videoId
     * @return
     */
    Img findByVideoId(Integer videoId);

    /**
     * 通过视频Id将所需要的海报信息返回给前端
     */
    ImgVO findByVideoID(Integer videoId);

    /**
     * 通过视频ID删除对应本数据库中的海报信息（海报信息上传到homed中）
     * @param videoId
     */
    Boolean deleteByVideoId(Integer videoId);

    /**
     * 将前端传来的图片文件进行本地适配存储，并将地址信息返回给前端，以便读取图片信息
     * @param file
     * @param videoId
     * @return
     */
    ImgVO addImg(MultipartFile file, Integer videoId);
    /**
     * 保存Img信息
     * @param img
     * @return
     */
    Boolean saveImg(Img img);
}
