package com.desarollo.salvavidasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.desarollo.salvavidasapp.Login.Registro;
import com.desarollo.salvavidasapp.ui.home.Home;

public class profile_initial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_initial);


        EditText nombres = findViewById(R.id.tv_nombre);
        EditText apellidos = findViewById(R.id.tv_apellido);
        EditText identificacion = findViewById(R.id.tv_identidad);
        EditText celular = findViewById(R.id.tv_celular);
        Button btnReg = findViewById(R.id.btn_registrar_perfil);

        //Acciones del botón registrar
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposVacios(nombres, apellidos, identificacion, celular)) {

                    Intent i = new Intent(getApplicationContext(), Home.class);
                    startActivity(i);
                    finish();
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
}