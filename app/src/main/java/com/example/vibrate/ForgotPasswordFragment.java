package com.example.vibrate;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.SignInMethodQueryResult;


public class ForgotPasswordFragment extends Fragment {

    TextInputEditText email;
    FirebaseAuth mAuth;
    ImageButton next;

    ProgressBar progressBar;

    public final static String EmailType = "com.example.vibrate.message_key";


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        mAuth = FirebaseAuth.getInstance();

        email = v.findViewById(R.id.editTextTextEmailAddress2);
        next = v.findViewById(R.id.submitButton);
        progressBar = v.findViewById(R.id.progressBarr);

        progressBar.setVisibility(View.INVISIBLE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                next.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                String Email = email.getText().toString();
                mAuth.fetchSignInMethodsForEmail(Email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser) {
                            Toast.makeText(getActivity(), "There is no account associated with that email address", Toast.LENGTH_LONG).show();
                            next.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);

                        } else if (!isNewUser){
                            mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Email sent Successfully to " + Email, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getActivity(), justActivity.class);
                                        intent.putExtra(EmailType, "ForgotPasswordEmail");
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Failed to send email to " + Email, Toast.LENGTH_SHORT).show();
                                    next.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);

                                }
                            });
                        }
                    }
                });
            }
        });





        // Inflate the layout for this fragment
        return v;
    }
}