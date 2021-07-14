package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.desarollo.salvavidasapp.ui.home.listShoppingCartAdapter;
import com.desarollo.salvavidasapp.ui.home.shoppingCart;
import com.desarollo.salvavidasapp.ui.seller.requested_products;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.StringValue;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class buyProduct extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefVendedores, myRefProductos, myRef;
    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    String idProducto ="";
    String idVendedor ="";
    String nombreProducto, origen;
    Double precioProducto = 0.0, precioDomicilio = 0.0;
    int nroProductos;
    Button btn_pago;
    Double valorComision;
    TextView tvPrecioProducto, tvPrecioDomicilio, tvValorComision, tvTotal, tvNombreProducto,
                tvCantidadProducto, tvEstadoProducto, tvSubTotalProducto, tvSubTotalProducto1,
                tvSignoMonedaSubTotal;
    LinearLayout linearLayoutBP, linearLayoutBP1, linearLayoutBP2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_product);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");
        myRefProductos = database.getReference("productos");
        myRef = database.getReference("usuarios");

        tvPrecioProducto = findViewById(R.id.tv_precio_producto);
        tvPrecioDomicilio = findViewById(R.id.tv_precio_domicilio);
        tvValorComision = findViewById(R.id.tv_valor_comision);
        tvTotal = findViewById(R.id.tv_total_producto);
        tvNombreProducto = findViewById(R.id.tv_nombre_producto);
        tvCantidadProducto = findViewById(R.id.tv_cantidad_producto);
        tvEstadoProducto = findViewById(R.id.tv_estado_Producto);
        tvSubTotalProducto = findViewById(R.id.tv_subTotal);
        tvSubTotalProducto1 = findViewById(R.id.tv_subTotal1);
        tvSignoMonedaSubTotal = findViewById(R.id.tv_signo_moneda1);
        linearLayoutBP = findViewById(R.id.LinearLayoutBuyProducto);
        linearLayoutBP1 = findViewById(R.id.LinearLayout1);
        linearLayoutBP2 = findViewById(R.id.LinearLayout2);
        btn_pago = findViewById(R.id.btn_pago);


        btn_pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://checkout.wompi.co/p/?public-key=pub_test_KY4VrC344hkv91RHAfu9XRajobfm0ROe&currency=COP&amount-in-cents=10000&reference="+UUID.randomUUID().toString()+"&redirect-url=http%3A%2F%2Flocalhost%2FsalvavidasWeb%2F");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        if (intent.getExtras() != null){
            Bundle extras = getIntent().getExtras();

            idProducto = extras.getString("idProducto");
            nombreProducto = extras.getString("nombreProducto");
            precioProducto = Double.parseDouble(extras.getString("totalProducto"));
            precioDomicilio = Double.parseDouble(extras.getString("precioDomicilio"));
            nroProductos = Integer.parseInt(extras.getString("nroProductos"));
            idVendedor = extras.getString("idVendedor");
            origen = extras.getString("origen");

            valorComision = precioProducto * nroProductos * 0.06;

            tvPrecioProducto.setText(String.valueOf(precioProducto*nroProductos));
            tvPrecioDomicilio.setText(String.valueOf(precioDomicilio));
            tvValorComision.setText(String.valueOf(valorComision));
            tvTotal.setText(String.valueOf(precioProducto*nroProductos + precioDomicilio + valorComision));

            if(origen.equals("carritoCompras")){
                consultarProductosCarrito();
            }else {
                tvNombreProducto.setText(nombreProducto);
            }
            tvCantidadProducto.setText(String.valueOf(nroProductos));
            tvSubTotalProducto.setText(String.valueOf(precioProducto*nroProductos));
            tvSubTotalProducto.setPaintFlags(tvSubTotalProducto.getPaintFlags());
            tvSubTotalProducto1.setPaintFlags(tvSubTotalProducto1.getPaintFlags());
            tvSignoMonedaSubTotal.setPaintFlags(tvSignoMonedaSubTotal.getPaintFlags());
        }
        consultarEstadoProductoEnTramite(idProducto);
    }

    public void consultarEstadoProductoEnTramite(String idProducto) {

        myRefVendedores.child(idVendedor).child("productos_en_tramite").child(currentUser.getUid()).child(idProducto).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String estadoSolicitud = snapshot.child("estado").getValue().toString();
                    tvEstadoProducto.setText(estadoSolicitud);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(buyProduct.this, "Error cargando el estado del Producto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarProductosCarrito() {

        myRef.child(currentUser.getUid()).child("carrito_compras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDeDatos.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        String idProd = objsnapshot.child("idProducto").getValue().toString();
                        String cantidad = objsnapshot.child("cantidadProducto").getValue().toString();

                        consultarDetalleProducto(idProd,cantidad);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(buyProduct.this, "Error cargando los productos del carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarDetalleProducto(String idProduct, String cantidad) {

        myRefProductos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    TextView nro = new TextView(buyProduct.this);
                    TextView nombreProducto = new TextView(buyProduct.this);
                    TextView valor = new TextView(buyProduct.this);
                    LinearLayout linearLayout = new LinearLayout(buyProduct.this);
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) { //Recorre los usuarios
                        for (DataSnapshot objsnapshot2 : objsnapshot.getChildren()) { //recorre los productos
                            Productos p = objsnapshot2.getValue(Productos.class);
                            String id = p.getIdProducto();
                            if (idProduct.equals(id)) {
                                listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                        p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                        p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(), p.getNombreEmpresa(), p.getDireccion(), Integer.parseInt(cantidad), p.getPrecioDomicilio(),
                                        p.getIdVendedor()));
                                linearLayoutBP1.setVisibility(View.GONE);

                                nro.setText(String.valueOf(cantidad) + "   ");
                                nro.setGravity(Gravity.CENTER);
                                nombreProducto.setText(p.getNombreProducto() + "   ");
                                valor.setText(String.valueOf(p.getPrecio()-p.getDescuento()));
                                //nro.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200,100);
                                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(630,100);
                                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(300,100);
                                nro.setLayoutParams(lp);
                                nombreProducto.setLayoutParams(lp1);
                                valor.setLayoutParams(lp2);
                                nro.setTextSize(24);
                                nombreProducto.setTextSize(24);
                                valor.setTextSize(24);

                                linearLayout.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                                linearLayoutBP.addView(linearLayout);
                                linearLayout.addView(nro);
                                linearLayout.addView(nombreProducto);
                                linearLayout.addView(valor);
                            }
                        }
                    }
                }
                    //Toast.makeText(buyProduct.this, "Producto: " + listaDeDatos.get(x).getNombreProducto(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}