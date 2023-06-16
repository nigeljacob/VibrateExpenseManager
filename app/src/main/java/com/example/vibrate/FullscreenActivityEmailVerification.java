package com.example.vibrate;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vibrate.databinding.ActivityFullscreenEmailVerificationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivityEmailVerification extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;

    TextView emailText, resendEmail, logoutButton, emailId, updateemail, submitnewEmail, userDifferent;
    EditText userEmailEdit;

    Button alreaduActivated, openGmail;

    FirebaseAuth mAuth;

    ProgressBar progressBar;

    public final static String Message = "com.example.vibrate.message_key";
    public final static String EmailType = "com.example.vibrate.message_key";

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };





    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_email_verification);

        if (getIntent().getBooleanExtra("NewClicked", false)) {
            finish();// finish yourself and make a create a new Instance of yours.
            Intent intent = new Intent(FullscreenActivityEmailVerification.this ,FullscreenActivityEmailVerification.class);
            startActivity(intent);
        }

        emailText = findViewById(R.id.fullscreen_content);

        Intent intent = getIntent();
        String message = intent.getStringExtra(Message);
        emailText.setText(message);

        if (message.equals("Verification email sent")) {
            emailText.setTextColor((Color.parseColor("#006400")));
        } else {
            emailText.setTextColor((Color.parseColor("#e34f4f")));
        }



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        emailText = findViewById(R.id.fullscreen_content);
        resendEmail = findViewById(R.id.resentEmailbutton);
        logoutButton = findViewById(R.id.logoutBut);
        emailId = findViewById(R.id.emailtxt);
        alreaduActivated = findViewById(R.id.dummy_button);
        openGmail = findViewById(R.id.open_Gmail);
        progressBar = findViewById(R.id.progressB);
        updateemail = findViewById(R.id.emailtxtupdate);
        submitnewEmail = findViewById(R.id.updateEmail);
        userEmailEdit = findViewById(R.id.updateEmailfield);
        userDifferent = findViewById(R.id.useDifferentEmail);

        progressBar.setVisibility(View.INVISIBLE);

        String userEmail = currentUser.getEmail();
        String formatedEmail = userEmail.replaceAll("![@gmail.com]", "*");

        emailId.setText(formatedEmail);


        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendEmail.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(FullscreenActivityEmailVerification.this, "Email sent to" + userEmail, Toast.LENGTH_SHORT).show();
                            resendEmail.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    }
                }). addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FullscreenActivityEmailVerification.this, "Failed to send email to " + userEmail, Toast.LENGTH_SHORT).show();
                        resendEmail.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });

        openGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Min SDK 15
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.equals("Verification email sent")) {
                    currentUser.delete();
                    userDifferent.setText("use different email address (This email account will be deleted)");
                }
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(FullscreenActivityEmailVerification.this, SplashScreenActivity.class));
                finish();
            }
        });

        updateemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateemail.setVisibility(View.GONE);
                userEmailEdit.setVisibility(View.VISIBLE);
                submitnewEmail.setVisibility(View.VISIBLE);

                submitnewEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newEmail = userEmailEdit.getText().toString();

                        currentUser.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(FullscreenActivityEmailVerification.this, "Email Updated successfully", Toast.LENGTH_SHORT).show();
                                currentUser.reload();
                                emailId.setText(currentUser.getEmail());
                                updateemail.setVisibility(View.VISIBLE);
                                userEmailEdit.setVisibility(View.GONE);
                                submitnewEmail.setVisibility(View.GONE);

                                currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(FullscreenActivityEmailVerification.this, "Email sent to" + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                            resendEmail.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.INVISIBLE);

                                        }
                                    }
                                }). addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(FullscreenActivityEmailVerification.this, "Failed to send email to " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                        resendEmail.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FullscreenActivityEmailVerification.this, e.getMessage(), Toast.LENGTH_SHORT);
                                updateemail.setVisibility(View.VISIBLE);
                                userEmailEdit.setVisibility(View.GONE);
                                submitnewEmail.setVisibility(View.GONE);
                            }
                        });
                    }
                });

            }
        });

        alreaduActivated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentUser.reload();

                if (currentUser.isEmailVerified()) {
                    if (message.equals("changeEmail")) {
                        Intent intent = new Intent(FullscreenActivityEmailVerification.this, justActivity.class);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        intent.putExtra(EmailType, "ChangeEmail");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(FullscreenActivityEmailVerification.this, "Email activation Successfull", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FullscreenActivityEmailVerification.this, MainActivity.class);
                        intent.putExtra("NewClicked", true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }

                } else if (!currentUser.isEmailVerified()) {
                    Toast.makeText(FullscreenActivityEmailVerification.this, "Email not verified yet", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void checkActivation(FirebaseUser currentUser) {


        if (currentUser != null) {

            boolean checkEmailActivation = currentUser.isEmailVerified();

            if (checkEmailActivation == false) {
                Toast.makeText(FullscreenActivityEmailVerification.this, "Email not verified yet", Toast.LENGTH_SHORT).show();

            } else {

                startActivity(new Intent(FullscreenActivityEmailVerification.this, MainActivity.class));
                Toast.makeText(FullscreenActivityEmailVerification.this, "Email activation Successfull", Toast.LENGTH_SHORT).show();
                finish();
            }

        } else {
            startActivity(new Intent(FullscreenActivityEmailVerification.this, SplashScreenActivity.class));
            finish();
            Toast.makeText(FullscreenActivityEmailVerification.this, "User Not signed in", Toast.LENGTH_SHORT).show();
        }

    }

}

