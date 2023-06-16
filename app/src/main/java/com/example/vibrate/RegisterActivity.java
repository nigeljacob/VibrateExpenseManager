package com.example.vibrate;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText userName, userEmail, userPassword, userConfirmPassword;
    private ProgressBar loadingProgress;
    private Button regBut;
    private ImageView BackButton;
    FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    String  userID;
    private TextView logintext, emailWarn, welcome2, welcomeText;

    public final static String Message = "com.example.vibrate.message_key";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.editTextTextname);
        userEmail = findViewById(R.id.editTextTextEmailAddress);
        userPassword = findViewById(R.id.editTextPassword);
        userConfirmPassword = findViewById(R.id.editTextPassword02);
        loadingProgress = findViewById(R.id.regProgressBar);
        regBut = findViewById(R.id.loginbutton);
        BackButton = findViewById(R.id.imageView2);
        logintext = findViewById(R.id.textregister02);
        emailWarn = findViewById(R.id.textvisible);
        welcome2 = findViewById(R.id.welcome2);
        welcomeText = findViewById(R.id.welcome);

        Intent intent = getIntent();
        String Email = intent.getStringExtra("Email");
        String Type = intent.getStringExtra("Type");
        if (Type != null) {
            userEmail.setText(Email);
            welcomeText.setText("Seems like your new here");
            welcome2.setText("Add your other details to continue");
        }

        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailWarn.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        regBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regBut.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String ConfirmPassword = userConfirmPassword.getText().toString();

                // check if user details are matching the requirement
                if (name.isEmpty()) {
                    showMessage("Name is Required");
                    loadingProgress.setVisibility(View.INVISIBLE);
                    regBut.setVisibility(View.VISIBLE);
                } else if (email.isEmpty())  {
                    showMessage("Email is Required");
                    loadingProgress.setVisibility(View.INVISIBLE);
                    regBut.setVisibility(View.VISIBLE);
                }else if (password.isEmpty()) {
                    showMessage("Password is required");
                    regBut.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else if (password.length() < 6) {
                    showMessage("Password must be at least 6 characters long");
                    regBut.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }else if (ConfirmPassword.isEmpty()) {
                    showMessage("Please Confirm Your Password");
                    regBut.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else if (!password.equals(ConfirmPassword)){
                    showMessage("Password is not matching");
                    regBut.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else  {
                    showMessage("Account Creation in Progress");
                    CreateAccount(name, email, password);

                }

                }

            });

        }


    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                            currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Email sent to" + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                        if (currentUser.isEmailVerified()) {
                                            Intent intent = new Intent(RegisterActivity.this, FullscreenActivityEmailVerification.class);
                                            intent.putExtra("NewClicked", true);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                            intent.putExtra(Message, "Verification email sent");
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            }). addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, "Failed to send email to " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                    regBut.setVisibility(View.VISIBLE);
                                    loadingProgress.setVisibility(View.INVISIBLE);
                                }
                            });
                        }

                    }
                });
    }

    private void CreateAccount(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            showMessage("Account Created Successfully for " + name);
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("user Profile").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Name", name);
                            user.put("Email", email);
                            user.put("Password", password);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }

                            });

                            UpdateUserInfo(mAuth.getCurrentUser(), name);

                        }
                        else {
                            showMessage("Error Creating user Account for " + name  +"\n" + task.getException().getMessage());
                            regBut.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });

    }

}