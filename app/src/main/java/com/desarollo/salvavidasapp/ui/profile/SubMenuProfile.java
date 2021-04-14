package com.desarollo.salvavidasapp.ui.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.desarollo.salvavidasapp.R;

public class SubMenuProfile extends Fragment {

    public SubMenuProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_menu_profile, container, false);

        TextView tvPerfilComprador = view.findViewById(R.id.tv_comprador);
        TextView tvPerfilVendedor = view.findViewById(R.id.tv_vendedor);

        tvPerfilComprador.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_subMenuProfile_to_nav_profile));
        tvPerfilVendedor.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_subMenuProfile_to_nav_seller));

        return view;
    }
}