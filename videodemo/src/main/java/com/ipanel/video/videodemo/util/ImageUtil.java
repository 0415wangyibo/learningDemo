package com.ipanel.video.videodemo.util;

import com.ipanel.video.videodemo.dto.*;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ImageUtil {
    public static String listToString(List<Integer> list){
        if (null==list||list.size()==0){
            return "";
        }
        List<ImgInteger> imgList = new LinkedList<>();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            ImgInteger imgInteger = new ImgInteger((Integer) iterator.next());
            imgList.add(imgInteger);
        }
        JSONArray jsonArray = JSONArray.fromObject(list);
        String jsonString = jsonArray.toString();
        return jsonString;
    }

    public static List<Integer> stringToList(String string){
        if (null==string||string.isEmpty()){
            return null;
        }
        JSONArray jsonArray = JSONArray.fromObject(string);
        List list = JSONArray.toList(jsonArray, new ImgInteger(), new JsonConfig());
        Iterator iterator = list.iterator();
        List<Integer> myList=new LinkedList<>();
        while (iterator.hasNext()){
            ImgInteger imgInteger = (ImgInteger) iterator.next();
            myList.add(imgInteger.getNumber());
        }
        return myList;
    }

    public static String sizeToString(List<Integer> listWidth,List<Integer> listHeight){
        if (null==listWidth||null==listHeight||listWidth.size()!=listHeight.size()){
            return "";
        }
        List<ImgSize> list = new LinkedList<>();
        Iterator iteratorWidth = listWidth.iterator();
        Iterator iteratorHeight = listHeight.iterator();
        for (int i=0;i<listWidth.size();i++){
            ImgSize imgSize = new ImgSize((Integer)iteratorWidth.next(),(Integer)iteratorHeight.next());
            list.add(imgSize);
        }
        JSONArray jsonArray = JSONArray.fromObject(list);
        String jsonString = jsonArray.toString();
        return jsonString;
    }

    public static List<ImgSize> stringToSize(String string){
        if (null==string||string.isEmpty()){
            return null;
        }
        JSONArray jsonArray = JSONArray.fromObject(string);
        List list = JSONArray.toList(jsonArray, new ImgSize(), new JsonConfig());
        Iterator iterator = list.iterator();
        List<ImgSize> myList = new LinkedList<>();
        while (iterator.hasNext()){
            myList.add((ImgSize) iterator.next());
        }
        return myList;
    }

    /**
     * 缩放图片方法,将缩放后的图片以Base64字符串形式返回
     * @param srcImageFile 要缩放的图片路径
     * @param height 目标高度像素
     * @param width  目标宽度像素
     * @param bb     是否补白
     */
    public  static String scale(String srcImageFile, int width, int height, boolean bb) {
        try {
            double ratio = 0.0; // 缩放比例
            File f = new File(srcImageFile);
            BufferedImage bi = ImageIO.read(f);
            double ratioHeight = (new Integer(height)).doubleValue() / bi.getHeight();
            double ratioWhidth = (new Integer(width)).doubleValue() / bi.getWidth();
            Image itemp = bi.getScaledInstance(width, height, bi.SCALE_SMOOTH);//bi.SCALE_SMOOTH  选择图像平滑度比缩放速度具有更高优先级的图像缩放算法。
            // 计算比例
            if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
                if (ratioHeight > ratioWhidth) {
                    ratio = ratioHeight;
                } else {
                    ratio = ratioWhidth;
                }
                AffineTransformOp op = new AffineTransformOp(AffineTransform//仿射转换
                        .getScaleInstance(ratio, ratio), null);//返回表示剪切变换的变换
                itemp = op.filter(bi, null);//转换源 BufferedImage 并将结果存储在目标 BufferedImage 中。
            }
            if (bb) {//补白
                BufferedImage image = new BufferedImage(width, height,
                        BufferedImage.TYPE_INT_RGB);//构造一个类型为预定义图像类型之一的 BufferedImage。
                Graphics2D g = image.createGraphics();//创建一个 Graphics2D，可以将它绘制到此 BufferedImage 中。
                g.setColor(Color.white);//控制颜色
                g.fillRect(0, 0, width, height);// 使用 Graphics2D 上下文的设置，填充 Shape 的内部区域。
                if (width == itemp.getWidth(null))
                    g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
                            itemp.getWidth(null), itemp.getHeight(null),
                            Color.white, null);
                else
                    g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
                            itemp.getWidth(null), itemp.getHeight(null),
                            Color.white, null);
                g.dispose();
                itemp = image;
            }
            BufferedImage image = (BufferedImage) itemp;
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
            return imageString;//输出压缩图片
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
