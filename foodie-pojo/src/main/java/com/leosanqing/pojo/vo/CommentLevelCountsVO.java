package com.leosanqing.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: leosanqing
 * @Date: 2019-12-08 21:37
 *
 * 商品 评价VO
 */
@Data
@AllArgsConstructor
public class CommentLevelCountsVO {
    private Integer totalCounts;
    private Integer goodCounts;
    private Integer normalCounts;
    private Integer badCounts;
}
