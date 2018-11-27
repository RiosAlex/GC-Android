package com.gascloud.www.gc;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NetworkFragment extends Fragment {


    public NetworkFragment() {
        // Required empty public constructor
    }

    private Button save;
    public FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_network, container, false);

        save = (Button)view.findViewById(R.id.btnNetworkSave);
        progressDialog = new ProgressDialog(getContext());
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                desvirgin(user);
            }
        });

        return view;
    }

    private void desvirgin(final String user) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null){
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Guardando configuración inicial...");
            progressDialog.setMessage("Esto puede tardar algunos segundos.");
            progressDialog.show();
            progressDialog.setCancelable(false);

            String userEmail = firebaseAuth.getCurrentUser().getEmail();
            final Map<String, Object> addUserMap = new HashMap<>();
            addUserMap.put("is_virgin", false);

            db.collection("Users").document(userEmail).update(addUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    desvirgin(user);
                    progressDialog.dismiss();
                    Intent intent = new Intent(getContext(), Home.class);
                    startActivity(intent);
                }
            });
        }
    }

}
