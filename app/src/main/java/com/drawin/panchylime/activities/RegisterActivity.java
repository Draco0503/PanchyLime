package com.drawin.panchylime.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.drawin.panchylime.R;
import com.drawin.panchylime.models.PetBag;
import com.drawin.panchylime.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Draco0503
 */
public class RegisterActivity extends AppCompatActivity {

    // LAYOUT VIEWS
    private EditText _username;
    private EditText _email;
    private EditText _passwd;
    private Button _submit;
    private Spinner _firstPet;

    // FIREBASE SERVICES
    private FirebaseAuth _mAuth;
    private FirebaseDatabase _database;

    /**
     * Is called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        _mAuth = FirebaseAuth.getInstance();

        _database = FirebaseDatabase.getInstance("https://panchylime-default-rtdb.europe-west1.firebasedatabase.app");
        init();
    }

    /**
     * Initializes the views
     */
    private void init() {
        _username = findViewById(R.id.username);
        _email = findViewById(R.id.email);
        _passwd = findViewById(R.id.passwd);
        _submit = findViewById(R.id.rg_submit);
        _firstPet = findViewById(R.id.firstPet_sp);

        setListeners();
    }

    /**
     * Set the button listener
     */
    private void setListeners() {
        _submit.setOnClickListener(view -> register());
    }

    /**
     * Validates the user's credentials and creates the user on the auth service and on the database
     */
    private void register() {
        try {
            String username = _username.getText().toString();
            if (username.equals("")) throw new Exception(getString(R.string.user_err));
            String email = _email.getText().toString();
            if (email.equals("")) throw new Exception(getString(R.string.email_err));
            String passwd = _passwd.getText().toString();
            if (passwd.equals("")) throw new Exception(getString(R.string.passwd_err));

            _mAuth.createUserWithEmailAndPassword(email, passwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = _mAuth.getCurrentUser();
                                // Sets auth user an username, an error may occur
                                UserProfileChangeRequest update = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                                assert currentUser != null;
                                currentUser.updateProfile(update);

                                List<PetBag> bag = new ArrayList<>();
                                bag.add(getFirstPet(_firstPet.getSelectedItemPosition()));

                                User user = new User(username, email, bag);

                                registerOnDB(user);
                                login(currentUser);
                            } else {
                                login(null);
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets the first pet of the user
     * @param pos the spinner position
     * @return first pet of the user
     */
    private PetBag getFirstPet(int pos) {
        PetBag firstPet = new PetBag("slime_verde");
        if (pos == 1) {
            firstPet = new PetBag("slime_rosa");
        } else if (pos == 2) {
            firstPet = new PetBag("slime_azul");
        }
        firstPet.set_selected(true);
        return firstPet;
    }

    /**
     * Register users default data on the database
     * @param user user's information
     */
    private void registerOnDB(User user) {
        DatabaseReference usersRef = _database.getReference();

        usersRef.child("users").child(user.get_username()).setValue(user);
    }

    /**
     * When all is done, the user will not need to log in again
     * @param mUser
     */
    private void login(FirebaseUser mUser) {
        if (mUser != null) {
            // Toast.makeText(LoginActivity.this, "Login Succeed", Toast.LENGTH_SHORT).show();
            Intent indexApp = new Intent(this, IndexActivity.class);
            indexApp.putExtra("username", mUser.getDisplayName());   // PK from userTable
            startActivity(indexApp);
        } else {
            Toast.makeText(RegisterActivity.this, getString(R.string.login_err), Toast.LENGTH_SHORT).show();
        }
    }
}