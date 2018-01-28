package com.example.fj.aop;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import com.example.fj.aop.annotation.BehaviorTrace;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    private static final String TAG = "JamFF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_shake, R.id.bt_audio, R.id.bt_video, R.id.bt_jump})
    void onClick(View view) {
        switch (view.getId()) {

            case R.id.bt_shake:
                mShake();
                break;

            case R.id.bt_audio:
                mAudio();
                break;

            case R.id.bt_video:
                mVideo();
                break;

            case R.id.bt_jump:
                Toast.makeText(this, "跳转", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @BehaviorTrace("摇一摇")
    private void mShake() {
        // long begin = System.currentTimeMillis();

        // 模拟网络延迟
        SystemClock.sleep(new Random().nextInt(3000));

        // long duration = System.currentTimeMillis() - begin;

        // Log.d(TAG, "摇一摇功能，耗时:" + duration);
    }

    @BehaviorTrace("语音消息")
    private void mAudio() {
        // long begin = System.currentTimeMillis();

        // 模拟网络延迟
        SystemClock.sleep(new Random().nextInt(3000));

        // long duration = System.currentTimeMillis() - begin;

        // Log.d(TAG, "语音消息功能，耗时:" + duration);
    }

    @BehaviorTrace("视频通话")
    private void mVideo() {
        // long begin = System.currentTimeMillis();

        // 模拟网络延迟
        SystemClock.sleep(new Random().nextInt(3000));

        // long duration = System.currentTimeMillis() - begin;

        // Log.d(TAG, "视频通话功能，耗时:" + duration);
    }
}
