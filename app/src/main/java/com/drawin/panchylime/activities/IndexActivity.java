package com.drawin.panchylime.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.drawin.panchylime.R;
import com.drawin.panchylime.fragments.BagFragment;
import com.drawin.panchylime.fragments.GachaFragment;
import com.drawin.panchylime.fragments.IndexFragment;
import com.drawin.panchylime.fragments.PlayFragment;
import com.drawin.panchylime.fragments.UserFragment;
import com.drawin.panchylime.models.Pet;
import com.drawin.panchylime.models.PetBag;
import com.drawin.panchylime.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Draco0503
 */
public class IndexActivity extends AppCompatActivity implements Serializable {
    // MUSIC OF THE GAME
    public static MediaPlayer BGM_PANCHYLIME;
    public static MediaPlayer BGM_GAMES_PANCHYLIME;
    private int mediaPaused; // To save the exact second when pause

    // NAVBAR ITEMS
    private final int[] NAVBAR_IDs = new int[] { R.id.bag, R.id.playIcon, R.id.index, R.id.gacha, R.id.user };

    // SCROLL ANIMATION VALUES
    private final int MIN_DISTANCE = 150;
    private final int  LEFT_ANIMATION_START = R.anim.enter_from_left;
    private final int LEFT_ANIMATION_EXIT = R.anim.exit_to_right;
    private final int RIGHT_ANIMATION_START = R.anim.enter_from_right;
    private final int RIGHT_ANIMATION_EXIT = R.anim.exit_to_left;
    private int _navbarPos;
    private float x1, x2;

    // INFO NEEDED FOR THE GAME
    private String _username;
    private String _indexPetURL;
    private int _coins_value;
    private User _user;
    private boolean _firstTime;

    // Key imgURL, Value usersPets
    private HashMap<String, PetBag> _usersBag;
    // Key petName, Value allPets
    private HashMap<String, Pet> _pets;

    private ConstraintLayout _background;
    private BottomNavigationView _navbar;
    private TextView _coins;

    // THE DIFFERENT "SCREENS" OF THE GAME SHOWN ON THE INDEXACTIVITY
    private FragmentManager _manager;
    private IndexFragment _index;
    private PlayFragment _play;
    private GachaFragment _gacha;
    private BagFragment _bag;
    private UserFragment _userF;

    // FIREBASE DATABASE
    private DatabaseReference _database;

    /**
     * Is called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        init();
    }

    /**
     * Event when the user touches the screen
     * @param event
     * @return false always
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int dir = 0;
        // SCROLL ANIMATION
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();

                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (x1 < x2) {
                        // SCROLL TO THE LEFT
                        _navbarPos--;
                    } else if (x1 > x2) {
                        // SCROLL TO THE RIGHT
                        _navbarPos++;
                        dir = 1;
                    }
                    // MANAGE THE NAVBAR
                    if (_navbarPos < 0) _navbarPos = (NAVBAR_IDs.length - 1);
                    else if (_navbarPos > (NAVBAR_IDs.length - 1)) _navbarPos = 0;
                    _navbar.getMenu().findItem(NAVBAR_IDs[_navbarPos]).setChecked(true);
                    // CHANGING THE FRAGMENT
                    callFragment(NAVBAR_IDs[_navbarPos], dir);
                }
                break;
        }
        return false;
    }

    /**
     * Initialize all the variables needed
     */
    private void init() {
        BGM_PANCHYLIME = MediaPlayer.create(IndexActivity.this, R.raw.panchylime_bgm);
        BGM_PANCHYLIME.start();
        BGM_PANCHYLIME.setLooping(true);

        BGM_GAMES_PANCHYLIME = MediaPlayer.create(IndexActivity.this, R.raw.games_bgm);
        BGM_GAMES_PANCHYLIME.setLooping(true);

        _navbarPos = 2;
        _username = getIntent().getStringExtra("username");
        _user = new User();
        _usersBag = new HashMap<>();
        _pets = new HashMap<>();
        _indexPetURL = "";
        _coins_value = 0;
        _firstTime = true;
        _coins = findViewById(R.id.coins);
        _manager = getSupportFragmentManager();

        dbParamsInit();

        _background = findViewById(R.id.background);

        _navbar = findViewById(R.id.menu_navbar);
        _navbar.getMenu().findItem(R.id.index).setChecked(true);
        setNavbarItemsListeners();

    }

    /**
     * Initialize the params from the database
     */
    private void dbParamsInit() {
        _database = FirebaseDatabase.getInstance().getReference();

        dbPetsInit();
        dbUserInit();

    }

