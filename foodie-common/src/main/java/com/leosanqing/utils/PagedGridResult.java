package com.leosanqing.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Title: PagedGridResult.java
 * @Package com.imooc.utils
 * @Description: 用来返回分页Grid的数据格式
 * Copyright: Copyright (c) 2019
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedGridResult {
    /**
     * 当前页数
     */
    private long page;
    /**
     * 总页数
     */
    private long total;
    /**
     * 总记录数
     */
    private long records;
    /**
     * 每行显示的内容
     */
    private List<?> rows;

    public static PagedGridResult pageSetter(IPage<?> pageResult) {
        return PagedGridResult.builder()
                .page(pageResult.getCurrent())
                .total(pageResult.getPages())
                .records(pageResult.getTotal())
                .rows(pageResult.getRecords())
                .build();
    }
}
