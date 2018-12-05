package com.example.quartz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/12/5 11:27
 * Modified By:
 * Description:
 */
@Service
public class TestNameService {

    @Value("${namePath}")
    private String filePath;

    public String getName(){
        try {
            File nameFile = ResourceUtils.getFile(filePath);
            FileInputStream inputStream = new FileInputStream(nameFile);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder jsonData = new StringBuilder();
            String tempData;
            while ((tempData = bufferedReader.readLine()) != null) {
                jsonData.append(tempData);
            }
            JSONObject object = JSON.parseObject(jsonData.toString());
            System.out.println(object.toString());
            if (object.containsKey("name")) {
                return object.getString("name");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
