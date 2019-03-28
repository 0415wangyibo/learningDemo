package com.wangyb.ftpdemo.controller.request;

import com.wangyb.ftpdemo.config.UploadCommon;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/2/15 16:20
 * Modified By:
 * Description: 用于更改上传配置信息
 */
@Data
@ApiModel("自动上传配置信息")
public class UploadReq implements Serializable {

    private static final long serialVersionUID = 7130250644216675184L;

    @ApiModelProperty(value = "存储的ftp")
    private String ftpHost;
    @ApiModelProperty(value = "ftp的端口")
    private int ftpPort;
    @ApiModelProperty(value = "ftp登录名")
    private String ftpUserName;
    @ApiModelProperty(value = "ftp登录密码")
    private String ftpPassword;
    @ApiModelProperty(value = "文件夹存放的目录")
    private String ftpPath = "/testUpload/";
    @ApiModelProperty(value = "下载完毕后是否自动上传，默认自动上传",required = true)
    private Integer ifUpload = 1;

    public UploadReq() {

    }

    public UploadReq(UploadCommon uploadCommon) {
        this.ftpHost = uploadCommon.getFtpHost();
        this.ftpPort = uploadCommon.getFtpPort();
        this.ftpUserName = uploadCommon.getFtpUserName();
        this.ftpPassword = uploadCommon.getFtpPassword();
        this.ftpPath = uploadCommon.getFtpPath();
        this.ifUpload = uploadCommon.getIfUpload();
    }
}
