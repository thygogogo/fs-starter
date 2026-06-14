package com.fs.starter.common.context;

/**
 * 当前操作者上下文（ThreadLocal）
 * 由各应用的 Filter 在请求进入时设置，MetaObjectHandler 读取后自动填充 createBy/updateBy
 */
public class UserContext {

    /** 管理员 */
    public static final int TYPE_ADMIN = 1;
    /** 微信用户 */
    public static final int TYPE_WX_USER = 2;

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Integer> USER_TYPE = new ThreadLocal<>();

    public static void set(Long userId, int userType) {
        USER_ID.set(userId);
        USER_TYPE.set(userType);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static Integer getUserType() {
        return USER_TYPE.get();
    }

    public static void clear() {
        USER_ID.remove();
        USER_TYPE.remove();
    }
}
