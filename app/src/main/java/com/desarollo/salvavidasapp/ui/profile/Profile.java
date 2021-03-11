package com.desarollo.salvavidasapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Home;
import com.desarollo.salvavidasapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Profile extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile,container,false);


        TextView UserName = view.findViewById(R.id.nombre_perfil);
        TextView UserMail = view.findViewById(R.id.correo_perfil);
        ImageView UserPhoto = view.findViewById(R.id.foto_perfil);
        EditText nombres = view.findViewById(R.id.tv_nombre);
        EditText apellidos = view.findViewById(R.id.tv_apellido);
        EditText direccion = view.findViewById(R.id.tv_direccion);
        EditText municipio = view.findViewById(R.id.tv_municipio);
        EditText identificacion = view.findViewById(R.id.tv_identidad);
        EditText celular = view.findViewById(R.id.tv_celular);
        Button btn_reg = view.findViewById(R.id.btn_registrar_perfil);

        //Actualiza los datos del perfil logeado en el fragmenProfile
        UserName.setText(currentUser.getDisplayName());
        UserMail.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(UserPhoto);


        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               registrar(UserMail,nombres,apellidos,direccion,municipio,identificacion,celular);

            }
        });
        return view;
    }


    private void registrar(TextView UserMail,EditText nombres, EditText apellidos, EditText direccion, EditText municipio, EditText identificacion, EditText celular) {

        Map<String,Object> map= new HashMap<>();
        map.put("nombre",nombres.getText().toString());
        map.put("apellido",apellidos.getText().toString());
        map.put("municipio",municipio.getText().toString());
        map.put("direccion",direccion.getText().toString());
        map.put("identificacion",identificacion.getText().toString());
        map.put("celular",celular.getText().toString());

        myRef.child(currentUser.getUid()).setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nombres.setText("");
                        apellidos.setText("");
                        municipio.setText("");
                        direccion.setText("");
                        identificacion.setText("");
                        celular.setText("");

                        Toast.makeText(getApplicationContext(), "Se actualizo de forma exitosa", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "No se actualizaron los datos, debido a un error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Consulta los datos del perfil del usuario logeado
    public void consultarDatosPerfil(){


    }



}