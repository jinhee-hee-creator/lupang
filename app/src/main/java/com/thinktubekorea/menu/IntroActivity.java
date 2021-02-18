package com.thinktubekorea.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
//바꾼부분을 알고싶느냐
public class IntroActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

                super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), IntroActivity2.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    @Override

    protected void onPause(){
        super.onPause();
        finish();
    }
}
