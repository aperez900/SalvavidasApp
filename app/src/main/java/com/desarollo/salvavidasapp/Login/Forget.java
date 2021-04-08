package com.desarollo.salvavidasapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget extends AppCompatActivity {

    private TextView et_recuperarEmail;
    private Button btn_recuperar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        et_recuperarEmail = findViewById(R.id.et_recuperarEmail);
        btn_recuperar = findViewById(R.id.btn_recuperar);

        btn_recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

    }

    private void validate() {
        String email = et_recuperarEmail.getText().toString().trim();
        if (email.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_recuperarEmail.setError("Correo no válido.");
            return;
        }
        sendEmail(email);
    }

    private void sendEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAdress = email;

        auth.sendPasswordResetEmail(emailAdress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Forget.this,"Correo enviado",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Forget.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else{
                            Toast.makeText(Forget.this,"Error procesando la solicitud.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent  intent = new Intent(Forget.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}