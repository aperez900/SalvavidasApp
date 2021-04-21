package com.desarollo.salvavidasapp.ui.sales;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.desarollo.salvavidasapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static com.facebook.FacebookSdk.getApplicationContext;

public class addProduct extends Fragment {

    private int dia, mes, anio, hora, minutos;

    public addProduct() {
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
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        Spinner categoriaProducto = view.findViewById(R.id.sp_categoria_producto);
        Spinner domicilioProducto = view.findViewById(R.id.sp_domicilio);
        TextView btnFechaInicio = view.findViewById(R.id.btn_fecha_inicio);
        TextView tvFechaInicio = view.findViewById(R.id.tv_fecha_inicio);
        TextView btnFechaFin = view.findViewById(R.id.btn_fecha_fin);
        TextView tvFechaFin = view.findViewById(R.id.tv_fecha_fin);
        TextView btnHoraInicio = view.findViewById(R.id.btn_hora_inicio);
        TextView tvHoraInicio = view.findViewById(R.id.tv_hora_inicio);
        TextView btnHoraFin = view.findViewById(R.id.btn_hora_fin);
        TextView tvHoraFin = view.findViewById(R.id.tv_hora_fin);
        Button btnRegistrarProducto = view.findViewById(R.id.btn_registrar_producto);
        EditText nombreProducto = view.findViewById(R.id.et_nombre_producto);
        EditText descripcionProducto = view.findViewById(R.id.et_descripcion_producto);
        EditText precioProducto = view.findViewById(R.id.et_precio);
        EditText descuentoProducto = view.findViewById(R.id.et_descuento);

        String[] ArrayCategorias = new String[]{"Selecciona una categoría", "Comida preparada","Comida cruda"};
        ArrayList<String> listCategoriaProductos = new ArrayList(Arrays.asList(ArrayCategorias));
        ArrayAdapter<String> adapterCategoriaProductos = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_modified, listCategoriaProductos);
        categoriaProducto.setAdapter(adapterCategoriaProductos);

        String[] ArrayDomicilio = new String[]{"¿Deseas ofrecer domicilio?", "Sí","No"};
        ArrayList<String> listDomicilioProductos = new ArrayList(Arrays.asList(ArrayDomicilio));
        ArrayAdapter<String> adapterDomicilioProductos = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_modified, listDomicilioProductos);
        domicilioProducto.setAdapter(adapterDomicilioProductos);

        btnFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario(tvFechaInicio);
            }
        });

        btnFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario(tvFechaFin);
            }
        });

        btnHoraInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarReloj(tvHoraInicio);
            }
        });

        btnHoraFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarReloj(tvHoraFin);
            }
        });

        btnRegistrarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCamposVacios(nombreProducto, descripcionProducto, categoriaProducto, precioProducto, descuentoProducto,
                        domicilioProducto, tvFechaInicio, tvFechaFin)) {
                    //registrar();
                }
            }
        });

        return view;
    } //fin OnCreateView





    public void mostrarCalendario(TextView Fecha){
        Calendar cal = Calendar.getInstance();
        anio = cal.get(Calendar.YEAR);
        mes = cal.get(Calendar.MONTH);
        dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                Fecha.setText(fecha);
            }
        },anio, mes, dia);
        dpd.show();
    }

    public void mostrarReloj(TextView Hora){
        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Hora.setText((hourOfDay)+ ":" + minute);
            }
        }, 0, 0,false);
        tpd.show();
    }

    public boolean validarCamposVacios(EditText et_nombre_producto, EditText et_descripcion_producto, Spinner sp_categoria_producto,
        EditText et_precio_producto, EditText et_descuento_producto, Spinner sp_domicilio_producto, TextView tv_fecha_inicio,
                                       TextView tv_fecha_fin){
        boolean campoLleno = true;

        String nombreProducto = et_nombre_producto.getText().toString();
        String descripcionProducto = et_descripcion_producto.getText().toString();
        String categoriaProducto = sp_categoria_producto.getSelectedItem().toString();
        String precioProducto = et_precio_producto.getText().toString();
        String descuentoProducto = et_descuento_producto.getText().toString();
        String domicilioProducto = sp_domicilio_producto.getSelectedItem().toString();
        String fechaInicio = tv_fecha_inicio.getText().toString();
        String fechaFin = tv_fecha_fin.getText().toString();

        if(nombreProducto.isEmpty()){
            et_nombre_producto.setError("Debe diligenciar un nombre para el producto");
            campoLleno=false;
        }if(descripcionProducto.isEmpty()){
            et_descripcion_producto.setError("Debe diligenciar una descripción para su producto");
            campoLleno=false;
        }if(categoriaProducto.equals("Selecciona una categoría")){
            Toast.makeText(getContext(), "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }if(nombreProducto.isEmpty()){
            et_nombre_producto.setError("Debe diligenciar un nombre para el producto");
            campoLleno=false;
        }if(precioProducto.isEmpty()){
            et_precio_producto.setError("Debe diligenciar un precio para el producto");
            campoLleno=false;
        }if(descuentoProducto.isEmpty()){
            et_descuento_producto.setError("Debe diligenciar un descuento para el producto. Si no tiene digite 0 (cero)");
            campoLleno=false;
        }if(domicilioProducto.equals("¿Deseas ofrecer domicilio?")){
            Toast.makeText(getContext(), "Seleccione si desea ofrecer domicilio", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }if(fechaInicio.equals("dd/mm/aaaa")){
            tv_fecha_inicio.setError("");
            Toast.makeText(getContext(), "Debe seleccionar una fecha de inicio", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }
        if(fechaFin.equals("hh/mm")){
            tv_fecha_fin.setError("");
            Toast.makeText(getContext(), "Debe seleccionar una fecha de fin", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }


        return campoLleno;
    }

}