package com.desarollo.salvavidasapp.ui.direction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class lookAtAddress extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ListDirecciones d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_at_address);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");

        EditText nombre = findViewById(R.id.et_alias);
        EditText direccion = findViewById(R.id.direccion);
        EditText municipio = findViewById(R.id.sp_municipio);
        Button btnModificar = findViewById(R.id.btn_modificar_direccion);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            nombre.setText(extras.getString("nombre"));
            direccion.setText(extras.getString("direccion"));
            municipio.setText(extras.getString("municipio"));

        }

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCamposVacios(nombre, direccion, municipio)) {
                    modificar(nombre, direccion, municipio);
                }
            }
        });

    }

    private void modificar(EditText nombre, EditText direccion, EditText municipio) {

        d = new ListDirecciones();
        d.nombreDireccion = nombre.getText().toString();
        d.direccionUsuario = direccion.getText().toString();
        d.municipioDireccion = municipio.getText().toString();
        d.seleccion = "false";

        //guarda los datos del usuario
        myRef.child(currentUser.getUid()).child("mis direcciones").child(d.nombreDireccion).setValue(d)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Dirección registrada correctamente", Toast.LENGTH_SHORT).show();
                        Intent h = new Intent(getApplicationContext(), Home.class);
                        startActivity(h);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error registrando la dirección", Toast.LENGTH_SHORT).show();
                    }
                });
        }


    public boolean validarCamposVacios(EditText nombre, EditText direccion, EditText municipio){

        d = new ListDirecciones();
        d.nombreDireccion = nombre.getText().toString();
        d.direccionUsuario = direccion.getText().toString();
        d.municipioDireccion = municipio.getText().toString();

        boolean campoLleno = true;

        if(d.nombreDireccion.isEmpty()){
            nombre.setError("Digite una dirección");
            campoLleno=false;
        }
        if(d.direccionUsuario.isEmpty()){
            direccion.setError("Digite una dirección");
            campoLleno=false;
        }

        if(d.municipioDireccion.isEmpty()){
            municipio.setError("Digite una dirección");
            campoLleno=false;
        }

        return campoLleno;
    }




}