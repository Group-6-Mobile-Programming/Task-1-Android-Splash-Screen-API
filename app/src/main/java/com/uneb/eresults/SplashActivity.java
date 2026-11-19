package com.uneb.eresults;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

/**
 * Splash screen of the UNEB e-Results app.
 *
 * <p>UI skeleton only. Loading checks, error states and the transition to
 * LoginActivity/DashboardActivity are added by the logic team.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Must be called before super.onCreate. It applies the splash theme.
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
