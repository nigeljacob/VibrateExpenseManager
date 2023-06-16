package com.example.vibrate;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.ConditionVariable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AccountsettingsFragment extends Fragment {

    private ConstraintLayout layoutPaswordChane, layoutEmailChange;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_accountsettings, container, false);

        layoutPaswordChane = v.findViewById(R.id.layout03);
        layoutEmailChange = v.findViewById(R.id.layout02);

        layoutEmailChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReplaceFragment(new ChangeEmailFragment());
            }
        });

        layoutPaswordChane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReplaceFragment(new ChangePasswordFragment());
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