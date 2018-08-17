package com.wu.demo.fileupload.demo.util;

import java.util.Iterator;
import java.util.List;

public class FileNameUtils {

    /**
     * 获取文件后缀
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName){
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 生成新的文件名
     * @param fileOriginName 源文件名
     * @return
     */
    public static String getFileName(String fileOriginName){
        return UUIDUtils.getUUID() + FileNameUtils.getSuffix(fileOriginName);
    }

    public static String listToString(List<Integer> list){
        if (null==list||list.size()==0){
            return "";
        }
        StringBuffer string =new StringBuffer("");
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            string.append(Integer.toString((Integer) iterator.next()));
        }
        return string.toString();
    }

}
