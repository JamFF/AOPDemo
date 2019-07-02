package com.jamff.thread;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jamff.thread.annotation.Async;
import com.jamff.thread.annotation.Main;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "JamFF";
    public static CompositeDisposable sDisposable = new CompositeDisposable();

    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_readFile).setOnClickListener(this);
        findViewById(R.id.bt_writeFile).setOnClickListener(this);
        findViewById(R.id.bt_doSomething).setOnClickListener(this);
        findViewById(R.id.bt_stopThread).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_readFile:
                readFile();
                break;
            case R.id.bt_writeFile:
                writeFile();
                break;
            case R.id.bt_doSomething:
                doSomething();
                break;
            case R.id.bt_stopThread:
                sDisposable.clear();
                Log.d(TAG, "CompositeDisposable clear");
                break;
        }
    }

    @Async
    private void writeFile() {
        Log.d(TAG, "Write File: " + Thread.currentThread());
        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 如果线程没有处理完，调用sDisposable.clear()，showResult会立即执行
        showResult();
    }

    @Async
    private void readFile() {
        Log.d(TAG, "Read File: " + Thread.currentThread());
        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 如果线程没有处理完，调用sDisposable.clear()，showResult会立即执行
        showResult();
    }

    /**
     * 普通RX方式
     */
    private void doSomething() {
        Completable.create(emitter -> {
            Log.d(TAG, "Do Something subscribe: " + Thread.currentThread());
            try {
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "Do Something onSubscribe: " + Thread.currentThread());
                        sDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        // 取消订阅后，onComplete不会执行
                        Log.d(TAG, "Do Something onComplete : " + Thread.currentThread());
                        showResult();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Do Something onError: " + Thread.currentThread());
                    }
                });
    }

    @Main
    private void showResult() {
        Log.d(TAG, "更新UI: " + Thread.currentThread());
        Toast.makeText(this, "更新UI：" + Thread.currentThread(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        sDisposable.clear();
        super.onDestroy();
    }
}
