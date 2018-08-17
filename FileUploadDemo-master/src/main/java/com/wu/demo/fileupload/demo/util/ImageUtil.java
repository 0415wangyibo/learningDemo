package com.wu.demo.fileupload.demo.util;

import com.wu.demo.fileupload.demo.dto.ImgInteger;
import com.wu.demo.fileupload.demo.dto.ImgSize;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

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
}
