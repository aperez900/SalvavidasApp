package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.Models.SubTipoProductos;
import com.desarollo.salvavidasapp.Models.TipoProductos;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class addProduct extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefProductos, myRefVendedores, myRefUsuarios,myRefTipoProducto;
    Productos p;
    String idProductoEdit ="";
    String nombreEstablecimiento;
    Spinner categoriaProducto;
    Spinner subCategoriaProducto;

    Map<String,Object> selectedItems = new HashMap<>();

    private int dia, mes, anio, hora, minutos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefProductos = database.getReference("productos");
        myRefVendedores = database.getReference("vendedores");
        myRefUsuarios = database.getReference("usuarios");
        myRefTipoProducto = database.getReference("tipo_comidas");

        categoriaProducto = findViewById(R.id.sp_categoria_producto);
        subCategoriaProducto = findViewById(R.id.sp_sub_categoria_producto);
        Spinner domicilioProducto = findViewById(R.id.sp_domicilio);
        TextView btnFechaInicio = findViewById(R.id.btn_fecha_inicio);
        TextView tvFechaInicio = findViewById(R.id.tv_fecha_inicio);
        TextView btnFechaFin = findViewById(R.id.btn_fecha_fin);
        TextView tvFechaFin = findViewById(R.id.tv_fecha_fin);
        TextView btnHoraInicio = findViewById(R.id.btn_hora_inicio);
        TextView tvHoraInicio = findViewById(R.id.tv_hora_inicio);
        TextView btnHoraFin = findViewById(R.id.btn_hora_fin);
        TextView tvHoraFin = findViewById(R.id.tv_hora_fin);
        Button btnRegistrarProducto = findViewById(R.id.btn_registrar_producto);
        EditText nombreProducto = findViewById(R.id.et_nombre_producto);
        EditText descripcionProducto = findViewById(R.id.et_descripcion_producto);
        EditText precioProducto = findViewById(R.id.et_precio);
        EditText cantidadProducto = findViewById(R.id.et_cantidad);
        EditText descuentoProducto = findViewById(R.id.et_descuento);
        Button direccionVenta = findViewById(R.id.bt_direccion);
        EditText direccionProducto = findViewById(R.id.et_direccion);
        EditText precioDomicilio = findViewById(R.id.et_precio_domicilio);

        String fotoConsulta = "";
        String ScategoriaProductos="";
        String SsubCategoriaProductos="";
        String SdomicilioProducto="";
        String type="";

        Intent intent = getIntent();

        if (intent.getExtras() != null){
            Bundle extras = getIntent().getExtras();

            idProductoEdit = extras.getString("idProducto");
            nombreProducto.setText(extras.getString("nombreProducto"));
            descripcionProducto.setText(extras.getString("descripcionProducto"));
            precioProducto.setText(extras.getString("precio"));
            cantidadProducto.setText(extras.getString("cantidadProducto"));
            descuentoProducto.setText(extras.getString("descuento"));
            ScategoriaProductos = extras.getString("tipoProducto");
            SsubCategoriaProductos = extras.getString("subTipoProducto");
            SdomicilioProducto = extras.getString("domicilioProducto");
            tvFechaInicio.setText(extras.getString("fechaInicio"));
            tvFechaFin.setText(extras.getString("fechaFin"));
            tvHoraInicio.setText(extras.getString("horaInicio"));
            tvHoraFin.setText(extras.getString("horaFin"));
            type = extras.getString("tipyEntry");
            fotoConsulta = extras.getString("urlFoto");
            direccionProducto.setText(extras.getString("direccionProducto"));
            precioDomicilio.setText(extras.getString("precioDomicilio"));

            //direccionVenta.setText(direccionUsuario);

            if(type.equals("Editar")){
                btnRegistrarProducto.setText("Editar Producto");
                btnRegistrarProducto.setVisibility(View.VISIBLE);
                // Toast.makeText(getApplicationContext(),"1",Toast.LENGTH_SHORT).show();
            }
            else if(type.equals("Consultar")){
                btnRegistrarProducto.setVisibility(View.INVISIBLE);
                //Toast.makeText(getApplicationContext(),"2",Toast.LENGTH_SHORT).show();
            }
        }

        consultarNombreEstablecimiento();

        if(type.equals("Editar") || type.equals("Consultar")){
            String[] ArrayCategorias = new String[]{ScategoriaProductos,"✚ Seleccione una categoría", "comida preparada","comida cruda"};
            ArrayList<String> listCategoriaProductos = new ArrayList(Arrays.asList(ArrayCategorias));
            ArrayAdapter<String> adapterCategoriaProductos = new ArrayAdapter<String>(this, R.layout.spinner_item_modified, listCategoriaProductos);
            categoriaProducto.setAdapter(adapterCategoriaProductos);

            String[] ArraySubCategorias = new String[]{SsubCategoriaProductos, "✚ Seleccione una sub categoría", "aceites", "aderezos", "carnes rojas", "condimentos", "flores", "frutas",
                    "frutos secos", "granos", "hortalizas", "legumbres", "pescado", "pollo","verduras"};
            ArrayList<String> listSubCategoriaProductos = new ArrayList(Arrays.asList(ArraySubCategorias));
            ArrayAdapter<String> adapterSubCategoriaProductos = new ArrayAdapter<String>(this, R.layout.spinner_item_modified, listSubCategoriaProductos);
            subCategoriaProducto.setAdapter(adapterSubCategoriaProductos);

            String[] ArrayDomicilio = new String[]{SdomicilioProducto, "✚ ¿Tienes domicilio?", "Sí","No"};
            ArrayList<String> listDomicilioProductos = new ArrayList(Arrays.asList(ArrayDomicilio));
            ArrayAdapter<String> adapterDomicilioProductos = new ArrayAdapter<String>(this, R.layout.spinner_item_modified, listDomicilioProductos);
            domicilioProducto.setAdapter(adapterDomicilioProductos);
        }else {

            consultarCategoriaProducto();
            consultarSubCategoriaProducto();

            String[] ArrayDomicilio = new String[]{"✚ ¿Tienes domicilio?", "Sí", "No"};
            ArrayList<String> listDomicilioProductos = new ArrayList(Arrays.asList(ArrayDomicilio));
            ArrayAdapter<String> adapterDomicilioProductos = new ArrayAdapter<String>(this, R.layout.spinner_item_modified, listDomicilioProductos);
            domicilioProducto.setAdapter(adapterDomicilioProductos);
        }

        domicilioProducto.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn,
                                               android.view.View v,
                                               int posicion,
                                               long id) {

                        if(spn.getItemAtPosition(posicion).toString().equals("Sí")){
                            precioDomicilio.setVisibility(View.VISIBLE);
                        }else{
                            precioDomicilio.setVisibility(View.INVISIBLE);
                        }

                    }
                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

        direccionVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearModalDirecciones(direccionProducto);
            }
        });

        btnFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario(tvFechaInicio);
            }
        });

        tvFechaInicio.setOnClickListener(new View.OnClickListener() {
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

        tvFechaFin.setOnClickListener(new View.OnClickListener() {
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

        String finalFotoConsulta = fotoConsulta;
        btnRegistrarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCamposVacios(nombreProducto, descripcionProducto, categoriaProducto, subCategoriaProducto, cantidadProducto,precioProducto, descuentoProducto,
                        domicilioProducto, tvFechaInicio, tvFechaFin, tvHoraInicio, tvHoraFin,direccionProducto,precioDomicilio)) {
                    registrar(nombreProducto, descripcionProducto, categoriaProducto, subCategoriaProducto, cantidadProducto, precioProducto, descuentoProducto,
                            domicilioProducto, tvFechaInicio, tvFechaFin, tvHoraInicio, tvHoraFin, finalFotoConsulta,idProductoEdit,direccionProducto,precioDomicilio);
                }
            }
        });
    }

    public void mostrarCalendario(TextView Fecha){
        Calendar cal = Calendar.getInstance();
        anio = cal.get(Calendar.YEAR);
        mes = cal.get(Calendar.MONTH);
        dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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

        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Hora.setText((hourOfDay)+ ":" + minute);
            }
        }, hora, min,false);
        tpd.show();
    }

    public void registrar(EditText et_nombre_producto, EditText et_descripcion_producto, Spinner sp_categoria_producto, Spinner sp_sub_categoria_producto,
                          EditText et_cantidad_producto ,EditText et_precio_producto, EditText et_descuento_producto, Spinner sp_domicilio_producto, TextView tv_fecha_inicio,
                          TextView tv_fecha_fin, TextView tv_hora_inicio, TextView tv_hora_fin,String fotoConsulta, String idProductoEdit,EditText direccionProducto, EditText et_precio_Domicilio){
        try {
            String idProducto,descripcionProducto,nombreProducto,categoriaProducto, subCategoriaProducto, estadoProducto, domicilio, fechaInicio, direccion,
                    horaInicio,  fechaFin,  horaFin;
            int cantidad;
            double precio,descuento,precioDomicilio_;
            String foto;

            if (idProductoEdit==""){
                idProducto = UUID.randomUUID().toString();
                foto = "https://firebasestorage.googleapis.com/v0/b/salvavidasapp-450aa.appspot.com/o/salvavidas%2FLogoSalvavidasSinFondo.png?alt=media&token=2b68b6bd-208e-48dd-88f2-059d68c7621e";
            }
            else{
                idProducto = idProductoEdit;
                foto = fotoConsulta;
            }

            if(et_precio_Domicilio.getText().toString().equals("")){
                precioDomicilio_ = 0.0 ;
            }else{
                precioDomicilio_ = Double.parseDouble(et_precio_Domicilio.getText().toString());
            }

           // Toast.makeText(getApplicationContext(),idProducto,Toast.LENGTH_SHORT).show();
            descripcionProducto = et_descripcion_producto.getText().toString();
            nombreProducto= et_nombre_producto.getText().toString();
            estadoProducto = "Programado";
            categoriaProducto = sp_categoria_producto.getSelectedItem().toString();
            subCategoriaProducto = sp_sub_categoria_producto.getSelectedItem().toString();
            cantidad = Integer.parseInt(et_cantidad_producto.getText().toString());
            precio = Double.parseDouble(et_precio_producto.getText().toString());
            descuento = Double.parseDouble(et_descuento_producto.getText().toString());
            domicilio = sp_domicilio_producto.getSelectedItem().toString();
            fechaInicio = tv_fecha_inicio.getText().toString();
            horaInicio = tv_hora_inicio.getText().toString();
            fechaFin =  tv_fecha_fin.getText().toString();
            horaFin = tv_hora_fin.getText().toString();
            direccion = direccionProducto.getText().toString();

            p = new Productos(idProducto  ,  nombreProducto,  descripcionProducto,  categoriaProducto,  subCategoriaProducto,
                    precio,  descuento,  domicilio,  estadoProducto,  foto,  fechaInicio,  horaInicio,  fechaFin,  horaFin,
                    nombreEstablecimiento,direccion, cantidad, cantidad, precioDomicilio_, currentUser.getUid()){};

            //guarda los datos del producto
            myRefProductos.child(currentUser.getUid()).child(p.getIdProducto()).setValue(p)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            /*limpiarCamposProducto(et_nombre_producto, et_descripcion_producto, sp_categoria_producto, et_precio_producto, et_descuento_producto,
                                    sp_domicilio_producto, tv_fecha_inicio, tv_fecha_fin, tv_hora_inicio, tv_hora_fin);
                             */
                            Intent intent = new Intent(addProduct.this, addPhoto.class);
                            intent.putExtra("idProducto", p.getIdProducto());
                            intent.putExtra("nombreProducto", p.getNombreProducto());
                            //intent.putExtra("getUrlFoto",fotoConsulta);
                            startActivity(intent);
                            finish();
                            Toast.makeText(addProduct.this, "Producto registrado correctamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(addProduct.this, "Error registrando el producto. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception  e) {
            Toast.makeText(getApplicationContext(),"Error :" + e,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /*
    public void limpiarCamposProducto(EditText et_nombre_producto, EditText et_descripcion_producto, Spinner sp_categoria_producto,
                                      EditText et_precio_producto, EditText et_descuento_producto, Spinner sp_domicilio_producto, TextView tv_fecha_inicio,
                                      TextView tv_fecha_fin, TextView tv_hora_inicio, TextView tv_hora_fin){
        et_nombre_producto.setText("");
        et_descripcion_producto.setText("");
        //sp_categoria_producto.setAdapter(adapterCategoriaProductos);
        et_precio_producto.setText("");
        et_descuento_producto.setText("");
        //sp_domicilio_producto.setAdapter(adapterDomicilioProductos);
        tv_fecha_inicio.setText("dd/mm/aaa");
        tv_fecha_fin.setText("dd/mm/aaa");
        tv_hora_inicio.setText("hh/mm");
        tv_hora_fin.setText("hh/mm");
    }
     */

    public boolean validarCamposVacios(EditText et_nombre_producto, EditText et_descripcion_producto, Spinner sp_categoria_producto,
                                       Spinner sp_sub_categoria_producto, EditText et_cantidad_producto, EditText et_precio_producto, EditText et_descuento_producto,
                                       Spinner sp_domicilio_producto, TextView tv_fecha_inicio, TextView tv_fecha_fin, TextView tv_hora_inicio,
                                       TextView tv_hora_fin , EditText direccionProducto,EditText et_precio_domicilio){
        boolean campoLleno = true;

        String nombreProducto = et_nombre_producto.getText().toString();
        String descripcionProducto = et_descripcion_producto.getText().toString();
        String categoriaProducto = sp_categoria_producto.getSelectedItem().toString();
        String subCategoriaProducto = sp_sub_categoria_producto.getSelectedItem().toString();
        String cantidadProducto = et_cantidad_producto.getText().toString();
        String precioProducto = et_precio_producto.getText().toString();
        String descuentoProducto = et_descuento_producto.getText().toString();
        String domicilioProducto = sp_domicilio_producto.getSelectedItem().toString();
        String fechaInicio = tv_fecha_inicio.getText().toString();
        String fechaFin = tv_fecha_fin.getText().toString();
        String horaInicio = tv_hora_inicio.getText().toString();
        String horaFin = tv_hora_fin.getText().toString();
        String direccion = direccionProducto.getText().toString();
        String precioDomicilio = et_precio_domicilio.getText().toString();

        if(nombreProducto.isEmpty()){
            et_nombre_producto.setError("Debe diligenciar un nombre para el producto");
            campoLleno=false;
        }if(descripcionProducto.isEmpty()){
            et_descripcion_producto.setError("Debe diligenciar una descripción para su producto");
            campoLleno=false;
        }if(categoriaProducto.equals("✚ Seleccione una categoría")){
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }if(subCategoriaProducto.equals("✚ Seleccione una sub categoría")){
            Toast.makeText(this, "Seleccione una sub categoría", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }if(nombreProducto.isEmpty()){
            et_nombre_producto.setError("Debe diligenciar un nombre para el producto");
            campoLleno=false;
        }if(cantidadProducto.isEmpty()) {
            et_precio_producto.setError("Debe diligenciar una cantidad para el producto");
            campoLleno = false;
        }if(precioProducto.isEmpty()) {
            et_precio_producto.setError("Debe diligenciar un precio para el producto");
            campoLleno = false;
        }if(precioDomicilio.isEmpty()){
            et_precio_domicilio.setError("Debe diligencia el precio del domicilio");
        }if(descuentoProducto.isEmpty()) {
            et_descuento_producto.setError("Debe diligenciar un descuento para el producto. Si no tiene digite 0 (cero)");
            campoLleno = false;
        }else if(Double.parseDouble(descuentoProducto)>Double.parseDouble(precioProducto)){
            et_descuento_producto.setError("El descuento debe ser menor al precio del producto");
            campoLleno = false;
        }if(domicilioProducto.equals("✚ ¿Tienes domicilio?")){
            Toast.makeText(this, "Seleccione si desea ofrecer domicilio", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }if(fechaInicio.equals("dd/mm/aaaa")){
            tv_fecha_inicio.setError("");
            Toast.makeText(this, "Debe seleccionar una fecha de inicio", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }if(fechaFin.equals("dd/mm/aaaa")){
            tv_fecha_fin.setError("");
            Toast.makeText(this, "Debe seleccionar una fecha de fin", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }if(horaInicio.equals("hh/mm")){
            tv_hora_inicio.setError("");
            Toast.makeText(this, "Debe seleccionar una hora de inicio", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }if(horaFin.equals("hh/mm")){
            tv_hora_fin.setError("");
            Toast.makeText(this, "Debe seleccionar una hora de fin", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }if(direccion.isEmpty()) {
            direccionProducto.setError("");
            Toast.makeText(this, "Debe agregar una dirección", Toast.LENGTH_SHORT).show();
            campoLleno = false;
        }else if(direccion.equals("Dirección 1") || direccion.equals("Dirección 2")
                || direccion.equals("Dirección 3" ) || direccion.equals("Dirección 4" )
                || direccion.equals("Dirección 5" )){
            direccionProducto.setError("");
            Toast.makeText(this, "Debe agregar una dirección válida", Toast.LENGTH_SHORT).show();
            campoLleno = false;
        }
        return campoLleno;
    }

    public void consultarNombreEstablecimiento(){
        //consultando datos del vendedor
        myRefVendedores.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if (snapshot.child("nombre_establecimiento").exists()) {
                                nombreEstablecimiento = Objects.requireNonNull(snapshot.child("nombre_establecimiento").getValue()).toString();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(addProduct.this, "Error consultando el nombre del establecimiento. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void crearModalDirecciones(EditText et_direccion_producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(addProduct.this );
        builder.setTitle("Elige una dirección");
        builder.setCancelable(false);

        String[] direcciones = new String[]
        {
                "Dirección 1", "Dirección 2", "Dirección 3", "Dirección 4", "Dirección 5"
        };

        //array booleano para marcar casillas por defecto
        boolean[] checkItems = new boolean[]{
                false, false, false, false, false
        };

        //consulta las direcciones del usuario
        myRefUsuarios.child(currentUser.getUid()).child("mis direcciones")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int i=0;
                        if(snapshot.exists()){
                            for(DataSnapshot objsnapshot : snapshot.getChildren()){
                                ListDirecciones d = objsnapshot.getValue(ListDirecciones.class);
                                direcciones[i] = d.getDireccionUsuario();
                                checkItems[i]=false;
                                if (i < direcciones.length){
                                    i=i+1;
                                }
                            }

                            final List<String> foodList = Arrays.asList(direcciones);

                            builder.setMultiChoiceItems(direcciones, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                    checkItems[i] = b; //verificar si existe un item seleccionado
                                    String currentItems = foodList.get(i); //Obtener el valor seleccionado
                                }
                            });

                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    selectedItems.clear();
                                    //recorre los items y valida cuales fueron checkeados
                                    for(int x=0;x<checkItems.length;x++){
                                        boolean checked = checkItems[x];
                                        if(checked){
                                            selectedItems.put(foodList.get(x),checked);
                                            et_direccion_producto.setText(foodList.get(x));
                                        }
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando las direcciones. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

         }

    public void consultarCategoriaProducto(){
        List<String> tipoProducto = new ArrayList<>();
        tipoProducto.add("✚ Seleccione una categoría");
        myRefTipoProducto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        TipoProductos t = objsnapshot.getValue(TipoProductos.class);
                        tipoProducto.add(t.getTipoComida());
                    }

                    ArrayAdapter<String> adapterCategoriaProductos = new ArrayAdapter<>(addProduct.this, R.layout.spinner_item_modified, tipoProducto);
                    categoriaProducto.setAdapter(adapterCategoriaProductos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void consultarSubCategoriaProducto() {
        List<String> SubTipoProducto = new ArrayList<>();
        SubTipoProducto.add("✚ Seleccione una sub categoría");
        myRefTipoProducto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        for (DataSnapshot objsnapshot2 : objsnapshot.child("subTipo").getChildren()) { //recorre los productos

                            SubTipoProductos st = objsnapshot2.getValue(SubTipoProductos.class);
                            SubTipoProducto.add(st.getSubTipoComida());
                        }

                    }

                    ArrayAdapter<String> adapterSubCategoriaProductos = new ArrayAdapter<String>(addProduct.this, R.layout.spinner_item_modified, SubTipoProducto);
                    subCategoriaProducto.setAdapter(adapterSubCategoriaProductos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });

    }

}