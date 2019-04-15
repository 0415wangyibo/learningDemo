package com.wangyb.utildemo.util;

import com.wangyb.utildemo.pojo.Size;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/7/12 14:52
 * Modified By:
 * Description:海报处理相关工具类
 */
@UtilityClass
public class ImageUtil {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    /**
     * 裁剪jpg图片，但不能裁剪透明png图片（会丢失透明度）
     *
     * @param bi     要缩放的图片
     * @param height 目标高度像素
     * @param width  目标宽度像素
     * @param bb     是否补白
     */
    public BufferedImage scale(BufferedImage bi, int width, int height, boolean bb) {

        double ratio = 0.0; // 缩放比例
        //如果图片的规格和所需要缩放的比例相同则不用缩放
        if (bi.getWidth() == width && bi.getHeight() == height) {
            return bi;
        }
        double ratioHeight = (new Integer(height)).doubleValue() / bi.getHeight();
        double ratioWhidth = (new Integer(width)).doubleValue() / bi.getWidth();
        Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);//bi.SCALE_SMOOTH  选择图像平滑度比缩放速度具有更高优先级的图像缩放算法。
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
			if (width == itemp.getWidth(null)) {
				g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null), itemp.getHeight(null),
						Color.white, null);
			} else {
				g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null),
						Color.white, null);
			}
            g.dispose();
            itemp = image;
        }
        return (BufferedImage) itemp;
    }

    /**
     * 裁剪PNG图片工具类，可按任意比例缩放图片，不会丢失透明度
     *
     * @param bi2          源文件
     * @param outputWidth  裁剪宽度
     * @param outputHeight 裁剪高度
     */
    public BufferedImage resizePng(BufferedImage bi2, int outputWidth, int outputHeight) throws Exception {
        try {
            int newWidth;
            int newHeight;
            // 为等比缩放计算输出的图片宽度及高度
            double rate1 = ((double) bi2.getWidth(null)) / (double) outputWidth;
            double rate2 = ((double) bi2.getHeight(null)) / (double) outputHeight;
            // 根据缩放比率大的进行缩放控制
            double rate = rate1 < rate2 ? rate1 : rate2;
            newWidth = (int) (((double) bi2.getWidth(null)) / rate);
            newHeight = (int) (((double) bi2.getHeight(null)) / rate);
            //计算裁剪的起始坐标
            int absWidth = Math.abs(newWidth - outputWidth);
            int absHeight = Math.abs(newHeight - outputHeight);
            int x = 0, y = 0;
            if (absWidth < absHeight) {
                y = 0 - (newHeight - outputHeight) / 2;
            } else {
                x = 0 - (newWidth - outputWidth) / 2;
            }
            //生成背景图,outputWidth,outputHeight控制背景图的大小
            BufferedImage to = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = to.createGraphics();
            to = g2d.getDeviceConfiguration().createCompatibleImage(outputWidth, outputHeight,
                    Transparency.TRANSLUCENT);
            g2d.dispose();
            g2d = to.createGraphics();
            //将图片转化成与目标图片最相近的同比例图片
            @SuppressWarnings("-access")
            Image from = bi2.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_AREA_AVERAGING);
            //裁剪成所需图片
            g2d.drawImage(from, x, y, null);
            g2d.dispose();
            return to;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bi2;
    }


    /**
     * 判断图片格式是否符合要求
     *
     * @param type
     * @return
     * @throws Exception
     */
    public Boolean checkType(String type) throws Exception {
        if (type.isEmpty()) {
            logger.info("图片类型为空");
            return false;
        }
        return type.matches("(png|gif|jpg|jpeg|PNG|GIF|JPG|JPEG)");
    }

    /**
     * 找出图片中尺寸和目标尺寸相同的图片
     *
     * @param list
     * @param width
     * @param height
     * @return
     */
    public List<String> findSameSizeName(List<String> list, String width, String height) throws Exception {
        List<String> sameNames = new LinkedList<>();
        if (null != list && list.size() != 0) {
            Iterator iterator = list.iterator();
            try {
                while (iterator.hasNext()) {
                    String name = (String) iterator.next();
                    //获取图片名，无后缀
                    String name1 = name.substring(0, name.lastIndexOf("."));
                    if (!name1.isEmpty()) {
                        //获取尺寸的宽
                        String width1 = name1.substring(0, name1.lastIndexOf("x"));
                        //获取尺寸的高
                        String height1 = StringUtils.substring(name1, name1.lastIndexOf("x") + 1);
                        if (width.equals(width1) && height.equals(height1)) {
                            sameNames.add(name);
                        }
                    }
                }
            } catch (Exception e) {
                logger.info("ftp服务器中，有海报命名不符合规范");
                return null;
            }
        }
        return sameNames;
    }

    /**
     * 判断指定输入的图片格式及命名是否符合规范
     * 如果命名符合规范则返回名字中海报的尺寸
     *
     * @param file
     * @return
     * @throws Exception
     */
    public Size checkImage(MultipartFile file) throws Exception {
        if (null == file) {
            logger.info("图片不能为空");
            return null;
        }
        String fileName = file.getOriginalFilename();
        String type = null;
        String name = null;
        try {
            type = StringUtils.substring(fileName, fileName.lastIndexOf(".") + 1);
            name = fileName.substring(0, fileName.lastIndexOf("."));
        } catch (Exception e) {
            logger.info("海报命名不符合规范");
            return null;
        }
        //如果图片类型不符合要求则返回false
        if (!checkType(type)) {
            logger.info("图片类型不符合要求");
            return null;
        }
        Size size = checkName(name);
        if (null == size) {
            logger.info("图片命名不符合要求");
            return null;
        }
        return size;
    }


    /**
     * 判断图片的名字是否符合规范
     * 如果符合规范，则统一命名成150x190的格式，便于管理
     *
     * @param name
     * @return
     * @throws Exception
     */
    public Size checkName(String name) throws Exception {
        if (name.isEmpty()) {
            logger.info("图片名字不能为空");
            return null;
        }
        if (name.contains("x")) {
            //获取尺寸的宽
            String width = name.substring(0, name.lastIndexOf("x"));
            //获取尺寸的高
            String height = StringUtils.substring(name, name.lastIndexOf("x") + 1);
            if (width.isEmpty() || height.isEmpty()) {
                return null;
            }
            if (stringToInteger(width) && stringToInteger(height)) {
                return new Size(Integer.valueOf(width), Integer.valueOf(height));
            }
            return null;
        }
        if (name.contains("X")) {
            //获取尺寸的宽
            String width = name.substring(0, name.lastIndexOf("X"));
            //获取尺寸的高
            String height = StringUtils.substring(name, name.lastIndexOf("X") + 1);
            if (width.isEmpty() || height.isEmpty()) {
                return null;
            }
            if (stringToInteger(width) && stringToInteger(height)) {
                return new Size(Integer.valueOf(width), Integer.valueOf(height));
            }
            return null;
        }
        return null;
    }

    /**
     * 判断字符串是否能够转化为正整数，能转化则返回true
     *
     * @param string
     * @return
     */
    public Boolean stringToInteger(String string) {
        try {
            int number = Integer.parseInt(string);
            if (number <= 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

//    /**
//     * 获得所有图片的尺寸
//     * @param files 图片
//     * @return 图片尺寸
//     */
//    public List<Size> getFileSizes(MultipartFile[] files) throws Exception{
//        List<Size> sizeList = new ArrayList<>();
//        if (null != files && files.length != 0) {
//            for (MultipartFile file : files) {
//                Size size = checkImage(file);
//                if (null != size) {
//                    sizeList.add(size);
//                }
//            }
//        }
//        return sizeList;
//    }
}