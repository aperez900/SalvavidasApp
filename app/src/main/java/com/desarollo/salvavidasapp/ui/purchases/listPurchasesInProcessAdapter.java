package com.desarollo.salvavidasapp.ui.purchases;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.ProductosEnTramite;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.sales.buyProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class listPurchasesInProcessAdapter extends RecyclerView.Adapter<listPurchasesInProcessAdapter.viewHolder> implements View.OnClickListener {

    ArrayList<ProductosEnTramite> listaDeDatos;
    LayoutInflater inflater;
    private View.OnClickListener listener;
    Activity  activity;
    String id_producto;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefVendedores, myRefUsuarios;

    public listPurchasesInProcessAdapter(Context context, ArrayList<ProductosEnTramite> listaDeDatos, Activity activity) {
        this.inflater = LayoutInflater.from(context);
        this.listaDeDatos = listaDeDatos;
        this.activity = activity;
    }

    @NonNull
    @Override
    public listPurchasesInProcessAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_compras_en_proceso,null,false);
        view.setOnClickListener(this);
        return new listPurchasesInProcessAdapter.viewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }


    @Override
    public void onBindViewHolder(@NonNull listPurchasesInProcessAdapter.viewHolder holder, int position) {
        String producto = listaDeDatos.get(position).getIdProducto();
        String idCompra = listaDeDatos.get(position).getIdCompra();
        String nombreProducto = listaDeDatos.get(position).getNombreProducto();
        String descripcionProducto = listaDeDatos.get(position).getDescripcionProducto();
        Double totalCompra = listaDeDatos.get(position).getPrecio();
        Double precioDomicilio = listaDeDatos.get(position).getPrecioDomicilio();
        Double descuento = listaDeDatos.get(position).getDescuento();
        //long porcDescuento = Math.round(descuento/precio*100);
        String fechaInicio = listaDeDatos.get(position).getFechaInicio();
        String fechaFin = listaDeDatos.get(position).getFechaFin();
        String getUrlFoto = listaDeDatos.get(position).getfoto();
        DecimalFormat df = new DecimalFormat("#.00");
        //double aleatorio = Math.random()*5;
        String direccion = listaDeDatos.get(position).getDireccion();
        String idVendedor = listaDeDatos.get(position).getIdVendedor();

        holder.nombreProducto.setText(nombreProducto);
        String patron = "###,###.##";
        DecimalFormat objDF = new DecimalFormat (patron);
        String nombreEstablecimiento = listaDeDatos.get(position).getNombreEmpresa();
        holder.nombreEmpresa.setText(nombreEstablecimiento);
        holder.precio.setText("$" + objDF.format(totalCompra));
        int cantidad = listaDeDatos.get(position).getCantidad();
        holder.tvcantidad.setText(String.valueOf(cantidad));
        String nombreUsuario = listaDeDatos.get(position).getUsuarioSolicitud();
        String correoUsuarioSolicitud = listaDeDatos.get(position).getCorreoUsuarioSolicitud();
        String estadoSolicitud = listaDeDatos.get(position).getEstadoSolicitud();
        holder.tvEstadSolicitud.setText(estadoSolicitud);

        Glide.with(activity)
                .load(getUrlFoto)
                .into(holder.imagenProducto);

        //Acción del botón ver mas
        holder.imgVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(activity , addProduct.class);
                Intent intent = new Intent(activity , buyProduct.class);
                //intent.putExtra("nombreProducto", listaDeDatos.get(position).getNombreProducto());

                intent.putExtra("idProducto" , producto);
                intent.putExtra("nombreProducto", nombreProducto);
                intent.putExtra("totalProducto", String.valueOf(totalCompra));
                intent.putExtra("precioDomicilio", String.valueOf(precioDomicilio));
                intent.putExtra("nroProductos", String.valueOf(cantidad));
                intent.putExtra("idVendedor" , idVendedor);
                intent.putExtra("origen" , "LookAtProduct");
                intent.putExtra("cantidadProductosDisponibles" , "0");
                intent.putExtra("idCompra",idCompra);

                activity.startActivity(intent);
            }
        });

        //Acción del botón Cancelar
        holder.imgCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estadoSolicitud.equals("Cancelado por el comprador")
                        || estadoSolicitud.equals("Cancelado por el vendedor")
                        ||estadoSolicitud.equals("Anulado por el comprador")){

                    Toast.makeText(activity, "el producto ya se encuentra cancelado", Toast.LENGTH_SHORT).show();

                }else if(estadoSolicitud.equals("Realizado") || estadoSolicitud.equals("Pagado")){
                    crearAlertDialog(idVendedor, producto, idCompra,"Anulado por el comprador");
                }else{
                    crearAlertDialog(idVendedor, producto, idCompra,"Cancelado por el comprador");
                }
            }
        });

    }

    public void crearAlertDialog(String idVendedor, String producto, String idCompra, String tipo){
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(activity);
        confirmacion.setMessage("¿Esta seguro que desea cancelar el pedido?. Notificaremos al vendedor.")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth = FirebaseAuth.getInstance();
                        currentUser = mAuth.getCurrentUser();
                        database = FirebaseDatabase.getInstance();
                        myRefVendedores = database.getReference("vendedores");
                        myRefUsuarios = database.getReference("usuarios");

                        myRefVendedores.child(idVendedor).child("productos_en_tramite").child(currentUser.getUid()).child(idCompra).child(producto).child("estado").setValue(tipo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(activity, "Se realizo con éxito", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(activity, "Se realizo con éxito", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        myRefUsuarios.child(currentUser.getUid()).child("mis_compras").child(idCompra).child(producto).child("estado").setValue(tipo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog titulo = confirmacion.create();
        titulo.setTitle("Cancelar producto");
        titulo.show();
    }



    @Override
    public int getItemCount() {

        int a ;
        if(listaDeDatos != null && !listaDeDatos.isEmpty()) {
            a = listaDeDatos.size();
        }
        else {
            a = 0;
        }
        return a;
    }


    @Override
    public void onClick(View view) {
        if(listener != null){
            listener.onClick(view);
        }
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView nombreProducto, precio, nombreEmpresa, tvcantidad, tvNombreUsuario, tvEstadSolicitud;
        ImageView imagenProducto,imgCancelar,imgVer;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            nombreProducto = itemView.findViewById(R.id.tv_nombre_producto);
            nombreEmpresa = itemView.findViewById(R.id.tv_nombre_empresa);
            tvcantidad = itemView.findViewById(R.id.tv_cantidad_producto2);
            imagenProducto = itemView.findViewById(R.id.img_imagen_producto);
            precio = itemView.findViewById(R.id.tv_total_producto2);
            tvEstadSolicitud = itemView.findViewById(R.id.tv_estado_solicitud);
            imgCancelar = itemView.findViewById(R.id.img_cancelar_producto);
            imgVer = itemView.findViewById(R.id.img_ver_mas_producto);
        }
    }
}
