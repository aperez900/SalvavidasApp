package com.desarollo.salvavidasapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.R;
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

        et_emailR = findViewById(R.id.et_emailR);
        et_passwordR = findViewById(R.id.et_passwordR);

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
        if(validarCamposVacios()) {
            createUserWithEmailAndPassword(email, password);
        }
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
                    if (user != null){
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "Email sent.");
                                        Toast.makeText(Registro.this, "email enviado para verificación",
                                                Toast.LENGTH_SHORT).show();
                                        Intent a = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(a);
                                        finish();
                                    }
                                }
                            });
                    }
                    /*
                    Toast.makeText(Registro.this, "Usuario creado",
                            Toast.LENGTH_SHORT).show();


                    //updateUI(user);
                    Intent a = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(a);
                    */

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(Registro.this, "El usuario ya se encuenttra creado",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
                // ...
            }
        });
    }

    public boolean validarCamposVacios(){
        boolean campoLleno = true;

        String correo= et_emailR.getText().toString();
        String clave = et_passwordR.getText().toString();

        if(correo.isEmpty()){
            et_emailR.setError("Debe diligenciar un correo");
            campoLleno=false;
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                et_emailR.setError("Debe diligenciar un correo válido");
                campoLleno=false;
            }
        }
        if(clave.isEmpty()){
            et_passwordR.setError("Debe diligenciar una clave");
            campoLleno=false;
        }else if(clave.length() < 6){
            et_passwordR.setError("La clave debe ser de al menos 6 digitos");
            campoLleno=false;
        }
        return campoLleno;
    }
}