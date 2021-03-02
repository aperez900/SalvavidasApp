package com.desarollo.salvavidasapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class Home extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ImageView imageView;
    private TextView id_nombre;
    private TextView id_correo;
    private GoogleApiClient googleApiClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        imageView = (ImageView) findViewById(R.id.imageView);
        id_nombre = (TextView) findViewById(R.id.id_nombre);
        id_correo = (TextView) findViewById(R.id.id_correo);


       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this::onConnectionFailed)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
        switch (item.getItemId()) {
            case R.id.action_settings:
                CerrarSesion();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void CerrarSesion() {

        if (FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Home.this, "Cerrando sesión.",
                    Toast.LENGTH_SHORT).show();
            Intent a = new Intent(this, MainActivity.class);
            startActivity(a);
        }
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
            Toast.makeText(Home.this, "Cerrando sesión facebook.",
                    Toast.LENGTH_SHORT).show();
            Intent a = new Intent(this, MainActivity.class);
            startActivity(a);
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Home.this);
        if (account != null) {
            logOut();
        }
    }

   public void logOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Toast.makeText(Home.this, "Cerrando sesión de gmail.",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Home.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    //Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

   /* @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();

            id_nombre.setText(account.getDisplayName());
            id_correo.setText(account.getEmail());
           // imageView.setText(account.getId());

            //Glide.with(this).load(account.getPhotoUrl()).into(photoImageView);

        } else {
            goLogInScreen();
        }
    }
*/
/*
    private void goLogInScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
*/
}