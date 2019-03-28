package com.wangyb.ftpdemo.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/2/28 15:21
 * Modified By:
 * Description: 路径工具类，规范路径，防止程序出错
 */
@UtilityClass
@Slf4j
public class PathUtil {

    /**
     * 规范ftp的路径形式，防止前后端缺省/号，导致程序出错，循环创建文件夹
     * @param oldPath ftp路径
     * @return 规范的ftp路径
     */
    public String formFtpPath(String oldPath) {
        String newPath = "/";
        if (!StringUtils.isEmpty(oldPath)) {
            if (!oldPath.substring(oldPath.length() - 1).equals("/")) {
                newPath = oldPath + "/";
            }else {
                newPath = oldPath;
            }
            if (!newPath.substring(0, 1).equals("/")) {
                newPath = "/" + newPath;
            }
        }
        return newPath;
    }

    /**
     *
     * @param path 以/结尾的路径
     * @return 不以/结尾的路径
     */
    public String getDownloadPath(String path) {
        return path.substring(0, path.length() - 1);
    }
}
