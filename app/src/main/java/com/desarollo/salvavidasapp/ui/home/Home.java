package com.desarollo.salvavidasapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Login.MainActivity;
import com.desarollo.salvavidasapp.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private GoogleApiClient googleApiClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

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
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_profile,
                R.id.nav_address, R.id.nav_seller)
                //.setDrawerLayout(drawer)
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
    }

    private void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //botón de cerrar sesión en el menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
                CerrarSesion();
        }
        return super.onOptionsItemSelected(item);
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
}