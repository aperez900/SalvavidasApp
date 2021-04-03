package com.desarollo.salvavidasapp.ui.seller;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Usuarios;
import com.desarollo.salvavidasapp.Models.Vendedores;
import com.desarollo.salvavidasapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.facebook.FacebookSdk.getApplicationContext;


public class seller extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Vendedores v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("vendedores");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_seller,container,false);
        EditText nombres = view.findViewById(R.id.tv_nombre);
        EditText apellidos = view.findViewById(R.id.tv_apellido);
        EditText identificacion = view.findViewById(R.id.tv_identidad);
        EditText celular = view.findViewById(R.id.tv_celular);
        Button btn_reg = view.findViewById(R.id.btn_registrar_vendedor);
        TextView estado = view.findViewById(R.id.estado);

        consultarDatosVendedor(nombres, apellidos, identificacion, celular,btn_reg,estado);

        //Acciones del botón registrar
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposVacios( nombres, apellidos, identificacion, celular)) {
                    registrar(nombres, apellidos, identificacion, celular);
                }
            }
        });

        return view;
    }



    public void consultarDatosVendedor(EditText et_nombres, EditText et_apellidos, EditText et_identificacion,
                                     EditText et_celular, Button btn_reg, TextView tv_estado){
        //consultando datos del vendedor
        myRef.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String nombre = snapshot.child("nombre").getValue().toString();
                            et_nombres.setText(nombre);
                            String apellido = snapshot.child("apellido").getValue().toString();
                            et_apellidos.setText(apellido);
                            String identificacion = snapshot.child("identificacion").getValue().toString();
                            et_identificacion.setText(identificacion);
                            String celular = snapshot.child("celular").getValue().toString();
                            et_celular.setText(celular);
                            String estado = snapshot.child("estado").getValue().toString();
                            tv_estado.setText(estado);
                            btn_reg.setText("Actualizar");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando los datos del vendedor. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void registrar(EditText nombres, EditText apellidos, EditText identificacion, EditText celular) {
        v = new Vendedores();
        v.nombre=nombres.getText().toString();
        v.apellido = apellidos.getText().toString();
        v.identificacion = identificacion.getText().toString();
        v.celular = celular.getText().toString();
        v.estado = "Pendiente";

        //guarda los datos del vendedor
        myRef.child(currentUser.getUid()).setValue(v)
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


    public boolean validarCamposVacios(EditText nombres, EditText apellidos, EditText identificacion, EditText celular){
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

        return campoLleno;
    }

}