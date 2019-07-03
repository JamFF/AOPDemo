package com.ff.proxy;

import android.app.Activity;

/**
 * description: 委托类接口
 * author: FF
 * time: 2019-07-03 11:40
 */
public interface ActivityManager {

    /**
     * 打开功能界面
     */
    void startActivity(Activity activity, @Constants.Activity int tag);
}
