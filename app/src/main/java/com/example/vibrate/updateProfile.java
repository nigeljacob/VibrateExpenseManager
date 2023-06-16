package com.example.vibrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class updateProfile extends AppCompatActivity {

    Button Submit;
    private TextInputEditText nameedit, dateOfBirth, phoneedit;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        nameedit = findViewById(R.id.editName);
        phoneedit = findViewById(R.id.PhonrNumber);
        dateOfBirth = findViewById(R.id.Birth);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Submit = findViewById(R.id.UpdateButton);

        nameedit.setText(currentUser.getDisplayName());
        phoneedit.setText(currentUser.getPhoneNumber());

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = nameedit.getText().toString();
                final String DOB = dateOfBirth.getText().toString();
                final String phoneNumber = phoneedit.getText().toString();

                UpdateUserInfo(currentUser, name);

                Intent intent = new Intent(updateProfile.this, MainActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                intent.putExtra("NewClicked", true);
                startActivity(intent);
                finish();

            }
        });
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
                            showMessage("Successfully updated New Profile");
                        }

                    }
                });
    }

}