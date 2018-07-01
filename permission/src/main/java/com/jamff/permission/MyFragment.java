package com.jamff.permission;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.jamff.lib.permission.annotation.Permission;
import com.jamff.lib.permission.annotation.PermissionCanceled;
import com.jamff.lib.permission.annotation.PermissionDenied;

/**
 * 描述：
 * 作者：JamFF
 * 创建时间：2018/6/30 16:20
 */
public class MyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "JamFF";
    private Button mButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permission, container, false);
        mButton = view.findViewById(R.id.bt_fragment);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        requestLocation();
    }

    @Permission(Manifest.permission.CAMERA)
    private void requestLocation() {
        Log.d(TAG, "Fragment中请求权限 Granted");
        Toast.makeText(getContext(), "Fragment中请求权限通过", Toast.LENGTH_SHORT).show();
    }

    @PermissionCanceled()
    private void canceled() {
        Log.d(TAG, "Fragment中请求权限 Canceled");
        Toast.makeText(getContext(), "Fragment中请求权限 Canceled", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied()
    private void denied() {
        Log.d(TAG, "Fragment中请求权限 Denied");
        Toast.makeText(getContext(), "Fragment中请求权限 Denied", Toast.LENGTH_SHORT).show();
    }
}
