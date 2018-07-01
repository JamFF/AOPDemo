package com.jamff.permission;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jamff.lib.permission.annotation.Permission;
import com.jamff.lib.permission.annotation.PermissionCanceled;
import com.jamff.lib.permission.annotation.PermissionDenied;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "JamFF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.bt_all).setOnClickListener(this);
        findViewById(R.id.bt_all_exclude).setOnClickListener(this);
        findViewById(R.id.bt_one_permission).setOnClickListener(this);
        findViewById(R.id.bt_two_permission).setOnClickListener(this);
        findViewById(R.id.bt_request_200).setOnClickListener(this);
        findViewById(R.id.bt_service).setOnClickListener(this);
    }

    private void initData() {
        MyFragment fragment = new MyFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_all:
                requestAll();
                break;
            case R.id.bt_all_exclude:
                requestAllExclude();
                break;
            case R.id.bt_one_permission:
                requestOnePermission();
                break;
            case R.id.bt_two_permission:
                requestTwoPermission();
                break;
            case R.id.bt_request_200:
                requestRequest200();
                break;
            case R.id.bt_service:
                requestService();
                break;
        }
    }

    @Permission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION})
    private void requestAll() {
        Log.d(TAG, "请求全部权限 Granted");
        Toast.makeText(this, "请求全部权限成功", Toast.LENGTH_SHORT).show();
    }

    @Permission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION})
    private void requestAllExclude() {
        Log.d(TAG, "请求除相机权限 Granted");
        Toast.makeText(this, "请求除相机权限成功", Toast.LENGTH_SHORT).show();
    }

    @Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private void requestOnePermission() {
        Log.d(TAG, "请求写入权限 Granted");
        Toast.makeText(this, "请求写入权限成功", Toast.LENGTH_SHORT).show();
    }

    @Permission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    private void requestTwoPermission() {
        Log.d(TAG, "请求两个权限 Granted");
        Toast.makeText(this, "请求两个权限成功", Toast.LENGTH_SHORT).show();
    }

    private void requestRequest200() {
        Log.d(TAG, "请求定位权限 Granted");
        Toast.makeText(this, "请求定位权限成功", Toast.LENGTH_SHORT).show();
    }

    private void requestService() {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    @PermissionCanceled()
    private void canceled() {
        Log.d(TAG, "请求权限 Canceled");
        Toast.makeText(this, "请求权限 Canceled", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied()
    private void denied() {
        Log.d(TAG, "请求权限 Denied");
        Toast.makeText(this, "请求权限 Denied", Toast.LENGTH_SHORT).show();
    }

    /*@PermissionCanceled(requestCode = 200)
    private void canceled200() {
        Log.d(TAG, "请求定位权限 Canceled");
        Toast.makeText(this, "请求定位权限 Canceled", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(requestCode = 200)
    private void denied200() {
        Log.d(TAG, "请求定位权限 Denied");
        Toast.makeText(this, "请求定位权限 Denied", Toast.LENGTH_SHORT).show();
    }*/
}
