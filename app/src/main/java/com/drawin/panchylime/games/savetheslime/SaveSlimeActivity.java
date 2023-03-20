package com.drawin.panchylime.games.savetheslime;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.drawin.panchylime.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

/**
 * @author Jaekwin, Draco0503
 */
public class SaveSlimeActivity extends AppCompatActivity {

    private String _user;
    private String _pet;

    /**
     * Is called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saveslime);

        _user = getIntent().getExtras().getString("username");
        _pet = getIntent().getExtras().getString("pet");

        ImageView pet = findViewById(R.id.ivSSGame);
        // GETS THE IMAGE FROM THE FIREBASE STORAGE SERVICE
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        if (_pet != null && !_pet.equals("")) {
            mStorage.getReference().child(_pet).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(pet);
                }
            });
        } else {
            pet.setBackgroundResource(R.drawable.unkown_slime100);
        }
    }

    /**
     * Starts the game
     * @param view the caller
     */
    public void startGame(View view) {
        SaveSlimeView saveSlimeView = new SaveSlimeView(SaveSlimeActivity.this, _user, _pet);
        setContentView(saveSlimeView);
    }

}