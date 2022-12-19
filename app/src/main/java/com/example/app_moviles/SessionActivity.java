package com.example.app_moviles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sesion = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                Boolean estado = sesion.getBoolean("estado", false);

                if(estado){
                    Intent menu = new Intent(SessionActivity.this, MainActivity.class);
                    startActivity(menu);
                    finish();
                }else{
                    Intent login = new Intent(SessionActivity.this, LoginActivity.class);
                    startActivity(login);
                    finish();
                }
            }
        }, 1000);
    }
}