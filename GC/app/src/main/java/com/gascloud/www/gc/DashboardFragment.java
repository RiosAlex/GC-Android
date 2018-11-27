package com.gascloud.www.gc;


import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.guilhe.views.CircularProgressView;
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
    private CircularProgressView progressGas, progressFuga;
    private SeekBar progressTemperatura;
    private TextView txtGas, txtFuga, txtTemperatura, txtTextoFuga;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        progressGas = (CircularProgressView)view.findViewById(R.id.progressNivel);
        progressFuga = (CircularProgressView)view.findViewById(R.id.progressFugas);
        progressTemperatura = (SeekBar) view.findViewById(R.id.seekBarTemperatura);

        txtGas = (TextView) view.findViewById(R.id.txtNivel);
        txtFuga = (TextView) view.findViewById(R.id.txtFugas);
        txtTemperatura = (TextView) view.findViewById(R.id.txtTemperatura);
        txtTextoFuga = (TextView)view.findViewById(R.id.txtTextoFugas);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeDashboard);

        progressTemperatura.setEnabled(false);

        progressDialog = new ProgressDialog(getContext());
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        validate(user);
        showLevels();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showLevels();
            }
        });

        return view;
    }

    private void cancelRefresh(){
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showLevels(){
        db.collection("Devices").document("helium8AD6").collection("Gascloud").document("levels").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists() && documentSnapshot != null) {
                        String gas = String.valueOf(documentSnapshot.get("gaslevel"));
                        String fuga = String.valueOf(documentSnapshot.get("leaklevel"));
                        String temperatura = String.valueOf(documentSnapshot.get("temperaturelevel"));

                        progressGas.setProgress(Integer.parseInt(gas));
                        progressFuga.setProgress(Integer.parseInt(fuga));
                        progressTemperatura.setProgress(Integer.parseInt(temperatura));

                        txtGas.setText(gas + " %");
                        txtFuga.setText(fuga + " %");
                        if (Integer.parseInt(fuga) == 0){
                            txtTextoFuga.setText("¡No se detectaron fugas!");
                        } else if (Integer.parseInt(fuga) > 0){
                            txtTextoFuga.setText("¡Alerta!, posibilidades de fuga de gas: " + fuga + " %");
                        }
                        txtTemperatura.setText(temperatura + "° C");

                        cancelRefresh();
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cancelRefresh();
            }
        });
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
