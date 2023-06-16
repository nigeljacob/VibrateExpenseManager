package com.example.vibrate;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePasswordFragment extends Fragment {

    private ImageButton nextButton;
    TextInputEditText passwordText;
    FirebaseAuth mAuth;
    ConstraintLayout error;
    ProgressBar progressBar;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_password, container, false);

        passwordText = v.findViewById(R.id.editTextTextEmailAddress2);
        mAuth  = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        nextButton = v.findViewById(R.id.submitButton);
        error = v.findViewById(R.id.errorMessage2);
        progressBar = v.findViewById(R.id.progressBarr2);

        progressBar.setVisibility(View.INVISIBLE);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password = passwordText.getText().toString();

                nextButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential = EmailAuthProvider
                        .getCredential(currentUser.getEmail() ,password);

                currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ReplaceFragment(new ChangePassword2Fragment());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        return v;
    }

    public void ReplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.frame_fragmentholder2, fragment);
        fragmentTransaction.commit();
    }


}