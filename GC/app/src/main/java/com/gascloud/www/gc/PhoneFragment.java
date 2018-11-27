package com.gascloud.www.gc;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneFragment extends Fragment {


    public PhoneFragment() {
        // Required empty public constructor
    }

    private FragmentManager manager;
    private CircularProgressButton save;
    private ImageView imgPhoto;
    private TextView txtName;
    private EditText phone;
    public FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone, container, false);
        manager = getFragmentManager();
        db = FirebaseFirestore.getInstance();

        save = (CircularProgressButton) view.findViewById(R.id.btnPhoneSave);
        imgPhoto = (ImageView)view.findViewById(R.id.profilePhotoConfig);
        txtName = (TextView)view.findViewById(R.id.profileNameConfig);
        phone = (EditText)view.findViewById(R.id.txtPhoneConfig);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            final String name = user.getDisplayName();
            final Uri photo = user.getPhotoUrl();

            txtName.setText(name);
            Picasso.get().load(photo).into(imgPhoto);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (phone.getText().toString().isEmpty()){
                        Toast.makeText(getContext(), "Número de celular obligatorio.", Toast.LENGTH_SHORT).show();
                    } else {
                        save.startAnimation();
                        String phoneNumber = phone.getText().toString();
                        final Map<String, Object> addUserMap = new HashMap<>();
                        addUserMap.put("name", name);
                        addUserMap.put("photoUrl", String.valueOf(photo));
                        addUserMap.put("phone", phoneNumber);
                        addUserMap.put("device", "helium8AD6");

                        db.collection("Users").document(user.getEmail()).update(addUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                QRCodeFragment qrCodeFragment = new QRCodeFragment();
                                manager.beginTransaction().setCustomAnimations(R.anim.slide_up, 0, 0, R.anim.slide_down).replace(R.id.container, qrCodeFragment).commit();
                            }
                        });
                    }

                }
            });
        }

        return view;
    }

}
