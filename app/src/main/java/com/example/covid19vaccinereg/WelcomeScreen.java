package com.example.covid19vaccinereg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeScreen extends AppCompatActivity {

    SessionManager sessionManager;
    DatabaseManager db;
    Animation myAnim;
    TextView tvSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        db = new DatabaseManager(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        //Initialize the animation to the animation layout in anim folder
        myAnim = AnimationUtils.loadAnimation(this,R.anim.splash_animation);
        tvSplash=findViewById(R.id.tvWelcome);
        tvSplash.setAnimation(myAnim);
        //create a timer task
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                finish();
                String user = sessionManager.getUsername();

                if(db.checkisAdmin(user)) {
                    //start the main activity after the timer ends.
                    startActivity(new Intent(WelcomeScreen.this,AdminHome.class));
                }else{
                        startActivity(new Intent(WelcomeScreen.this,MainActivity.class));
                    }
                }
        };
        //set timer to display this activity for 5 seconds.
        Timer opening = new Timer();
        opening.schedule(task,5000);



    }
}