package com.zf.iocdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zf.annotaion.BuriedUtils;
import com.zf.annotaion.annotations.BuriedPoint;
import com.zf.annotaion.annotations.LayoutView;
import com.zf.annotaion.annotations.BindView;
import com.zf.annotaion.annotations.OnClick;
import com.zf.iocdemo.base.BaseActivity;
@LayoutView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.textView)
    private TextView textView;

    @BindView(R.id.button)
    private TextView button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"button",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.textView)
    public void aaabbbccc(View view){
        Toast.makeText(this,"我是自定义的点击事件",Toast.LENGTH_LONG).show();
    }

    @Override
    @BuriedPoint()
    public void onClick(View v) {
//        Log.e("==============","onClick");
        Toast.makeText(this,"我是原生的点击事件",Toast.LENGTH_LONG).show();
    }
}
