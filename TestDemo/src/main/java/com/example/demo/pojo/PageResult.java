package com.example.demo.pojo;

import lombok.Data;

import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/1/7 14:25
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
