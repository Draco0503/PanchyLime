package com.drawin.panchylime.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.drawin.panchylime.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * @author Draco0503
 */
public class LogOutDialog extends Dialog implements View.OnClickListener {

    private Activity _parent;
    private Button _OK, _KO;

    /**
     * Constructor of the LogOutDialog class
     * @param _parent the caller activity
     */
    public LogOutDialog(Activity _parent) {
        super(_parent);
        this._parent = _parent;
    }

    /**
     * Is called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.logout_dialog);
        _OK = (Button) findViewById(R.id.btnOK_INFO);
        _KO = (Button) findViewById(R.id.btnKO);
        _OK.setOnClickListener(this);
        _KO.setOnClickListener(this);
    }

    /**
     * If LOGOUT button is pressed, logs out from the Firebase auth
     * @param view the caller
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOK_INFO:
                FirebaseAuth.getInstance().signOut();
                _parent.finish();
                break;
            case R.id.btnKO:
                dismiss();
                break;
        }
        dismiss();
    }
}
