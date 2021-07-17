package com.desarollo.salvavidasapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Login.MainActivity;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.seller.requested_products;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private GoogleApiClient googleApiClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef, myRefVendedores;

    MenuItem menuItemCarrito, menuItemProductosSolicitados;
    TextView badgeCounter, badgeCounterSeller;
    ImageView imgCarrito, imgProductos;
    int nroProductosCarrito = 0;
    int nroProductosSolicitados = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        myRefVendedores = database.getReference("vendedores");

        //Botón flotante
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_statistics, R.id.nav_settings, R.id.nav_profile,
                R.id.nav_address, R.id.subMenuProfile, R.id.nav_sales, R.id.nav_purchases)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this::onConnectionFailed)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        actualizarDatosPerfil();

    } // fin OnCreate

    private void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.home, menu);

        menuItemCarrito = menu.findItem(R.id.shop);

        if(nroProductosCarrito == 0){
            menuItemCarrito.setActionView(null);
        }

        //Ver nroProductosCarrito
        myRef.child(currentUser.getUid()).child("carrito_compras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    nroProductosCarrito=0;
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        nroProductosCarrito = nroProductosCarrito + 1;
                    }
                    menuItemCarrito.setActionView(R.layout.notification_badge);
                    View view = menuItemCarrito.getActionView();
                    badgeCounter = view.findViewById(R.id.tv_nro_productos_carrito);
                    imgCarrito = view.findViewById(R.id.img_carrito_compras);
                    badgeCounter.setText(String.valueOf(nroProductosCarrito));
                    //Toast.makeText(getApplicationContext(), "Hay " + nroProductosCarrito + " productos en el carrito" , Toast.LENGTH_LONG).show();
                    imgCarrito.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            irAlCarrito();
                        }
                    });

                    badgeCounter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            irAlCarrito();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error contando los productos del carrito", Toast.LENGTH_SHORT).show();
            }
        });

        menuItemProductosSolicitados = menu.findItem(R.id.notification_seller);

        if(nroProductosSolicitados == 0){
            menuItemProductosSolicitados.setActionView(null);
        }

        //Ver nroProductosSolicitados
        myRefVendedores.child(currentUser.getUid()).child("productos_en_tramite").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    nroProductosSolicitados=0;
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        for(DataSnapshot objsnapshot2 : objsnapshot.getChildren()) { //recorre los productos
                            nroProductosSolicitados = nroProductosSolicitados + 1;
                        }
                    }
                    menuItemProductosSolicitados.setActionView(R.layout.notification_badge_seller);
                    View view = menuItemProductosSolicitados.getActionView();
                    badgeCounterSeller = view.findViewById(R.id.tv_nro_productos_solicitados);
                    imgProductos = view.findViewById(R.id.img_notificacion_productos);
                    badgeCounterSeller.setText(String.valueOf(nroProductosSolicitados));
                    //Toast.makeText(getApplicationContext(), "Hay " + nroProductosCarrito + " productos en el carrito" , Toast.LENGTH_LONG).show();
                    imgProductos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            irAProductosSolicitados();
                        }
                    });

                    badgeCounterSeller.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            irAProductosSolicitados();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error contando los productos solicitados", Toast.LENGTH_SHORT).show();
            }
        });

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //botón de cerrar sesión y carrito compras en el menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
                CerrarSesion();
        }
        if (item.getItemId() == R.id.shop) {
            irAlCarrito();
        }

        if (item.getItemId() == R.id.notification_seller) {
            irAProductosSolicitados();
        }

        return super.onOptionsItemSelected(item);
    }

    /*Si se pulsa el botón atrás dentro del home o los fragment lo llevará siempre al home,
    solución temporal al tema de permitir el ingreso al módulo "Mis Ventas" sin estar
    autorizado*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK){
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
     * @autor: Edison Cardona
     * @since: 08/04/2021
     * @Version: 02
     * Método para cerrar la sesión del usuario logeuado
     * */
    public void CerrarSesion() {
        if(currentUser != null){
            for (UserInfo profile : currentUser.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();
                //Toast.makeText(MainActivity.this, "Proveedor " + providerId, Toast.LENGTH_LONG).show();
                if (providerId.equals("facebook.com")){
                    LoginManager.getInstance().logOut();
                    Toast.makeText(Home.this, "Cerrando sesión con facebook.",
                            Toast.LENGTH_SHORT).show();
                    Intent a = new Intent(this, MainActivity.class);
                    startActivity(a);
                    finish();
                }
                if (providerId.equals("google.com")){
                    logOut();
                }
                FirebaseAuth.getInstance().signOut();
                Intent a = new Intent(this, MainActivity.class);
                startActivity(a);
                finish();
            }
        }

        /*
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Home.this, "Cerrando sesión.",
                    Toast.LENGTH_SHORT).show();
            Intent a = new Intent(this, MainActivity.class);
            startActivity(a);
        }

        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
            Intent a = new Intent(this, MainActivity.class);
            startActivity(a);
        }
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Home.this);
        if (account != null) {
            logOut();
        }
        */
    }

   public void logOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Toast.makeText(Home.this, "Cerrando sesión con Google.",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Home.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                    Toast.makeText(Home.this, "Error cerrando sesión con Google.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
     * @autor: Edison Cardona
     * @since: 04/03/2021
     * @Version: 01
     * Método para actualizar los datos del usuario logeado en el
     * navheader del menú
     * */
    public void actualizarDatosPerfil(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.id_nombre_perfil);
        TextView navUserMail = headerView.findViewById(R.id.id_correo_perfil);
        //ImageView navUserPhoto = headerView.findViewById(R.id.id_foto_perfil);

        //navUserMail.setText(currentUser.getEmail());
        //navUserName.setText(currentUser.getDisplayName());
        //Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                navUserName.setText(name);
                String email = profile.getEmail();
                navUserMail.setText(email);
            }
        }
    }

    public int verNroProductosCarritoCompras(){
        //Toast.makeText(getApplicationContext(), "Entró" , Toast.LENGTH_LONG).show();
        myRef.child(currentUser.getUid()).child("carrito_compras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        nroProductosCarrito = nroProductosCarrito + 1;
                    }
                    Toast.makeText(getApplicationContext(), "Hay " + nroProductosCarrito + " productos en el carrito" , Toast.LENGTH_LONG).show();
                    //badgeCounter.setText(nroProductosCarrito);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error contando los productos del carrito", Toast.LENGTH_SHORT).show();
            }
        });
        return nroProductosCarrito;
    }

    public void irAlCarrito(){
        Intent intent = new Intent(this, shoppingCart.class);
        startActivity(intent);
        finish();
    }

    public void irAProductosSolicitados(){
        Intent intent = new Intent(this, requested_products.class);
        startActivity(intent);
        finish();
    }
}