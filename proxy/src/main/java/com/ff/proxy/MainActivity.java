package com.ff.proxy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 跳转界面的代理
    private ActivityManager loginProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_me).setOnClickListener(this);
        findViewById(R.id.bt_pay).setOnClickListener(this);
        findViewById(R.id.bt_message).setOnClickListener(this);

        // 委托类的实例
        ActivityManagerImpl manager = new ActivityManagerImpl();
        // 创建动态代理处理类
        ProxyHandler proxyHandler = new ProxyHandler(this, manager);
        // 代理类的实例
        loginProxy = (ActivityManager) proxyHandler.getProxyInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_me:
                // 调用代理类的方法，相比委托类增加了判断登录的逻辑
                loginProxy.startActivity(this, Constants.ACTIVITY_ME);
                break;
            case R.id.bt_pay:
                loginProxy.startActivity(this, Constants.ACTIVITY_PAY);
                break;
            case R.id.bt_message:
                loginProxy.startActivity(this, Constants.ACTIVITY_MESSAGE);
                break;
        }
    }
}
