package com.desarollo.salvavidasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Registro extends AppCompatActivity {

    EditText et_emailR, et_passwordR;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        et_emailR = (EditText)findViewById(R.id.et_emailR);
        et_passwordR = (EditText)findViewById(R.id.et_passwordR);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    //Creación de nuevo usuario Firebase
    public void registro (View view){
        String email = et_emailR.getText().toString();
        String password = et_passwordR.getText().toString();

        createUserWithEmailAndPassword(email, password);

    }

    public void createUserWithEmailAndPassword (String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    //envío de emial para verificación
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Registro.this, "email enviado para verificación",
                                                Toast.LENGTH_SHORT).show();
                                        Log.d("TAG", "Email sent.");
                                    }
                                }
                            });
                    Toast.makeText(Registro.this, "Usuario creado",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(user);
                    Intent a = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(a);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(Registro.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
                // ...
            }
        });
    }
}