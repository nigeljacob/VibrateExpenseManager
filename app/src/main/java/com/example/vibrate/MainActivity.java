package com.example.vibrate;

import androidx.annotation.FractionRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vibrate.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {

    private TextView userName, userEmail, useremailedit, userSubmit;
    private EditText usernametext;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser ;
    private ProgressBar loadingbar01, loadingbar02;
    private BottomNavigationView bottombar;
    ActivityMainBinding binding;

    public final static String Message = "com.example.vibrate.message_key";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("NewClicked", false)) {
            finish();// finish yourself and make a create a new Instance of yours.
            Intent intent = new Intent(MainActivity.this ,MainActivity.class);
            startActivity(intent);
        }

        adjustFontScale(getResources().getConfiguration());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent2 = getIntent();
        String type = intent2.getStringExtra("addExpense");
        if (type != null) {
            if (type.equals("Exxpense")) {
                ReplaceFragment(new UploadFragment());
            }

        } else {
            ReplaceFragment(new HomeFragment());
        }

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkBlur));


        bottombar = findViewById(R.id.bottomnav);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser == null) {

            startActivity(new Intent(MainActivity.this, SplashScreenActivity.class));
            finish();

        } else {

            currentUser.reload();

            if (!currentUser.isEmailVerified()) {

                Intent intent = new Intent(MainActivity.this, FullscreenActivityEmailVerification.class);
                intent.putExtra(Message, "Email Not Verified Yet");
                startActivity(intent);
                finish();

            }

            binding.bottomnav.setOnItemSelectedListener(item -> {

                switch (item.getItemId()) {
                    case R.id.home:
                        ReplaceFragment(new HomeFragment());
                        break;
                    case R.id.activity:
                        ReplaceFragment(new UploadFragment());
                        break;
                    case R.id.favorites:
                        ReplaceFragment(new Expense());
                        break;
                    case R.id.settings:
                        ReplaceFragment(new SettingsFragment());
                        break;
                }

                return true;
            });

        }
    }




    public void showMessage(String Message) {
    Toast.makeText(getApplicationContext(),Message,Toast.LENGTH_LONG).show();
    }

    public void UpdateUserInfo(FirebaseUser currentUser, String name) {

    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build();

    currentUser.updateProfile(profileUpdate)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        // user info updated successfully
                        showMessage("Successfully updated New Name");
                        recreate();
                    }

                }
            });
    }

    public void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_fragmentholder, fragment);
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



