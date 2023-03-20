package com.drawin.panchylime.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.drawin.panchylime.R;
import com.drawin.panchylime.dialogs.GachaDialog;
import com.drawin.panchylime.models.Pet;
import com.drawin.panchylime.models.PetBag;
import com.drawin.panchylime.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Draco0503
 */
public class GachaFragment extends Fragment {

    // THE KEY OF THE ARGUMENTS NEEDED
    private static final String ARG_USER = "user";
    private static final String ARG_PETS = "pets";

    private View _view;

    private User _user;
    private ArrayList<Pet> _normalPets;
    private ArrayList<Pet> _rarePets;
    private ArrayList<Pet> _srarePets;

    private Button _gachaBtn;

    /**
     * Empty constructor
     */
    public GachaFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of the GachaFragment class
     * @param user user class
     * @param pets all pets
     * @return GachaFragment
     */
    public static GachaFragment newInstance(User user, HashMap<String, Pet> pets) {
        GachaFragment fragment = new GachaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putSerializable(ARG_PETS, pets);
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
            _user = (User) getArguments().getSerializable(ARG_USER);
            HashMap<String, Pet> pets = (HashMap<String, Pet>) getArguments().getSerializable(ARG_PETS);
            loadArrays(pets);
        }
    }

    /**
     * Is called when the view is created
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_gacha, container, false);

        _gachaBtn = _view.findViewById(R.id.gachaBtn);
        _gachaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gachaFunc();
            }
        });

        return _view;
    }

    /**
     * Loads the lists based on their rarities
     * @param pets all pets
     */
    private void loadArrays(HashMap<String, Pet> pets) {
        _normalPets = new ArrayList<>();
        _rarePets = new ArrayList<>();
        _srarePets = new ArrayList<>();
        for (Map.Entry<String, Pet> entry : pets.entrySet()) {
            switch (entry.getValue().get_rarity()) {
                case "N":
                    _normalPets.add(entry.getValue());
                    break;
                case "R":
                    _rarePets.add(entry.getValue());
                    break;
                case "SR":
                    _srarePets.add(entry.getValue());
                    break;
            }
        }
    }

    /**
     * Checks if the user has enough coins to try one
     */
    private void gachaFunc() {
        if (_user.get_coins() < 5) {
            Toast.makeText(getContext(), R.string.coins_warning, Toast.LENGTH_SHORT).show();
        } else {
            _user.set_coins(_user.get_coins() - 5);
            gachaLogic();
        }
    }

    /**
     * Logic of the gacha (80% for Normal, 15% for Rare, 5% for SuperRare)
     */
    private void gachaLogic() {
        Random rnd = new Random();
        int rarity = rnd.nextInt(100); // [0,100)
        int arrayPos;
        GachaDialog gachaDialog;
        Pet slime;
        if (rarity >= 95) { // [95, 99] -> %5
            arrayPos = rnd.nextInt(_srarePets.size());
            slime = _srarePets.get(arrayPos);
        } else if (rarity >= 80) { // [80, 94] -> %15
            arrayPos = rnd.nextInt(_rarePets.size());
            slime = _rarePets.get(arrayPos);
        } else { // [0, 79] -> 80%
            arrayPos = rnd.nextInt(_normalPets.size());
            slime = _normalPets.get(arrayPos);
        }
        if (searchSlimeInUsersBag(slime)) {
            // FORM 1: NO REPEATED, BUT ERROR IF THE USER HAS ALL, TO SLOW IF THE USER HAS ALL BUT 1
            // gachaLogic(); // RECURSIVE WAY
            // FORM 2: EVADING THE INFINITE LOOP ERROR IF REPEATED GIVES THE USER 2 COINS IN EXCHANGE
            gachaDialog = new GachaDialog(getActivity(), slime, 2);
        } else {
            // ADDS THE NEW SLIME TO USERS BAG
            _user.get_bag().add(new PetBag(slime.get_petName()));
            gachaDialog = new GachaDialog(getActivity(), slime);
        }
        gachaDialog.show();
        saveUsersData();
    }

    /**
     * Checks if the user already owns the pet
     * @param pet pet given
     * @return true if the user has it, false if not
     */
    private boolean searchSlimeInUsersBag(Pet pet) {
        for (int i = 0; i < _user.get_bag().size(); i++) {
            if (_user.get_bag().get(i).get_petName().equals(pet.get_petName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Saves the new data from the user to its ref in the database
     */
    private void saveUsersData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(_user.get_username()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> postValues = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    postValues.put(snapshot.getKey(),snapshot.getValue());
                }
                postValues.put("_coins", _user.get_coins());
                postValues.put("_bag", _user.get_bag());
                ref.child("users").child(_user.get_username()).updateChildren(postValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("saveUsersData", error.getMessage());
            }
        });
    }

}