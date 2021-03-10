package com.desarollo.salvavidasapp.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.R;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;


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

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        myRef.child(currentUser.getUid());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        //Actualiza los datos del perfil logeado en el fragmenProfile
        TextView UserName = view.findViewById(R.id.nombre_perfil);
        UserName.setText(currentUser.getDisplayName());

        TextView UserMail = view.findViewById(R.id.correo_perfil);
        UserMail.setText(currentUser.getEmail());

        ImageView UserPhoto = view.findViewById(R.id.foto_perfil);
        Glide.with(this).load(currentUser.getPhotoUrl()).into(UserPhoto);

        EditText primer_nombre = view.findViewById(R.id.tv_nombre);
        primer_nombre.setText("Hola");

        return view;
    }
}