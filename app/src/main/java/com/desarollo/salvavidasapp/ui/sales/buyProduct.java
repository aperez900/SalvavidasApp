package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.purchases.cancelledPurchases;
import com.desarollo.salvavidasapp.ui.purchases.purchasesInProcess;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class buyProduct extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    HashMap<String, String> producto = new HashMap<String, String>();
    FirebaseDatabase database;
    DatabaseReference myRefVendedores, myRefProductos, myRef;
    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    String idProducto ="";
    String idVendedor ="";
    String nombreProducto, origen,idCompra, tokenId, nombreVendedor, nombreComprador;
    Double precioProducto = 0.0, precioDomicilio = 0.0;
    int nroProductos, cantidadProductosDisponibles;
    Button btn_pago,tvEstadoProducto;
    Double valorComision;
    TextView tvPrecioProducto, tvPrecioDomicilio, tvValorComision, tvTotal, tvNombreProducto,
                tvCantidadProducto,  tvSubTotalProducto, tvSubTotalProducto1,
                tvSignoMonedaSubTotal,tvCancelarPedido;
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
        tvEstadoProducto = findViewById(R.id.tv_estado_producto);
        tvSubTotalProducto = findViewById(R.id.tv_subTotal);
        tvSubTotalProducto1 = findViewById(R.id.tv_subTotal1);
        tvSignoMonedaSubTotal = findViewById(R.id.tv_signo_moneda1);
        tvCancelarPedido = findViewById(R.id.tv_cancelar_Pedido);
        linearLayoutBP = findViewById(R.id.LinearLayoutBuyProducto);
        btn_pago = findViewById(R.id.btn_pago);

        Intent intent = getIntent();
        if (intent.getExtras() != null){
            Bundle extras = getIntent().getExtras();

            String patron = "###,###.##";
            DecimalFormat objDF = new DecimalFormat (patron);

            idProducto = extras.getString("idProducto");
            nombreProducto = extras.getString("nombreProducto");
            precioProducto = Double.parseDouble(extras.getString("totalProducto"));
            precioDomicilio = Double.parseDouble(extras.getString("precioDomicilio"));
            nroProductos = Integer.parseInt(extras.getString("nroProductos"));
            cantidadProductosDisponibles = Integer.parseInt(extras.getString("cantidadProductosDisponibles"));
            idVendedor = extras.getString("idVendedor");
            origen = extras.getString("origen");
            valorComision = precioProducto * nroProductos * 0.06;
            tvPrecioProducto.setText(objDF.format(precioProducto));
            tvPrecioDomicilio.setText(objDF.format(precioDomicilio));
            tvValorComision.setText(objDF.format(valorComision));
            idCompra = extras.getString("idCompra");
            tokenId = extras.getString("tokenId");
            nombreVendedor = extras.getString("nombreVendedor");
            nombreComprador = extras.getString("nombreComprador");

            tvTotal.setText(objDF.format(precioProducto*nroProductos + precioDomicilio + valorComision));

            if(origen.equals("carritoCompras")){
                consultarProductosCarrito();
            }else {
                tvNombreProducto.setText(nombreProducto);
            }
            tvCantidadProducto.setText(String.valueOf(nroProductos));
            tvSubTotalProducto.setText(objDF.format(precioProducto*nroProductos));
            tvSubTotalProducto.setPaintFlags(tvSubTotalProducto.getPaintFlags());
            tvSubTotalProducto1.setPaintFlags(tvSubTotalProducto1.getPaintFlags());
            tvSignoMonedaSubTotal.setPaintFlags(tvSignoMonedaSubTotal.getPaintFlags());
        }
        consultarEstadoProductoEnTramite(idCompra, idProducto);

        btn_pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double valorTotal_ = precioProducto*nroProductos + precioDomicilio + valorComision ;
                int vt = (int) valorTotal_*100;
                String vt_ = Integer.toString(vt);
                String Reference = currentUser.getUid() + "/" + idCompra + "/" + idProducto +"/" + idVendedor;

                registrarsolicitudVendedor(vt_,idCompra, idProducto,idVendedor,Reference);
                registrarCompraAlComprador(vt_,idCompra, idProducto);

            }
        });

        tvCancelarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelarCompraAlComprador();


            }
        });
    }

    private void cancelarCompraAlComprador() {
        myRef.child(currentUser.getUid()).child("mis_compras").child(idCompra).child(idProducto).child("estado").setValue("Cancelado por el comprador")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cancelarSolicitudVendedor();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(buyProduct.this, "Error agregando la compra en la base de datos. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cancelarSolicitudVendedor() {
        myRefVendedores.child(idVendedor).child("productos_en_tramite").child(currentUser.getUid()).child(idCompra).child(idProducto).child("estado").setValue("Cancelado por el comprador")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        enviar_notificacion_push(tokenId, nombreComprador, nombreVendedor);
                        Intent intent = new Intent(buyProduct.this , cancelledPurchases.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(buyProduct.this, "Error cancelando la compra en la base de datos. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void enviar_notificacion_push(String token, String nombreComprador, String nombreVendedor){
        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();
        try{
            json.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title","Cancelación de compra");
            notification.put("body", "Hola " + nombreVendedor +", nuestro usuario: " + nombreComprador +
                    " canceló el pedido de " + nombreProducto);
            notification.put("priority", "high");
            notification.put("sound","default");

            json.put("notification",notification);

            String URL = "https://fcm.googleapis.com/fcm/send";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,json, null, null){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();

                    header.put("Authorization","key=AAAAYqhQGoo:APA91bH6pY-0i7a7NM76Gj4QcUQjMqkEoFbt2Ne4xJA6Zj2mUqSIUNkauH9bBuOaVIq49YJo8GM3UdglAQqGvvxp3V2zBqBXiYk1oQRLkEK7A_iUCAubPIZNJYdJGYAaGhxW7RbMNe1L");
                    header.put("Content-Type","application/json");

                    return header;
                }
            };

            myrequest.add(request);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void registrarsolicitudVendedor(String vt_, String idCompra, String idProducto,String idVendedor,String Reference){
        myRefVendedores.child(idVendedor).child("productos_en_tramite").child(currentUser.getUid()).child(idCompra).child(idProducto).child("estado").setValue("Procesando pago")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Uri uri = Uri.parse("https://checkout.wompi.co/p/?public-key=pub_test_KY4VrC344hkv91RHAfu9XRajobfm0ROe&currency=COP&amount-in-cents="+vt_+"&reference="+Reference+"&redirect-url=https://www.salvavidas.app/");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(buyProduct.this, "Error agregando la compra en la base de datos. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });

        myRefProductos.child(idVendedor).child(idProducto).child("cantidadDisponible").setValue(cantidadProductosDisponibles - 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Uri uri = Uri.parse("https://checkout.wompi.co/p/?public-key=pub_test_KY4VrC344hkv91RHAfu9XRajobfm0ROe&currency=COP&amount-in-cents="+vt_+"&reference="+Reference+"&redirect-url=https://www.salvavidas.app/");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(buyProduct.this, "Error agregando la compra en la base de datos. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registrarCompraAlComprador(String vt_, String idCompra, String idProducto){
        myRef.child(currentUser.getUid()).child("mis_compras").child(idCompra).child(idProducto).child("estado").setValue("Procesando pago")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                       // Intent intent = new Intent(buyProduct.this , purchasesInProcess.class);
                       // startActivity(intent);
                       // finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(buyProduct.this, "Error agregando la compra en la base de datos. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void consultarEstadoProductoEnTramite(String idCompra, String idProducto) {

        myRefVendedores.child(idVendedor).child("productos_en_tramite").child(currentUser.getUid()).child(idCompra).child(idProducto).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String estadoSolicitud = snapshot.child("estado").getValue().toString();
                    tvEstadoProducto.setText(estadoSolicitud);
                    if(estadoSolicitud.equals("Solicitado")){
                        tvEstadoProducto.setTextColor(Color.WHITE);
                        btn_pago.setVisibility(View.VISIBLE);
                    }else if (estadoSolicitud.equals("Procesando pago")
                            ||estadoSolicitud.equals("Cancelado por el comprador")||estadoSolicitud.equals("Cancelado por el vendedor")
                            ||estadoSolicitud.equals("Anulado por el comprador")) {
                        btn_pago.setVisibility(View.INVISIBLE);
                    }else if (estadoSolicitud.equals("Aprobado por el vendedor")){
                        tvEstadoProducto.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ok, 0);
                        btn_pago.setVisibility(View.VISIBLE);
                    }else if (estadoSolicitud.equals("Realizado") || estadoSolicitud.equals("Pagado")){
                        tvEstadoProducto.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ok, 0);
                        btn_pago.setVisibility(View.INVISIBLE);
                    }
                    else{
                        btn_pago.setVisibility(View.INVISIBLE);
                    }
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
                        String cantidad = objsnapshot.child("cantidadProductosSolicitados").getValue().toString();

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
                                        p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(), p.getNombreEmpresa(), p.getDireccion(), Integer.parseInt(cantidad), p.getCantidadDisponible(), p.getPrecioDomicilio(),
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