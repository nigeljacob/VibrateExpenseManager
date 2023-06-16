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
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword2Fragment extends Fragment {

    TextInputEditText newPassword1, newPassword2;
    ImageButton next;
    FirebaseAuth mAuth;

    ProgressBar progressBar;
    public final static String EmailType = "com.example.vibrate.message_key";


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_change_password2, container, false);

        newPassword1 = v.findViewById(R.id.editPass1);
        newPassword2 = v.findViewById(R.id.editPass2);
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
                if (newPassword1.getText().toString().isEmpty() || newPassword1.getText().toString().isEmpty() ) {
                    Toast.makeText(getActivity(), "Password fields must be filled", Toast.LENGTH_SHORT).show();
                    next.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);

                } else {
                    String newPass = newPassword1.getText().toString();
                    String newPass2 = newPassword2.getText().toString();

                    if (newPass.equals(newPass2)) {
                        currentUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Successfully updated password", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), justActivity.class);
                                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    intent.putExtra(EmailType, "ChangePasswordUpdate");
                                    startActivity(intent);
                                    getActivity().finish();
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
                        Toast.makeText(getActivity(), "Passwords don't Match", Toast.LENGTH_SHORT).show();
                        next.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });




        return v;
    }
}