package com.desarollo.salvavidasapp.ui.direction;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.R;

import java.util.ArrayList;

public class ListAddress extends Fragment {

    ListAddressAdapter listAddressAdapter;
    RecyclerView recyclerViewDirecciones;
    ArrayList<ListDirecciones> listaDirecciones;
    TextView btnAgregarDirecciones;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_address, container, false);
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        recyclerViewDirecciones = view.findViewById(R.id.recycle_address);
        btnAgregarDirecciones = view.findViewById(R.id.btnAgregarDirecciones);
        Button btnAgregarDir = view.findViewById(R.id.btnAgregarDirecciones);
        listaDirecciones = new ArrayList<>();
        //cargar la lista
        cargarLista();
        //mostrar los datos
        mostrarData();

        btnAgregarDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent h = new Intent(getContext(), FrmAddress.class);
                startActivity(h);
            }
        });
        return view;
    }

    public void cargarLista(){
        listaDirecciones.add(new ListDirecciones("Mi Casa", "Avenida Siempre viva","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Trabajo", "Calle 55 55 55","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Casa", "Avenida Siempre viva","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Trabajo", "Calle 55 55 55","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Casa", "Avenida Siempre viva","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Trabajo", "Calle 55 55 55","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Casa", "Avenida Siempre viva","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Trabajo", "Calle 55 55 55","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Casa", "Avenida Siempre viva","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Trabajo", "Calle 55 55 55","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Casa", "Avenida Siempre viva","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Trabajo", "Calle 55 55 55","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Casa", "Avenida Siempre viva","Bello Antioq",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Mi Trabajo", "Calle 55 55 55","Bello Antioq",R.drawable.ic_icono_address));

    }
    public void mostrarData(){
        recyclerViewDirecciones.setLayoutManager(new LinearLayoutManager(getContext()));
        listAddressAdapter = new ListAddressAdapter(getContext(),listaDirecciones);
        recyclerViewDirecciones.setAdapter(listAddressAdapter);

        //Acciones al dar clic en un item de la lista
        listAddressAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreDireccion = listaDirecciones.get(recyclerViewDirecciones.getChildAdapterPosition(view)).getNombreDireccion();
                Toast.makeText(getContext(),"Seleccionó: " + nombreDireccion,Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Acceso por firebase con email and clave
    public void agregarDirecciones(View v) {
        Intent h = new Intent(getContext(), FrmAddress.class);
        startActivity(h);
    }
}