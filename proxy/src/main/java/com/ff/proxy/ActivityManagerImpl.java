package com.ff.proxy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * description: 委托类
 * author: FF
 * time: 2019-07-03 11:45
 */
public class ActivityManagerImpl implements ActivityManager {

    @Override
    public void startActivity(Activity activity, int tag) {
        Intent intent = new Intent(activity, MemberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(MemberActivity.KEY, tag);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }
}
