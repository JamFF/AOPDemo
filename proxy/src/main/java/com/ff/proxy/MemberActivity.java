package com.ff.proxy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MemberActivity extends AppCompatActivity {

    public static final String KEY = "MemberActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menber);
        TextView tv = findViewById(R.id.tv);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            switch (bundle.getInt(KEY, 0)) {
                case Constants.ACTIVITY_ME:
                    tv.setText(R.string.tv_me);
                    break;
                case Constants.ACTIVITY_PAY:
                    tv.setText(R.string.tv_pay);
                    break;
                case Constants.ACTIVITY_MESSAGE:
                    tv.setText(R.string.tv_message);
                    break;
            }
        }
    }
}
