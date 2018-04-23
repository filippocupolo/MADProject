package com.example.andrea.lab11;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

//TODO xml login - register text
//TODO MAIL field blocked in edit profile
public class register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText pwd;
    private EditText confirm_pwd;
    private static final String TAG = "registrazione";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.register);

        //form fields
        email = findViewById(R.id.register_email);
        pwd = findViewById(R.id.register_pwd);
        confirm_pwd = findViewById(R.id.register_confirm_pwd);

    }

    public void loginOnClick(View v){
        Intent intent = new Intent(
                getApplicationContext(),
                login.class
        );
        intent.putExtra("caller", "register");
        startActivity(intent);
        finish();
    }

    public void registerOnClick(View v){
        createAccount(email.getText().toString(), pwd.getText().toString(), confirm_pwd.getText().toString());
    }

    private void createAccount(String email, String password, String confirm_password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm(email, password, confirm_password)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            //send email verification
                            sendEmailVerification();

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            //failed
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthUserCollisionException e) {
                                updateUIWithErrors(getString(R.string.already_registered));
                            } catch(Exception e) {
                                Log.w(TAG, "createUserWithEmail:failure" + e.getMessage());
                                updateUIWithErrors(getString(R.string.error_account_creation));
                            }
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        Log.d(TAG, "registration_ok");
        Intent intent = new Intent(
                getApplicationContext(),
                login.class
        );
        intent.putExtra("info", "register_successful");
        startActivity(intent);
        finish();
    }

    private void updateUIWithErrors(String text){
        //set error message on the login screen
        TextView errorMessage = findViewById(R.id.register_error);
        errorMessage.setText(text);
    }

    private boolean validateForm(String email, String password, String confirm_password){
        boolean valid = true;

        //empty email
        if(TextUtils.isEmpty(email)){
            valid = false;
            this.email.setError(getString(R.string.required));
        }

        //empty pwd
        if(TextUtils.isEmpty(password)){
            valid = false;
            this.pwd.setError(getString(R.string.required));
        }

        //empty pwd_confirm
        if(TextUtils.isEmpty(confirm_password)){
            valid = false;
            this.confirm_pwd.setError(getString(R.string.required));
        }

        //pwds are different
        if(!password.equals(confirm_password)) {
            updateUIWithErrors(getString(R.string.pwd_unmatch));
            valid = false;
        }
        else if(password.length() < 6){
            //pwd too short
            updateUIWithErrors(getString(R.string.pwd_short));
            valid = false;
        }


        return valid;

    }

    private void sendEmailVerification() {
        //findViewById(R.id.verify_email_button).setEnabled(false);

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Re-enable button
                        //findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Log.d(TAG, "mail inviata");
                        } else {
                            Log.d(TAG, "mail non inviata");
                        }
                    }
                });
    }
}
