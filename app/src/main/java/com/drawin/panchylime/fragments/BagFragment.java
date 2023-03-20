package com.drawin.panchylime.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.drawin.panchylime.R;
import com.drawin.panchylime.activities.IndexActivity;
import com.drawin.panchylime.models.Pet;
import com.drawin.panchylime.models.PetBag;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Draco0503
 */
public class BagFragment extends Fragment {

    // THE KEY OF THE ARGUMENTS NEEDED
    private static final String ARG_BAG = "bag";
    private static final String ARG_USER = "user";

    // IDs FROM THE DEFAULT IMAGES
    private final int SELECTED_CARD = R.drawable.selected_card_view;
    private final int NON_SELECTED_CARD = R.drawable.card_view;

    private String _username;

    private HashMap<String, ImageView> _images;
    private HashMap<String, PetBag> _bag;

    private View _view;

    /**
     * Empty constructor
     */
    public BagFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of the BagFragment class
     * @param bag users bag
     * @param user username
     * @return BagFragment
     */
    public static BagFragment newInstance(HashMap<String, PetBag> bag, String user) {
        BagFragment fragment = new BagFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BAG, bag);
        args.putString(ARG_USER, user);
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
            _bag = (HashMap<String, PetBag>) getArguments().getSerializable(ARG_BAG);
            _username = getArguments().getString(ARG_USER);
        }
    }

    /**
     * Is called when the view is created
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_bag, container, false);

        init();

        return _view;
    }

    /**
     * Initializes the variables needed
     */
    private void init() {
        loadImgViews();
        setDataToView();
    }

    /**
     * Loads all the ImageViews of the layout
     * (Should be improved)
     */
    private void loadImgViews() {
        _images = new HashMap<>();

        _images.put("img0", (ImageView) _view.findViewById(R.id.iv0));
        _images.put("img1", (ImageView) _view.findViewById(R.id.iv1));
        _images.put("img2", (ImageView) _view.findViewById(R.id.iv2));
        _images.put("img3", (ImageView) _view.findViewById(R.id.iv3));
        _images.put("img4", (ImageView) _view.findViewById(R.id.iv4));
        _images.put("img5", (ImageView) _view.findViewById(R.id.iv5));
        _images.put("img6", (ImageView) _view.findViewById(R.id.iv6));
        _images.put("img7", (ImageView) _view.findViewById(R.id.iv7));
        _images.put("img8", (ImageView) _view.findViewById(R.id.iv8));
        _images.put("img9", (ImageView) _view.findViewById(R.id.iv9));
        _images.put("img10", (ImageView) _view.findViewById(R.id.iv10));
        _images.put("img11", (ImageView) _view.findViewById(R.id.iv11));
        // FOR MORE SPACE
        /*
        _images.put("img12", (ImageView) _view.findViewById(R.id.iv12));
        _images.put("img13", (ImageView) _view.findViewById(R.id.iv13));
        _images.put("img14", (ImageView) _view.findViewById(R.id.iv14));
        _images.put("img15", (ImageView) _view.findViewById(R.id.iv15));
        _images.put("img16", (ImageView) _view.findViewById(R.id.iv16));
        _images.put("img17", (ImageView) _view.findViewById(R.id.iv17));
        _images.put("img18", (ImageView) _view.findViewById(R.id.iv18));
        _images.put("img19", (ImageView) _view.findViewById(R.id.iv19));
        _images.put("img20", (ImageView) _view.findViewById(R.id.iv20));*/
    }

    /**
     * Loads all the players bag and shows the selected one
     */
    private void setDataToView() {
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        int i = 0;
        for (Map.Entry<String, PetBag> entry : _bag.entrySet()) {
            ImageView iv = _images.get("img" + i);
            PetBag petBag = entry.getValue();

            mStorage.getReference().child(entry.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(iv);
                }
            });
            assert iv != null;
            if (petBag.is_selected()) {
                iv.setBackgroundResource(SELECTED_CARD);
            } else {
                iv.setBackgroundResource(NON_SELECTED_CARD);
            }
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedPet(entry.getKey());
                }
            });
            i++;
        }
    }

    /**
     * Sets the new selected pet from the ones tha the player has, modifies the data from the database
     * @param url pets url
     */
    private void setSelectedPet(String url) {
        int i = 0;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ArrayList<PetBag> _bag = new ArrayList<>();
        for (Map.Entry<String, PetBag> entry : this._bag.entrySet()) {
            ImageView iv = _images.get("img"+i);
            assert iv != null;
            if (entry.getKey().equals(url)) {
                entry.getValue().set_selected(true);
                iv.setBackgroundResource(SELECTED_CARD);
            } else {
                entry.getValue().set_selected(false);
                iv.setBackgroundResource(NON_SELECTED_CARD);
            }
            _bag.add(entry.getValue());
            i++;
        }
        ref.child("users").child(_username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> postValues = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    postValues.put(snapshot.getKey(),snapshot.getValue());
                }
                postValues.put("_bag", _bag);
                ref.child("users").child(_username).updateChildren(postValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("setSelectedPet", error.getMessage());
            }
        });

    }
}