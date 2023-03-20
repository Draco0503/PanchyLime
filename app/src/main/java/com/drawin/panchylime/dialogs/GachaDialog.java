package com.drawin.panchylime.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.drawin.panchylime.R;
import com.drawin.panchylime.models.Pet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * @author Draco0503
 */
public class GachaDialog extends Dialog implements View.OnClickListener {
    // ATTRIBUTES
    private Activity _parent;

    private Button _OK;
    private TextView _tvGachaInfo;
    private ImageView _ivGachaImg;

    private Pet _pet;
    private int _coins;

    /**
     * Constructor for no repeated pet
     * @param parent the caller
     * @param pet the pet won
     */
    public GachaDialog(Activity parent, Pet pet) {
        super(parent);
        _parent = parent;
        _pet = pet;
        _coins = 0;
    }

    /**
     * Constructor for repeated pet
     * @param parent the caller
     * @param pet the repeated pet
     * @param coins the coins in exchange
     */
    public GachaDialog(Activity parent, Pet pet, int coins) {
        super(parent);
        _parent = parent;
        _pet = pet;
        _coins = coins;
    }

    /**
     * Is called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gacha_dialog);

        _ivGachaImg = findViewById(R.id.gachaImg);
        loadImg();

        _OK = findViewById(R.id.gachaOK);
        _OK.setOnClickListener(this);

        _tvGachaInfo = findViewById(R.id.gachaInfo);
        if (_coins == 0) {
            _tvGachaInfo.setText(R.string.newOne);
        } else {
            _tvGachaInfo.setText("+ " + _coins);
        }
    }

    /**
     * Whatever button is clicked nothing will happen
     * @param view the caller
     */
    @Override
    public void onClick(View view) {
        dismiss();
    }

    /**
     * Displays the image of the pet won
     */
    private void loadImg() {
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        ref.child(_pet.get_image()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(_ivGachaImg);
            }
        });
    }
}
