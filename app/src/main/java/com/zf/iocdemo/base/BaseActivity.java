package com.zf.iocdemo.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zf.annotaion.InjectManager;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectManager.inject(this);
    }
}
