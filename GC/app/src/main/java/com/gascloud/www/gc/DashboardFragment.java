package com.gascloud.www.gc;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {


    public DashboardFragment() {
        // Required empty public constructor
    }

    public FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        progressDialog = new ProgressDialog(getContext());
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        validate(user);

        return view;
    }

    private void validate(final String user){

            db.collection("Users").document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        if (documentSnapshot.exists() && documentSnapshot != null) {
                            Boolean is_virgin = documentSnapshot.getBoolean("is_virgin");

                            if (is_virgin == true) {
                                Intent intent = new Intent(getContext(), InitialConfig.class);
                                startActivity(intent);
                            }
                        }
                        
                        else if (!documentSnapshot.exists()){
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            if (currentUser != null){
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setTitle("Preparando usuario para primer uso...");
                                progressDialog.setMessage("Esto puede tardar algunos segundos.");
                                progressDialog.show();
                                progressDialog.setCancelable(false);

                                String userEmail = firebaseAuth.getCurrentUser().getEmail();
                                final Map<String, Object> addUserMap = new HashMap<>();
                                addUserMap.put("is_virgin", true);

                                db.collection("Users").document(userEmail).set(addUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        validate(user);
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }
                    }
                }
            });

    }

}
