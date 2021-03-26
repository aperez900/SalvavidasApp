package com.desarollo.salvavidasapp.ui.direction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Municipios;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
/*
    Formulario para registrar y actualizar las direcciones
 */
public class FrmAddress extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ListDirecciones d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_address);

        Button btn_reg = (Button)findViewById(R.id.btn_reg);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");

        TextView alias = findViewById(R.id.et_alias);
        Spinner primerTextoDireccion = findViewById(R.id.sp_texto_1_direccion);
        TextView segundoTextoDireccion = findViewById(R.id.et_texto_2_direccion);
        Spinner tercerTextoDireccion = findViewById(R.id.sp_texto_3_direccion);
        TextView cuartoTextoDireccion = findViewById(R.id.et_texto_4_direccion);
        Spinner quintoTextoDireccion = findViewById(R.id.sp_texto_5_direccion);
        TextView sextoTextoDireccion = findViewById(R.id.et_texto_6_direccion);
        Spinner municipio = findViewById(R.id.sp_municipio);
        TextView barrio = findViewById(R.id.et_barrio);
        TextView adicional = findViewById(R.id.et_adicional);

        String[] dir_1 = new String[]{
                "Avenida","Avenida Calle","Avenida Carrera", "Calle","Carrera", "Circular", "Circunvalar","Diagonal","Manzana","Transversal","Via"
        };

        String[] dir_2 = new String[]{
                "","A","AA","B","BB","C","CC","D","DD","E","EE"
        };

        ArrayList<String> listOne = new ArrayList(Arrays.asList(dir_1));
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_modified, listOne);
        primerTextoDireccion.setAdapter(adapter1);

        ArrayList<String> listTwo = new ArrayList(Arrays.asList(dir_2));
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_modified, listTwo);
        tercerTextoDireccion.setAdapter(adapter2);

        Municipios arrayMunicipio = new Municipios();

        //Datos para el spinner de municipio
        String [] listaMunicipios = arrayMunicipio.getMunicipio();
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_modified, listaMunicipios);
        municipio.setAdapter(adapter3);
        quintoTextoDireccion.setAdapter(adapter2);

        //acciones del boton registrar
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCamposVacios(alias,primerTextoDireccion,segundoTextoDireccion,tercerTextoDireccion,cuartoTextoDireccion,quintoTextoDireccion,sextoTextoDireccion,municipio,barrio,adicional)) {
                    registrar(alias,primerTextoDireccion,segundoTextoDireccion,tercerTextoDireccion,cuartoTextoDireccion,quintoTextoDireccion,sextoTextoDireccion,municipio,barrio,adicional);
                }
            }
        });
    }

    /*
     * @autor: Andrés Pérez
     * @since: 10/03/2021
     * @Version: 01
     * Método para registrar o actualizar los datos del perfil en la BD
     * */
    private void registrar(TextView alias, Spinner primerTextoDireccion, TextView segundoTextoDireccion,
                           Spinner tercerTextoDireccion, TextView cuartoTextoDireccion,
                           Spinner quintoTextoDireccion,TextView sextoTextoDireccion,Spinner municipio,TextView barrio, TextView adicional ) {

        d = new ListDirecciones();
        d.nombreDireccion = alias.getText().toString();
        d.direccionUsuario = primerTextoDireccion.getSelectedItem().toString() + " " +
                segundoTextoDireccion.getText().toString() + " " +
                tercerTextoDireccion.getSelectedItem().toString() + " " +
                cuartoTextoDireccion.getText().toString() + " " +
                quintoTextoDireccion.getSelectedItem().toString() + " " +
                sextoTextoDireccion.getText().toString() + " " +
                barrio.getText().toString() + " "+
                adicional.getText().toString();
        d.municipioDireccion = municipio.getSelectedItem().toString();

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

    public boolean validarCamposVacios(TextView alias,Spinner primerTextoDireccion, TextView segundoTextoDireccion,
                                       Spinner tercerTextoDireccion, TextView cuartoTextoDireccion,
                                       Spinner quintoTextoDireccion,TextView sextoTextoDireccion,
                                       Spinner municipio,TextView barrio, TextView adicional){

        d = new ListDirecciones();
        d.nombreDireccion = alias.getText().toString();
        d.direccionUsuario = primerTextoDireccion.getSelectedItem().toString();
        /*
        d.segundoTextoDireccion = segundoTextoDireccion.getText().toString();
        d.tercerTextoDireccion = tercerTextoDireccion.getSelectedItem().toString();
        d.cuartoTextoDireccion = cuartoTextoDireccion.getText().toString();
        d.quintoTextoDireccion = quintoTextoDireccion.getSelectedItem().toString();
        d.sextoTextoDireccion = sextoTextoDireccion.getText().toString();
        d.municipio = municipio.getSelectedItem().toString();
        d.barrio = barrio.getText().toString();
        d.adicional = adicional.getText().toString();

         */

        boolean campoLleno = true;

        if(d.nombreDireccion.isEmpty()){
            Toast.makeText(getApplicationContext(), "Digite un nombre para su dirección", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }
        if(d.direccionUsuario.isEmpty()){
            segundoTextoDireccion.setError("Digite una dirección");
            campoLleno=false;
        }
        /*
        if(d.cuartoTextoDireccion.isEmpty()){
            cuartoTextoDireccion.setError("Digite el campo");
            campoLleno=false;
        }
        if(d.sextoTextoDireccion.isEmpty()){
            cuartoTextoDireccion.setError("Digite el campo");
            campoLleno=false;
        }
        if(d.municipio.isEmpty()){
            Toast.makeText(getApplicationContext(), "Digite el municipio donde se encuentra ubicado", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }

        if(d.barrio.isEmpty()){
            cuartoTextoDireccion.setError("Digite el campo");
            campoLleno=false;
        }
         */


        return campoLleno;
    }
}