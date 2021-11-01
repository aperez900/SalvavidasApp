package com.desarollo.salvavidasapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.desarollo.salvavidasapp.ui.sales.lookAtProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefVendedores;
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");

        Log.e("token", "mi token es: " + s);
        guardarToken(s);

    }

    private void guardarToken(String s) {
        if (currentUser != null){
            myRefVendedores.child(currentUser.getUid()).child("tokenId").setValue(s)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("tokenGuardado", "mi token es: " + s);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", "Error al guardar mi token: " + s);
                    }
                });
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();

        if(remoteMessage.getNotification() != null){
            String id = "mensaje";
            String titulo = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mayorQueOreo(titulo, body);
            }
        }

        /*
        if(remoteMessage.getData().size() > 0){

            String titulo = remoteMessage.getData().get("titulo");
            String detalle = remoteMessage.getData().get("detalle");
            Log.e("TAG", "titulo " + titulo);
            Log.e("TAG", "detalle " + detalle);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mayorQueOreo(titulo, detalle);
            }
        }

         */

    }

    private void mayorQueOreo(String titulo, String detalle) {
        String id = "mensaje";
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id, "nuevo",NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            nm.createNotificationChannel(nc);
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.drawable.logoprincipal)
                .setContentIntent(clickNotificacion())
                .setContentText(detalle)
                .setContentInfo("nuevo")
                .setSound(defaultSoundUri);
        Random random = new Random();
        int idNotify = random.nextInt(8000);

        nm.notify(idNotify, builder.build());

    }

    public PendingIntent clickNotificacion(){
        Intent nf = new Intent(getApplicationContext(), lookAtProduct.class);
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this,0,nf,0);
    }
}
