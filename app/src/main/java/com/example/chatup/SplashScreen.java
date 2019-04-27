package com.example.chatup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity{

    private String email;
    static final int SECOND_DELAY = 1;

    private boolean updateUI() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            email = user.getEmail();
            return true;

        }else{
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        Boolean userExists = updateUI();

        if(userExists) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent splash_to_main = new Intent(SplashScreen.this, MainActivity.class);
                    splash_to_main.putExtra("login_data", email);
                    splash_to_main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(splash_to_main);
                    finish();
                }
            }, SECOND_DELAY * 1000);
        }else{
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent splash_to_log = new Intent(SplashScreen.this, LoginActivity.class);
                    splash_to_log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(splash_to_log);
                    finish();
                }
            }, SECOND_DELAY * 1000);
        }
    }
}
