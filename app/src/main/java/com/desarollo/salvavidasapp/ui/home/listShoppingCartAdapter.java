package com.desarollo.salvavidasapp.ui.home;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.sales.buyProduct;
import com.desarollo.salvavidasapp.ui.sales.lookAtProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.facebook.FacebookSdk.getApplicationContext;

import org.json.JSONException;
import org.json.JSONObject;

public class listShoppingCartAdapter extends RecyclerView.Adapter<listShoppingCartAdapter.viewHolder> implements View.OnClickListener {

    ArrayList<Productos> listaDeDatos;
    LayoutInflater inflater;
    private View.OnClickListener listener;
    Activity  activity;
    String nombreComprador;
    Session session;
    ProgressDialog cargando;
    HashMap<String, String> producto = new HashMap<>();
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    DatabaseReference myRefUsuario, myRefVendedor;
    Calendar c;
    Date getCurrentDateTime;

    public listShoppingCartAdapter(Context context, ArrayList<Productos> listaDeDatos, Activity activity) {
        this.inflater = LayoutInflater.from(context);
        this.listaDeDatos = listaDeDatos;
        this.activity = activity;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefUsuario = database.getReference("usuarios");
        myRefVendedor = database.getReference("vendedores");

        c = Calendar.getInstance();
        getCurrentDateTime = null;

        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                nombreComprador = profile.getDisplayName();
            }
        }

        cargando = new ProgressDialog(activity);
    }

    @NonNull
    @Override
    public listShoppingCartAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_carrito_compras,null,false);
        view.setOnClickListener(this);
        return new listShoppingCartAdapter.viewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull listShoppingCartAdapter.viewHolder holder, int position) {

        String idProducto = listaDeDatos.get(position).getIdProducto();
        String nombreProducto = listaDeDatos.get(position).getNombreProducto();
        String descripcionProducto = listaDeDatos.get(position).getDescripcionProducto();
        int cantidadProductosDisponibles = listaDeDatos.get(position).getCantidadDisponible();
        Double precioProducto = listaDeDatos.get(position).getPrecio();
        Double descuentoProducto = listaDeDatos.get(position).getDescuento();
        String domicilioProducto = listaDeDatos.get(position).getDomicilio();
        String tipoProducto = listaDeDatos.get(position).getCategoriaProducto();
        String fechaInicio = listaDeDatos.get(position).getFechaInicio();
        String horaInicio = listaDeDatos.get(position).getHoraInicio();
        String fechaFin = listaDeDatos.get(position).getFechaFin();
        String horaFin = listaDeDatos.get(position).getHoraFin();
        final int[] cantidad = {listaDeDatos.get(position).getCantidad()};
        String getUrlFoto = listaDeDatos.get(position).getfoto();
        Double precioDomicilio = listaDeDatos.get(position).getPrecioDomicilio();
        String nombreEstablecimiento = listaDeDatos.get(position).getNombreEmpresa();
        final String[] idVendedor = {listaDeDatos.get(position).getIdVendedor()};

        final String[] patron = {"###,###.##"};
        DecimalFormat objDF = new DecimalFormat (patron[0]);

        holder.tvNombreProducto.setText(nombreProducto);
        holder.tvNombreEmpresa.setText(nombreEstablecimiento);
        holder.tvPrecio.setText("$" + objDF.format(precioProducto-descuentoProducto));
        holder.tvCantidad.setText(String.valueOf(cantidad[0]));

        Glide.with(activity)
                .load(getUrlFoto)
                .into(holder.imagenProducto);

        holder.imagenProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irADetalleDeProducto(nombreProducto, idProducto, tipoProducto, domicilioProducto, descripcionProducto,
                        cantidad[0], precioProducto, descuentoProducto, precioDomicilio, fechaInicio, horaInicio,
                        fechaFin, horaFin, getUrlFoto, idVendedor[0]);
            }
        });

        holder.imgBorrarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarProductodelCarrito(idProducto);
            }
        });

        holder.btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroActual = holder.tvCantidad.getText().toString();

                cantidad[0] = Integer.parseInt(numeroActual);
                cantidad[0] = cantidad[0] + 1;
                holder.tvCantidad.setText(String.valueOf(cantidad[0]));

                actualizarCantidadEnCarritoCompras(idProducto, cantidad[0]);
            }
        });

        holder.btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroActual = holder.tvCantidad.getText().toString();
                if (numeroActual.equals("1")) {

                }else{
                    cantidad[0] = Integer.parseInt(numeroActual);
                    cantidad[0] = cantidad[0] - 1;
                    holder.tvCantidad.setText(String.valueOf(cantidad[0]));
                    actualizarCantidadEnCarritoCompras(idProducto, cantidad[0]);
                }
            }
        });

        holder.btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroActual = holder.tvCantidad.getText().toString();
                if (Integer.parseInt(numeroActual) <= cantidadProductosDisponibles){
                cargando.setTitle("Cargando");
                cargando.setMessage("Un momento por favor...");
                cargando.show();

                double total = (precioProducto - descuentoProducto);

                String idCompra = UUID.randomUUID().toString();
                registrarProductoSolicitadoAlVendedor(idProducto, nombreProducto, idCompra, total, cantidad[0], precioDomicilio, idVendedor[0]);
                registrarProductoSolicitadoAlComprador(idProducto, nombreProducto, idCompra, total, cantidad[0], precioDomicilio, idVendedor[0]);
                consultarDatosVendedor(idVendedor[0], idProducto, nombreProducto, total, precioDomicilio, cantidad[0]);
                }else{
                    Toast.makeText(getApplicationContext(), "Solo hay " + cantidadProductosDisponibles
                            + " producto(s) disponible(s)", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void irADetalleDeProducto(String nombreProducto, String idProducto, String tipoProducto, String domicilioProducto,
                                      String descripcionProducto, int cantidadProducto, Double precioProducto, Double descuentoProducto,
                                      Double precioDomicilio, String fechaInicio, String horaInicio, String fechaFin,
                                      String horaFin, String getUrlFoto, String idVendedor){
        Intent intent = new Intent(activity , lookAtProduct.class);
        intent.putExtra("nombreProducto", nombreProducto);
        intent.putExtra("idProducto" , idProducto);
        intent.putExtra("tipoProducto" , tipoProducto);
        intent.putExtra("domicilioProducto" , domicilioProducto);
        intent.putExtra("descripcionProducto" , descripcionProducto);
        intent.putExtra("cantidadProducto" , String.valueOf(cantidadProducto));
        intent.putExtra("precio" , String.valueOf(precioProducto));
        intent.putExtra("descuento" , String.valueOf(descuentoProducto));
        intent.putExtra("precioDomicilio" , String.valueOf(precioDomicilio));
        intent.putExtra("fechaInicio", fechaInicio);
        intent.putExtra("horaInicio", horaInicio);
        intent.putExtra("fechaFin", fechaFin);
        intent.putExtra("horaFin", horaFin);
        intent.putExtra("getUrlFoto" , getUrlFoto);
        intent.putExtra("idVendedor" , idVendedor);
        intent.putExtra("tipyEntry" , "Consultar");
        activity.startActivity(intent);
    }

    private void registrarProductoSolicitadoAlVendedor(String idProducto, String nombreProducto, String idCompra, double total, int numeroProductos, double precioDomicilio, String idVendedor){
        getCurrentDateTime = c.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf4 = new SimpleDateFormat("E");
        String fechaHora = sdf.format(getCurrentDateTime);
        String fecha = sdf2.format(getCurrentDateTime);
        String hora = sdf3.format(getCurrentDateTime);
        String diaSemana = sdf4.format(getCurrentDateTime);
        if (diaSemana.equals("Sun") || diaSemana.equals("Sunday")){
            diaSemana = "Domingo";
        }
        if (diaSemana.equals("Mon") || diaSemana.equals("Monday")){
            diaSemana = "Lunes";
        }
        if (diaSemana.equals("Tue") || diaSemana.equals("Tuesday")){
            diaSemana = "Martes";
        }
        if (diaSemana.equals("Wed") || diaSemana.equals("Wednesday")){
            diaSemana = "Miércoles";
        }
        if (diaSemana.equals("Thu") || diaSemana.equals("Thursday")){
            diaSemana = "Jueves";
        }
        if (diaSemana.equals("Fri") || diaSemana.equals("Friday")){
            diaSemana = "Viernes";
        }
        if (diaSemana.equals("Sat") || diaSemana.equals("Satuday")){
            diaSemana = "Sábado";
        }

        producto.clear();
        producto.put("idCompra", idCompra);
        producto.put("idProducto", idProducto);
        producto.put("nombreProducto", nombreProducto);
        producto.put("valorProducto",String.valueOf(total));
        producto.put("cantidadProducto",String.valueOf(numeroProductos));
        producto.put("usuarioSolicitud",currentUser.getUid());
        producto.put("precioDomicilio",String.valueOf(precioDomicilio));
        producto.put("fechaHora", fechaHora);
        producto.put("fecha", fecha);
        producto.put("hora", hora);
        producto.put("diaSemana", diaSemana);
        producto.put("estado", "Solicitado");

        myRefVendedor.child(idVendedor).child("productos_en_tramite").child(currentUser.getUid()).child(idCompra).child(idProducto).setValue(producto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(lookAtProduct.this, "Producto solicitado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error agregando solicitud del producto. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registrarProductoSolicitadoAlComprador(String idProducto, String nombreProducto, String idCompra, double total, int numeroProductos, double precioDomicilio, String idVendedor){
        getCurrentDateTime = c.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf4 = new SimpleDateFormat("E");
        String fechaHora = sdf.format(getCurrentDateTime);
        String fecha = sdf2.format(getCurrentDateTime);
        String hora = sdf3.format(getCurrentDateTime);
        String diaSemana = sdf4.format(getCurrentDateTime);
        if (diaSemana.equals("Sun") || diaSemana.equals("Sunday") || diaSemana.equals("dom.")
                || diaSemana.equals("domingo")){
            diaSemana = "Domingo";
        }
        if (diaSemana.equals("Mon") || diaSemana.equals("Monday") || diaSemana.equals("lun.")
                || diaSemana.equals("lunes")){
            diaSemana = "Lunes";
        }
        if (diaSemana.equals("Tue") || diaSemana.equals("Tuesday") || diaSemana.equals("mar.")
                || diaSemana.equals("martes")){
            diaSemana = "Martes";
        }
        if (diaSemana.equals("Wed") || diaSemana.equals("Wednesday") || diaSemana.equals("mié.")
                || diaSemana.equals("miércoles")){
            diaSemana = "Miércoles";
        }
        if (diaSemana.equals("Thu") || diaSemana.equals("Thursday") || diaSemana.equals("jue.")
                || diaSemana.equals("jueves")){
            diaSemana = "Jueves";
        }
        if (diaSemana.equals("Fri") || diaSemana.equals("Friday") || diaSemana.equals("vie.")
                || diaSemana.equals("viernes")){
            diaSemana = "Viernes";
        }
        if (diaSemana.equals("Sat") || diaSemana.equals("Satuday") || diaSemana.equals("sáb.")
                || diaSemana.equals("sábado")){
            diaSemana = "Sábado";
        }

        producto.clear();
        producto.put("idCompra", idCompra);
        producto.put("idProducto", idProducto);
        producto.put("nombreProducto", nombreProducto);
        producto.put("valorProducto",String.valueOf(total));
        producto.put("cantidadProducto",String.valueOf(numeroProductos));
        producto.put("usuarioSolicitud",currentUser.getUid());
        producto.put("precioDomicilio",String.valueOf(precioDomicilio));
        producto.put("fechaHora", fechaHora);
        producto.put("fecha", fecha);
        producto.put("hora", hora);
        producto.put("diaSemana", diaSemana);
        producto.put("estado", "Solicitado");

        myRefUsuario.child(currentUser.getUid()).child("mis_compras").child(idCompra).child(idProducto).setValue(producto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(lookAtProduct.this, "Producto solicitado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error agregando solicitud del producto. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void consultarDatosVendedor(String idVendedor, String idProducto, String nombreProducto, double total, double precioDomicilio, int numeroProductos) {
        myRefUsuario.child(idVendedor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String emailVendedor = snapshot.child("correo").getValue().toString();
                    String nombreVendedor = snapshot.child("nombre").getValue().toString();
                    enviarEmailVendedor(nombreComprador, emailVendedor, nombreVendedor, nombreProducto, numeroProductos);
                    String token = snapshot.child("tokenId").getValue().toString();
                    //enviarNotificacionPush(nombreComprador, emailVendedor, nombreVendedor, token, nombreProducto, numeroProductos);
                    enviar_notificacion_push2(token);
                    cargando.dismiss();
                    Intent intent = new Intent(getApplicationContext() , buyProduct.class);
                    intent.putExtra("idProducto" , idProducto);
                    intent.putExtra("nombreProducto", nombreProducto);
                    intent.putExtra("totalProducto", String.valueOf(total));
                    intent.putExtra("precioDomicilio", String.valueOf(precioDomicilio));
                    intent.putExtra("nroProductos", String.valueOf(numeroProductos));
                    intent.putExtra("idVendedor" , idVendedor);
                    intent.putExtra("origen" , "LookAtProduct");
                    activity.startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los datos del vendedor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void actualizarCantidadEnCarritoCompras(String idProducto, int cantidad){
        //guarda los datos del carrito de compras
        myRefUsuario.child(currentUser.getUid()).child("carrito_compras").child(idProducto).child("cantidadProductosSolicitados").setValue(cantidad)
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

    public void borrarProductodelCarrito(String idProducto){
        FirebaseAuth mAuth;
        FirebaseUser currentUser;
        FirebaseDatabase database;
        DatabaseReference myRef;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios").child(currentUser.getUid()).child("carrito_compras").child(idProducto);
        myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Producto borrado del carrito", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
            }
        });

        /*Intent intent = new Intent(activity , shoppingCart.class);
        activity.startActivity(intent);
         */
    }

    public void enviarEmailVendedor(String nombreComprador, String emailVendedor, String nombreVendedor, String nombreProducto,
                                      int numeroProductos){

        //String correoEnvia = correo.getText().toString();
        String correoEnvia = "ceo@salvavidas.app";
        //String contraseñaCorreoEnvia = contraseña.getText().toString();
        String contrasenaCorreoEnvia = "Great_Simplicity01945#";

        String cuerpoCorreo = "<p style='text-align: justify'> Hola " + nombreVendedor +", <br><br>" +
                "nuestro usuario <b>" + nombreComprador + "</b> desea comprar lo siguiente: <br><br>" +
                "<u>Producto:</u> <b>" + nombreProducto + "</b><br>" +
                "<u>Cantidad:</u> <b>" + numeroProductos + "</b><br><br>" +
                "Ingresa a Salvavidas App para aceptar el pedido.<br></p>Cordialmente,<br> <b>Equipo de Salvavidas App</b><br>" +
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
        String to_ = emailVendedor;
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
                message.setSubject("Solicitud de compra - Salvavidas App");
                message.setText(cuerpoCorreo, "ISO-8859-1","html");
                message.setRecipients(MimeMessage.RecipientType.TO,InternetAddress.parse(to_));
                //message.setContent("Hola mundo","txt/html; charset= utf-8");
                Transport.send(message);

                Toast.makeText(getApplicationContext(), "Hemos enviado una solicitud de compra al vendedor. Espera un momento hasta que la solicitud sea aceptada", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Error enviando la solicitud. " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void enviar_notificacion_push2(String token){
        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();
        try{
            json.put("to", token);
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo","Solicitud de compra");
            notificacion.put("detalle", "Hola, alguien desee comprar unos de tus productos");

            json.put("data",notificacion);

            String URL = "https://fcm.googleapis.com/fcm/send";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,json, null, null){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();

                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAYqhQGoo:APA91bH6pY-0i7a7NM76Gj4QcUQjMqkEoFbt2Ne4xJA6Zj2mUqSIUNkauH9bBuOaVIq49YJo8GM3UdglAQqGvvxp3V2zBqBXiYk1oQRLkEK7A_iUCAubPIZNJYdJGYAaGhxW7RbMNe1L");

                    return header;
                }
            };

            myrequest.add(request);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void enviarNotificacionPush(String nombreComprador, String emailVendedor, String nombreVendedor, String token,
                                         String nombreProducto, int numeroProductos){

        try {
            Intent intent = new Intent(getApplicationContext(), lookAtProduct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            //String channelId = consultarToken();
            String channelId = token;

            if (channelId.equals("")){
                Toast.makeText(getApplicationContext(), "Para enviar notificaciones push el vendedor debera actualizar su perfil", Toast.LENGTH_SHORT).show();

            }
            else{
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(getApplicationContext(), channelId)
                                .setSmallIcon(R.drawable.logoprincipal)
                                .setContentTitle("Solicitud de compra - Salvavidas App")
                                .setContentText(" Hola " + nombreVendedor +"," +
                                        " nuestro usuario " + nombreComprador + " desea comprar lo siguiente:" +
                                        " Producto: " + nombreProducto  +
                                        " Cantidad: " + numeroProductos +
                                        " Ingresa a Salvavidas App para aceptar el pedido.")
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

            }
        }
        catch (Exception e){
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
        TextView tvNombreProducto, tvPrecio, tvNombreEmpresa, tvCantidad;
        ImageView imagenProducto, imgBorrarProducto;
        Button btnMas, btnMenos, btnComprar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreProducto = itemView.findViewById(R.id.tv_nombre_producto);
            tvNombreEmpresa = itemView.findViewById(R.id.tv_nombre_empresa);
            tvCantidad = itemView.findViewById(R.id.tv_cantidad_producto2);
            imagenProducto = itemView.findViewById(R.id.img_imagen_producto);
            tvPrecio = itemView.findViewById(R.id.tv_total_producto2);
            imgBorrarProducto = itemView.findViewById(R.id.img_borrar_producto);
            btnMenos = itemView.findViewById(R.id.btn_menos);
            btnMas = itemView.findViewById(R.id.btn_mas);
            btnComprar = itemView.findViewById(R.id.btn_comprar_carrito);

        }
    }
}
