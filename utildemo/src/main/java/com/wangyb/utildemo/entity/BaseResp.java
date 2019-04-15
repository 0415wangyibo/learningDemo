package com.wangyb.utildemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * @author wangyb
 * @Date 2019/4/15 14:41
 * Modified By:
 * Description:
 */
@Data
@JacksonXmlRootElement(localName = "Body")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResp {
    // 响应码
    @JacksonXmlProperty(localName = "Ret")
    private Integer ret;
    // 响应消息
    @JacksonXmlProperty(localName = "RetMsg")
    private String retMsg;
}
