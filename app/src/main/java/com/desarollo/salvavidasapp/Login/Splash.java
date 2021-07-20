package com.desarollo.salvavidasapp.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.sales.lookAtProduct;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        TimerTask inicio = new TimerTask() {
            @Override
            public void run() {

                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        };

        TimerTask finish = new TimerTask() {
            @Override
            public void run() {

                Intent intent = new Intent(Splash.this, error.class);
                startActivity(intent);
                finish();
            }

        };





        Timer tiempo = new Timer();


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            tiempo.schedule(inicio,4000);
        } else {

            Toast.makeText(Splash.this, "Revisar tu conexion a internet", Toast.LENGTH_SHORT).show();
            tiempo.schedule(finish,4000);

        }
    }
}