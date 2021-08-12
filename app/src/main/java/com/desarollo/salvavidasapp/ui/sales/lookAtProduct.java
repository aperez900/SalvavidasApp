package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.desarollo.salvavidasapp.ui.home.ListSellAdapter;
import com.desarollo.salvavidasapp.ui.seller.seller2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import static com.facebook.FacebookSdk.getApplicationContext;
import static com.google.firebase.messaging.Constants.MessagePayloadKeys.SENDER_ID;

public class lookAtProduct extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef, myRefCarrito, myRefVendedor;
    String idProducto ="";
    String idVendedor ="";
    String urlFoto="";
    String fechaFin="";
    String horaFin="";
    int numeroProductos = 1;
    Double total, precioDomicilio;
    HashMap<String, String> producto = new HashMap<String, String>();
    Session session;
    String nombreComprador, nombreProducto;
    ProgressDialog cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_at_product);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        cargando = new ProgressDialog(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");
        myRefCarrito = database.getReference("usuarios");
        myRefVendedor = database.getReference("vendedores");

        ImageView imgProducto = findViewById(R.id.img_imagen_producto);
        TextView tvnombreProducto = findViewById(R.id.tv_nombre_producto);
        TextView tvdescripcionProducto = findViewById(R.id.tv_descripcion_producto);
        TextView tvprecioProducto = findViewById(R.id.tv_precio_producto);
        TextView tvdescuentoProducto = findViewById(R.id.tv_descuento_producto);
        TextView tvporcDescuento = findViewById(R.id.tv_porc_descuento);
        TextView tvtotalProducto = findViewById(R.id.tv_total_producto);
        TextView tvinicioProducto = findViewById(R.id.tv_fecha_inicio_producto);
        TextView tvfinProducto = findViewById(R.id.tv_fecha_fin_producto);
        Button tvanadirCarrito = findViewById(R.id.bt_añadir_carrito);
        Button btnMas = findViewById(R.id.btn_mas);
        Button btnMenos = findViewById(R.id.btn_menos);
        Button btn_comprar_producto = findViewById(R.id.btn_comprar_producto);
        TextView tvNumeroProductos = findViewById(R.id.tv_numero_productos);
        tvanadirCarrito.setPaintFlags(tvprecioProducto.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Intent intent = getIntent();
        if (intent.getExtras() != null){
            Bundle extras = getIntent().getExtras();

            idProducto = extras.getString("idProducto");
            nombreProducto = extras.getString("nombreProducto");
            tvnombreProducto.setText(nombreProducto);
            tvdescripcionProducto.setText(extras.getString("descripcionProducto"));
            tvprecioProducto.setText(String.valueOf(extras.getString("precio")));
            tvprecioProducto.setPaintFlags(tvprecioProducto.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Double precio = Double.parseDouble(extras.getString("precio"));
            Double descuento = Double.parseDouble(extras.getString("descuento"));
            precioDomicilio = Double.parseDouble(extras.getString("precioDomicilio"));
            tvdescuentoProducto.setText(String.valueOf(descuento));
            long porcDescuento = Math.round(descuento/precio*100);
            tvporcDescuento.setText(String.valueOf(-porcDescuento));
            total = precio-descuento;
            tvtotalProducto.setText(String.valueOf(total));
            //ScategoriaProductos = extras.getString("tipoProducto");
            //SdomicilioProducto = extras.getString("domicilioProducto");
            fechaFin = extras.getString("fechaFin");
            horaFin =  extras.getString("horaFin");
            tvinicioProducto.setText(extras.getString("fechaInicio") + " " + extras.getString("horaInicio"));
            tvfinProducto.setText(fechaFin + " " + horaFin);
            //tvHoraInicio.setText(extras.getString("horaInicio"));
            //tvHoraFin.setText(extras.getString("horaFin"));
            //type = extras.getString("tipyEntry");
            //fotoConsulta = extras.getString("getUrlFoto");
            idVendedor = extras.getString("idVendedor");
        }

        consultarImagen(imgProducto);

        tvanadirCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarCarrito();
                //Toast.makeText(lookAtProduct.this, "En construcción", Toast.LENGTH_SHORT).show();
            }
        });

        tvanadirCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarCarrito();
            }
        });


        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numeroProductos = numeroProductos+1;
                tvNumeroProductos.setText(String.valueOf(numeroProductos));
            }
        });

        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroActual;
                numeroActual = tvNumeroProductos.getText().toString();
                if (numeroActual.equals("1")) {

                }else{
                    numeroProductos = numeroProductos-1;
                    tvNumeroProductos.setText(String.valueOf(numeroProductos));
                }
            }
        });

        btn_comprar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargando.setTitle("Cargando");
                cargando.setMessage("Un momento por favor...");
                cargando.show();
                registrarProductoSolicitado();
                consultarDatosVendedor(idVendedor);

            }
        });

        //Actualiza los datos del perfil logeado en el fragmenProfile
        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                nombreComprador = profile.getDisplayName();
            }
        }
    }


    String token;
    public String consultarToken() {
        myRefVendedor.child(idVendedor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    token = snapshot.child("tokenId").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(lookAtProduct.this, "Error cargando los datos del vendedor", Toast.LENGTH_SHORT).show();
            }
        });

        return token;
    }

    public void consultarImagen(ImageView imgProducto){

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        for(DataSnapshot objsnapshot2 : objsnapshot.getChildren()){ //recorre los productos
                            if (objsnapshot2.child("idProducto").getValue().toString().equals(idProducto)) {
                                urlFoto = objsnapshot2.child("foto").getValue().toString();
                                Glide.with(getApplicationContext())
                                        .load(urlFoto)
                                        .into(imgProducto);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(lookAtProduct.this, "Error cargando la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void agregarCarrito(){
        producto.clear();
        producto.put("idProducto", idProducto);
        producto.put("valorProducto",String.valueOf(total));
        producto.put("cantidadProducto",String.valueOf(numeroProductos));
        producto.put("precioDomicilio",String.valueOf(precioDomicilio));
        producto.put("fechaFin",String.valueOf(fechaFin));
        producto.put("horaFin",String.valueOf(horaFin));

       // producto.put("nombreProducto",nombreProducto);

        //guarda los datos del carrito de compras
        myRefCarrito.child(currentUser.getUid()).child("carrito_compras").child(idProducto).setValue(producto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(lookAtProduct.this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(lookAtProduct.this, "Error agregando producto al carrito. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void consultarDatosVendedor(String idVendedor) {
        myRefCarrito.child(idVendedor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String emailVendedor = snapshot.child("correo").getValue().toString();
                    String nombreVendedor = snapshot.child("nombre").getValue().toString();
                    enviar_email_vendedor(nombreComprador, emailVendedor, nombreVendedor);
                    String token = snapshot.child("tokenId").getValue().toString();
                    enviar_notificacion_push(nombreComprador, emailVendedor, nombreVendedor, token);
                    cargando.dismiss();
                    Intent intent = new Intent(lookAtProduct.this , buyProduct.class);
                    intent.putExtra("idProducto" , idProducto);
                    intent.putExtra("nombreProducto", nombreProducto);
                    intent.putExtra("totalProducto", String.valueOf(total));
                    intent.putExtra("precioDomicilio", String.valueOf(precioDomicilio));
                    intent.putExtra("nroProductos", String.valueOf(numeroProductos));
                    intent.putExtra("idVendedor" , idVendedor);
                    intent.putExtra("origen" , "LookAtProduct");

                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(lookAtProduct.this, "Error cargando los datos del vendedor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void enviar_email_vendedor(String nombreComprador, String emailVendedor, String nombreVendedor){

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


    public void enviar_notificacion_push(String nombreComprador, String emailVendedor, String nombreVendedor, String token){

        try {
            Intent intent = new Intent(getApplicationContext(), lookAtProduct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            //String channelId = consultarToken();
            String channelId = token;

            if (channelId.equals("")){
                Toast.makeText(lookAtProduct.this, "Para enviar notificaciones push el vendedor debera actualizar su perfil", Toast.LENGTH_SHORT).show();

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
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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

    public void registrarProductoSolicitado(){

        producto.clear();
        producto.put("idProducto", idProducto);
        producto.put("valorProducto",String.valueOf(total));
        producto.put("cantidadProducto",String.valueOf(numeroProductos));
        producto.put("usuarioSolicitud",currentUser.getUid());
        producto.put("precioDomicilio",String.valueOf(precioDomicilio));
        producto.put("estado","Solicitado");

        myRefVendedor.child(idVendedor).child("productos_en_tramite").child(currentUser.getUid()).child(idProducto).setValue(producto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(lookAtProduct.this, "Producto solicitado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(lookAtProduct.this, "Error agregando solicitud del producto. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}