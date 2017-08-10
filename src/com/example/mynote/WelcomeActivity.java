package com.example.mynote;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.activity_welcome);
        
        final Intent it = new Intent(new Intent(WelcomeActivity.this, MainActivity.class));
        Timer timer = new Timer();  
        TimerTask task = new TimerTask() {  
          @Override  
          public void run() {   
          startActivity(it);
          WelcomeActivity.this.finish();//执行  
           }  
         }; 
        timer.schedule(task, 1000 * 1+100*3); //10秒后 
        
    }
}