    /**
     * Loads all the "pets" of the game, this is for the image to be loaded
     * (Should be improved)
     */
    private void dbPetsInit() {
        _database.child("pets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Pet pet = ds.getValue(Pet.class);
                    assert pet != null;
                    _pets.put(pet.get_petName(), pet);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("dbPetsInit", getString(R.string.load_error));
                Toast.makeText(IndexActivity.this, R.string.load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Loads all the user data
     */
    private void dbUserInit() {
        _database.child("users").child(_username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                _user = snapshot.getValue(User.class);
                assert _user != null;
                _coins_value = _user.get_coins();
                _coins.setText(String.valueOf(_coins_value));
                updateSelectedPet();
                // FOR THE APP TO LOAD AGAIN WHEN THE DATA IS REALLY LOADED
                if (_firstTime) {
                    callIndexFragment(LEFT_ANIMATION_START, LEFT_ANIMATION_EXIT);
                    _firstTime = false;
                    updatePet();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("dbUserInit", getString(R.string.load_error));
                Toast.makeText(IndexActivity.this, R.string.load_error, Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * Loads the selected pet, this is the pet shown at the index fragment
     */
    private void updateSelectedPet() {
        ArrayList<PetBag> pets = (ArrayList<PetBag>) _user.get_bag();

        for (int i = 0; i < pets.size(); i++) {
            PetBag single_pet = pets.get(i);
            _usersBag.put(Objects.requireNonNull(_pets.get(single_pet.get_petName())).get_image(), single_pet);
        }


        if (_user.getSelectedPet() == null) {
            try {
                throw new Exception("NO PET SELECTED");
            } catch (Exception e) {
                Log.e("updateSelectedPet", e.getMessage());
            }
        }
        else _indexPetURL = Objects.requireNonNull(_pets.get(_user.getSelectedPet().get_petName())).get_image();

    }

    /**
     * Updates the selected pet values when app starts, those values cannot be less than 0
     */
    private void updatePet() {
        int hunger = _user.getSelectedPet().get_hunger() - 10;
        int cleanness = _user.getSelectedPet().get_cleanness() - 10;
        int happiness = _user.getSelectedPet().get_happiness() - 20;
        if (hunger < 0) {
            hunger = 0;
        }
        if (cleanness < 0) {
            cleanness = 0;
        }
        if (happiness < 0) {
            happiness = 0;
        }
        _user.getSelectedPet().set_hunger(hunger);
        _user.getSelectedPet().set_cleanness(cleanness);
        _user.getSelectedPet().set_happiness(happiness);
        updateDB();
    }

    /**
     * Updates the user's information on the database
     */
    private void updateDB() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(_user.get_username()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> postValues = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    postValues.put(snapshot.getKey(),snapshot.getValue());
                }
                postValues.put("_bag", _user.get_bag());
                ref.child("users").child(_user.get_username()).updateChildren(postValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("updateDB", error.getMessage());
            }
        });
    }

    /**
     * Sets the listener of the navbar
     */
    private void setNavbarItemsListeners() {
        _navbar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                dbUserInit();
                int id = item.getItemId();
                callFragment(id, 0);
                return true;
            }
        });
    }

    /**
     * Calls the fragment to be shown
     * @param id the navbar id item
     * @param direction 1 for RIGHT, 0 for LEFT
     */
    @SuppressLint("NonConstantResourceId")
    private void callFragment(int id, int direction) {
        int start = LEFT_ANIMATION_START;
        int exit = LEFT_ANIMATION_EXIT;
        if (direction == 1) {
            start = RIGHT_ANIMATION_START;
            exit = RIGHT_ANIMATION_EXIT;
        }

        switch (id) {
            case R.id.index:
                callIndexFragment(start, exit);
                break;
            case R.id.playIcon:
                callPlayFragment(start, exit);
                break;
            case R.id.gacha:
                callGachaFragment(start, exit);
                break;
            case R.id.bag:
                callBagFragment(start, exit);
                break;
            case R.id.user:
                callUserFragment(start, exit);
                break;
        }
    }

    /**
     * Calls the index fragment
     * @param start animation start
     * @param exit animation end
     */
    private void callIndexFragment(int start, int exit) {
        _background.setBackgroundResource(R.drawable.main_index);

        _index = IndexFragment.newInstance(_indexPetURL, _user);

        _manager.beginTransaction()
                .setCustomAnimations(start, exit)
                .replace(R.id.fragContView, _index, null)
                .setReorderingAllowed(true)
                .addToBackStack("index")
                .commit();
    }

    /**
     * Calls the gacha fragment
     * @param start animation start
     * @param exit animation end
     */
    private void callGachaFragment(int start, int exit) {
        _background.setBackgroundResource(R.drawable.main_bg2);

        _gacha = GachaFragment.newInstance(_user, _pets);

        _manager.beginTransaction()
                .setCustomAnimations(start, exit)
                .replace(R.id.fragContView, _gacha, null)
                .setReorderingAllowed(true)
                .addToBackStack("gacha")
                .commit();
    }

    /**
     * Calls the bag fragment
     * @param start animation start
     * @param exit animation end
     */
    private void callBagFragment(int start, int exit) {
        _background.setBackgroundResource(R.drawable.main_bg3);

        _bag = BagFragment.newInstance(_usersBag, _username);

        _manager.beginTransaction()
                .setCustomAnimations(start, exit)
                .replace(R.id.fragContView, _bag, null)
                .setReorderingAllowed(true)
                .addToBackStack("bag")
                .commit();
    }

    /**
     * Calls the user fragment
     * @param start animation start
     * @param exit animation end
     */
    private void callUserFragment(int start, int exit) {
        _background.setBackgroundResource(R.drawable.main_bg3);

        _userF = UserFragment.newInstance(_user);

        _manager.beginTransaction()
                .setCustomAnimations(start, exit)
                .replace(R.id.fragContView, _userF, null)
                .setReorderingAllowed(true)
                .addToBackStack("user")
                .commit();
    }

    /**
     * Calls the play fragment
     * @param start animation start
     * @param exit animation end
     */
    private void callPlayFragment(int start, int exit) {
        _background.setBackgroundResource(R.drawable.main_bg2);

        _play = PlayFragment.newInstance(_user, _indexPetURL);

        _manager.beginTransaction()
                .setCustomAnimations(start, exit)
                .replace(R.id.fragContView, _play, null)
                .setReorderingAllowed(true)
                .addToBackStack("play")
                .commit();
    }

    /**
     * When the activity pauses
     */
    @Override
    protected void onPause() {
        super.onPause();
        BGM_PANCHYLIME.pause();
        mediaPaused = BGM_PANCHYLIME.getCurrentPosition();
    }

    /**
     * When the activity resumes
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (BGM_GAMES_PANCHYLIME.isPlaying()) BGM_GAMES_PANCHYLIME.stop();
        BGM_PANCHYLIME.seekTo(mediaPaused);
        BGM_PANCHYLIME.start();
    }
}