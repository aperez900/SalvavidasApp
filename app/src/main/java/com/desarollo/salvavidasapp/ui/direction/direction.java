package com.desarollo.salvavidasapp.ui.direction;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Direcciones;
import com.desarollo.salvavidasapp.Models.Municipios;
import com.desarollo.salvavidasapp.Models.Usuarios;
import com.desarollo.salvavidasapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

public class direction extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Direcciones d;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_direction, container, false);

        TextView alias = view.findViewById(R.id.et_alias);
        Spinner primerTextoDireccion = view.findViewById(R.id.sp_texto_1_direccion);
        TextView segundoTextoDireccion = view.findViewById(R.id.et_texto_2_direccion);
        Spinner tercerTextoDireccion = view.findViewById(R.id.sp_texto_3_direccion);
        TextView cuartoTextoDireccion = view.findViewById(R.id.et_texto_4_direccion);
        Spinner quintoTextoDireccion = view.findViewById(R.id.sp_texto_5_direccion);
        TextView sextoTextoDireccion = view.findViewById(R.id.et_texto_6_direccion);
        Spinner municipio = view.findViewById(R.id.sp_municipio);
        TextView barrio = view.findViewById(R.id.et_barrio);
        TextView adicional = view.findViewById(R.id.et_adicional);
        RecyclerView recycle_direction = view.findViewById(R.id.recycle_direction);
        Button btn_reg = view.findViewById(R.id.btn_reg);

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

        recycle_direction.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //recycle_direction.setAdapter();


        //Acciones del botón registrar
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(validarCamposVacios(alias,primerTextoDireccion,segundoTextoDireccion,tercerTextoDireccion,cuartoTextoDireccion,quintoTextoDireccion,sextoTextoDireccion,municipio,barrio,adicional)) {
                    registrar(alias,primerTextoDireccion,segundoTextoDireccion,tercerTextoDireccion,cuartoTextoDireccion,quintoTextoDireccion,sextoTextoDireccion,municipio,barrio,adicional);
               }
            }
        });



        return view;

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

        d = new Direcciones();
        d.alias = alias.getText().toString();
        d.primerTextoDireccion = primerTextoDireccion.getSelectedItem().toString();
        d.segundoTextoDireccion = segundoTextoDireccion.getText().toString();
        d.tercerTextoDireccion = tercerTextoDireccion.getSelectedItem().toString();
        d.cuartoTextoDireccion = cuartoTextoDireccion.getText().toString();
        d.quintoTextoDireccion = quintoTextoDireccion.getSelectedItem().toString();
        d.sextoTextoDireccion = sextoTextoDireccion.getText().toString();
        d.municipio = municipio.getSelectedItem().toString();
        d.barrio = barrio.getText().toString();
        d.adicional = adicional.getText().toString();

        //guarda los datos del usuario
        myRef.child(currentUser.getUid()).child("mis direcciones").child(d.alias).setValue(d)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getApplicationContext(), "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error actualizando el usuario", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public boolean validarCamposVacios(TextView alias,Spinner primerTextoDireccion, TextView segundoTextoDireccion,
                                       Spinner tercerTextoDireccion, TextView cuartoTextoDireccion,
                                       Spinner quintoTextoDireccion,TextView sextoTextoDireccion,
                                       Spinner municipio,TextView barrio, TextView adicional){

        d = new Direcciones();
        d.alias = alias.getText().toString();
        d.primerTextoDireccion = primerTextoDireccion.getSelectedItem().toString();
        d.segundoTextoDireccion = segundoTextoDireccion.getText().toString();
        d.tercerTextoDireccion = tercerTextoDireccion.getSelectedItem().toString();
        d.cuartoTextoDireccion = cuartoTextoDireccion.getText().toString();
        d.quintoTextoDireccion = quintoTextoDireccion.getSelectedItem().toString();
        d.sextoTextoDireccion = sextoTextoDireccion.getText().toString();
        d.municipio = municipio.getSelectedItem().toString();
        d.barrio = barrio.getText().toString();
        d.adicional = adicional.getText().toString();

        boolean campoLleno = true;

        if(d.primerTextoDireccion.isEmpty()){
            Toast.makeText(getApplicationContext(), "Digite el primer cuadro de texto de la direccion", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }
        if(d.segundoTextoDireccion.isEmpty()){
            segundoTextoDireccion.setError("Digite el campo");
            campoLleno=false;
        }
        if(d.cuartoTextoDireccion.isEmpty()){
            cuartoTextoDireccion.setError("Digite el campo");
            campoLleno=false;
        }
        if(d.sextoTextoDireccion.isEmpty()){
            cuartoTextoDireccion.setError("Digite el campo");
            campoLleno=false;
        }
        if(d.municipio.isEmpty()){
            Toast.makeText(getApplicationContext(), "Digite el primer cuadro de texto de la direccion", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }

        if(d.barrio.isEmpty()){
            cuartoTextoDireccion.setError("Digite el campo");
            campoLleno=false;
        }


        return campoLleno;
    }
}