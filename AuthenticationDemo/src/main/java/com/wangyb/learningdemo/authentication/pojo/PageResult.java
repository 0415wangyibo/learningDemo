package com.wangyb.learningdemo.authentication.pojo;

import java.util.List;

import lombok.Data;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/10/8 16:34
 * Modified By:
 * Description:
 */

@Data
public class PageResult<T> {

    private List<T> rows;

    private int page;

    private int pageSize;

    private long total;

    public PageResult(List<T> rows, int page, int pageSize, long total) {
        super();
        this.rows = rows;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }

}