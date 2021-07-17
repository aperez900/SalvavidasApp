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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.facebook.FacebookSdk.getApplicationContext;

public class listShoppingCartAdapter extends RecyclerView.Adapter<listShoppingCartAdapter.viewHolder> implements View.OnClickListener {

    ArrayList<Productos> listaDeDatos;
    LayoutInflater inflater;
    private View.OnClickListener listener;
    Activity  activity;
    String id_producto, nombreComprador;
    int cantidad;
    Session session;
    ProgressDialog cargando;
    HashMap<String, String> producto = new HashMap<String, String>();
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    DatabaseReference myRefCarrito, myRefVendedor;

    public listShoppingCartAdapter(Context context, ArrayList<Productos> listaDeDatos, Activity activity) {
        this.inflater = LayoutInflater.from(context);
        this.listaDeDatos = listaDeDatos;
        this.activity = activity;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefCarrito = database.getReference("usuarios");
        myRefVendedor = database.getReference("vendedores");

        //Actualiza los datos del perfil logeado en el fragmenProfile
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

        id_producto = listaDeDatos.get(position).getIdProducto();

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

        holder.nombre_producto.setText(nombre_producto);
        String patron = "###,###.##";
        DecimalFormat objDF = new DecimalFormat (patron);
        String nombreEstablecimiento = listaDeDatos.get(position).getNombreEmpresa();
        holder.nombre_empresa.setText(nombreEstablecimiento);
        holder.precio.setText("$" + objDF.format(precio-descuento));
        cantidad = listaDeDatos.get(position).getCantidad();
        holder.tvcantidad.setText(String.valueOf(cantidad));

        Glide.with(activity)
                .load(getUrlFoto)
                .into(holder.imagenProducto);


        holder.imagenProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(activity , addProduct.class);
                Intent intent = new Intent(activity , lookAtProduct.class);
                intent.putExtra("nombreProducto", listaDeDatos.get(position).getNombreProducto());
                intent.putExtra("idProducto" , listaDeDatos.get(position).getIdProducto());
                intent.putExtra("tipoProducto" , listaDeDatos.get(position).getCategoriaProducto());
                intent.putExtra("domicilioProducto" , listaDeDatos.get(position).getDomicilio());
                intent.putExtra("descripcionProducto" , listaDeDatos.get(position).getDescripcionProducto());
                intent.putExtra("precio" , String.valueOf(listaDeDatos.get(position).getPrecio()));
                intent.putExtra("descuento" , String.valueOf(listaDeDatos.get(position).getDescuento()));
                intent.putExtra("fechaInicio", listaDeDatos.get(position).getFechaInicio());
                intent.putExtra("horaInicio", listaDeDatos.get(position).getHoraInicio());
                intent.putExtra("fechaFin", listaDeDatos.get(position).getFechaFin());
                intent.putExtra("horaFin", listaDeDatos.get(position).getHoraFin());
                intent.putExtra("getUrlFoto" , listaDeDatos.get(position).getfoto());

                intent.putExtra("tipyEntry" , "Consultar");

                activity.startActivity(intent);


            }
        });

        holder.imgBorrarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarProductoCarrito(listaDeDatos.get(position).getIdProducto());
            }
        });

        holder.btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroActual = holder.tvcantidad.getText().toString();
                cantidad = Integer.parseInt(numeroActual);
                cantidad = cantidad + 1;
                holder.tvcantidad.setText(String.valueOf(cantidad));
                agregarCarrito(listaDeDatos.get(position).getIdProducto(), cantidad);
            }
        });

        holder.btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroActual = holder.tvcantidad.getText().toString();
                if (numeroActual.equals("1")) {

                }else{
                    cantidad = Integer.parseInt(numeroActual);
                    cantidad = cantidad - 1;
                    holder.tvcantidad.setText(String.valueOf(cantidad));
                    agregarCarrito(listaDeDatos.get(position).getIdProducto(), cantidad);
                }
            }
        });

        holder.btn_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cargando.setTitle("Cargando");
                cargando.setMessage("Un momento por favor...");
                cargando.show();

                double total = (listaDeDatos.get(position).getPrecio() - listaDeDatos.get(position).getDescuento());

                registrarProductoSolicitado(listaDeDatos.get(position).getIdProducto(), total, listaDeDatos.get(position).getCantidad(),
                                            listaDeDatos.get(position).getPrecioDomicilio(), listaDeDatos.get(position).getIdVendedor());
                consultarDatosVendedor(listaDeDatos.get(position).getIdVendedor(), listaDeDatos.get(position).getIdProducto(), listaDeDatos.get(position).getNombreProducto(),
                                        total, listaDeDatos.get(position).getPrecioDomicilio(), listaDeDatos.get(position).getCantidad());

            }
        });
    }

    public void registrarProductoSolicitado(String idProducto, double total, int numeroProductos, double precioDomicilio, String idVendedor){

        producto.clear();
        producto.put("idProducto", idProducto);
        producto.put("valorProducto",String.valueOf(total));
        producto.put("cantidadProducto",String.valueOf(numeroProductos));
        producto.put("usuarioSolicitud",currentUser.getUid());
        producto.put("precioDomicilio",String.valueOf(precioDomicilio));
        producto.put("estado", "Solicitado");

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
                        Toast.makeText(getApplicationContext(), "Error agregando solicitud del producto. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void consultarDatosVendedor(String idVendedor, String idProducto, String nombreProducto, double total, double precioDomicilio, int numeroProductos) {
        myRefCarrito.child(idVendedor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String emailVendedor = snapshot.child("correo").getValue().toString();
                    String nombreVendedor = snapshot.child("nombre").getValue().toString();
                    enviar_email_vendedor(nombreComprador, emailVendedor, nombreVendedor, nombreProducto, numeroProductos);
                    String token = snapshot.child("tokenId").getValue().toString();
                    enviar_notificacion_push(nombreComprador, emailVendedor, nombreVendedor, token, nombreProducto, numeroProductos);
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

    public void agregarCarrito(String idProducto, int cantidad){
        //guarda los datos del carrito de compras
        myRefCarrito.child(currentUser.getUid()).child("carrito_compras").child(idProducto).child("cantidadProducto").setValue(cantidad)
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

    public void borrarProductoCarrito(String idProducto){
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

    public void enviar_email_vendedor(String nombreComprador, String emailVendedor, String nombreVendedor, String nombreProducto,
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

    public void enviar_notificacion_push(String nombreComprador, String emailVendedor, String nombreVendedor, String token,
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
        TextView nombre_producto, precio, nombre_empresa, tvcantidad;
        ImageView imagenProducto, imgBorrarProducto;
        Button btnMas, btnMenos, btn_comprar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            nombre_producto = itemView.findViewById(R.id.tv_nombre_producto);
            nombre_empresa = itemView.findViewById(R.id.tv_nombre_empresa);
            tvcantidad = itemView.findViewById(R.id.tv_cantidad_producto2);
            imagenProducto = itemView.findViewById(R.id.img_imagen_producto);
            precio = itemView.findViewById(R.id.tv_total_producto2);
            imgBorrarProducto = itemView.findViewById(R.id.img_borrar_producto);
            btnMenos = itemView.findViewById(R.id.btn_menos);
            btnMas = itemView.findViewById(R.id.btn_mas);
            btn_comprar = itemView.findViewById(R.id.btn_comprar_carrito);

        }
    }
}
