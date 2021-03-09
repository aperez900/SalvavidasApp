package com.desarollo.salvavidasapp.ui.profile;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Profile extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
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

        return view;
    }
}