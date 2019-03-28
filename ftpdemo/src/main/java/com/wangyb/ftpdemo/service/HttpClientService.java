package com.wangyb.ftpdemo.service;

import com.wangyb.ftpdemo.pojo.HttpResult;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/3/22 15:52
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

}
