package com.example.admin.skip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import me.sugarkahwi.skipcountdownbutton.SkipCountDownButton;

public class MainActivity extends AppCompatActivity implements SkipCountDownButton.OnSkipCountDownListener {
    SkipCountDownButton mDefaultButton;
    SkipCountDownButton mDiyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDefaultButton = (SkipCountDownButton) findViewById(R.id.DEFAULT);
        mDiyButton = (SkipCountDownButton) findViewById(R.id.DIY);
        mDiyButton.setOnCountDownStopListener(this);

        mDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDefaultButton.skip();
            }
        });

        mDiyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDiyButton.skip();
            }
        });
    }


    @Override
    public void onSkip() {
        Toast.makeText(this, "click cancel or auto over", Toast.LENGTH_SHORT).show();
    }

    public void start(View view) {
        switch (view.getId()){
            case R.id.default_button:
                mDefaultButton.start();
                break;
            case R.id.diy_button:
                mDiyButton.start();
                break;
        }
    }


}
