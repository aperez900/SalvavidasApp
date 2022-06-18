package com.desarollo.salvavidasapp.Login;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.desarollo.salvavidasapp.ui.direction.Maps;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.desarollo.salvavidasapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@Keep
public class MainActivity extends AppCompatActivity {

    private EditText et_email, et_password;
    private Button fb, google;
    private FirebaseAuth mAuth;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    public static final int RC_SIGN_IN = 0;

    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRefUsuarios;

    private ProgressDialog cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefUsuarios = database.getReference("usuarios");

        cargando = new ProgressDialog(this);
        
        fb = (Button) findViewById(R.id.fb);
        google = (Button) findViewById(R.id.google);

        et_email = findViewById(R.id.et_mail);
        et_password = findViewById(R.id.et_password);

        //mostrando usuarios y clave para que no tenga que volver a digitarlos
        SharedPreferences preferences = getSharedPreferences("datosLogin", Context.MODE_PRIVATE);
        et_email.setText(preferences.getString("email",""));
        et_password.setText(preferences.getString("password", ""));

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        //Acceso por facebook
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setPermissions("email", "public_profile");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("TAG", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Login cancelado",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), "Error de inicio de sesión",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        fb.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  loginButton.callOnClick();
              }
          });

        // Ingresar por Google
        signInButton =  findViewById(R.id.signInButton);

        // Formato para el boton de login por google
        signInButton.setSize(SignInButton.SIZE_WIDE);

        //Dejar solo el icono de google
        //signInButton.setSize(SignInButton.SIZE_ICON_ONLY);

        signInButton.setColorScheme(SignInButton.COLOR_DARK);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }// fin OnCreate

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> Task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = Task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch(ApiException e){
                Toast.makeText(MainActivity.this,"Error de inicio de sesión con google" + e,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            ingreso();
                            Toast.makeText(MainActivity.this,"Ingreso correcto con Google",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"Error inciando sesión con Google",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            ingreso();
                            Toast.makeText(getApplicationContext(), "Ingreso correcto con facebook",Toast.LENGTH_SHORT).show();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Error de autenticación",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        // ...
                    }
                });
    }

    /*Al iniciar la activity de Login Valida si el usuario ya está logeado o no
     y lo re direcciona al home*/
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        if(currentUser != null){
            for (UserInfo profile : currentUser.getProviderData()) {
                    // Id of the provider (ex: google.com)
                    String providerId = profile.getProviderId();
                    //Toast.makeText(MainActivity.this, "Proveedor " + providerId, Toast.LENGTH_LONG).show();
                    if (providerId.equals("facebook.com")){
                        ingreso();
                    }
                    if (providerId.equals("google.com")){
                        ingreso();
                    }
                    if(currentUser.isEmailVerified()){
                        ingreso();
                    }
                }
               // Toast.makeText(MainActivity.this, "Ya estas logueado. ", Toast.LENGTH_SHORT).show();
            }
    }

    public void Registro(View view) {
        Intent i = new Intent(getApplicationContext(), Registro.class);
        startActivity(i);
        finish();
    }

    public void forget(View view) {
        Intent i = new Intent(getApplicationContext(), Forget.class);
        startActivity(i);
        finish();
    }

    //Acceso por firebase con email and clave
    public void Iniciar(View view) {

        String correo = et_email.getText().toString();
        String clave = et_password.getText().toString();

        //Guardando usuario y clave para mostrarlos al iniciar la sesión
        SharedPreferences preferencias = getSharedPreferences("datosLogin",Context.MODE_PRIVATE);
        SharedPreferences.Editor objEditor = preferencias.edit();
        objEditor.putString("email", correo);
        objEditor.putString("password", clave);
        objEditor.apply();

        if(validarCamposVacios()) {
            signInWithEmailAndPassword(correo, clave);
        }
    }

    public void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).
        addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete (@NonNull Task< AuthResult > task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    //updateUI(user);
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            ingreso();
                        }else{
                            Toast.makeText(getApplicationContext(), "Debe verificar el email",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Error de autenticación " + task.getException().toString(),
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                    // ...
                }
                // ...
            }
        });
    }

    private void ingreso() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        myRefUsuarios.child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()){
                    myRefUsuarios.child(currentUser.getUid()).child("mis_direcciones").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.getResult().exists()){
                                Intent h = new Intent(MainActivity.this, Home.class);
                                startActivity(h);
                                finish();
                            }else{
                                Intent h = new Intent(MainActivity.this, Maps.class);
                                startActivity(h);
                                finish();
                            }
                        }
                    });
                }else{
                    if (currentUser != null) {
                        Intent intent = new Intent(MainActivity.this , profile_initial.class);
                        for (UserInfo profile : currentUser.getProviderData()) {
                            //String name = profile.getDisplayName();
                            String email = profile.getEmail();
                            intent.putExtra("email", email );
                            //Uri photoUrl = profile.getPhotoUrl();
                        }
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        /*
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        myRefUsuarios.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Intent h = new Intent(MainActivity.this, Home.class);
                            startActivity(h);
                            finish();
                        }
                        else{
                            if (currentUser != null) {
                                Intent intent = new Intent(MainActivity.this , profile_initial.class);
                                for (UserInfo profile : currentUser.getProviderData()) {
                                    //String name = profile.getDisplayName();
                                    String email = profile.getEmail();
                                    intent.putExtra("email", email );
                                    //Uri photoUrl = profile.getPhotoUrl();
                                }
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando los datos del usuario. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

         */
    }

    /*
    private void datosIniciales() {

        Intent h = new Intent(getApplicationContext(), Home.class);
        startActivity(h);
        finish();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        myRef.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            Intent h = new Intent(getApplicationContext(), Home.class);
                            startActivity(h);
                            finish();

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando los datos del usuario. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
*/

    public boolean validarCamposVacios(){
        boolean campoLleno = true;

        String correo= et_email.getText().toString();
        String clave = et_password.getText().toString();

        if(correo.isEmpty()){
            et_email.setError("Debe diligenciar un correo");
            campoLleno=false;
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                et_email.setError("Debe diligenciar un correo válido");
                campoLleno=false;
            }
        }
        if(clave.isEmpty()){
            et_password.setError("Debe diligenciar una clave");
            campoLleno=false;
        }else if(clave.length() < 6){
            et_password.setError("La clave debe ser de al menos 6 digitos");
            campoLleno=false;
        }
        return campoLleno;
    }
}