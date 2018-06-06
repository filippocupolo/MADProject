package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class login extends AppCompatActivity implements
        View.OnClickListener {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private MyUser user;
    private CallbackManager mCallbackManager; //fb callback manager
    private static final String TAG = "login";
    private EditText email;
    private EditText pwd;
    private ProgressBar spinner;
    private RelativeLayout layout;
    private String deBugTag;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deBugTag = this.getClass().getName();

        mAuth = FirebaseAuth.getInstance();
        context = getApplicationContext();

        //set view
        setContentView(R.layout.login);

        //hide spinner
        spinner = findViewById(R.id.progressBarLogin);
        spinner.setVisibility(View.GONE);

        layout = findViewById(R.id.login_form_wrapper);

        //HIDE SOME ELEMENTS
        //findViewById(R.id.reset_login).setVisibility(View.GONE);
        //findViewById(R.id.reset_pwd_button).setVisibility(View.GONE);
        //findViewById(R.id.reset_pwd_help).setVisibility(View.GONE);

        /*-----GOOGLE------*/

        //button listener
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);

        //register button -> redirects to register activity
        findViewById(R.id.register_link).setOnClickListener(v->{Intent intent = new Intent(
                getApplicationContext(),
                register.class
        );
        intent.putExtra("caller", "login");
        startActivity(intent);});

        //email and password fields
        email = findViewById(R.id.login_email);
        pwd = findViewById(R.id.login_pwd);

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
                updateUIWithErrors("Error with Facebook Authentication");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("steps", "facebook3" + error.toString());
                updateUIWithErrors("Error with Facebook Authentication");
            }
        });
    }

    //forgot password listener
    public void reset_password(View v){
        //reset layout
        this.pwd.setVisibility(View.GONE);
        findViewById(R.id.facebook_sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.pwd_forgotten_link).setVisibility(View.GONE);
        findViewById(R.id.register_link).setVisibility(View.GONE);
        //findViewById(R.id.reset_login).setVisibility(View.VISIBLE);
        //findViewById(R.id.reset_pwd_button).setVisibility(View.VISIBLE);
        findViewById(R.id.login_button).setVisibility(View.GONE);
        //findViewById(R.id.reset_pwd_help).setVisibility(View.VISIBLE);
    }

    public void reset_login(View v){
        //reset layout
        this.pwd.setVisibility(View.VISIBLE);
        findViewById(R.id.facebook_sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.pwd_forgotten_link).setVisibility(View.VISIBLE);
        findViewById(R.id.register_link).setVisibility(View.VISIBLE);
        //findViewById(R.id.reset_login).setVisibility(View.GONE);
        //findViewById(R.id.reset_pwd_button).setVisibility(View.GONE);
        findViewById(R.id.login_button).setVisibility(View.VISIBLE);
        //findViewById(R.id.reset_pwd_help).setVisibility(View.GONE);
    }

    public void resetPasswordButton(View v){
        String email = this.email.getText().toString();

        if(TextUtils.isEmpty(email)){
            this.email.setError(getString(R.string.required));
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            reset_login(v);
                            updateUIWithErrors(getString(R.string.reset_email_sent));
                        }
                        else{
                            updateUIWithErrors(getString(R.string.reset_email_error));
                        }
                    }
                });
    }

    public void loginEmailUser(View v){

        if(TextUtils.isEmpty(this.email.getText().toString())){
            email.setError(getString(R.string.required));
            return;
        }

        if(TextUtils.isEmpty(this.pwd.getText().toString())){
            pwd.setError(getString(R.string.required));
            return;
        }

        Utilities.loading_and_blur_background(layout, spinner);

        String email = this.email.getText().toString();
        String password = this.pwd.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //check if email has been confirmed
                            if (user.isEmailVerified()) {
                                Log.d(TAG, "ok3");

                                updateUI(user);
                            }
                            else{
                                Log.d(TAG, "ok2");

                                updateUIWithErrors(getString(R.string.verify_email));
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            updateUIWithErrors(getString(R.string.unknown_user));
                        }
                    }
                });
    }

    //Check for existing Accounts on firebase, if the user already signed in
    @Override
    protected void onStart(){
        super.onStart();
        Log.d("steps", "onStart");


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            String provider = currentUser.getProviders().get(0);
            if(!provider.equals("facebook.com") && !provider.equals("google.com")) {
                //email user -> check if the mail has been verified
                if (currentUser.isEmailVerified()) {
                    updateUI(currentUser);
                } else {
                    updateUIWithErrors(getString(R.string.verify_email));
                }
            }
            else{
                updateUI(currentUser);
            }
        }
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
                updateUIWithErrors("Error with Google Authentication");

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
        Utilities.loading_and_blur_background(layout, spinner);
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
                            updateUIWithErrors("Error with Facebook Authentication");
                        }
                    }
                });
    }


    /*------ GOOGLE ---- */
    @Override
    public void onClick(View v) {
        Log.d("steps", "Google_onClick");
        Utilities.loading_and_blur_background(layout, spinner);

        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
        }
    }

    //intent to select a google account
    private void signIn() {
        Utilities.show_background(layout, spinner);
        Log.d("steps", "Google_signIn");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("steps", "firebaseAuthWithGoogle");
        Utilities.loading_and_blur_background(layout, spinner);
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
                            updateUIWithErrors("Error with Google Authentication");
                        }
                    }
                });
    }

    /*----- UPDATE UI --- */
    private void updateUI(@Nullable FirebaseUser account){

        if (account != null) {

            Intent intent;
            user = new MyUser(getApplicationContext());

            if(user.getUserID()!=null){
                if(user.getUserID().equals(account.getUid())){
                    //user already on the phone
                    intent = new Intent(
                            getApplicationContext(),
                            MainPageActivity.class
                    );
                    intent.putExtra("caller", "login");
                    Utilities.show_background(layout, spinner);
                    startActivity(intent);
                    finish();
                }else {
                    lookForUser(account.getUid(), account.getEmail());
                }
            }
            else{
                lookForUser(account.getUid(),account.getEmail());
            }
        }
        else{
            Log.d("steps", "Google_updateUi null");
        }

    }

    private void lookForUser(String userIdLogin, String email){

        //look if there is the user on Firebase
        Query dbQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByKey().equalTo(userIdLogin);
        Log.d(deBugTag,"creo listener e cerco "+ userIdLogin);
        dbQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(deBugTag,"onDataChange");

                if(dataSnapshot.exists()){

                    user.setUserID(userIdLogin);

                    for( DataSnapshot d : dataSnapshot.child(userIdLogin).getChildren()){

                        if(d.getKey().equals("name")){
                            user.setName(d.getValue(String.class));
                        }else if(d.getKey().equals("surname")){
                            user.setSurname(d.getValue(String.class));
                        }else if(d.getKey().equals("email")){
                            user.setEmail(d.getValue(String.class));
                        }else if(d.getKey().equals("city")){
                            user.setCity(d.getValue(String.class));
                        }else if(d.getKey().equals("town")){
                            user.setTown(d.getValue(String.class));
                        }else if(d.getKey().equals("biography")){
                            user.setBiography(d.getValue(String.class));
                        }else if(d.getKey().equals("image")){
                            if(d.getValue(Boolean.class) == true){
                                Log.d(deBugTag,"Download image");
                                user.setImageExist(true);
                                user.downloadImage();
                            }else{
                                Log.d(deBugTag,"imagedoesnotExist");
                                user.setImageExist(false);
                            }
                        }
                    }

                    Intent intent = new Intent(
                            getApplicationContext(),
                            MainPageActivity.class
                    );
                    intent.putExtra("caller", "login");
                    Utilities.show_background(layout, spinner);
                    startActivity(intent);
                    finish();

                }else{

                    //reset user
                    user.setUserID(userIdLogin);
                    user.setBiography(null);
                    user.setCity(null);
                    user.setEmail(email);
                    user.setSurname(null);
                    user.setName(null);
                    user.setImageExist(false);

                    //launch EditProfile
                    Intent intent = new Intent(
                            getApplicationContext(),
                            editProfile.class
                    );
                    intent.putExtra("caller", "login");
                    Utilities.show_background(layout, spinner);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d(deBugTag,"onCancelled");
                Utilities.show_background(layout, spinner);
                Toast.makeText(context,getString(R.string.connection_error),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithErrors(String text){
        Utilities.show_background(layout, spinner);
        //set error message on the login screen
        TextView errorMessage = findViewById(R.id.login_error);
        errorMessage.setText(text);
    }

    @Override
    public void onBackPressed() {

        //exit application
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return;
    }
}
