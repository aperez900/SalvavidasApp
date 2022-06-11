package com.desarollo.salvavidasapp.ui.direction;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.desarollo.salvavidasapp.ui.seller.seller2;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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

public class Maps extends FragmentActivity implements GoogleMap.OnMarkerDragListener, OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int REQUEST_LOCATION = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;

    EditText etDireccion;
    EditText etMunicipio;
    EditText etAlias;
    Button btnReg;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ListDirecciones d;
    Marker makerActual = null;
    String tipo = "usuarios";

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

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            tipo = extras.getString("tipo");
            if (tipo.equals("vendedores")) {
                myRef = database.getReference("vendedores");
            } else {
                myRef = database.getReference("usuarios");
            }

        } else {
            myRef = database.getReference("usuarios");
        }

        etDireccion = findViewById(R.id.et_direccion);
        etMunicipio = findViewById(R.id.et_municipio);
        etAlias = findViewById(R.id.et_alias);
        btnReg = findViewById(R.id.btn_reg);

        permisosGeoLocalizacion();


    }// Fin OnCreate

    private void permisosGeoLocalizacion() {
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permiso == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCamposVacios(etAlias, etDireccion, etMunicipio)) {
                    registrar(etAlias, etDireccion, etMunicipio);
                }
            }
        });
        //arrastrar el marcador
        googleMap.setOnMarkerDragListener(this);
    }// fin onMapReady

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //miUbicacion();
                } else {
                    Toast.makeText(getApplicationContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
                    Intent h = new Intent(getApplicationContext(), Home.class);
                    startActivity(h);
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //  Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        LocationManager locationManager = (LocationManager) Maps.this.getSystemService(Context.LOCATION_SERVICE);

        if(makerActual == null) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            actualizarUbicacion(location);
        }
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(makerActual == null){
                    actualizarUbicacion(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e("StatusChanged", "provider: " + provider + ", status: "
                + status + ", extras" + extras);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.e("ProviderEnabled", "provider Enabled: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.e("ProviderDisabled", "provider Disabled: " + provider);
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
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

    private void agregarMarcador(double lat, double lng) {
        LatLng miUbicacion = new LatLng(lat, lng);
        if(makerActual!= null){
            makerActual.remove();
        }
        makerActual = mMap.addMarker(new MarkerOptions()
                .position(miUbicacion)
                .title("Ubicación actual")
                .draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
        /*CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(miUbicacion)
                .zoom(14)
                .bearing(90)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 16));
    }


    private  void convertirDireccion(double lat, double lng){
        Geocoder geocoder =  new Geocoder(Maps.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat,lng,1);
            String direccion = addresses.get(0).getAddressLine(0);
            String municipio = addresses.get(0).getLocality();
            etDireccion.setText(direccion);
            etMunicipio.setText(municipio);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error :" + e,Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void registrar(TextView et_alias,  TextView et_direccion,TextView et_municipio ) {

        d = new ListDirecciones();
        d.nombreDireccion = et_alias.getText().toString();
        d.direccionUsuario = et_direccion.getText().toString();
        d.municipioDireccion = et_municipio.getText().toString();
        d.seleccion = "true";

        //guarda los datos del usuario
        myRef.child(currentUser.getUid()).child("mis direcciones").child(d.nombreDireccion).setValue(d)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (tipo.equals("vendedores")){
                            Toast.makeText(getApplicationContext(), "Dirección registrada correctamente", Toast.LENGTH_SHORT).show();
                            Intent h = new Intent(getApplicationContext(), seller2.class);
                            h.putExtra("direccion",et_direccion.getText().toString());
                            startActivity(h);
                        }else{
                            Toast.makeText(getApplicationContext(), "Dirección registrada correctamente", Toast.LENGTH_SHORT).show();
                            Intent h = new Intent(getApplicationContext(), Home.class);
                            startActivity(h);
                        }
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
            et_alias.setError("Digite un nombre para su dirección");
            campoLleno=false;
        }
        if(d.direccionUsuario.isEmpty()){
            et_direccion.setError("Digite una dirección");
            campoLleno=false;
        }

        if(d.municipioDireccion.isEmpty()){
            et_municipio.setError("Digite el municipio de la dirección");
            campoLleno=false;
        }

        return campoLleno;
    }


    //eventos para arrastrar el market
    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(getApplicationContext(), "Arrastre el icono a la dirección deseada", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (marker != null){
            Toast.makeText(getApplicationContext(), "Actualizando dirección", Toast.LENGTH_SHORT).show();
            double lat = marker.getPosition().latitude;
            double lng = marker.getPosition().longitude;
            marker.setTitle("Nueva ubicación");
            convertirDireccion(lat, lng);
        }
    }
}