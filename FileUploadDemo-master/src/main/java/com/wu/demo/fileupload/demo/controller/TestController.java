package com.wu.demo.fileupload.demo.controller;

import com.wu.demo.fileupload.demo.dto.ImgGroupsString;
import com.wu.demo.fileupload.demo.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
public class TestController {

    private final ResourceLoader resourceLoader;

    @Autowired
    public TestController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Value("${web.upload-path}")
    private String path;

    /**
     * 跳转到文件上传页面
     * @return
     */
    @RequestMapping("test")
    public String toUpload(){
        return "test";
    }

    /**
     *
     * @param file 要上传的文件
     * @return
     */
    @RequestMapping("fileUpload")
    public String upload(@RequestParam("fileName") MultipartFile file, Map<String, Object> map){

        // 要上传的目标文件存放路径
        String localPath = "E:/Develop/Files/Photos";
        // 上传成功或者失败的提示
        String msg = "";
        if (FileUtils.upload(file, localPath, file.getOriginalFilename())){
            // 上传成功，给出页面提示
            msg = "上传成功！";
        }else {
            msg = "上传失败！";
        }
        //图片以base64编码形式发送到前端，解析后直接显示图片，不用再次调用读取
        String base64 = FileUtils.uploadImg(file,localPath, file.getOriginalFilename(),200,300);
        System.out.println("原文件名："+file.getOriginalFilename());
        // 显示图片
        map.put("msg", msg);
        map.put("fileName1", "320x400"+file.getOriginalFilename());
        map.put("fileName2", "240x300"+file.getOriginalFilename());
        map.put("fileName3", "160x200"+file.getOriginalFilename());
        map.put("fileName4", "500x280"+file.getOriginalFilename());
        map.put("fileName5", "375x210"+file.getOriginalFilename());
        map.put("fileName6", "246x138"+file.getOriginalFilename());
        map.put("fileName7", "182x102"+file.getOriginalFilename());
        map.put("fileName8", base64);
        byte[] bytes = FileUtils.transformImgToByte(file);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        BufferedImage bi1 = null;
        try {
            bi1 = ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File w2 = new File("E:/Develop/Files/Photos/tu.png");//可以是jpg,png,gif格式
            ImageIO.write(bi1, "png", w2);
            bais.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //测试1
//        List<ImgGroupsString> list = new LinkedList<>();
//        ImgGroupsString group1 = new ImgGroupsString(1);
//        ImgGroupsString group2 = new ImgGroupsString(2);
//        list.add(group1);
//        list.add(group2);
//        JSONArray jsonArray = JSONArray.fromObject(list);
//        String jsonString = jsonArray.toString();
//        System.out.println(jsonString);
//        JSONArray jsonArray1 = JSONArray.fromObject(jsonString);
//        List myList = JSONArray.toList(jsonArray1, new ImgGroupsString(), new JsonConfig());
//        Iterator it = myList.iterator();
//        while (it.hasNext()){
//            ImgGroupsString imgGroupsString = (ImgGroupsString)it.next();
//            System.out.println(imgGroupsString.getGroupId());
//        }

        //测试2
//        List<String> list = new LinkedList<>();
//        list.add("wang");
//        list.add("yi");
//        list.add("bo");
//        JSONArray jsonArray = JSONArray.fromObject(list);
//        String jsonString = jsonArray.toString();
//        System.out.println(jsonString);
//        JSONArray jsonArray1 = JSONArray.fromObject(jsonString);
//        List mylist = JSONArray.toList(jsonArray1);
//        Iterator iterator = mylist.iterator();
//        while (iterator.hasNext()){
//            String string = (String) iterator.next();
//            System.out.println(string);
//        }
        return "forward:/test";
    }

    /**
     * 显示单张图片
     * @return
     */
    @RequestMapping("show")
    public ResponseEntity showPhotos1(String fileName){

        try {
            // 由于是读取本机的文件，file是一定要加上的， path是在application配置文件中的路径
            return ResponseEntity.ok(resourceLoader.getResource("file:" + path + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


}
