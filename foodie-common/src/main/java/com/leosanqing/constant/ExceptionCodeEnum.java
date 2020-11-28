package com.leosanqing.constant;

/**
 * @author zhuerchong
 */

public enum ExceptionCodeEnum implements IExceptionCode {
    IMG_TYPE_ERROR(10001,"图片格式不正确"),
    PRODUCT_HAS_COMMENT(10002,"商品已经评价过"),
    COMMENT_LIST_IS_EMPTY(10003,"评价列表为空"),
    ORDER_LIST_IS_EMPTY(10004,"订单列表为空"),
    ORDER_NOT_EXIST(10005,"订单不存在"),
    SHOP_CART_DATA_INCORRECT(10006,"购物车数据不正确"),
    USERNAME_IS_EXIST(10007,"用户名已存在"),
    CONFIRM_PASSWORD_INCORRECT(10008,"两次密码不一致"),
    PASSWORD_INCORRECT(10009,"密码输入不正确"),
    FACE_UPLOAD_FAILED(10010,"上传用户头像失败"),
    KEYWORD_IS_EMPTY(10011,"关键字为空"),
    CONFIRM_RECEIVE_FAILED(10012,"确认收货失败"),
    DELETE_ORDER_FAILED(10013,"删除订单失败"),
    NEED_TO_LOGIN(10014,"请登录"),
    ANOTHER_LOCATION_NEED_TO_LOGIN(10015,"异地登录，请重新登录"),
    ;

    private int code;
    private String desc;

    ExceptionCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMessage() {
        return this.desc;
    }
}
