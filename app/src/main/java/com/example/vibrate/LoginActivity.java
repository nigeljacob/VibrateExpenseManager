package com.example.vibrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText userEmail, userPassword;
    private Button logBut;
    private ProgressBar loadingbar;
    private FirebaseAuth mAuth;
    private TextView logintext2,forgotPassword, welcomeText, registerText;
    private ImageView backbutton;

    public final static String TYPE = "com.example.vibrate.message_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.editTextTextEmailAddress);
        userPassword = findViewById(R.id.editTextPassword);
        loadingbar = findViewById(R.id.regProgressBar);
        logBut = findViewById(R.id.loginbutton);
        mAuth = FirebaseAuth.getInstance();
        logintext2 = findViewById(R.id.textregister02);
        backbutton = findViewById(R.id.imageView2);
        forgotPassword = findViewById(R.id.forgotPassword);
        welcomeText = findViewById(R.id.welcome2);
        loadingbar.setVisibility(View.INVISIBLE);
        registerText = findViewById(R.id.textregister01);

        Intent intent = getIntent();
        String Email = intent.getStringExtra("Email");
        String Type = intent.getStringExtra("Type");
        if (Type != null) {
            userEmail.setText(Email);
            welcomeText.setText("Enter Password to continue");
            registerText.setText("Want to register instead? ");
        }


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, AccountSettingsActivity.class);
                intent.putExtra(TYPE, "ForgotPassword");
                startActivity(intent);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        logintext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        logBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingbar.setVisibility(View.VISIBLE);
                logBut.setVisibility(View.INVISIBLE);

                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();

                if (email.isEmpty()) {
                    ShowMessage("Email is Required");
                    logBut.setVisibility(View.VISIBLE);
                    loadingbar.setVisibility(View.INVISIBLE);
                } else if (password.isEmpty()) {
                    ShowMessage("Password is Required");
                    logBut.setVisibility(View.VISIBLE);
                    loadingbar.setVisibility(View.INVISIBLE);
                } else {
                    LoginUser(email, password);
                }
            }
        });
    }

    public void ShowMessage(String Message) {
        Toast.makeText(getApplicationContext(),Message, Toast.LENGTH_LONG).show();
    }

    public void LoginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loadingbar.setVisibility(View.INVISIBLE);
                    logBut.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("NewClicked", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }else {
                    ShowMessage("Error Login in \n" + task.getException().getMessage());
                    logBut.setVisibility(View.VISIBLE);
                    loadingbar.setVisibility(View.INVISIBLE);
            }
        }
        });
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