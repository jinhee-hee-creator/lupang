package com.thinktubekorea.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class IntroActivity2 extends Activity {
    @Override

    protected void onCreate(Bundle savedInstanceState){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout2);
    }

    public boolean onTouchEvent(MotionEvent event){

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            // 다음 화면으로 넘어갈 클래스 지정
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);

            startActivity(intent); // 다음 화면으로 이동

        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
