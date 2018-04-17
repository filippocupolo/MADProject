package com.example.andrea.lab11;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class login extends AppCompatActivity implements
        View.OnClickListener {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private MyUser user;
    private CallbackManager mCallbackManager; //fb callback manager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        //set view
        setContentView(R.layout.login);

        /*-----GOOGLE------*/

        //button listener
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("802835323223-n9v6qn7kim6deeei1c6qtgg4cfomju6f.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /*-----FACEBOOK------*/

        //Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.facebook_sign_in_button);
        loginButton.setReadPermissions("email", "public_profile");

        //facebook button callbacks manager
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("steps", "facebook1");
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("steps", "facebook3");
                updateUIWithErrors();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("steps", "facebook3" + error.toString());
                updateUIWithErrors();
            }
        });
    }

    //Check for existing Google/Facebook Sign In account, if the user is already signed in
    //every time the app appears  on screen
    @Override
    protected void onStart(){
        super.onStart();
        Log.d("steps", "onStart");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    //Result returned from launching either the Intent from GoogleSignInApi.getSignInIntent(...);
    //or after having clicked the facebook button
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed
                updateUIWithErrors();
            }
        }
        else if(FacebookSdk.isFacebookRequestCode(requestCode)){
            /*----FACEBOOK-----*/
            //passing control to the fb callback handler
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*-----FACEBOOK------*/
    //called when the access token is issued correctly
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("steps", "handleFacebookAccessToken:" + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d("steps", "facebook_ok");

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            //Sign in fails
                            Log.d("steps", "facebook_fails");
                            updateUIWithErrors();
                        }
                    }
                });
    }


    /*------ GOOGLE ---- */
    @Override
    public void onClick(View v) {
        Log.d("steps", "Google_onClick");
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
        }
    }

    //intent to select a google account
    private void signIn() {
        Log.d("steps", "Google_signIn");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("steps", "firebaseAuthWithGoogle");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("steps", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("warn", "signInWithCredential:failure", task.getException());
                            updateUIWithErrors();
                        }
                    }
                });
    }


    /*----- UPDATE UI --- */
    private void updateUI(@Nullable FirebaseUser account){
        if (account != null) {
            Log.d("steps", account.getUid());
            user = new MyUser(getApplicationContext());
            user.setEmail(account.getEmail());
            //user.setName(account.getDisplayName());
            user.commit();


            Intent intent = new Intent(
                    getApplicationContext(),
                    editProfile.class
            );
            intent.putExtra("caller", "login");
            startActivity(intent);
        }
        else{
            Log.d("steps", "Google_updateUi null");
        }
    }

    private void updateUIWithErrors(){
        //set error message on the login screen
        TextView errorMessage = findViewById(R.id.login_error);
        errorMessage.setText("Error with Google Authentication");
    }
}
