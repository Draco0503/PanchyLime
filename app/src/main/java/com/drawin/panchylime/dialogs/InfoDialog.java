package com.drawin.panchylime.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.drawin.panchylime.R;

/**
 * @author Draco0503
 */
public class InfoDialog extends Dialog implements View.OnClickListener {

    private Activity _parent;
    private TextView _tvTitle;
    private TextView _tvDesc;
    private Button _OK;

    private String _desc;
    private String _title;

    public InfoDialog(Activity parent, int desc, int title) {
        super(parent);
        this._parent = parent;
        _desc = _parent.getString(desc);
        _title = _parent.getString(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_dialog);
        _tvTitle = findViewById(R.id.gameTitle);
        _tvTitle.setText(_title);

        _tvDesc = findViewById(R.id.infoDesc);
        _tvDesc.setText(_desc);

        _OK = (Button) findViewById(R.id.btnOK_INFO);
        _OK.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        dismiss();
    }
}
