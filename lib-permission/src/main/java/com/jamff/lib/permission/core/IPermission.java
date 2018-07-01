package com.jamff.lib.permission.core;

/**
 * 描述：
 * 作者：JamFF
 * 创建时间：2018/6/30 11:25
 */
public interface IPermission {

    /**
     * 授权
     */
    void onGranted();

    /**
     * 取消
     */
    void onCanceled();


    /**
     * 拒绝，不在提示
     */
    void onDenied();
}
