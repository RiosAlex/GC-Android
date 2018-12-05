package com.gascloud.www.gc;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private TextView txtGas, txtFuga, txtTemperatura, txtTextoFuga;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference mLevelsDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        progressGas = (CircularProgressView)view.findViewById(R.id.progressNivel);
        progressFuga = (CircularProgressView)view.findViewById(R.id.progressFugas);

        txtGas = (TextView) view.findViewById(R.id.txtNivel);
        txtFuga = (TextView) view.findViewById(R.id.txtFugas);
        txtTemperatura = (TextView) view.findViewById(R.id.txtTemp);
        txtTextoFuga = (TextView)view.findViewById(R.id.txtTextoFugas);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeDashboard);

        mLevelsDatabase = FirebaseDatabase.getInstance().getReference();

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
        mLevelsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String temp = dataSnapshot.child("Temp").getValue().toString();
                    String tanque = String.valueOf(dataSnapshot.child("tanque").getValue().toString());
                    int totalTank = Integer.parseInt(tanque)/10;

                    if (totalTank <= 0){
                        txtGas.setText("0 %");
                        progressGas.setProgress(0);

                        NotificationCompat.Builder notification = new NotificationCompat.Builder(getContext())
                                .setSmallIcon(R.mipmap.logo)
                                .setContentTitle("¡Notificación de alerta!")
                                .setContentText("Se acabo el gas :(")
                                .setAutoCancel(true)
                                .setWhen(System.currentTimeMillis());

                        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(0, notification.build());

                    } else if (totalTank > 0 && totalTank <= 99){
                        txtGas.setText(String.valueOf(totalTank) + " %");
                        progressGas.setProgress(totalTank);
                    } else if (totalTank >= 100){
                        txtGas.setText("100 %");
                        progressGas.setProgress(100);
                    }

                    txtTemperatura.setText(temp);
                    cancelRefresh();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Ocurrio un error al cargar información.", Toast.LENGTH_SHORT).show();
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
