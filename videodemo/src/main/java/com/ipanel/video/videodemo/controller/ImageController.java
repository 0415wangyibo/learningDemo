package com.ipanel.video.videodemo.controller;

import com.ipanel.video.videodemo.controller.request.AddImg;
import com.ipanel.video.videodemo.controller.request.ShowImg;
import com.ipanel.video.videodemo.dao.ImageMapper;
import com.ipanel.video.videodemo.model.Image;
import com.ipanel.video.videodemo.service.ImageService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "image")
public class ImageController {
    private ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService){
        this.imageService = imageService;
    }
//
//    @RequestMapping(value = {"index","/"},method = RequestMethod.GET)
//    public String toIndex(Model model){
//        model.addAttribute("showImg",new ShowImg());
//        return "index";
//    }
    @ApiOperation(value = "根据视频Id查询海报信息")
    @ApiImplicitParam(name = "videoId", value = "视频Id", required = true, dataType = "int", paramType = "path")
    @RequestMapping(value = "/images/{videoId}",method = RequestMethod.GET)
    public List<Image> findByVideoId(@PathVariable(value = "videoId")Integer videoId){
        return imageService.findByVideoId(videoId);
    }

    @ApiOperation(value = "变更海报信息")
    @RequestMapping(value = "change", method = RequestMethod.POST)
    public Boolean changeImage(MultipartFile file, @ModelAttribute AddImg addImg) throws Exception{
        return imageService.changeImg(file, addImg);
    }

    @ApiOperation(value = "展示指定规格、尺寸的海报图片")
    @RequestMapping(value = "showImage",method = RequestMethod.POST)
    public String showImage(@ModelAttribute ShowImg showImg)throws Exception {
        return imageService.showImgBySize(showImg);
    }
}
