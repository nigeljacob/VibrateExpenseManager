package com.example.vibrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;


public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser ;

    private TextInputEditText textInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        }

        Button Continue = (Button)findViewById(R.id.Continue);
        textInputEditText = findViewById(R.id.EmailEditText);

        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textInputEditText.getText().toString().isEmpty()) {
                    Toast.makeText(SplashScreenActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    String email = textInputEditText.getText().toString();
                    mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                            if (isNewUser) {
                                Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                                intent.putExtra("Email", email);
                                intent.putExtra("Type", "EmailSignIn");
                                startActivity(intent);
                            } else if (!isNewUser) {
                                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                intent.putExtra("Email", email);
                                intent.putExtra("Type", "EmailSignIn");
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SplashScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });




    }
}