package com.wangyb.ftpdemo.controller.request;

import com.wangyb.ftpdemo.config.DownloadCommon;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/28 15:33
 * Modified By:
 * Description: 用于更改下载配置信息
 */
@Data
@ApiModel("更改配置信息")
public class CommonReq implements Serializable {

    private static final long serialVersionUID = 7130250644216675184L;

    @ApiModelProperty(value = "文件存储ftp的主机地址")
    private String ftpHost;
    @ApiModelProperty(value = "文件存储ftp的端口")
    private int ftpPort;
    @ApiModelProperty(value = "ftp登录名")
    private String ftpUserName;
    @ApiModelProperty(value = "ftp登录密码")
    private String ftpPassword;
    @ApiModelProperty(value = "ftp中存储有更新内容的根地址")
    private String ftpPath;
    @ApiModelProperty(value = "将要存储的目的地址")
    private String destinationPath;
    @ApiModelProperty(value = "邮件称谓")
    private String emailName;
    @ApiModelProperty(value = "用于接收邮件的邮箱")
    private String receivers;
    @ApiModelProperty(value = "下载哪一天的数据，0—当天的，-1-昨天的，-2-前天的，以此类推,如果为正整数则不自动添加下载任务", required = true)
    private Integer downDate = 0;
    @ApiModelProperty(value = "是否发邮件给现场人员汇报文件下载情况", required = true)
    private Integer ifListen = 1;

    public CommonReq() {

    }

    public CommonReq(DownloadCommon common) {
        this.ftpHost = common.getFtpHost();
        this.ftpPort = common.getFtpPort();
        this.ftpUserName = common.getFtpUserName();
        this.ftpPassword = common.getFtpPassword();
        this.ftpPath = common.getFtpPath();
        this.destinationPath = common.getDestinationPath();
        this.emailName = common.getEmailName();
        String[] broad = common.getReceivers();
        if (null!=broad && broad.length > 0) {
            StringBuilder broadReceiver = new StringBuilder();
            for (int i = 0; i < broad.length; i++) {
                broadReceiver.append(broad[i]);
                if (i != broad.length - 1) {
                    broadReceiver.append(";");
                }
            }
            this.receivers = broadReceiver.toString();
        } else {
            this.receivers = null;
        }
        this.downDate = common.getDownDate();
        this.ifListen = common.getIfListen();
    }
}

