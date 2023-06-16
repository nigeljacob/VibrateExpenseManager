package com.example.vibrate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.vibrate.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AccountSettingsActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    public final static String TYPE = "com.example.vibrate.message_key";

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        adjustFontScale(getResources().getConfiguration());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        Intent intent = getIntent();
        String message = intent.getStringExtra(TYPE);

        mAuth = FirebaseAuth.getInstance();

        if (message.equals("ForgotPassword")) {
            ReplaceFragment(new ForgotPasswordFragment());

        } else if (message.equals("AcountSettings")) {
            ReplaceFragment(new AccountsettingsFragment());
        }
    }

    public void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame_fragmentholder2, fragment);
        fragmentTransaction.commit();
    }

    private void adjustFontScale(Configuration configuration) {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

}