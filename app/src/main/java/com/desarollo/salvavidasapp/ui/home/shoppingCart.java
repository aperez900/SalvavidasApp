package com.desarollo.salvavidasapp.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.ListSellAdapter;
import com.desarollo.salvavidasapp.ui.sales.buyProduct;
import com.desarollo.salvavidasapp.ui.sales.lookAtProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.facebook.FacebookSdk.getApplicationContext;

public class shoppingCart extends AppCompatActivity {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    listShoppingCartAdapter ListShoppingCartAdapter;
    TextView titulo_carrito, subtitulo_carrito, total_carrito;
    //Button btn_comprar;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef, myRefProductos, myRefVendedor;
    Double subTotalCarrito=0.0;
    ProgressDialog cargando;
    HashMap<String, String> producto = new HashMap<String, String>();
    Session session;
    String nombreComprador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        cargando = new ProgressDialog(this);

        titulo_carrito = findViewById(R.id.tv_titulo_carrito);
        subtitulo_carrito = findViewById(R.id.tv_subtitulo_carrito);
        total_carrito = findViewById(R.id.tv_subtotal_carrito);
        //btn_comprar = findViewById(R.id.btn_comprar_carrito);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        myRefProductos = database.getReference("productos");
        myRefVendedor = database.getReference("vendedores");

        listado = findViewById(R.id.listadoCarrito);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listado.setLayoutManager(manager);
//        listado.setHasFixedSize(true);
        ListShoppingCartAdapter = new listShoppingCartAdapter(this, listaDeDatos,this);
        listado.setAdapter(ListShoppingCartAdapter);

        crearListado();

        /*
        btn_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cargando.setTitle("Cargando");
                cargando.setMessage("Un momento por favor...");
                cargando.show();

                for(int x=0;x<listaDeDatos.size();x++) {
                    producto.clear();
                    producto.put("idProducto", listaDeDatos.get(x).getIdProducto());
                    producto.put("valorProducto",String.valueOf(listaDeDatos.get(x).getPrecio() - listaDeDatos.get(x).getDescuento()));
                    producto.put("cantidadProducto",String.valueOf(listaDeDatos.get(x).getCantidad()));
                    producto.put("usuarioSolicitud",currentUser.getUid());
                    producto.put("precioDomicilio",String.valueOf(listaDeDatos.get(x).getPrecioDomicilio()));
                    producto.put("estado","Solicitado");

                    registrarProductoSolicitado(listaDeDatos.get(x).getIdVendedor(), listaDeDatos.get(x).getIdProducto(), producto);
                    consultarDatosVendedor(listaDeDatos.get(x).getIdVendedor(), listaDeDatos.get(x).getIdProducto(), String.valueOf(listaDeDatos.get(x).getCantidad()),listaDeDatos.get(x).getNombreProducto());
                }

            }
        });
         */

