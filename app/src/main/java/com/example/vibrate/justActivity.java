package com.example.vibrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class justActivity extends AppCompatActivity {

    private Button button;
    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;
    private TextView emailTypeText, title;
    ImageView image;

    public final static String EmailType = "com.example.vibrate.message_key";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_just);

        button = findViewById(R.id.OkButton);
        emailTypeText = findViewById(R.id.titleT2);
        title = findViewById(R.id.titleT);
        image = findViewById(R.id.emailSent);


        Intent intent = getIntent();

        String emailType = intent.getStringExtra(EmailType);

        if (emailType.equals("ForgotPasswordEmail")) {
            emailTypeText.setText("We have sent you an email. click on the link provided and reset your password and login again");
            title.setText("Check Your Mail");
            image.setImageResource(R.drawable.email_envelope_svgrepo_com__1_);

            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(justActivity.this, SplashScreenActivity.class));
                    finish();
                }
            });
        } else if (emailType.equals("ChangePasswordUpdate")) {
            emailTypeText.setText("Your PassWord has been updated successfully. You can use your new password to sign in next time.");
            title.setText("Password Updated Successfully");
            image.setImageResource(R.drawable.unlocked_svgrepo_com);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(justActivity.this, MainActivity.class);
                    intent.putExtra("NewClicked", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });

        } else if (emailType.equals("ChangeEmail")) {
            emailTypeText.setText("Your Email has been updated successfully. You can use your new Email to sign in next time.");
            title.setText("Email Updated Successfully");
            image.setImageResource(R.drawable._4239755831600459987);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(justActivity.this, MainActivity.class);
                    intent.putExtra("NewClicked", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
        }


    }
}