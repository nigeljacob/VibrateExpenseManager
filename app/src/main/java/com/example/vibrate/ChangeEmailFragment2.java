package com.example.vibrate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailFragment2 extends Fragment {

    TextInputEditText newEmail, newEmail2;
    ImageButton next;
    FirebaseAuth mAuth;

    ProgressBar progressBar;
    public final static String EmailType = "com.example.vibrate.message_key";
    public final static String Message = "com.example.vibrate.message_key";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_change_email2, container, false);

        newEmail = v.findViewById(R.id.editPass1);
        newEmail2 = v.findViewById(R.id.editPass2);
        next = v.findViewById(R.id.submitButton);
        progressBar = v.findViewById(R.id.progressBarr3);

        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if (newEmail.getText().toString().isEmpty() || newEmail2.getText().toString().isEmpty() ) {
                    Toast.makeText(getActivity(), "Email fields must be filled", Toast.LENGTH_SHORT).show();
                    next.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);

                } else {
                    String newemail = newEmail.getText().toString();
                    String newemail2 = newEmail2.getText().toString();

                    if (newemail.equals(newemail2)) {
                        currentUser.updateEmail(newemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    currentUser.reload();
                                    Toast.makeText(getActivity(), "Successfully updated Email", Toast.LENGTH_SHORT).show();
                                    if (!currentUser.isEmailVerified()) {
                                        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Intent intent = new Intent(getActivity(), FullscreenActivityEmailVerification.class);
                                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                intent.putExtra("NewClicked", true);
                                                intent.putExtra(Message, "changeEmail");
                                                startActivity(intent);
                                                getActivity().finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Intent intent = new Intent(getActivity(), justActivity.class);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        intent.putExtra(EmailType, "ChangeEmail");
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                next.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Emails don't Match", Toast.LENGTH_SHORT).show();
                        next.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });



        return v;
    }
}