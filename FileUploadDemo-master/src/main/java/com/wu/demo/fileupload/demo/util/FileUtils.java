package com.wu.demo.fileupload.demo.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 文件上传工具包
 */
public class FileUtils {

    /**
     * @param file     文件
     * @param path     文件存放路径
     * @param fileName 源文件名
     * @return
     */
    public static boolean upload(MultipartFile file, String path, String fileName) {


//         //生成新的文件名
//        String realPath = path + "/" + FileNameUtils.getFileName(fileName);

        //使用原文件名
        String realPath1 = path + "/" + "320x400" + fileName;
        String realPath2 = path + "/" + "240x300" + fileName;
        String realPath3 = path + "/" + "160x200" + fileName;
        String realPath4 = path + "/" + "500x280" + fileName;
        String realPath5 = path + "/" + "375x210" + fileName;
        String realPath6 = path + "/" + "246x138" + fileName;
        String realPath7 = path + "/" + "182x102" + fileName;
        File dest1 = new File(realPath1);
        File dest2 = new File(realPath2);
        File dest3 = new File(realPath3);
        File dest4 = new File(realPath4);
        File dest5 = new File(realPath5);
        File dest6 = new File(realPath6);
        File dest7 = new File(realPath7);
        //判断文件父目录是否存在
        if (!dest1.getParentFile().exists()) {
            dest1.getParentFile().mkdirs();
        }
        try {
            //保存文件
            BufferedImage image = ImageIO.read(file.getInputStream());
            ImageIO.write(image, "png", dest1);
            FileCopyUtils.copy(dest1, dest2);
            FileCopyUtils.copy(dest1, dest3);
            FileCopyUtils.copy(dest1, dest4);
            FileCopyUtils.copy(dest1, dest5);
            FileCopyUtils.copy(dest1, dest6);
            FileCopyUtils.copy(dest1, dest7);
            ImgUtils.scale(realPath1, realPath1, 320, 400, true);
            ImgUtils.scale(realPath2, realPath2, 240, 300, true);
            ImgUtils.scale(realPath3, realPath3, 160, 200, true);
            ImgUtils.scale(realPath4, realPath4, 500, 280, true);
            ImgUtils.scale(realPath5, realPath5, 375, 210, true);
            ImgUtils.scale(realPath6, realPath6, 246, 138, true);
            ImgUtils.scale(realPath7, realPath7, 182, 102, true);
            return true;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    public static Boolean saveImg(MultipartFile file,String path){
        File dest = new File(path);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            //保存文件
            BufferedImage image = ImageIO.read(file.getInputStream());
            ImageIO.write(image, "png", dest);
            return true;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }
    public static String uploadImg(MultipartFile file, String path, String fileName, Integer width, Integer height) {
        String realPath = path + "/" + fileName;
        File dest = new File(realPath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        //获取图片后缀
        String type = StringUtils.substring(fileName, fileName.lastIndexOf(".") + 1);
        System.out.println(type);
        //获取图片名称，无后缀
        String name = fileName.substring(0, fileName.lastIndexOf("."));//获取除后缀1位的名称
        System.out.println(name);
        try {
            //保存文件
            BufferedImage image1 = ImageIO.read(file.getInputStream());
            ImageIO.write(image1, "png", dest);
            BufferedImage image = ImgUtils2.scale(realPath, width, height, true);
            String imageString = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (null == image) {
                return null;
            }
            ImageIO.write(image, "png", bos);
            byte[] imageBytes = bos.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);
            bos.close();
            return imageString;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] transformImgToByte(MultipartFile file) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            ImageIO.write(image, "png", outputStream);
            byte[] bytes = outputStream.toByteArray();
            outputStream.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        try {
//            byte[] bytes = file.getBytes();
//            return bytes;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    //base64字符串转化成图片
    public static boolean generateImage(String imgStr) {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }
            //生成jpeg图片
            String imgFilePath = "E:/Develop/Files/Photos/la.png";//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
