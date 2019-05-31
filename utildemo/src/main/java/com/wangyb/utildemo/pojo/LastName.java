package com.wangyb.utildemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangyb
 * @Date 2019/5/22 10:42
 * Modified By:
 * Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastName {
    private String chName;//中文名
    private String toneName;//含音调拼音
    private String withoutToneName;//无音调拼音
}
