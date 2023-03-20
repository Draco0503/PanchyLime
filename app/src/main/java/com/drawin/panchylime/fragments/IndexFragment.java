package com.drawin.panchylime.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.drawin.panchylime.R;
import com.drawin.panchylime.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Draco0503
 */
public class IndexFragment extends Fragment {

    // THE KEY OF THE ARGUMENTS NEEDED
    private static final String ARG_IMGURL = "image";
    private static final String ARG_USER = "user";

    private String _imageURL;
    private User _user;

    private View _view;
    private ImageView _ivPet;

    // DECORATORS
    private ImageView _notClean;
    private ImageView _dialog;

    // "Buttons"
    private ImageView _ivFeed;
    private ImageView _ivClean;

    /**
     * Empty constructor
     */
    public IndexFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of the IndexFragment class
     * @param imageURL selected pet image
     * @param user user
     * @return IndexFragment
     */
    public static IndexFragment newInstance(String imageURL, User user) {
        IndexFragment fragment = new IndexFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMGURL, imageURL);
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Is called when the class is created
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _imageURL = getArguments().getString(ARG_IMGURL);
            _user = (User) getArguments().getSerializable(ARG_USER);
            try {
                if (_user.getSelectedPet().get_cleanness() <= 30) {
                    _notClean.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                System.out.println("NO HA CARGADO EL USUARIO");
            }
        }
    }

    /**
     * Is called when the view is created
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        onCreate(savedInstanceState);
        _view = inflater.inflate(R.layout.fragment_index, container, false);
        _dialog = _view.findViewById(R.id.ivReact);
        _ivPet = _view.findViewById(R.id.iv_pet);
        _ivPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStatus(new Random().nextInt(3));
            }
        });

        _ivFeed = _view.findViewById(R.id.ivFeed);
        _ivFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedPet();
                showStatus(0);
            }
        });

        _ivClean = _view.findViewById(R.id.ivClean);
        _ivClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanPet();
                showStatus(1);
            }
        });
        _notClean = _view.findViewById(R.id.notClean);

        try {
            if (_user.getSelectedPet().get_cleanness() <= 30) {
                _notClean.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e("USER-DATA-ERROR", e.getMessage());
        }

        init();

        return _view;
    }

    /**
     * Initialize all the variables needed
     */
    private void init() {
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        if (_imageURL != null && !_imageURL.equals("")) {
            mStorage.getReference().child(_imageURL).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(_ivPet);
                }
            });
        } else {
            _ivPet.setBackgroundResource(R.drawable.unkown_slime100);
        }
        // ANOTHER WAY TO DISPLAY THE IMAGES, IF THE FIREBASE STORAGE SERVICE DOES NOT WORK
        // Picasso.get().load("https://cdn.discordapp.com/attachments/420618197411299349/1066864749058465842/slime_azul.png").into(_ivPet);
    }

    /**
     * Shows the status of the pet, but depends on the "status" given it will show status for HUNGER, CLEANNESS and HAPPINESS
     * @param status 0 for HUNGER, 1 for CLEANNESS, 2 for HAPPINESS
     */
    private void showStatus(int status) {
        int img = R.drawable.bocadillo_hi;
        try {
            switch (status) {
                case 0: // HUNGER
                    if (_user.getSelectedPet().get_hunger() >= 60) {
                        img = R.drawable.bocadillo_uwu;
                    } else {
                        img = R.drawable.bocadillo_triste;
                    }
                    break;
                case 1: // CLEANNESS
                    if (_user.getSelectedPet().get_cleanness() >= 50) {
                        img = R.drawable.bocadillo_feliz;
                    } else {
                        img = R.drawable.bocadillo_triste;
                    }
                    break;
                case 2: // HAPPINESS
                    if (_user.getSelectedPet().get_happiness() == 100) {
                        img = R.drawable.bocadillo_corazon;
                    }
                    break;
            }
        } catch (Exception e) {
            img = 0;
        }
        _dialog.setBackgroundResource(img);
    }

    /**
     * Feeds the pet
     */
    private void feedPet() {
        int value = _user.getSelectedPet().get_hunger() + 50;
        if (value > 100) {
            _user.getSelectedPet().set_hunger(100);
        } else {
            _user.getSelectedPet().set_hunger(value);
        }
        updateDB();
    }

    /**
     * Cleans the pet
     */
    private void cleanPet() {
        int value = _user.getSelectedPet().get_cleanness() + 30;
        if (value > 100) {
            _user.getSelectedPet().set_cleanness(100);
        } else {
            _user.getSelectedPet().set_cleanness(value);
        }
        if (_user.getSelectedPet().get_cleanness() > 30) {
            _notClean.setVisibility(View.INVISIBLE);
        }

        value = _user.getSelectedPet().get_happiness() + 10;
        if (value > 100) {
            _user.getSelectedPet().set_happiness(100);
        } else {
            _user.getSelectedPet().set_happiness(value);
        }

        updateDB();
    }

    /**
     * Updates the data of the selected pet from the user in the database
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

}