package com.drawin.panchylime.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.drawin.panchylime.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author Draco0503
 */
public class LoginActivity extends AppCompatActivity {
    // LAYOUT VIEWS
    private EditText _email;
    private EditText _passwd;
    private Button _submit;
    private Button _register;

    // FIREBASE AUTHENTICATION USER
    private FirebaseAuth _mAuth;

    /**
     * Is called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _mAuth = FirebaseAuth.getInstance();

        init();
    }

    /**
     * Initializes the views
     */
    private void init() {
        _register = findViewById(R.id.lg_register);
        _email = findViewById(R.id.email);
        _passwd = findViewById(R.id.passwd);
        _submit = findViewById(R.id.lg_submit);

        setListeners();
    }

    /**
     * When the activity starts tries to get the user logged
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = _mAuth.getCurrentUser();
        if (currentUser != null) {
            // Pasar los datos del usuario
            login(currentUser);
        }
    }

    /**
     * Sets the buttons listeners
     */
    private void setListeners() {
        _submit.setOnClickListener(view -> getDataToLogin());
        _register.setOnClickListener(view -> register());

    }

    /**
     * Tries to get the data from the views to log in
     */
    private void getDataToLogin() {
        try {
            String email = _email.getText().toString();
            if (email.equals("")) throw new Exception("Introduce el email");
            String passwd = _passwd.getText().toString();
            if (passwd.equals("")) throw new Exception("La contraseña no puede estar vacia");

            validateUser(email, passwd);
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Verifies the user's credentials
     * @param email user's email
     * @param passwd user's password
     */
    private void validateUser(String email, String passwd) {
        _mAuth.signInWithEmailAndPassword(email, passwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            login(_mAuth.getCurrentUser());
                        } else {
                            login(null);
                        }
                    }
                });
    }

    /**
     * Calls the Register Activity to sign in on the app
     */
    private void register() {
        Intent registerApp = new Intent(this, RegisterActivity.class);
        startActivity(registerApp);
    }

    /**
     * When the user is verified, goes to the Index Activity
     * @param mUser
     */
    private void login(FirebaseUser mUser) {
        if (mUser != null) {
            Intent indexApp = new Intent(this, IndexActivity.class);
            indexApp.putExtra("username", mUser.getDisplayName());   // PK from userTable
            startActivity(indexApp);
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.login_err), Toast.LENGTH_SHORT).show();
        }
    }
}