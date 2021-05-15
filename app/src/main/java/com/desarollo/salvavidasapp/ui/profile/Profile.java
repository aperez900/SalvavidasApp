package com.desarollo.salvavidasapp.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Usuarios;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.seller.seller2;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Profile extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Usuarios u;
    ListDirecciones d;
    ArrayList<ListDirecciones> listaDirecciones;

    //items seleccionados de comidas preferidas
    Map <String,Object> selectedItems = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
    }

    //Infla el fragment de perfil
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        TextView UserName = view.findViewById(R.id.nombre_perfil);
        TextView UserMail = view.findViewById(R.id.correo_perfil);
        ImageView UserPhoto = view.findViewById(R.id.foto_perfil);
        EditText nombres = view.findViewById(R.id.tv_nombre);
        EditText apellidos = view.findViewById(R.id.tv_apellido);
        TextView lista_comida = view.findViewById(R.id.lista_comidas);
        EditText identificacion = view.findViewById(R.id.tv_identidad);
        EditText celular = view.findViewById(R.id.tv_celular);
        Button btn_reg = view.findViewById(R.id.btn_registrar_perfil);
        Button btn_desactivar_usuario = view.findViewById(R.id.btn_desactivar_usuario);
        TextView tv_vendedor = view.findViewById(R.id.tv_quiero_ser_vendedor);

        listaDirecciones = new ArrayList<>();

        //Actualiza los datos del perfil logeado en el fragmenProfile
        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                String name = profile.getDisplayName();
                UserName.setText(name);
                String email = profile.getEmail();
                UserMail.setText(email);
                Uri photoUrl = profile.getPhotoUrl();
                Glide.with(UserPhoto.getContext())
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(UserPhoto);
            }
        }

        //pasar de un fragment a otro
        //tv_vendedor.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_profile_to_nav_seller));

        tv_vendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), seller2.class);
                startActivity(intent);
            }
        });

        //UserName.setText(currentUser.getDisplayName());
        //UserMail.setText(currentUser.getEmail());
        //Glide.with(this).load(currentUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(UserPhoto);


        //Llena los campos del formulario con los datos de la bd
        consultarDatosPerfil(nombres, apellidos, identificacion, celular,btn_reg, btn_desactivar_usuario);

        //Acciones del botón registrar
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposVacios(UserMail, nombres, apellidos, identificacion, celular)) {
                    registrar(UserMail, nombres, apellidos, identificacion, celular);
                }
            }
        });

        //Acciones del botón desactivar usuario
        btn_desactivar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_desactivar_usuario.getText().toString().equals("Desactivar Usuario")){
                    desactivarUsuario(UserMail, nombres, apellidos, identificacion, celular);
                }
                if (btn_desactivar_usuario.getText().toString().equals("Activar Usuario")){
                    if(validarCamposVacios(UserMail, nombres, apellidos, identificacion, celular)) {
                        registrar(UserMail, nombres, apellidos, identificacion, celular);
                    }
                }
            }
        });

        //Clic al boton de Lista de comidas preferidas
        lista_comida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearModalComidasPreferidas();
            }
        });

        return view;
    }

    /*
     * @autor: Edison Cardona
     * @since: 11/03/2021
     * @Version: 01
     * Método para consultar los datos del perfil en la BD y pintar
     * el formulario con ellos
     * */
    public void consultarDatosPerfil(EditText et_nombres, EditText et_apellidos, EditText et_identificacion,
                                     EditText et_celular,Button btn_reg, Button btn_desactivar_usuario){
        //consultando datos del usuario
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
                            btn_reg.setText("Actualizar");
                            consultarEstadoUsuario(btn_desactivar_usuario);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando los datos del usuario. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

        //consulta comidas preferidas del usuario
        myRef.child(currentUser.getUid()).child("comidas_preferidas")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String Todas = snapshot.child("Todas").getValue().toString();
                            boolean bTodas = Boolean.parseBoolean(Todas);
                            String Verduras = snapshot.child("Verduras").getValue().toString();
                            boolean bVerduras = Boolean.parseBoolean(Verduras);
                            String Frutas = snapshot.child("Frutas").getValue().toString();
                            boolean bFrutas = Boolean.parseBoolean(Frutas);
                            String Hamburguesa = snapshot.child("Hamburguesas").getValue().toString();
                            boolean bHamburguesa = Boolean.parseBoolean(Hamburguesa);
                            String Otros = snapshot.child("Otros").getValue().toString();
                            boolean bOtros = Boolean.parseBoolean(Otros);

                            selectedItems.put("Todas",bTodas);
                            selectedItems.put("Verduras",bVerduras);
                            selectedItems.put("Frutas",bFrutas);
                            selectedItems.put("Hamburguesas",bHamburguesa);
                            selectedItems.put("Otros",bOtros);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando las comidas preferidas. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

        //Consultando datos de las direcciones
        myRef.child(currentUser.getUid()).child("mis direcciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDirecciones.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        d = new ListDirecciones();
                        d = objsnapshot.getValue(ListDirecciones.class);
                        listaDirecciones.add(new ListDirecciones(d.getNombreDireccion(),d.getDireccionUsuario(), d.getMunicipioDireccion(),R.drawable.ic_icono_address));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error consultando las direcciones. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * @autor: Andrés Pérez
     * @since: 15/03/2021
     * @Version: 01
     * Método para crear el modal de las comidas preferidas
     * */
    private void crearModalComidasPreferidas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext() );
        builder.setTitle("Elige tus comidas preferidas");
        builder.setCancelable(false);

        String[] comidas = new String[]{
                "Todas","Verduras", "Frutas","Hamburguesas", "Otros"
        };

        //array booleano para marcar casillas por defecto
        boolean[] checkItems = new boolean[]{
                true,false,false,false,false
        };

        //consulta comidas preferidas del usuario
        myRef.child(currentUser.getUid()).child("comidas_preferidas")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //si el usuario tiene datos de comidas preferidas en la bd
                        //le muestra sus datos, de lo contrario muestra los valores por defecto
                        if(snapshot.exists()){
                            String Todas = snapshot.child("Todas").getValue().toString();
                            boolean bTodas = Boolean.parseBoolean(Todas);
                            String Verduras = snapshot.child("Verduras").getValue().toString();
                            boolean bVerduras = Boolean.parseBoolean(Verduras);
                            String Frutas = snapshot.child("Frutas").getValue().toString();
                            boolean bFrutas = Boolean.parseBoolean(Frutas);
                            String Hamburguesa = snapshot.child("Hamburguesas").getValue().toString();
                            boolean bHamburguesa = Boolean.parseBoolean(Hamburguesa);
                            String Otros = snapshot.child("Otros").getValue().toString();
                            boolean bOtros = Boolean.parseBoolean(Otros);

                            //array booleano para marcar casillas del usuario
                            checkItems[0]=bTodas;
                            checkItems[1]=bVerduras;
                            checkItems[2]=bFrutas;
                            checkItems[3]=bHamburguesa;
                            checkItems[4]=bOtros;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando las comidas preferidas. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

        final List<String> foodList = Arrays.asList(comidas);

        builder.setMultiChoiceItems(comidas, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
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
                    //if(checked){
                        selectedItems.put(foodList.get(x),checked);
                    //}
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

    /*
     * @autor: Andrés Pérez
     * @since: 10/03/2021
     * @Version: 01
     * Método para registrar o actualizar los datos del perfil en la BD
     * */
    private void registrar(TextView UserMail,EditText nombres, EditText apellidos, EditText identificacion, EditText celular) {
        u = new Usuarios();
        u.nombre=nombres.getText().toString();
        u.apellido = apellidos.getText().toString();
        u.identificacion = identificacion.getText().toString();
        u.celular = celular.getText().toString();
        u.correo = UserMail.getText().toString();
        u.habilitado = true;

        //guarda los datos del usuario
        myRef.child(currentUser.getUid()).setValue(u)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error actualizando el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
        //guarda los datos de las comidas elegidas
        myRef.child(currentUser.getUid()).child("comidas_preferidas").setValue(selectedItems)
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

        //Guardando datos de las direcciones
        for (int i=0; i<listaDirecciones.size();i++){
            d = listaDirecciones.get(i);
            String nombreDir = d.getNombreDireccion();
            myRef.child(currentUser.getUid()).child("mis direcciones").child(nombreDir).setValue(d)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
        }
    }


    /*
     * @autor: Edison Cardona
     * @since: 15/03/2021
     * @Version: 01
     * Método para desactivar al usuario logueado.
     * */
    private void desactivarUsuario(TextView UserMail,EditText nombres, EditText apellidos, EditText identificacion, EditText celular) {
        myRef.child(currentUser.getUid()).child("habilitado").setValue(false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Usuario desactivado correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error desactivando el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*
     * @autor: Edison Cardona
     * @since: 15/03/2021
     * @Version: 01
     * Método para consultar el estado del usuario y modificar
     * el nombre del botón activar/desactivar usuario
     * */
    public void consultarEstadoUsuario(Button btn_desactivar_usuario){
        myRef.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String Estado = snapshot.child("habilitado").getValue().toString();
                            if (Estado.equals("false")){
                                btn_desactivar_usuario.setText("Activar Usuario");
                            }else if (Estado.equals("true")){
                                btn_desactivar_usuario.setText("Desactivar Usuario");
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultado el estado del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*
     * @autor: Edison Cardona
     * @since: 11/03/2021
     * @Version: 01
     * Método para validar que todos los campos del formuarlio se
     * encuentren diligenciados y que cumplen con ciertos criterios de acuerdo
     * al campo
     * */
    public boolean validarCamposVacios(TextView UserMail,EditText nombres, EditText apellidos, EditText identificacion, EditText celular){
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
        if(selectedItems.isEmpty()){
            crearModalComidasPreferidas();
            campoLleno=false;
            Toast.makeText(getContext(), "Seleccione sus comidas preferidas primero", Toast.LENGTH_LONG).show();
        }
        return campoLleno;
    }
}