package com.desarollo.salvavidasapp.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.desarollo.salvavidasapp.Models.Municipios;
import com.desarollo.salvavidasapp.Models.Usuarios;
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
    private Spinner municipio;
    Usuarios u;



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
        EditText direccion = view.findViewById(R.id.tv_direccion);
        TextView lista_comida = view.findViewById(R.id.lista_comidas);
        municipio = view.findViewById(R.id.sp_municipio);

        Municipios arrayMunicipio = new Municipios();

        //Datos para el spinner de municipio
        String [] listaMunicipios = arrayMunicipio.getMunicipio();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_modified, listaMunicipios);
        municipio.setAdapter(adapter);
        EditText identificacion = view.findViewById(R.id.tv_identidad);
        EditText celular = view.findViewById(R.id.tv_celular);
        Button btn_reg = view.findViewById(R.id.btn_registrar_perfil);
        Button btn_desactivar_usuario = view.findViewById(R.id.btn_desactivar_usuario);

        //Actualiza los datos del perfil logeado en el fragmenProfile
        UserName.setText(currentUser.getDisplayName());
        UserMail.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(UserPhoto);

        //Llena los campos del formulario con los datos de la bd
        consultarDatosPerfil(nombres, apellidos, direccion, municipio, identificacion, celular,btn_reg, btn_desactivar_usuario);

        //Acciones del botón registrar
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposVacios(UserMail, nombres, apellidos, direccion, municipio, identificacion, celular)) {
                    registrar(UserMail, nombres, apellidos, direccion, municipio, identificacion, celular);
                }
            }
        });

        //Acciones del botón desactivar usuario
        btn_desactivar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_desactivar_usuario.getText().toString().equals("Desactivar Usuario")){
                    desactivarUsuario(UserMail, nombres, apellidos, direccion, municipio, identificacion, celular);
                }
                if (btn_desactivar_usuario.getText().toString().equals("Activar Usuario")){
                    if(validarCamposVacios(UserMail, nombres, apellidos, direccion, municipio, identificacion, celular)) {
                        registrar(UserMail, nombres, apellidos, direccion, municipio, identificacion, celular);
                    }
                }
            }
        });

        //Lista de comidas preferidas
        lista_comida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext() );
                builder.setTitle("Elige tus comidas preferidas");
                builder.setCancelable(false);

                String[] comida = new String[]{"comida","otra"};
                boolean[] estado = new boolean[]{true,true};

                builder.setMultiChoiceItems(comida, estado, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            // If the user checked the item, add it to the selected items
                            //selectedItems.put(foodList.indexOf(i) ,b);
                            //u.setComidas_preferidas.set("ff",true);

                        } else  {
                            // Else, if the item is already in the array, remove it
                            //selectedItems.remove(Integer.valueOf(i));
                        }
                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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
        });


        return view;
    }

    /*
     * @autor: Andrés Pérez
     * @since: 10/03/2021
     * @Version: 01
     * Método para registrar o actualizar los datos del perfil en la BD
     * */
    private void registrar(TextView UserMail,EditText nombres, EditText apellidos, EditText direccion, Spinner sp_municipio, EditText identificacion, EditText celular) {

        Map <String,Object> selectedItems = new HashMap<>();
        selectedItems.put("fruta",true);
        selectedItems.put("verdura",false);
        selectedItems.put("pecedero",true);
        selectedItems.put("no perecederos",true);

        u = new Usuarios();
        u.nombre=nombres.getText().toString();
        u.apellido = apellidos.getText().toString();
        u.municipio = sp_municipio.getSelectedItem().toString();
        u.direccion = direccion.getText().toString();
        u.identificacion = identificacion.getText().toString();
        u.celular = celular.getText().toString();
        u.correo = UserMail.getText().toString();
        u.habilitado = true;
        //u.comidas_preferidas = selectedItems;

        myRef.child(currentUser.getUid()).setValue(u)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        /*
                        nombres.setText("");
                        apellidos.setText("");
                        String [] listaMunicipios2 = {"Municipio/Departamento"};
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listaMunicipios2);
                        sp_municipio.setAdapter(adapter1);
                        direccion.setText("");
                        identificacion.setText("");
                        celular.setText("");
                        */
                        Toast.makeText(getApplicationContext(), "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error actualizando el usuario", Toast.LENGTH_SHORT).show();
                    }
                });


        myRef.child(currentUser.getUid()).child("comidas_preferidas").setValue(u.comidas_preferidas)
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getApplicationContext(), "SHola", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public String[] test(){
        String[] comida = {""};
        myRef.child("tipo_alimentos")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String[] comida = new String[]{snapshot.getValue().toString()};
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

        return comida;

    }



    /*
     * @autor: Edison Cardona
     * @since: 15/03/2021
     * @Version: 01
     * Método para desactivar al usuario logueado.
     * */
    private void desactivarUsuario(TextView UserMail,EditText nombres, EditText apellidos, EditText direccion, Spinner sp_municipio, EditText identificacion, EditText celular) {

        u.nombre=nombres.getText().toString();
        u.apellido = apellidos.getText().toString();
        u.municipio = sp_municipio.getSelectedItem().toString();
        u.direccion = direccion.getText().toString();
        u.identificacion = identificacion.getText().toString();
        u.celular = celular.getText().toString();
        u.correo = UserMail.getText().toString();
        u.habilitado = false;

        Map <String,Object> selectedItems = new HashMap<>();
        selectedItems.put("fruta",true);
        selectedItems.put("verdura",false);
        selectedItems.put("pecedero",true);
        selectedItems.put("no perecederos",true);

        myRef.child(currentUser.getUid()).setValue(u)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Usuario desactivado", Toast.LENGTH_SHORT).show();
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

                    }
                });
    }


    /*
    * @autor: Edison Cardona
    * @since: 11/03/2021
    * @Version: 01
    * Método para consultar los datos del perfil en la BD y pintar
    * el formulario con ellos
    * */
    public void consultarDatosPerfil(EditText et_nombres, EditText et_apellidos, EditText et_direccion, Spinner sp_municipio, EditText et_identificacion,
                                     EditText et_celular,Button btn_reg, Button btn_desactivar_usuario){
        myRef.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombre = snapshot.child("nombre").getValue().toString();
                    et_nombres.setText(nombre);
                    String apellido = snapshot.child("apellido").getValue().toString();
                    et_apellidos.setText(apellido);
                    String direccion = snapshot.child("direccion").getValue().toString();
                    et_direccion.setText(direccion);

                    String [] municipio = {snapshot.child("municipio").getValue().toString()};
                    Municipios arrayMunicipios = new Municipios();
                    ArrayList<String> listOne = new ArrayList(Arrays.asList(municipio));
                    ArrayList<String> listTwo = new ArrayList(Arrays.asList(arrayMunicipios.getMunicipio()));

                    listOne.addAll(listTwo);
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listOne);
                    sp_municipio.setAdapter(adapter1);
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
    public boolean validarCamposVacios(TextView UserMail,EditText nombres, EditText apellidos, EditText direccion, Spinner municipio, EditText identificacion, EditText celular){
        boolean campoLleno = true;

        String nombreV = nombres.getText().toString();
        String apellidoV = apellidos.getText().toString();
        String direccionV = direccion.getText().toString();
        String municipioV = municipio.getSelectedItem().toString();
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
        if(direccionV.isEmpty()){
            direccion.setError("Debe diligenciar una dirección");
            campoLleno=false;
        }
        if(municipioV.equals("Municipio/Departamento")){
            Toast.makeText(getApplicationContext(), "Debe seleccionar un municipio", Toast.LENGTH_LONG).show();
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