package com.drawin.panchylime.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

/**
 * @author Draco0503
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Is called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, LoginActivity.class);
        SystemClock.sleep(1500);
        startActivity(intent);
        finish();

    }


}