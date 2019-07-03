package com.ff.proxy;

import android.support.annotation.IntDef;

/**
 * description:
 * author: FF
 * time: 2019-07-03 14:24
 */
public interface Constants {

    int ACTIVITY_ME = 1;// 我的界面
    int ACTIVITY_PAY = 2;// 支付界面
    int ACTIVITY_MESSAGE = 3;// 消息界面

    @IntDef({ACTIVITY_ME, ACTIVITY_PAY, ACTIVITY_MESSAGE})
    @interface Activity {
    }
}
