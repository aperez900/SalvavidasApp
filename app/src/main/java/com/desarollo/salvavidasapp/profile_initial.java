package com.desarollo.salvavidasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Login.Registro;
import com.desarollo.salvavidasapp.Models.Usuarios;
import com.desarollo.salvavidasapp.ui.direction.Maps;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;

import static com.facebook.FacebookSdk.getApplicationContext;

public class profile_initial extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Usuarios u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_initial);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");


        EditText nombres = findViewById(R.id.tv_nombre);
        EditText apellidos = findViewById(R.id.tv_apellido);
        EditText identificacion = findViewById(R.id.tv_identidad);
        EditText celular = findViewById(R.id.tv_celular);
        Button btnReg = findViewById(R.id.btn_registrar_perfil);
        String email;
        email = "";

        Intent intent = getIntent();
        if (intent.getExtras()  != null){
            Bundle extras = getIntent().getExtras();
            email = extras.getString("email");
        }


        //Acciones del botón registrar
        String finalEmail = email;
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposVacios(nombres, apellidos, identificacion, celular)) {
                    registrar(finalEmail, nombres, apellidos, identificacion, celular);
                }
            }
        });



    }


    public boolean validarCamposVacios( EditText nombres, EditText apellidos, EditText identificacion, EditText celular){
        boolean campoLleno = true;

        String nombreV = nombres.getText().toString();
        String apellidoV = apellidos.getText().toString();
        String identificacionV = identificacion.getText().toString();
        String celularV = celular.getText().toString();

        if(nombreV.isEmpty()){
            nombres.setError("Debe diligenciar un nombre");
            campoLleno=false;
        }
        if(apellidoV.isEmpty()){
            apellidos.setError("Debe diligenciar un apellido");
            campoLleno=false;
        }
        if(identificacionV.isEmpty()){
            identificacion.setError("Debe diligenciar un nro. de identificación");
            campoLleno=false;
        }else if (identificacionV.length() < 5 || identificacionV.length() > 15){
            identificacion.setError("Digite un número de cédula válido");
            campoLleno=false;
        }
        if(celularV.isEmpty()){
            celular.setError("Debe diligenciar un celular");
            campoLleno=false;
        }else if (celularV.length() != 10){
            celular.setError("El celular debe tener 10 digitos");
            campoLleno=false;
        }
        /*if(selectedItems.isEmpty()){
            crearModalComidasPreferidas();
            campoLleno=false;
            Toast.makeText(getContext(), "Seleccione sus comidas preferidas primero", Toast.LENGTH_LONG).show();
        }
         */
        return campoLleno;
    }


    /*
     * @autor: Andrés Pérez
     * @since: 10/03/2021
     * @Version: 01
     * Método para registrar o actualizar los datos del perfil en la BD
     * */
    private void registrar(String email ,EditText nombres, EditText apellidos, EditText identificacion, EditText celular) {
        u = new Usuarios();
        u.nombre=nombres.getText().toString();
        u.apellido = apellidos.getText().toString();
        u.identificacion = identificacion.getText().toString();
        u.celular = celular.getText().toString();
        u.correo = email;
        u.habilitado = true;

        //guarda los datos del usuario
        myRef.child(currentUser.getUid()).setValue(u)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getApplicationContext(), "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                        Intent h = new Intent(profile_initial.this, Maps.class);
                        startActivity(h);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getApplicationContext(), "Error actualizando el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
         registrar_token();
    }

    private void registrar_token() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    String token ="";
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Fetching FCM registration token failed" +task.getException(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        myRef.child(currentUser.getUid()).child("tokenId").setValue(token)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                });
    }
}