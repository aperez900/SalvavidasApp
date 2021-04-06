package com.desarollo.salvavidasapp.ui.seller;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Usuarios;
import com.desarollo.salvavidasapp.Models.Vendedores;
import com.desarollo.salvavidasapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.facebook.FacebookSdk.getApplicationContext;


public class seller extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Vendedores v;
    Session session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("vendedores");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_seller,container,false);
        EditText nombres = view.findViewById(R.id.tv_nombre);
        EditText apellidos = view.findViewById(R.id.tv_apellido);
        EditText identificacion = view.findViewById(R.id.tv_identidad);
        EditText celular = view.findViewById(R.id.tv_celular);
        Button btn_reg = view.findViewById(R.id.btn_registrar_vendedor);
        TextView estado = view.findViewById(R.id.estado);
        EditText correo = view.findViewById(R.id.tv_correo);
        EditText contraseña = view.findViewById(R.id.tv_contraseña);
        EditText to = view.findViewById(R.id.tv_to);

        consultarDatosVendedor(nombres, apellidos, identificacion, celular,btn_reg,estado);

        //Acciones del botón registrar
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposVacios( nombres, apellidos, identificacion, celular)) {
                    registrar(nombres, apellidos, identificacion, celular);
                    enviar_email(correo,contraseña,to, nombres, celular);
                }
            }
        });

        return view;
    }

    public void consultarDatosVendedor(EditText et_nombres, EditText et_apellidos, EditText et_identificacion,
                                     EditText et_celular, Button btn_reg, TextView tv_estado){
        //consultando datos del vendedor
        myRef.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String nombre = snapshot.child("nombre").getValue().toString();
                            et_nombres.setText(nombre);
                            String apellido = snapshot.child("apellido").getValue().toString();
                            et_apellidos.setText(apellido);
                            String identificacion = snapshot.child("identificacion").getValue().toString();
                            et_identificacion.setText(identificacion);
                            String celular = snapshot.child("celular").getValue().toString();
                            et_celular.setText(celular);
                            String estado = snapshot.child("estado").getValue().toString();
                            tv_estado.setText(estado);
                            btn_reg.setText("Reenviar solicitud");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando los datos del vendedor. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void registrar(EditText nombres, EditText apellidos, EditText identificacion, EditText celular) {
        v = new Vendedores();
        v.nombre=nombres.getText().toString();
        v.apellido = apellidos.getText().toString();
        v.identificacion = identificacion.getText().toString();
        v.celular = celular.getText().toString();
        v.estado = "Pendiente";

        //guarda los datos del vendedor
        myRef.child(currentUser.getUid()).setValue(v)
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

    public void enviar_email( EditText correo, EditText contraseña,EditText to, EditText nombres, EditText celular){

        String correoEnvia = correo.getText().toString();
        //String correoEnvia = "";
        String contraseñaCorreoEnvia = contraseña.getText().toString();
        //String contraseñaCorreoEnvia = "";

        String cuerpoCorreo = "<p style='text-align: justify'> El(la) Sr.(a) <b>" + nombres.getText().toString() + "</b> desea trabajar con nosotros"
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
        String to_ = to.getText().toString();
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
                    return new PasswordAuthentication(correoEnvia,contraseñaCorreoEnvia);
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

                Toast.makeText(getApplicationContext(), "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error enviando la solicitud. " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validarCamposVacios(EditText nombres, EditText apellidos, EditText identificacion, EditText celular){
        boolean campoLleno = true;

        String nombreV = nombres.getText().toString();
        String apellidoV = apellidos.getText().toString();
        String identificacionV = identificacion.getText().toString();
        String celularV = celular.getText().toString();

        if(nombreV.isEmpty()){
            nombres.setError("Debe diligenciar un nombre");
            campoLleno=false;
        }
        if(apellidoV.isEmpty()){
            apellidos.setError("Debe diligenciar un apellido");
            campoLleno=false;
        }
        if(identificacionV.isEmpty()){
            identificacion.setError("Debe diligenciar un nro. de identificación");
            campoLleno=false;
        }else if (identificacionV.length() < 5 || identificacionV.length() > 15){
            identificacion.setError("Digite un número de cédula válido");
            campoLleno=false;
        }
        if(celularV.isEmpty()){
            celular.setError("Debe diligenciar un celular");
            campoLleno=false;
        }else if (celularV.length() != 10){
            celular.setError("El celular debe tener 10 digitos");
            campoLleno=false;
        }

        return campoLleno;
    }

}