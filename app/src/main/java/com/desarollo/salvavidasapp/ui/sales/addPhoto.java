package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Login.MainActivity;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import io.grpc.Context;

public class addPhoto extends AppCompatActivity {

    Button seleccionarFoto, subirFoto;
    ImageView foto;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference imgRef;
    StorageReference storageReference;
    ProgressDialog cargando;
    String idProducto, nombreProducto;
    Bitmap thumb_bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        Intent intent = getIntent();
        if (intent != null){
            Bundle extras = getIntent().getExtras();
            idProducto = extras.getString("idProducto");
            nombreProducto = extras.getString("nombreProducto");
            //String foto2 = extras.getString("fotoConsulta");
        }

        foto = findViewById(R.id.img_foto);
        seleccionarFoto = findViewById(R.id.btn_seleccoinar_foto);
        subirFoto = findViewById(R.id.btn_subir_foto);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        imgRef = FirebaseDatabase.getInstance().getReference("productos").child(currentUser.getUid()).child(idProducto);
        storageReference = FirebaseStorage.getInstance().getReference().child("vendedores");
        cargando = new ProgressDialog(this);

        seleccionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(addPhoto.this);
            }
        });

    } //fin OnCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            Uri imageUri = CropImage.getPickImageResultUri(addPhoto.this,data);

            //recortar imagen
            CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setRequestedSize(1000, 1000)
                .setAspectRatio(1,1)
                .start(addPhoto.this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK){
                Uri resultUri = result.getUri();

                File url = new File(resultUri.getPath());

                Picasso.with(addPhoto.this).load(url).into(foto);

                //comprimiendo imagen
                try{
                    thumb_bitmap = new Compressor(addPhoto.this)
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

                subirFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cargando.setTitle("Subiendo foto");
                        cargando.setMessage("Cargando...");
                        cargando.show();

                        final StorageReference ref = storageReference.child(currentUser.getUid()).child("productos").child(idProducto).child(nombreProducto);
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
                                crearAlertDialog();
                            }
                        });
                    } // fin OnClick
                });//fin setOnClickListener
            }
        }
    }

    public void crearAlertDialog(){
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(addPhoto.this);
        confirmacion.setMessage("¿Deseas agregar más fotos?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*
                        Intent intent = new Intent(getApplicationContext(), addMorePhoto.class);
                        startActivity(intent);
                        finish();
                         */
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(addPhoto.this, Home.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog titulo = confirmacion.create();
        titulo.setTitle("Agregar mas fotos");
        titulo.show();
    }
}