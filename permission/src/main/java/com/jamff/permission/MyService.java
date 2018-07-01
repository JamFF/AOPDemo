package com.jamff.permission;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.jamff.lib.permission.annotation.Permission;
import com.jamff.lib.permission.annotation.PermissionCanceled;
import com.jamff.lib.permission.annotation.PermissionDenied;

/**
 * 描述：
 * 作者：JamFF
 * 创建时间：2018/6/30 16:44
 */
public class MyService extends Service {

    private static final String TAG = "JamFF";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestCamera();
        return super.onStartCommand(intent, flags, startId);
    }

    @Permission(Manifest.permission.CAMERA)
    private void requestCamera() {
        Log.d(TAG, "Service中请求权限 Granted");
        Toast.makeText(this, "Service中请求权限通过", Toast.LENGTH_SHORT).show();
    }

    @PermissionCanceled()
    private void canceled() {
        Log.d(TAG, "Service中请求权限 Canceled");
        Toast.makeText(this, "Service中请求权限 Canceled", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied()
    private void denied() {
        Log.d(TAG, "Service中请求权限 Denied");
        Toast.makeText(this, "Service中请求权限 Denied", Toast.LENGTH_SHORT).show();
    }
}
