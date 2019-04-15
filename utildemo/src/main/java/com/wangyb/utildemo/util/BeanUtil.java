package com.wangyb.utildemo.util;

import lombok.experimental.UtilityClass;

/**
 * @author wangyb
 * @Date 2019/4/15 14:49
 * Modified By:
 * Description:
 */
@UtilityClass
public class BeanUtil {
    /**
     * 实现javabean的复制，只会复制拥有相同名称的字段
     * @param source
     * @param target
     */
    public void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }
}
