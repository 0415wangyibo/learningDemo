package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/7 14:50
 * Modified By:
 * Description:
 */
@Configuration
@ConfigurationProperties(prefix = "config")
@Data
public class MyConfig {
    private Map<String, Integer> history;
    private Map<String, Integer> httpclient;

    public Integer getNumber() {
        return getHistory().get("number");
    }

    public Integer getConnMaxTotal() {
        return getHttpclient().get("conn-max-total");
    }

    public Integer getMaxPerRoute() {
        return getHttpclient().get("max-per-route");
    }

    public Integer getConnTimeout() {
        return getHttpclient().get("conn-timeout");
    }

    public Integer getConnRequestTimeout() {
        return getHttpclient().get("conn-request-timeout");
    }

    public Integer getSocketTimeout() {
        return getHttpclient().get("socket-timeout");
    }

    public Integer getRetryTime(){
        return getHttpclient().get("retry-time");
    }
}
