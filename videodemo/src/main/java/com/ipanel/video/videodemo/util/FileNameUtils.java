package com.ipanel.video.videodemo.util;

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