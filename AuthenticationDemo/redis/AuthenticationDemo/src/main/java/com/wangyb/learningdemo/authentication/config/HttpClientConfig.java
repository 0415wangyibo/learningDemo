package com.wangyb.learningdemo.authentication.config;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:07
 * Modified By:
 * Description:
 */
@Configuration
public class HttpClientConfig {

    @Value("${config.httpclient.conn-max-total}")
    private int connMaxTotal;

    @Value("${config.httpclient.max-per-route}")
    private int MaxPerRoute;

    @Value("${config.httpclient.conn-timeout}")
    private int connTimeout;

    @Value("${config.httpclient.conn-request-timeout}")
    private int connRequestTimeout;

    @Value("${config.httpclient.socket-timeout}")
    private int socketTimeout;

    @Value("${config.httpclient.retry-time}")
    private int retryTime;

    @Bean
    public HttpRequestRetryHandler httpRequestRetryHandler() {
        // 请求重试
        return new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                // 如果重试次数超过了retryTime,则不再重试请求
                if (executionCount >= retryTime) {
                    return false;
                }
                // 服务端断掉客户端的连接异常
                if (exception instanceof NoHttpResponseException) {
                    return true;
                }
                // time out 超时重试
                if (exception instanceof InterruptedIOException) {
                    return true;
                }
                // Unknown host
                if (exception instanceof UnknownHostException) {
                    return false;
                }
                // Connection refused
                if (exception instanceof ConnectTimeoutException) {
                    return false;
                }
                // SSL handshake exception
                if (exception instanceof SSLException) {
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * 首先实例化一个连接池管理器，设置最大连接数、并发连接数
     *
     * @return
     */
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
        // 最大连接数
        httpClientConnectionManager.setMaxTotal(connMaxTotal);
        // 并发数
        httpClientConnectionManager.setDefaultMaxPerRoute(MaxPerRoute);
        return httpClientConnectionManager;
    }

    /**
     * 实例化连接池，设置连接池管理器。 这里需要以参数形式注入上面实例化的连接池管理器
     *
     * @param httpClientConnectionManager
     * @return
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(
            @Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager) {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager).setConnectionManagerShared(true);
        // 设置重试handler
        httpClientBuilder.setRetryHandler(httpRequestRetryHandler());

        return httpClientBuilder;
    }

    /**
     * 注入连接池，用于获取httpClient
     *
     * @param httpClientBuilder
     * @return
     */
    @Bean
    public CloseableHttpClient getCloseableHttpClient(
            @Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
    }

    /**
     * Builder是RequestConfig的一个内部类 通过RequestConfig的custom方法来获取到一个Builder对象
     * 设置builder的连接信息 这里还可以设置proxy，cookieSpec等属性。有需要的话可以在此设置
     *
     * @return
     */
    @Bean(name = "builder")
    public RequestConfig.Builder getBuilder() {
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(connTimeout).setConnectionRequestTimeout(connRequestTimeout)
                .setSocketTimeout(socketTimeout);
    }

    /**
     * 使用builder构建一个RequestConfig对象
     *
     * @param builder
     * @return
     */
    @Bean
    public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder) {
        return builder.build();
    }

    /**
     * 开一个守护线程定时清理过期和空闲的连接
     *
     * @param httpClientConnectionManager
     * @return
     */
    @Bean
    public IdleConnectionEvictor getIdleConnectionEvictor(
            @Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager) {
        IdleConnectionEvictor evictor = new IdleConnectionEvictor(httpClientConnectionManager, 5, TimeUnit.SECONDS);
        evictor.start();
        return evictor;
    }

}
