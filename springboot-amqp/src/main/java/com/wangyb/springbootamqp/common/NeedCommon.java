package com.wangyb.springbootamqp.common;

/**
 * @author wangyb
 * @Date 2019/4/26 16:46
 * Modified By:
 * Description:
 */
public enum NeedCommon {
    NEED_COMMON;
    private Integer need = 0;

    public Integer getNeed() {
        return need;
    }

    public void setNeed(Integer need) {
        this.need = need;
    }
}
