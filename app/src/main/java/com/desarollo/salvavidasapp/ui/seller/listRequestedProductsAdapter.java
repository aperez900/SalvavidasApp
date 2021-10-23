package com.desarollo.salvavidasapp.ui.seller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.Models.ProductosEnTramite;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.sales.lookAtProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.facebook.FacebookSdk.getApplicationContext;

public class listRequestedProductsAdapter extends RecyclerView.Adapter<listRequestedProductsAdapter.viewHolder> implements View.OnClickListener {

    ArrayList<ProductosEnTramite> listaDeDatos;
    LayoutInflater inflater;
    private View.OnClickListener listener;
    Activity  activity;
    ProgressDialog cargando;
    Session session;

    public listRequestedProductsAdapter(Context context, ArrayList<ProductosEnTramite> listaDeDatos, Activity activity) {
        this.inflater = LayoutInflater.from(context);
        this.listaDeDatos = listaDeDatos;
        this.activity = activity;

    }

    @NonNull
    @Override
    public listRequestedProductsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_productos_solicitados,null,false);
        view.setOnClickListener(this);
        cargando = new ProgressDialog(activity);
        return new listRequestedProductsAdapter.viewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }


    @Override
    public void onBindViewHolder(@NonNull listRequestedProductsAdapter.viewHolder holder, int position) {
        String id_producto = listaDeDatos.get(position).getIdProducto();
        String id_compra = listaDeDatos.get(position).getIdCompra();

        String nombre_producto = listaDeDatos.get(position).getNombreProducto();
        String descripcion_producto = listaDeDatos.get(position).getDescripcionProducto();
        Double precio = listaDeDatos.get(position).getPrecio();
        Double descuento = listaDeDatos.get(position).getDescuento();
        long porcDescuento = Math.round(descuento/precio*100);
        String fechaInicio = listaDeDatos.get(position).getFechaInicio();
        String fechaFin = listaDeDatos.get(position).getFechaFin();
        String getUrlFoto = listaDeDatos.get(position).getfoto();
        DecimalFormat df = new DecimalFormat("#.00");
        //double aleatorio = Math.random()*5;
        String direccion = listaDeDatos.get(position).getDireccion();
        String idVendedor = listaDeDatos.get(position).getIdVendedor();
        String idUsuarioSolicitud = listaDeDatos.get(position).getIdusuarioSolicitud();
        String categoriaProducto = listaDeDatos.get(position).getCategoriaProducto();
        String domicilio = listaDeDatos.get(position).getDomicilio();
        String horaInicio = listaDeDatos.get(position).getHoraInicio();
        String horaFin = listaDeDatos.get(position).getHoraFin();
        Double precioDomicilio = listaDeDatos.get(position).getPrecioDomicilio();

        holder.nombre_producto.setText(nombre_producto);
        String patron = "###,###.##";
        DecimalFormat objDF = new DecimalFormat (patron);
        String nombreEstablecimiento = listaDeDatos.get(position).getNombreEmpresa();
        holder.nombre_empresa.setText(nombreEstablecimiento);
        holder.precio.setText("$" + objDF.format(precio-descuento));
        int cantidad = listaDeDatos.get(position).getCantidad();
        holder.tvcantidad.setText(String.valueOf(cantidad));
        String nombreUsuario = listaDeDatos.get(position).getUsuarioSolicitud();
        holder.tvNombreUsuario.setText(nombreUsuario);
        String correoUsuarioSolicitud = listaDeDatos.get(position).getCorreoUsuarioSolicitud();
        String estadoSolicitud = listaDeDatos.get(position).getEstadoSolicitud();
        holder.tvEstadSolicitud.setText(estadoSolicitud);

        Glide.with(activity)
                .load(getUrlFoto)
                .into(holder.imagenProducto);


        holder.imagenProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(activity , addProduct.class);
                Intent intent = new Intent(activity , lookAtProduct.class);
                intent.putExtra("nombreProducto", nombre_producto);
                intent.putExtra("idProducto" , id_producto);
                intent.putExtra("tipoProducto" , categoriaProducto);
                intent.putExtra("domicilioProducto" , domicilio);
                intent.putExtra("descripcionProducto" , descripcion_producto);
                intent.putExtra("precio" , String.valueOf(precio));
                intent.putExtra("descuento" , String.valueOf(descuento));
                intent.putExtra("precioDomicilio" , String.valueOf(precioDomicilio));
                intent.putExtra("fechaInicio", fechaInicio);
                intent.putExtra("horaInicio", horaInicio);
                intent.putExtra("fechaFin", fechaFin);
                intent.putExtra("horaFin", horaFin);
                intent.putExtra("getUrlFoto" , getUrlFoto);

                intent.putExtra("tipyEntry" , "Consultar");

                activity.startActivity(intent);

            }
        });

        holder.imgAceptarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estadoSolicitud.equals("Solicitado")){
                    cargando.setTitle("Cargando");
                    cargando.setMessage("Un momento por favor...");
                    cargando.show();
                    enviar_email_aceptacion_comprador(nombreUsuario, correoUsuarioSolicitud,
                            nombre_producto, cantidad);
                    actualizarEstadoProductoVendedor(idVendedor, idUsuarioSolicitud,
                            id_producto, id_compra,"Aprobado por el vendedor");
                    actualizarEstadoProductoComprador(idUsuarioSolicitud,
                            id_producto, id_compra,"Aprobado por el vendedor");
                }else{
                    Toast.makeText(activity, "El producto ya no se puede aceptar", Toast.LENGTH_SHORT).show();
                }
                cargando.dismiss();
            }
        });

        holder.img_rechazar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estadoSolicitud.equals("Solicitado")) {
                    cargando.setTitle("Cargando");
                    cargando.setMessage("Un momento por favor...");
                    cargando.show();
                    enviar_email_rechazo_comprador(nombreUsuario, correoUsuarioSolicitud,
                            nombre_producto, cantidad);
                    actualizarEstadoProductoVendedor(idVendedor, idUsuarioSolicitud,
                            id_producto,  id_compra,"Rechazado por el vendedor");
                    actualizarEstadoProductoComprador(idUsuarioSolicitud,
                            id_producto, id_compra,"Rechazado por el vendedor");
                }else{
                    Toast.makeText(activity, "El producto ya no se puede rechazar", Toast.LENGTH_SHORT).show();
                }
                cargando.dismiss();
            }
        });
    }

    public void enviar_email_aceptacion_comprador(String nombreComprador, String emailComprador, String nombreProducto, int numeroProductos){

        //String correoEnvia = correo.getText().toString();
        String correoEnvia = "ceo@salvavidas.app";
        //String contraseñaCorreoEnvia = contraseña.getText().toString();
        String contrasenaCorreoEnvia = "Great_Simplicity01945#";

        String cuerpoCorreo = "<p style='text-align: justify'> Hola " + nombreComprador +", <br><br>" +
                "El vendedor ha aprobado tú solicitud: <br><br>" +
                "<u>Producto:</u> <b>" + nombreProducto + "</b><br>" +
                "<u>Cantidad:</u> <b>" + numeroProductos + "</b><br><br>" +
                "Ingresa a Salvavidas App para pagar y revisar el estado del pedido.<br></p>Cordialmente,<br> <b>Equipo de Salvavidas App</b><br>" +
                "<p style='text-align: justify'><font size=1><i>Este mensaje y sus archivos adjuntos van dirigidos exclusivamente a su destinatario pudiendo contener información confidencial " +
                "sometida a secreto profesional. No está permitida su reproducción o distribución sin la autorización expresa de SALVAVIDAS APP, Si usted no es el destinatario " +
                "final por favor elimínelo e infórmenos por esta vía. Según la Ley Estatutaria 1581 de 2.012 de Protección de Datos y sus normas reglamentarias, " +
                "el Titular presta su consentimiento para que sus datos, facilitados voluntariamente, pasen a formar parte de una base de datos, cuyo responsable " +
                "es SALVAVIDAS APP, cuyas finalidades son: Gestión administrativa, Gestión de clientes, Prospección comercial, Fidelización de clientes, Marketing y " +
                "el envío de comunicaciones comerciales sobre nuestros productos y/o servicios. Puede usted ejercer los derechos de acceso, corrección, supresión, " +
                "revocación o reclamo por infracción sobre sus datos, mediante escrito dirigido a SALVAVIDAS APP a la dirección de correo electrónico " +
                "ceo@salvavidas.app indicando en el asunto el derecho que desea ejercer, o mediante correo ordinario remitido a la Carrera XX # XX – XX Medellín, Antioquia." +
                "</font></i></p>";
        //String to_ = to.getText().toString();
        String to_ = emailComprador;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties = new Properties();
        properties.put("mail.smtp.host","smtp.googlemail.com");
        properties.put("mail.smtp.socketFactory.port","465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.port","587");

        try {
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correoEnvia,contrasenaCorreoEnvia);
                }
            });
            if(session!=null){
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(correoEnvia));
                message.setSubject("Aceptación de compra - Salvavidas App");
                message.setText(cuerpoCorreo, "ISO-8859-1","html");
                message.setRecipients(MimeMessage.RecipientType.TO,InternetAddress.parse(to_));
                //message.setContent("Hola mundo","txt/html; charset= utf-8");
                Transport.send(message);

                Toast.makeText(getApplicationContext(), "Hemos enviado la aprobación al comprador. " +
                        "Te recomendamos iniciar con la preparación o envío del producto.", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Error enviando la solicitud. " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarEstadoProductoVendedor(String idVendedor, String idUsuarioSolicitud, String idProducto, String idCompra,
                                                 String estado){

        FirebaseDatabase database;
        DatabaseReference myRefVendedor;
        database = FirebaseDatabase.getInstance();
        myRefVendedor = database.getReference("vendedores");
        myRefVendedor.child(idVendedor).child("productos_en_tramite").child(idUsuarioSolicitud).child(idCompra).child(idProducto).child("estado").setValue(estado)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getApplicationContext(), "Estado actualizado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error guardando el estado de la solicitud. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void actualizarEstadoProductoComprador(String idUsuarioSolicitud, String idProducto, String idCompra, String estado){

        FirebaseDatabase database;
        DatabaseReference myRefusuario;
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefusuario = database.getReference("usuarios");
        myRefusuario.child(currentUser.getUid()).child("mis_compras").child(idCompra).child(idProducto).child("estado").setValue(estado)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getApplicationContext(), "Estado actualizado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error guardando el estado de la solicitud. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void enviar_email_rechazo_comprador(String nombreComprador, String emailComprador, String nombreProducto, int numeroProductos){
        //String correoEnvia = correo.getText().toString();
        String correoEnvia = "ceo@salvavidas.app";
        //String contraseñaCorreoEnvia = contraseña.getText().toString();
        String contrasenaCorreoEnvia = "Great_Simplicity01945#";

        String cuerpoCorreo = "<p style='text-align: justify'> Hola " + nombreComprador +", <br><br>" +
                "El vendedor ha rechazado tú solicitud: <br><br>" +
                "<u>Producto:</u> <b>" + nombreProducto + "</b><br>" +
                "<u>Cantidad:</u> <b>" + numeroProductos + "</b><br><br>" +
                "Probablemente ya no tenía productos disponibles. Ingresa a Salvavidas App y revisa otros productos.<br></p>Cordialmente,<br> <b>Equipo de Salvavidas App</b><br>" +
                "<p style='text-align: justify'><font size=1><i>Este mensaje y sus archivos adjuntos van dirigidos exclusivamente a su destinatario pudiendo contener información confidencial " +
                "sometida a secreto profesional. No está permitida su reproducción o distribución sin la autorización expresa de SALVAVIDAS APP, Si usted no es el destinatario " +
                "final por favor elimínelo e infórmenos por esta vía. Según la Ley Estatutaria 1581 de 2.012 de Protección de Datos y sus normas reglamentarias, " +
                "el Titular presta su consentimiento para que sus datos, facilitados voluntariamente, pasen a formar parte de una base de datos, cuyo responsable " +
                "es SALVAVIDAS APP, cuyas finalidades son: Gestión administrativa, Gestión de clientes, Prospección comercial, Fidelización de clientes, Marketing y " +
                "el envío de comunicaciones comerciales sobre nuestros productos y/o servicios. Puede usted ejercer los derechos de acceso, corrección, supresión, " +
                "revocación o reclamo por infracción sobre sus datos, mediante escrito dirigido a SALVAVIDAS APP a la dirección de correo electrónico " +
                "ceo@salvavidas.app indicando en el asunto el derecho que desea ejercer, o mediante correo ordinario remitido a la Carrera XX # XX – XX Medellín, Antioquia." +
                "</font></i></p>";
        //String to_ = to.getText().toString();
        String to_ = emailComprador;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties = new Properties();
        properties.put("mail.smtp.host","smtp.googlemail.com");
        properties.put("mail.smtp.socketFactory.port","465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.port","587");

        try {
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(correoEnvia,contrasenaCorreoEnvia);
                }
            });
            if(session!=null){
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(correoEnvia));
                message.setSubject("Rechazo de compra - Salvavidas App");
                message.setText(cuerpoCorreo, "ISO-8859-1","html");
                message.setRecipients(MimeMessage.RecipientType.TO,InternetAddress.parse(to_));
                //message.setContent("Hola mundo","txt/html; charset= utf-8");
                Transport.send(message);

                Toast.makeText(getApplicationContext(), "Hemos notificado al comprador de que no le puedes vender este producto",
                        Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Error enviando la solicitud. " + e, Toast.LENGTH_SHORT).show();
        }
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
        TextView nombre_producto, precio, nombre_empresa, tvcantidad, tvNombreUsuario, tvEstadSolicitud;
        ImageView imagenProducto, imgAceptarProducto, img_rechazar_producto;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            nombre_producto = itemView.findViewById(R.id.tv_nombre_producto);
            nombre_empresa = itemView.findViewById(R.id.tv_nombre_empresa);
            tvcantidad = itemView.findViewById(R.id.tv_cantidad_producto2);
            imagenProducto = itemView.findViewById(R.id.img_imagen_producto);
            precio = itemView.findViewById(R.id.tv_total_producto2);
            imgAceptarProducto = itemView.findViewById(R.id.img_aceptar_producto);
            img_rechazar_producto = itemView.findViewById(R.id.img_rechazar_producto);
            tvNombreUsuario = itemView.findViewById(R.id.tv_nombre_usuario);
            tvEstadSolicitud = itemView.findViewById(R.id.tv_estado_solicitud);

        }
    }
}
