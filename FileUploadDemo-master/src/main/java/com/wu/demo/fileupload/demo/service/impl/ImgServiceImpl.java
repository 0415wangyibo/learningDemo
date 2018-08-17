package com.wu.demo.fileupload.demo.service.impl;

import com.wu.demo.fileupload.demo.controller.response.ImgVO;
import com.wu.demo.fileupload.demo.dao.ImgDao;
import com.wu.demo.fileupload.demo.entity.Img;
import com.wu.demo.fileupload.demo.service.ImgService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Service
public class ImgServiceImpl implements ImgService{

    @Resource
    private ImgDao imgDao;

    @Override
    public Img findByVideoId(Integer videoId) {
        return imgDao.findByVideoId(videoId);
    }

    @Override
    public ImgVO findByVideoID(Integer videoId) {
        Img img = imgDao.findByVideoId(videoId);
        if (null==img) {
            return null;
        }else {
            return new ImgVO(img);
        }
    }

    @Override
    public ImgVO addImg(MultipartFile file, Integer videoId) {
        Img img = imgDao.findByVideoId(videoId);
        if (null==img){

        }
        return null;
    }

    @Override
    public Boolean deleteByVideoId(Integer videoId) {
        imgDao.deleteByVideoId(videoId);
        return true;
    }

    @Override
    public Boolean saveImg(Img img) {
        imgDao.save(img);
        return true;
    }
}
