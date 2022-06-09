package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.desarollo.salvavidasapp.ui.seller.seller2;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

import org.json.JSONException;
import org.json.JSONObject;

public class lookAtProduct extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef, myRefUsuarios, myRefVendedor;
    String idProducto ="";
    String idVendedor ="";
    String urlFoto="";
    String fechaFin="";
    String horaFin="";
    int numeroProductos = 1;
    int cantidadProductosDisponibles;
    Double total, precioDomicilio;
    HashMap<String, String> producto = new HashMap<String, String>();
    Session session;
    String nombreComprador, nombreProducto, token, emailVendedor, nombreVendedor;
    ProgressDialog cargando;
    Calendar c;
    Date getCurrentDateTime;
    Boolean primeraVez=true;;
    String patron = "###,###.##";
    DecimalFormat objDF = new DecimalFormat (patron);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_at_product);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        cargando = new ProgressDialog(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");
        myRefUsuarios = database.getReference("usuarios");
        myRefVendedor = database.getReference("vendedores");

        c = Calendar.getInstance();
        getCurrentDateTime = null;

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
            cantidadProductosDisponibles = Integer.parseInt(extras.getString("cantidadProductosDisponibles"));
            tvnombreProducto.setText(nombreProducto);
            tvdescripcionProducto.setText(extras.getString("descripcionProducto"));
            Double precio = Double.parseDouble(extras.getString("precio"));
            tvprecioProducto.setText(String.valueOf(objDF.format(precio)));
            tvprecioProducto.setPaintFlags(tvprecioProducto.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            Double descuento = Double.parseDouble(extras.getString("descuento"));
            precioDomicilio = Double.parseDouble(extras.getString("precioDomicilio"));
            tvdescuentoProducto.setText(String.valueOf(objDF.format(descuento)));
            long porcDescuento = Math.round(descuento/precio*100);
            tvporcDescuento.setText(String.valueOf(-porcDescuento));
            total = precio-descuento;
            tvtotalProducto.setText(String.valueOf(objDF.format(total)));
            fechaFin = extras.getString("fechaFin");
            horaFin =  extras.getString("horaFin");
            tvinicioProducto.setText(extras.getString("fechaInicio") + " " + extras.getString("horaInicio"));
            tvfinProducto.setText(fechaFin + " " + horaFin);
            idVendedor = extras.getString("idVendedor");
        }

        consultarImagen(imgProducto);

        tvanadirCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarCarrito();
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
                if (currentUser.getUid().equals(idVendedor)) {
                    Toast.makeText(lookAtProduct.this, "No puedes comprar tus mismos productos", Toast.LENGTH_SHORT).show();
                }else{
                    if (numeroProductos <= cantidadProductosDisponibles) {
                        /*
                        cargando.setTitle("Cargando");
                        cargando.setMessage("Un momento por favor...");
                        cargando.show();
                         */
                        FancyAlertDialog.Builder
                                .with(lookAtProduct.this)
                                .setTitle("Confirmación de compra")
                                .setBackgroundColor(Color.parseColor("#EC7063"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                                .setMessage("¿estas seguro que deseas realizar la compra?... Notificaremos al vendedor")
                                .setPositiveBtnBackground(Color.parseColor("#EC7063"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                                .setPositiveBtnText("Comprar")
                                .setNegativeBtnBackground(Color.parseColor("#EC7063"))  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                                .setNegativeBtnText("Volver")
                                .setAnimation(Animation.POP)
                                .isCancellable(true)
                                .setIcon(R.drawable.icono_ok, View.VISIBLE)
                                .onPositiveClicked(new FancyAlertDialogListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        String idCompra = UUID.randomUUID().toString();
                                        consultarDatosVendedor(idVendedor, idCompra);
                                    }
                                })
                                .onNegativeClicked(
                                        dialog -> Toast.makeText(lookAtProduct.this, "Cancelando", Toast.LENGTH_SHORT).show()
                                )
                                .build()
                                .show();
                    } else {
                        Toast.makeText(lookAtProduct.this, "Solo hay " + cantidadProductosDisponibles
                                + " producto(s) disponible(s)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }); //fin método

        //Actualiza los datos del perfil logeado en el fragmenProfile
        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                nombreComprador = profile.getDisplayName();
            }
        }
    }

    public void consultarToken(String nombreVendedor, String idCompra) {
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
        registrarProductoSolicitadoAlVendedor(idCompra);
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
        producto.put("cantidadProductosSolicitados",String.valueOf(numeroProductos));
        producto.put("cantidadProductosDisponibles",String.valueOf(cantidadProductosDisponibles));
        producto.put("precioDomicilio",String.valueOf(precioDomicilio));
        producto.put("fechaFin",String.valueOf(fechaFin));
        producto.put("horaFin",String.valueOf(horaFin));

       // producto.put("nombreProducto",nombreProducto);

        //guarda los datos del carrito de compras
        myRefUsuarios.child(currentUser.getUid()).child("carrito_compras").child(idProducto).setValue(producto)
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

    public void consultarDatosVendedor(String idVendedor,String idCompra) {
        myRefUsuarios.child(idVendedor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    emailVendedor = snapshot.child("correo").getValue().toString();
                    nombreVendedor = snapshot.child("nombre").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(lookAtProduct.this, "Error cargando los datos del vendedor", Toast.LENGTH_SHORT).show();
            }
        });
        consultarToken(nombreVendedor, idCompra);
    }

    public void enviar_email_vendedor(String nombreComprador, String emailVendedor, String nombreVendedor){
        primeraVez=false;
        //String correoEnvia = correo.getText().toString();
        String correoEnvia = "ceo@salvavidas.app";
        //String contraseñaCorreoEnvia = contraseña.getText().toString();
        String contrasenaCorreoEnvia = "Great_Simplicity01945#";

        String cuerpoCorreo = "<p style='text-align: justify'> Hola " + nombreVendedor +", <br><br>" +
                "nuestro usuario: <b>" + nombreComprador + "</b> desea comprar lo siguiente: <br><br>" +
                "<u>Producto:</u> <b>" + nombreProducto + "</b><br>" +
                "<u>Cantidad:</u> <b>" + numeroProductos + "</b><br><br>" +
                "Ingresa a Surplapp App para hacerle seguimiento al pedido.<br></p>Cordialmente,<br> <b>Equipo de Surplapp App</b><br>" +
                "<p style='text-align: justify'><font size=1><i>Este mensaje y sus archivos adjuntos van dirigidos exclusivamente a su destinatario pudiendo contener información confidencial " +
                "sometida a secreto profesional. No está permitida su reproducción o distribución sin la autorización expresa de SURPLAPP, Si usted no es el destinatario " +
                "final por favor elimínelo e infórmenos por esta vía. Según la Ley Estatutaria 1581 de 2.012 de Protección de Datos y sus normas reglamentarias, " +
                "el Titular presta su consentimiento para que sus datos, facilitados voluntariamente, pasen a formar parte de una base de datos, cuyo responsable " +
                "es SURPLAPP, cuyas finalidades son: Gestión administrativa, Gestión de clientes, Prospección comercial, Fidelización de clientes, Marketing y " +
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
                message.setSubject("Solicitud de compra - Surplapp");
                message.setText(cuerpoCorreo, "ISO-8859-1","html");
                message.setRecipients(MimeMessage.RecipientType.TO,InternetAddress.parse(to_));
                //message.setContent("Hola mundo","txt/html; charset= utf-8");
                Transport.send(message);

                Toast.makeText(getApplicationContext(), "Hemos enviado una solicitud de compra al vendedor. Recuerda realizar el pago para que tenga validez.", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Error enviando la solicitud. " + e, Toast.LENGTH_SHORT).show();
        }
    }


    public void enviar_notificacion_push2(String token, String nombreComprador, String nombreVendedor, String idCompra){
        primeraVez=false;
        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();
        try{
            json.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title","Solicitud de compra");
            notification.put("body", "Hola " + nombreVendedor +", nuestro usuario: " + nombreComprador +
                    " desea comprar " + numeroProductos + " " + nombreProducto);
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

    public void registrarProductoSolicitadoAlVendedor(String idCompra){

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
        producto.put("estado","Solicitado");

        myRefVendedor.child(idVendedor).child("productos_en_tramite").child(currentUser.getUid()).child(idCompra).child(idProducto).setValue(producto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        registrarProductoSolicitadoAlComprador(idCompra);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(lookAtProduct.this, "Error agregando solicitud del producto. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void registrarProductoSolicitadoAlComprador(String idCompra){
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
        producto.put("estado","Solicitado");

        myRefUsuarios.child(currentUser.getUid()).child("mis_compras").child(idCompra).child(idProducto).setValue(producto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(primeraVez){
                            enviar_email_vendedor(nombreComprador, emailVendedor, nombreVendedor);
                            enviar_notificacion_push2(token, nombreComprador, nombreVendedor, idCompra);

                            //cargando.dismiss();

                            Intent intent = new Intent(lookAtProduct.this , buyProduct.class);
                            intent.putExtra("idProducto" , idProducto);
                            intent.putExtra("nombreProducto", nombreProducto);
                            intent.putExtra("totalProducto", String.valueOf(total));
                            intent.putExtra("precioDomicilio", String.valueOf(precioDomicilio));
                            intent.putExtra("nroProductos", String.valueOf(numeroProductos));
                            intent.putExtra("cantidadProductosDisponibles", String.valueOf(cantidadProductosDisponibles));
                            intent.putExtra("idVendedor" , idVendedor);
                            intent.putExtra("nombreVendedor" , nombreVendedor);
                            intent.putExtra("idCompra" , idCompra);
                            intent.putExtra("nombreComprador" , nombreComprador);
                            intent.putExtra("tokenId" , token);
                            intent.putExtra("origen" , "LookAtProduct");
                            startActivity(intent);
                            finish();
                        }
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