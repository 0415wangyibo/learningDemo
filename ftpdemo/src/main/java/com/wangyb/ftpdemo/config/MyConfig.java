package com.wangyb.ftpdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/3/22 15:36
 * Modified By:
 * Description:
 */
@Configuration
@ConfigurationProperties(prefix = "config")
@Data
public class MyConfig {

    private String email;
    private Integer number;
    private Integer connMaxTotal;
    private Integer maxPerRoute;
    private Integer connTimeout;
    private Integer connRequestTimeout;
    private Integer socketTimeout;
    private Integer retryTime;

}
