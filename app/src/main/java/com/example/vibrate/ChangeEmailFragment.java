package com.example.vibrate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

public class ChangeEmailFragment extends Fragment {

    TextInputEditText email;
    FirebaseAuth mAuth;
    ImageButton next;

    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_email, container, false);


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

                if (Email.equals(mAuth.getCurrentUser().getEmail())) {
                    ReplaceFragment(new ChangeEmailFragment2());
                } else {
                    Toast.makeText(getActivity(), "Email address is invalid", Toast.LENGTH_LONG).show();
                    next.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
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