package com.uneb.eresults;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

/**
 * SplashActivity serves as the entry point for the UNEB e-Results application,
 * handling the splash screen transition and initialization sequence.
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Called when the activity is starting. Installs the core Android splash screen
     * and sets up the layout content view.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down then this Bundle contains the data it most
     *                           recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Must be called before super.onCreate. It applies the splash theme.
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}