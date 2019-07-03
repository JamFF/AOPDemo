package com.example.fj.aop;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.fj.aop.annotation.BehaviorTrace;
import com.example.fj.aop.annotation.UserInfoBehaviorTrace;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "JamFF";

    private Unbinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBinder = ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_shake, R.id.bt_audio, R.id.bt_video})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_shake:
                mShake();
                break;
            case R.id.bt_audio:
                mAudio(1);
                break;
            case R.id.bt_video:
                mVideo(1, 2);
                break;
        }
    }

    @UserInfoBehaviorTrace
    @BehaviorTrace("语音消息")
    private void mAudio(int i) {
        // long begin = System.currentTimeMillis();

        Log.d(TAG, "mAudio: ");
        // 模拟网络延迟
        SystemClock.sleep(new Random().nextInt(3000));

        // long duration = System.currentTimeMillis() - begin;

        // Log.d(TAG, "语音消息功能，耗时:" + duration);
    }

    @UserInfoBehaviorTrace
    @BehaviorTrace("摇一摇")
    private void mShake() {
        // long begin = System.currentTimeMillis();

        Log.d(TAG, "mShake: ");
        // 模拟网络延迟
        SystemClock.sleep(new Random().nextInt(3000));

        // long duration = System.currentTimeMillis() - begin;

        // Log.d(TAG, "摇一摇功能，耗时:" + duration);
    }

    @UserInfoBehaviorTrace
    @BehaviorTrace("视频通话")
    private void mVideo(int i, int j) {
        // long begin = System.currentTimeMillis();

        Log.d(TAG, "mVideo: ");
        // 模拟网络延迟
        SystemClock.sleep(new Random().nextInt(3000));

        // long duration = System.currentTimeMillis() - begin;

        // Log.d(TAG, "视频通话功能，耗时:" + duration);
    }

    @Override
    protected void onDestroy() {
        mBinder.unbind();
        super.onDestroy();
    }
}
