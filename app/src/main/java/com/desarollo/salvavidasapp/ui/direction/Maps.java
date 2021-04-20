package com.desarollo.salvavidasapp.ui.direction;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Maps extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE =1;
    private static final int REQUEST_LOCATION = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    EditText et_direccion;
    EditText et_municipio;
    EditText et_alias;
    Button btn_reg;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ListDirecciones d;
    Marker makerActual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");

        et_direccion = findViewById(R.id.et_direccion);
        et_municipio = findViewById(R.id.et_municipio);
        et_alias = findViewById(R.id.et_alias);
        btn_reg = findViewById(R.id.btn_reg);

        if (ContextCompat.checkSelfPermission(Maps.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Maps.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);


        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        miUbicacion();

        //acciones del boton registrar
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCamposVacios(et_alias,et_direccion,et_municipio)) {
                    registrar(et_alias,et_direccion,et_municipio);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    miUbicacion();
                }  else {

                    Toast.makeText(getApplicationContext(),"Permiso denegado",Toast.LENGTH_SHORT).show();
                    Intent h = new Intent(getApplicationContext(), Home.class);
                    startActivity(h);
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }




    private void agregarMarcador(double lat, double lng) {

        LatLng coordenadas = new LatLng(lat, lng);

        if(makerActual!= null){
            makerActual.remove();
        }

        makerActual = mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Mi ubicacion")
                .draggable(true));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordenadas));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 16));

    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            //6.258329, -75.594995;
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            agregarMarcador(lat, lng);
            convertirDireccion(lat, lng);
        }

    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {

            actualizarUbicacion(location);

        }

    };

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(Maps.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        else{
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            actualizarUbicacion(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15000,0,locationListener);
        }

    }


    private  void convertirDireccion(double lat, double lng){

        Geocoder geocoder =  new Geocoder(Maps.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat,lng,1);
            String direccion = addresses.get(0).getAddressLine(0);
            String municipio = addresses.get(0).getLocality();
            et_direccion.setText(direccion);
            et_municipio.setText(municipio);

        } catch (IOException e) {

            Toast.makeText(getApplicationContext(),"Error al extraer direccion",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void registrar(TextView et_alias,  TextView et_direccion,TextView et_municipio ) {

        d = new ListDirecciones();
        d.nombreDireccion = et_alias.getText().toString();
        d.direccionUsuario = et_direccion.getText().toString();
        d.municipioDireccion = et_municipio.getText().toString();

        //guarda los datos del usuario
        myRef.child(currentUser.getUid()).child("mis direcciones").child(d.nombreDireccion).setValue(d)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Dirección registrada correctamente", Toast.LENGTH_SHORT).show();
                        Intent h = new Intent(getApplicationContext(), Home.class);
                        startActivity(h);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error registrando la dirección", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public boolean validarCamposVacios(TextView et_alias,  TextView et_direccion,TextView et_municipio){

        d = new ListDirecciones();
        d.nombreDireccion = et_alias.getText().toString();
        d.direccionUsuario = et_direccion.getText().toString();
        d.municipioDireccion = et_municipio.getText().toString();

        boolean campoLleno = true;

        if(d.nombreDireccion.isEmpty()){
            et_alias.setError("Digite una dirección");
            campoLleno=false;
        }
        if(d.direccionUsuario.isEmpty()){
            et_direccion.setError("Digite una dirección");
            campoLleno=false;
        }

        if(d.municipioDireccion.isEmpty()){
            et_municipio.setError("Digite una dirección");
            campoLleno=false;
        }

        return campoLleno;
    }


}