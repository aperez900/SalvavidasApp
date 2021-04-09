package com.desarollo.salvavidasapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.desarollo.salvavidasapp.ui.home.Home;
import com.desarollo.salvavidasapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.LoginManager;
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

import java.security.Provider;


public class MainActivity extends AppCompatActivity {

    EditText et_email, et_password;
    private FirebaseAuth mAuth;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    public static final int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_email = findViewById(R.id.et_mail);
        et_password = findViewById(R.id.et_password);

        mAuth = FirebaseAuth.getInstance();

        //Acesso por facebook
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setPermissions("email", "public_profile");
        //loginButton.setReadPermissions("email", "public_profile");

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

        // Ingresar por Google
        signInButton =  findViewById(R.id.signInButton);

        // Formato para el boton de login por google
        signInButton.setSize(SignInButton.SIZE_WIDE);

        //Dejar solo el icono de google
        //signInButton.setSize(SignInButton.SIZE_ICON_ONLY);

        signInButton.setColorScheme(SignInButton.COLOR_DARK);

        signInButton.setOnClickListener(new View.OnClickListener() {
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
    }

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
                Toast.makeText(MainActivity.this,"Error de inicio de sesión con google",Toast.LENGTH_SHORT).show();
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
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            startActivity(intent);
                            finish();
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

    //Al iniciar la activity de Login Valida si el usuario ya está logeado o no
    // y lo re direcciona al home
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
                    Toast.makeText(MainActivity.this, "Ya estas logueado. ", Toast.LENGTH_SHORT).show();
                    ingreso();
                }
                if (providerId.equals("google.com")){
                    Toast.makeText(MainActivity.this, "Ya estas logueado. ", Toast.LENGTH_SHORT).show();
                    ingreso();
                }
                if(currentUser.isEmailVerified()){
                    Toast.makeText(MainActivity.this, "Ya estas logueado. ", Toast.LENGTH_SHORT).show();
                    ingreso();
                }
            }
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String correo = et_email.getText().toString();
        String clave = et_password.getText().toString();

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
        Intent h = new Intent(getApplicationContext(), Home.class);
        startActivity(h);
        finish();
    }

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