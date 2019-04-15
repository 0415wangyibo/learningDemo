package com.wangyb.utildemo.pojo;

import lombok.Data;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/7/10 14:52
 * Modified By:
 * Description:海报的尺寸
 */
@Data
public class Size {
    private Integer width;
    private Integer height;

    public Size(Integer width, Integer height) {
        this.width = width;
        this.height = height;
    }
}
