package com.desarollo.salvavidasapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.desarollo.salvavidasapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;

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
                                        FancyAlertDialog.Builder
                                                .with(Registro.this)
                                                .setTitle("Felicitaciones !")
                                                .setBackgroundColor(Color.parseColor("#EC7063"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                                                .setMessage("Ahora revisa tu correo electronico para activa la cuenta!")
                                                .setPositiveBtnBackground(Color.parseColor("#EC7063"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                                                .setPositiveBtnText("Ok")
                                                .setNegativeBtnBackground(Color.parseColor("#EC7063"))  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                                                .setNegativeBtnText("Volver")
                                                .setAnimation(Animation.POP)
                                                .isCancellable(true)
                                                .setIcon(R.drawable.icono_ok, View.VISIBLE)
                                                .onPositiveClicked(new FancyAlertDialogListener() {
                                                    @Override
                                                    public void onClick(Dialog dialog) {
                                                        Intent a = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(a);
                                                        finish();
                                                    }
                                                 })
                                                .onNegativeClicked(new FancyAlertDialogListener() {
                                                    @Override
                                                    public void onClick(Dialog dialog) {
                                                        Intent a = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(a);
                                                        finish();
                                                    }
                                                })
                                                .build()
                                                .show();



                                    }
                                }
                            });
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(Registro.this, "El usuario ya se encuentra creado",
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
            et_passwordR.setError("La clave debe ser de al menos 7 digitos");
            campoLleno=false;
        }
        return campoLleno;
    }
}