        //Actualiza los datos del perfil logeado en el fragmenProfile
        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                nombreComprador = profile.getDisplayName();
            }
        }
    }

    public void crearListado() {

        myRef.child(currentUser.getUid()).child("carrito_compras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    subtitulo_carrito.setText("Los siguientes productos estan disponibles en el carrito hasta que se cumpla su fecha y hora de fin");
                    listaDeDatos.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        String idProd = objsnapshot.child("idProducto").getValue().toString();
                        String cantidad = objsnapshot.child("cantidadProducto").getValue().toString();
                        //String nombreProducto = objsnapshot.child("nombreProducto").getValue().toString();

                        consultarDetalleProducto(idProd,cantidad);

                    }
                }else{
                    Intent intent = new Intent(shoppingCart.this , Home.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(shoppingCart.this, "Carrito de compras vacío", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(shoppingCart.this, "Error cargando los productos del carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarDetalleProducto(String idProduct, String cantidad){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        subTotalCarrito=0.0;
        myRefProductos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    producto.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        for(DataSnapshot objsnapshot2 : objsnapshot.getChildren()){ //recorre los productos
                            Productos p = objsnapshot2.getValue(Productos.class);
                            String id = p.getIdProducto();

                            Date fechaInicio = null;
                            Date fechaFin = null;
                            Date getCurrentDateTime = null;

                            if(idProduct.equals(id)) {
                                String estado = p.getEstadoProducto();
                                try {
                                    fechaInicio = sdf.parse(p.getFechaInicio() + " " + p.getHoraInicio());
                                    fechaFin = sdf.parse(p.getFechaFin() + " " + p.getHoraFin());
                                    getCurrentDateTime = c.getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (getCurrentDateTime.compareTo(fechaInicio) > 0 && getCurrentDateTime.compareTo(fechaFin) < 0
                                        && !estado.equals("Cancelado por el vendedor")) {
                                    listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                            p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                            p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(), p.getNombreEmpresa(), p.getDireccion(), Integer.parseInt(cantidad), p.getPrecioDomicilio(),
                                            p.getIdVendedor()));
                                    subTotalCarrito = subTotalCarrito + (p.getPrecio() - p.getDescuento()) * Integer.parseInt(cantidad);
                                }
                            }
                        }
                    }
                }
                ListShoppingCartAdapter = new listShoppingCartAdapter(shoppingCart.this, listaDeDatos, shoppingCart.this);
                listado.setAdapter(ListShoppingCartAdapter);
                String patron = "###,###.##";
                DecimalFormat objDF = new DecimalFormat (patron);
                total_carrito.setText("Sub Total: $" + objDF.format(subTotalCarrito));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registrarProductoSolicitado(String idVendedor, String idProducto, HashMap producto){

        myRefVendedor.child(idVendedor).child("productos_en_tramite").child(currentUser.getUid()).child(idProducto).setValue(producto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(shoppingCart.this, "Producto registrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(shoppingCart.this, "Error agregando solicitud del producto. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void consultarDatosVendedor(String idVendedor, String idProducto, String nroProductos,String NombreProducto ) {
        myRef.child(idVendedor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String emailVendedor = snapshot.child("correo").getValue().toString();
                    String nombreVendedor = snapshot.child("nombre").getValue().toString();
                    String token = snapshot.child("tokenId").getValue().toString();
                    enviar_email_vendedor(nombreComprador, emailVendedor, nombreVendedor, idProducto, nroProductos,NombreProducto);
                    enviar_notificacion_push(nombreComprador, emailVendedor, nombreVendedor,idVendedor,token);
                    cargando.dismiss();

                    double sumaProductos = 0.0;
                    double sumaDomicilios = 0.0;
                    int cantidad_productos = 0;
                    for(int x=0;x<listaDeDatos.size();x++) {
                        sumaProductos = sumaProductos + (listaDeDatos.get(x).getPrecio() - listaDeDatos.get(x).getDescuento());
                        sumaDomicilios = sumaDomicilios + listaDeDatos.get(x).getPrecioDomicilio();
                        cantidad_productos =  cantidad_productos + listaDeDatos.get(x).getCantidad();
                    }

                    Intent intent = new Intent(shoppingCart.this , buyProduct.class);

                    intent.putExtra("idProducto" , idProducto);
                    intent.putExtra("nombreProducto", NombreProducto);
                    intent.putExtra("totalProducto", String.valueOf(sumaProductos));
                    intent.putExtra("precioDomicilio", String.valueOf(sumaDomicilios));
                    intent.putExtra("nroProductos", String.valueOf(nroProductos));
                    intent.putExtra("idVendedor" , idVendedor);
                    intent.putExtra("origen" , "carritoCompras");

                    startActivity(intent);
                    finish();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(shoppingCart.this, "Error cargando los datos del vendedor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void enviar_email_vendedor(String nombreComprador, String emailVendedor, String nombreVendedor, String idProducto, String nroProductos, String NombreProducto){

        //String correoEnvia = correo.getText().toString();
        String correoEnvia = "ceo@salvavidas.app";
        //String contraseñaCorreoEnvia = contraseña.getText().toString();
        String contrasenaCorreoEnvia = "Great_Simplicity01945#";

        String cuerpoCorreo = "<p style='text-align: justify'> Hola " + nombreVendedor +", <br><br>" +
                "nuestro usuario <b>" + nombreComprador + "</b> desea comprar lo siguiente: <br><br>" +
                "<u>Producto:</u> <b>" + NombreProducto + "</b><br>" +
                "<u>Cantidad:</u> <b>" + nroProductos + "</b><br><br>" +
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


    public void enviar_notificacion_push(String nombreComprador, String emailVendedor, String nombreVendedor,String idVendedor,String token){

        try {
            Intent intent = new Intent(getApplicationContext(), lookAtProduct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            String channelId = token;

            if (channelId.isEmpty()){
                Toast.makeText(shoppingCart.this, "Para enviar notificaciones push el vendedor debera actualizar su perfil", Toast.LENGTH_SHORT).show();

            }
            else{
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(getApplicationContext(), channelId)
                                .setSmallIcon(R.drawable.logoprincipal)
                                .setContentTitle("Solicitud de compra - Salvavidas App")
                                .setContentText(" Hola " + nombreVendedor +"," +
                                        " nuestro usuario " + nombreComprador + " desea comprar lo siguiente:" +
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
            Toast.makeText(getApplicationContext(), "Error enviando la solicitud. " + e, Toast.LENGTH_SHORT).show();
        }
    }

}