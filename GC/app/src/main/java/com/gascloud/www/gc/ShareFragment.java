package com.gascloud.www.gc;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment {


    public ShareFragment() {
        // Required empty public constructor
    }

    private CircularProgressButton anim;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        anim = (CircularProgressButton) view.findViewById(R.id.btnShareCont);

        anim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim.startAnimation();
            }
        });

        return view;
    }

}
