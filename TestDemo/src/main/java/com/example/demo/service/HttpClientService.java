package com.example.demo.service;

import com.example.demo.pojo.HttpResult;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/7 14:29
 * Modified By:
 * Description:
 */
@Service
public class HttpClientService {

    private final CloseableHttpClient httpClient;

    private final RequestConfig config;

    @Autowired
    public HttpClientService(CloseableHttpClient httpClient, RequestConfig config) {
        this.httpClient = httpClient;
        this.config = config;
    }

    /**
     * POST请求没有
     *
     * @param url
     * @return
     * @throws IOException
     */
    public HttpResult doPost(String url) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        httpPost.setHeader("Content-Type", "application/json");
        HttpResult result;
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            result = new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, "UTF-8"));
            EntityUtils.consumeQuietly(entity);
        }

        return result;
    }

    /**
     * POST请求参数为FORM-DATA
     *
     * @param url
     * @param map
     * @return
     * @throws IOException
     */
    public HttpResult doPost(String url, Map<String, Object> map) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);

        if (map != null) {
            List<BasicNameValuePair> parameters = new ArrayList<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            httpPost.setEntity(urlEncodedFormEntity);
        }
        // 原生表单
        httpPost.setHeader("Content-Type", "x-www-form-urlencoded");
        HttpResult result = null;
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            result = new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, "UTF-8"));
            EntityUtils.consumeQuietly(entity);
        }
        return result;
    }

    /**
     * POST请求参数为JSON格式数据
     *
     * @param url
     * @param bodyData
     * @return
     * @throws IOException
     */
    public HttpResult doPost(String url, String bodyData) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        StringEntity body = new StringEntity(bodyData);
        httpPost.setEntity(body);
        httpPost.setHeader("Content-Type", "application/json");
        HttpResult result;
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            result = new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, "UTF-8"));
            EntityUtils.consumeQuietly(entity);
        }

        return result;
    }

    /**
     * 不带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
     *
     * @param url
     * @return
     * @throws Exception
     */
    public HttpResult doGet(String url) throws Exception {
        HttpResult result;

        // 声明 http get 请求
        HttpGet httpGet = new HttpGet(url);

        // 装载配置信息
        httpGet.setConfig(config);

        // 发起请求
        try (CloseableHttpResponse response = this.httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            result = new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(entity, "UTF-8"));
        }
        return result;
    }

    /**
     * 拼接带参数的get请求地址
     *
     * @param url    url根地址
     * @param params 需要拼接的参数
     * @return get请求的全地址
     */
    public static String getRequestUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url);
        boolean isFirst = true;
        for (String key : params.keySet()) {
            if (key != null && params.get(key) != null) {
                if (isFirst) {
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key)
                        .append("=")
                        .append(params.get(key));
            }
        }
        return builder.toString();
    }
}
