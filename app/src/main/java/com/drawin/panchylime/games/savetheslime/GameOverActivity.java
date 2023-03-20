package com.drawin.panchylime.games.savetheslime;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.drawin.panchylime.R;
import com.drawin.panchylime.dialogs.GameDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jaekwin, Draco0503
 */
public class GameOverActivity extends AppCompatActivity {

    // LAYOUT VIEWS
    private TextView tvPoints;
    private TextView tvHighest;
    private ImageView ivNewHighest;

    // USERS DATA
    private String _username;
    private String _pet;
    private boolean _highest; // HighScore

    // SHAREDPREFERENCES REFERENCE
    private SharedPreferences sharedPreferences;

    /**
     * Is called when the activity is created
     * @param saveInstanceState
     */
    public void onCreate(@Nullable Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_ss_game_over);

        _highest = false;
        _username = getIntent().getExtras().getString("user");
        _pet = getIntent().getExtras().getString("pet");

        tvPoints = findViewById(R.id.tvPoints);
        tvHighest = findViewById(R.id.tvHighest);
        ivNewHighest = findViewById(R.id.ivNewHighest);
        int points = getIntent().getExtras().getInt("points");
        tvPoints.setText(String.valueOf(points));
        sharedPreferences = getSharedPreferences("my_pref", 0);
        int highest = sharedPreferences.getInt("highest", 0);
        // Saves the highscore into the shared-pref file
        if(points > highest){
            _highest = true;
            ivNewHighest.setVisibility((View.VISIBLE));
            highest = points;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highest", highest);
            editor.apply();
        }
        tvHighest.setText(String.valueOf(highest));
    }

    /**
     * Contains the logic of the game when finished
     * @param view the caller
     */
    public void finishActivity(View view){
        // When the user gets a new highscore he is rewarded with 5 coins
        if (_highest) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("users").child(_username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> postValues = new HashMap<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        postValues.put(snapshot.getKey(),snapshot.getValue());
                    }
                    long lastCoins = (long) postValues.get("_coins");
                    postValues.put("_coins", lastCoins + 5);
                    ref.child("users").child(_username).updateChildren(postValues);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        // Displays the dialog
        GameDialog gameDialog;
        int msg = R.string.game_1_normal;
        int color = Color.YELLOW;
        int coins = 0;
        if (_highest) {
            msg = R.string.game_1_record;
            color = Color.GREEN;
            coins = 5;
        }
        gameDialog = new GameDialog(this, new SaveSlimeActivity(), msg, color, coins, _username, _pet);
        gameDialog.show();
    }

}
