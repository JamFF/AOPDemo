package com.jamff.lib.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.jamff.lib.permission.core.IPermission;

/**
 * 描述：
 * 作者：JamFF
 * 创建时间：2018/6/30 10:48
 */
public class PermissionActivity extends Activity {

    public static final String PARAM_PERMISSION = "param_permission";
    public static final String PARAM_REQUEST_CODE = "param_request_code";

    public static final int REQUEST_CODE_ERROR = -1;

    private String[] mPermissions;
    private int mRequestCode;
    private static IPermission permissionListener;

    // 切入的时候，需要添加的代码
    public static void requestPermission(Context context, String[] permissions, int requestCode, IPermission iPermission) {

        permissionListener = iPermission;
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putStringArray(PARAM_PERMISSION, permissions);
        bundle.putInt(PARAM_REQUEST_CODE, requestCode);
        intent.putExtras(bundle);
        context.startActivity(intent);

        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_layout);

        if (getIntent() == null || getIntent().getExtras() == null) {
            this.finish();
            return;
        }

        Bundle bundle = getIntent().getExtras();
        mPermissions = bundle.getStringArray(PARAM_PERMISSION);
        mRequestCode = bundle.getInt(PARAM_REQUEST_CODE, REQUEST_CODE_ERROR);

        if (mPermissions == null || mRequestCode == REQUEST_CODE_ERROR || permissionListener == null) {
            this.finish();
            return;
        }

        if (PermissionUtils.hasPermission(this, mPermissions)) {
            permissionListener.onGranted();
            finish();
            return;
        }

        ActivityCompat.requestPermissions(this, mPermissions, mRequestCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionUtils.verifyPermission(this, grantResults)) {
            // 请求权限成功
            permissionListener.onGranted();
            finish();
            return;
        }

        if (PermissionUtils.shouldShowRequestPermissionRational(this, permissions)) {
            // 拒绝
            permissionListener.onCanceled();
        } else {
            // 拒绝并勾选不再提示
            permissionListener.onDenied();
        }
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        // 屏蔽Activity切换动画
        overridePendingTransition(0, 0);
    }
}
