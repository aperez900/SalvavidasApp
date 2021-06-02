package com.desarollo.salvavidasapp.ui.seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Vendedores;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.direction.Maps;
import com.desarollo.salvavidasapp.ui.sales.addProduct;
import com.desarollo.salvavidasapp.ui.sales.lookAtProduct;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import id.zelory.compressor.Compressor;

import static com.facebook.FacebookSdk.getApplicationContext;

public class seller2 extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefVendedores, myRefPerfilUsuario, imgRef;
    Map<String,Object> selectedItems = new HashMap<>();

    Vendedores v;
    Session session;
    StorageReference storageReference;
    Bitmap thumb_bitmap = null;
    byte [] thumb_byte = null;
    ProgressDialog cargando;

    private String name;
    private String emailUser;
    private String correo="";
    private String contrasena="";
    ImageView foto_tienda=null;
    String urlFoto="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller2);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");
        myRefPerfilUsuario = database.getReference("usuarios");
        imgRef = myRefVendedores.child(currentUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference().child("vendedores");
        cargando = new ProgressDialog(this);

        TextView nombres = findViewById(R.id.tv_nombre);
        TextView apellidos = findViewById(R.id.tv_apellido);
        TextView identificacion = findViewById(R.id.tv_identidad);
        TextView celular = findViewById(R.id.tv_celular);
        EditText nombreEstablecimiento = findViewById(R.id.et_razon_social);
        EditText nit = findViewById(R.id.et_nit);
        Button btn_reg = findViewById(R.id.btn_registrar_vendedor);
        Button btn_ag_direccion = findViewById(R.id.agregar_direccion);
        TextView estado = findViewById(R.id.estado);
        Spinner sp_actividad_econimica = findViewById(R.id.sp_actividad_economica);
        TextView seleccionarFoto = findViewById(R.id.tv_seleccionar_foto);
        foto_tienda = findViewById(R.id.img_foto_tienda);
        EditText direccionVendedor = findViewById(R.id.et_direccion);

        String[] ArrayActividadesEconimicas = new String[]{
                "✚ Seleccione una actividad económica", "Actividad 1","Actividad 2","Actividad 3", "Actividad 4"
        };

        ArrayList<String> listActividadesEconomicas = new ArrayList(Arrays.asList(ArrayActividadesEconimicas));
        ArrayAdapter<String> adapterActividadesEconomicas = new ArrayAdapter<String>(seller2.this, R.layout.spinner_item_modified, listActividadesEconomicas);
        sp_actividad_econimica.setAdapter(adapterActividadesEconomicas);

        //Actualiza los datos del perfil logeado en el fragmenProfile
        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                name = profile.getDisplayName();
                //UserName.setText(name);
                emailUser = profile.getEmail();
                //UserMail.setText(email);
                /*
                Uri photoUrl = profile.getPhotoUrl();
                Glide.with(UserPhoto.getContext())
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(UserPhoto);
                 */
            }
        }


        Intent intent = getIntent();
        if (intent.getExtras()  != null){
            Bundle extras = getIntent().getExtras();
            String direccion = extras.getString("direccion");
            direccionVendedor.setText(direccion);

        }
        consultarDatosVendedor(nombres, apellidos, identificacion, celular, nombreEstablecimiento, nit, sp_actividad_econimica,
                btn_reg,estado,direccionVendedor);

        consultarDatosPerfilUsuario(nombres, apellidos, identificacion, celular);

        seleccionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(seller2.this);
            }
        });

        //Acciones del botón registrar
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposVacios( nombres, apellidos, identificacion, celular, nombreEstablecimiento, nit, sp_actividad_econimica,
                        urlFoto,direccionVendedor)) {
                    registrar(nombres, apellidos, identificacion, celular, nombreEstablecimiento, nit, sp_actividad_econimica, estado,direccionVendedor);
                    enviar_email(correo,contrasena, nombres, celular);
                    enviar_email_usuario(correo,contrasena, nombres, celular);
                }
            }
        });

        btn_ag_direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                boolean gpsActivo = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (gpsActivo != false){
                    Intent h = new Intent(seller2.this, Maps.class);
                    h.putExtra("tipo", "vendedores");
                    startActivity(h);

                }
                else{
                    Toast.makeText(seller2.this,"activa el GPS para poder continuar...",Toast.LENGTH_SHORT).show();
                }*/

                crearModalDirecciones(direccionVendedor);
            }
        });

    }//fin OnCreate


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            Uri imageUri = CropImage.getPickImageResultUri(seller2.this,data);

            //recortar imagen
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(1000, 1000)
                    .setAspectRatio(1,1)
                    .start(seller2.this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK){
                Uri resultUri = result.getUri();

                File url = new File(resultUri.getPath());

                //Picasso.with(seller2.this).load(url).into(foto_tienda);

                Glide.with(seller2.this)
                        .load(url)
                        //.apply(RequestOptions.circleCropTransform())
                        .into(foto_tienda);
                comprimirImagen(url);
            }
            cargando.setTitle("Subiendo foto");
            cargando.setMessage("Cargando...");
            cargando.show();

            final StorageReference ref = storageReference.child(currentUser.getUid()).child("logo").child("logo_empresa");
            UploadTask uploadTask = ref.putBytes(thumb_byte);

            //subir imagen en Storage
            Task<Uri> UriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    //Actualizar URL en la BD
                    Uri downloaduri = task.getResult();
                    imgRef.child("url_logo").setValue(downloaduri.toString());
                    urlFoto = downloaduri.toString();
                    cargando.dismiss();
                }
            });
        }
    }

    public void comprimirImagen(File url){
        //comprimiendo imagen
        try{
            thumb_bitmap = new Compressor(seller2.this)
                    .setMaxWidth(1000)
                    .setMaxHeight(1000)
                    .setQuality(90)
                    .compressToBitmap(url);
        }catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,90, byteArrayOutputStream);
        thumb_byte = byteArrayOutputStream.toByteArray();
        //fin del compresor
    }

    public void consultarDatosVendedor(TextView tv_nombres, TextView tv_apellidos, TextView et_identificacion,
                                       TextView tv_celular, EditText et_nombreEstablecimiento, EditText et_nit,
                                       Spinner sp_actividad_econimica, Button btn_reg, TextView tv_estado,EditText direccionVendedor){

        //consultando datos del vendedor
        myRefVendedores.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if (snapshot.child("nombre").exists()) {
                                String nombre = Objects.requireNonNull(snapshot.child("nombre").getValue()).toString();
                                tv_nombres.setText(nombre);
                                btn_reg.setText("Reenviar solicitud");
                            }
                            if (snapshot.child("apellido").exists()) {
                                String apellido = Objects.requireNonNull(snapshot.child("apellido").getValue()).toString();
                                tv_apellidos.setText(apellido);
                            }
                            if (snapshot.child("identificacion").exists()) {
                                String identificacion = Objects.requireNonNull(snapshot.child("identificacion").getValue()).toString();
                                et_identificacion.setText(identificacion);
                            }
                            if (snapshot.child("celular").exists()) {
                                String celular = Objects.requireNonNull(snapshot.child("celular").getValue()).toString();
                                tv_celular.setText(celular);
                            }
                            if (snapshot.child("nombre_establecimiento").exists()) {
                                String nombre_establecimiento = Objects.requireNonNull(snapshot.child("nombre_establecimiento").getValue()).toString();
                                et_nombreEstablecimiento.setText(nombre_establecimiento);
                            }
                            if (snapshot.child("nit").exists()) {
                                String nit = Objects.requireNonNull(snapshot.child("nit").getValue()).toString();
                                et_nit.setText(nit);
                            }
                            if (snapshot.child("actividad_economica").exists()) {
                                String actividad_economica = Objects.requireNonNull(snapshot.child("actividad_economica").getValue()).toString();
                                String[] ArrayActividadEconomica = new String[]{actividad_economica, "✚ Seleccione una actividad económica", "Actividad 1", "Actividad 2", "Actividad 3", "Actividad 4"};
                                ArrayList<String> listActividadesEconomicas = new ArrayList(Arrays.asList(ArrayActividadEconomica));
                                ArrayAdapter<String> adapterActividadesEconomicas = new ArrayAdapter<String>(seller2.this, R.layout.spinner_item_modified, listActividadesEconomicas);
                                sp_actividad_econimica.setAdapter(adapterActividadesEconomicas);
                            }
                            if (snapshot.child("estado").exists()) {
                                String estado = Objects.requireNonNull(snapshot.child("estado").getValue()).toString();
                                tv_estado.setText(estado);
                            }

                            if (snapshot.child("direccion").exists()) {
                                String direccion = Objects.requireNonNull(snapshot.child("direccion").getValue()).toString();
                                direccionVendedor.setText(direccion);
                            }

                            if (snapshot.child("url_logo").exists()) {
                                urlFoto = Objects.requireNonNull(snapshot.child("url_logo").getValue()).toString();
                                Glide.with(getApplicationContext())
                                        .load(urlFoto)
                                        //.apply(RequestOptions.circleCropTransform())
                                        .into(foto_tienda);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(seller2.this, "Error consultando los datos del vendedor. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void consultarDatosPerfilUsuario(TextView tv_nombres, TextView tv_apellidos, TextView tv_identificacion,
                                            TextView tv_celular){
        myRefPerfilUsuario.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.child("nombre").exists()) {
                                String nombre = Objects.requireNonNull(snapshot.child("nombre").getValue()).toString();
                                tv_nombres.setText(nombre);
                            }
                            if(snapshot.child("apellido").exists()) {
                                String apellido = Objects.requireNonNull(snapshot.child("apellido").getValue()).toString();
                                tv_apellidos.setText(apellido);
                            }
                            if(snapshot.child("identificacion").exists()) {
                                String identificacion = Objects.requireNonNull(snapshot.child("identificacion").getValue()).toString();
                                tv_identificacion.setText(identificacion);
                            }
                            if(snapshot.child("celular").exists()) {
                                String celular = Objects.requireNonNull(snapshot.child("celular").getValue()).toString();
                                tv_celular.setText(celular);
                            }
                        }else{
                            Toast.makeText(seller2.this, "Debes completar primero el perfil del comprador", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(seller2.this, "Error consultando los datos del perfil. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void registrar(TextView nombres, TextView apellidos, TextView identificacion, TextView celular, EditText nombre_establecimiento, EditText nit, Spinner sp_actividad_econimica,
                           TextView estado, EditText direccionVendedor) {
        v = new Vendedores();
        v.setNombre(nombres.getText().toString());
        v.setApellido(apellidos.getText().toString());
        v.setIdentificacion(identificacion.getText().toString());
        v.setCelular(celular.getText().toString());
        if(estado.getText().toString().equals("SIN REGISTRO")){
            estado.setText("Pendiente");
        }
        v.setEstado(estado.getText().toString());
        v.setNombre_establecimiento(nombre_establecimiento.getText().toString());
        v.setNit(nit.getText().toString());
        v.setActividad_economica(sp_actividad_econimica.getSelectedItem().toString());
        v.setUrl_logo(urlFoto);
        v.setDireccion(direccionVendedor.getText().toString());

        //guarda los datos del vendedor
        myRefVendedores.child(currentUser.getUid()).setValue(v)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getApplicationContext(), "Solicitud registrada correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getApplicationContext(), "Error registrando la solicitud. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void enviar_email( String correo, String contrasena, TextView nombres, TextView celular){

        //String correoEnvia = correo.getText().toString();
        String correoEnvia = "ceo@salvavidas.app";
        //String contraseñaCorreoEnvia = contraseña.getText().toString();
        String contrasenaCorreoEnvia = "Great_Simplicity01945#";

        String cuerpoCorreo = "<p style='text-align: justify'> Hola Administrador de Salvavidas App, <b>" + nombres.getText().toString() + "</b> desea ser vendedor en nuestra APP y"
                + " lo puedes contactar en el número móvil: <u>" + celular.getText().toString() + "</u></p><br>Cordialmente,<br> <b>Equipo de Salvavidas App</b><br>" +
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
        String to_ = "ceo@salvavidas.app";
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
                message.setSubject("Solicitud de nuevo vendedor Salvavidas App");
                message.setText(cuerpoCorreo, "ISO-8859-1","html");
                message.setRecipients(MimeMessage.RecipientType.TO,InternetAddress.parse(to_));
                //message.setContent("Hola mundo","txt/html; charset= utf-8");
                Transport.send(message);

                Toast.makeText(getApplicationContext(), "Solicitud enviada correctamente", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error enviando la solicitud. " + e, Toast.LENGTH_LONG).show();
        }
    }

    public void enviar_email_usuario( String correo, String contrasena, TextView nombres, TextView celular){

        //String correoEnvia = correo.getText().toString();
        String correoEnvia = "ceo@salvavidas.app";
        //String contraseñaCorreoEnvia = contraseña.getText().toString();
        String contrasenaCorreoEnvia = "Great_Simplicity01945#";

        String cuerpoCorreo = "<p style='text-align: justify'> Hola <b>" + nombres.getText().toString() + "</b> recibimos con agrado tu solicitud "
                + "y te estaremos respondiendo en el menor tiempo posible.<br></p><br>Cordialmente,<br> <b>Equipo de Salvavidas App</b><br>" +
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
        String to_ = emailUser;
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
                message.setSubject("Solicitud de nuevo vendedor Salvavidas App");
                message.setText(cuerpoCorreo, "ISO-8859-1","html");
                message.setRecipients(MimeMessage.RecipientType.TO,InternetAddress.parse(to_));
                //message.setContent("Hola mundo","txt/html; charset= utf-8");
                Transport.send(message);

                //Toast.makeText(getApplicationContext(), "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Error enviando la solicitud. " + e, Toast.LENGTH_SHORT).show();
        }
    }
    public boolean validarCamposVacios(TextView nombres, TextView apellidos, TextView identificacion, TextView celular,
                                       EditText nombre_establec, EditText et_nit, Spinner sp_actividades_economicas, String urlFoto,EditText direccionVendedor){
        boolean campoLleno = true;

        String nombreV = nombres.getText().toString();
        String apellidoV = apellidos.getText().toString();
        String identificacionV = identificacion.getText().toString();
        String celularV = celular.getText().toString();
        String nombre_establecimiento = nombre_establec.getText().toString();
        String nit = et_nit.getText().toString();
        String actividades_econo = sp_actividades_economicas.getSelectedItem().toString();
        String direccion = direccionVendedor.getText().toString();

        if(nombreV.equals("Nombres")){
            nombres.setError("Debe diligenciar un nombre");
            Toast.makeText(getApplicationContext(), "Debe completar primero el perfil de comprador", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }
        if(apellidoV.equals("Apellidos")){
            apellidos.setError("Debe diligenciar un apellido");
            campoLleno=false;
        }
        if(identificacionV.equals("Documento de identidad")){
            identificacion.setError("Debe diligenciar un nro. de identificación");
            campoLleno=false;
        }else if (identificacionV.length() < 5 || identificacionV.length() > 15){
            identificacion.setError("Digite un número de cédula válido");
            campoLleno=false;
        }
        if(celularV.equals("Celular")){
            celular.setError("Debe diligenciar un celular");
            campoLleno=false;
        }else if (celularV.length() != 10){
            celular.setError("El celular debe tener 10 digitos");
            campoLleno=false;
        }
        if(nombre_establecimiento.isEmpty()){
            nombre_establec.setError("Debe diligenciar un nombre de establecimiento");
            campoLleno=false;
        }
        if(direccion.isEmpty()){
            direccionVendedor.setError("Debe diligenciar la direccion");
            campoLleno=false;
        }
        if(nit.isEmpty()){
            et_nit.setError("Debe diligenciar un NIT");
            campoLleno=false;
        }if(actividades_econo.equals("✚ Seleccione una actividad económica")){
            Toast.makeText(getApplicationContext(), "Seleccione una actividad económica", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }if(urlFoto.equals("")){
            Toast.makeText(getApplicationContext(), "Por favor cargue el logo de su empresa", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }
        return campoLleno;
    }


    private void crearModalDirecciones(EditText et_direccion_producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(seller2.this );
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

        //consulta las direcciones del vendedor
        myRefPerfilUsuario.child(currentUser.getUid()).child("mis direcciones")
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando las direcciones. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

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
