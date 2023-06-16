package com.example.vibrate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SettingsFragment extends Fragment {

    Button editprofile;
    CardView layoutAccountSettings, logout;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser ;
    TextView name, email;
    public final static String TYPE = "com.example.vibrate.message_key";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        logout = v.findViewById(R.id.Button04);
        layoutAccountSettings = v.findViewById(R.id.Button02);
        name = v.findViewById(R.id.NameText);
        email = v.findViewById(R.id.EmailTextofUser);
        editprofile = v.findViewById(R.id.edit);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        name.setText(currentUser.getDisplayName());
        email.setText(currentUser.getEmail());

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(getActivity(), updateProfile.class);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent);
            }
        });


        layoutAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                intent.putExtra(TYPE, "AcountSettings");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });




        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               showImageDownloadDailog(getActivity(), "Are you sure you want to LogOut?");

            }

        });


        return v;
    }

    public  void showImageDownloadDailog(Activity activity, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //TODO
                    }
                });

        AlertDialog alert = builder.create();
        alert.setOnShowListener(arg0 -> {
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.darkBlur));
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ErrorRed));
        });
        alert.show();
    }

}