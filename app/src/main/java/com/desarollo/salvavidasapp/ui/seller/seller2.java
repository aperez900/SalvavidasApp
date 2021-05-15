package com.desarollo.salvavidasapp.ui.seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.desarollo.salvavidasapp.Models.Vendedores;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.sales.addPhoto;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    DatabaseReference myRefVendedores, myRefPerfilUsuario;
    Vendedores v;
    Session session;
    ProgressDialog cargando;
    Bitmap thumb_bitmap = null;

    private String name;
    private String emailUser;
    private String correo="";
    private String contrasena="";
    ImageView foto_tienda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller2);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");
        myRefPerfilUsuario = database.getReference("usuarios");

        TextView nombres = findViewById(R.id.tv_nombre);
        TextView apellidos = findViewById(R.id.tv_apellido);
        TextView identificacion = findViewById(R.id.tv_identidad);
        TextView celular = findViewById(R.id.tv_celular);
        EditText nombreEstablecimiento = findViewById(R.id.et_razon_social);
        EditText nit = findViewById(R.id.et_nit);
        Button btn_reg = findViewById(R.id.btn_registrar_vendedor);
        TextView estado = findViewById(R.id.estado);
        Spinner sp_actividad_econimica = findViewById(R.id.sp_actividad_economica);
        TextView seleccionarFoto = findViewById(R.id.tv_seleccionar_foto);
        foto_tienda = findViewById(R.id.img_foto_tienda);

        String[] ArrayActividadesEconimicas = new String[]{
                "Seleccione actividad económica", "Actividad 1","Actividad 2","Actividad 3", "Actividad 4"
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
                Uri photoUrl = profile.getPhotoUrl();
                /*Glide.with(UserPhoto.getContext())
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(UserPhoto);
                 */
            }
        }
        consultarDatosVendedor(nombres, apellidos, identificacion, celular, nombreEstablecimiento, nit, sp_actividad_econimica, btn_reg,estado);

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
                if(validarCamposVacios( nombres, apellidos, identificacion, celular, nombreEstablecimiento, nit, sp_actividad_econimica)) {
                    Toast.makeText(seller2.this, "Procesando solicitud", Toast.LENGTH_SHORT).show();
                    registrar(nombres, apellidos, identificacion, celular, nombreEstablecimiento, nit, sp_actividad_econimica);
                    enviar_email(correo,contrasena, nombres, celular);
                    enviar_email_usuario(correo,contrasena, nombres, celular);
                }
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

                Picasso.with(seller2.this).load(url).into(foto_tienda);

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
                final byte [] thumb_byte = byteArrayOutputStream.toByteArray();
                //fin del compresor
                //

                /*
                subirFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cargando.setTitle("Subiendo foto");
                        cargando.setMessage("Cargando...");
                        cargando.show();

                        final StorageReference ref = storageReference.child(currentUser.getUid()).child(idProducto).child(nombreProducto);
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
                                imgRef.child("foto").setValue(downloaduri.toString());
                                cargando.dismiss();

                                Toast.makeText(getApplicationContext(), "Imagen cargada con éxito",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } // fin OnClick
                });//fin subir foto
                */
            }
        }
    }


    public void consultarDatosVendedor(TextView tv_nombres, TextView tv_apellidos, TextView et_identificacion,
                                       TextView tv_celular, EditText et_nombreEstablecimiento, EditText et_nit, Spinner sp_actividad_econimica, Button btn_reg, TextView tv_estado){
        //consultando datos del vendedor
        myRefVendedores.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String nombre = snapshot.child("nombre").getValue().toString();
                            tv_nombres.setText(nombre);
                            String apellido = snapshot.child("apellido").getValue().toString();
                            tv_apellidos.setText(apellido);
                            String identificacion = snapshot.child("identificacion").getValue().toString();
                            et_identificacion.setText(identificacion);
                            String celular = snapshot.child("celular").getValue().toString();
                            tv_celular.setText(celular);
                            String nombre_establecimiento = snapshot.child("nombre_establecimiento").getValue().toString();
                            et_nombreEstablecimiento.setText(nombre_establecimiento);
                            String nit = snapshot.child("nit").getValue().toString();
                            et_nit.setText(nit);
                            String actividad_economica = snapshot.child("actividad_economica").getValue().toString();
                            //sp_actividad_econimica.setAdapter(actividad_economica);
                            String estado = snapshot.child("estado").getValue().toString();
                            tv_estado.setText(estado);
                            btn_reg.setText("Reenviar solicitud");
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
                            String nombre = snapshot.child("nombre").getValue().toString();
                            tv_nombres.setText(nombre);
                            String apellido = snapshot.child("apellido").getValue().toString();
                            tv_apellidos.setText(apellido);
                            String identificacion = snapshot.child("identificacion").getValue().toString();
                            tv_identificacion.setText(identificacion);
                            String celular = snapshot.child("celular").getValue().toString();
                            tv_celular.setText(celular);
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

    private void registrar(TextView nombres, TextView apellidos, TextView identificacion, TextView celular, EditText nombre_establecimiento, EditText nit, Spinner sp_actividad_econimica) {
        v = new Vendedores();
        v.setNombre(nombres.getText().toString());
        v.setApellido(apellidos.getText().toString());
        v.setIdentificacion(identificacion.getText().toString());
        v.setCelular(celular.getText().toString());
        v.setEstado("Pendiente");
        v.setNombre_establecimiento(nombre_establecimiento.getText().toString());
        v.setNit(nit.getText().toString());
        v.setActividad_economica(sp_actividad_econimica.getSelectedItem().toString());

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
                                       EditText nombre_establec, EditText et_nit, Spinner sp_actividades_economicas){
        boolean campoLleno = true;

        String nombreV = nombres.getText().toString();
        String apellidoV = apellidos.getText().toString();
        String identificacionV = identificacion.getText().toString();
        String celularV = celular.getText().toString();
        String nombre_establecimiento = nombre_establec.getText().toString();
        String nit = et_nit.getText().toString();
        String actividades_econo = sp_actividades_economicas.getSelectedItem().toString();

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
        if(nit.isEmpty()){
            et_nit.setError("Debe diligenciar un NIT");
            campoLleno=false;
        }if(actividades_econo.equals("Seleccione actividad económica")){
            Toast.makeText(getApplicationContext(), "Seleccione una actividad económica", Toast.LENGTH_SHORT).show();
            campoLleno=false;
        }
        return campoLleno;
    }
}