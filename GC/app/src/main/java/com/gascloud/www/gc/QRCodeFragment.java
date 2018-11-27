package com.gascloud.www.gc;


import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class QRCodeFragment extends Fragment implements QRCodeReaderView.OnQRCodeReadListener{

    public QRCodeFragment() {
        // Required empty public constructor
    }

    private FragmentManager manager;
    private Button save;
    private QRCodeReaderView qrCodeReaderView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);
        manager = getFragmentManager();

        save = (Button)view.findViewById(R.id.btnQRCodeSave);

        qrCodeReaderView = (QRCodeReaderView)view.findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkFragment networkFragment = new NetworkFragment();
                manager.beginTransaction().setCustomAnimations( R.anim.slide_up, 0, 0, R.anim.slide_down).replace(R.id.container, networkFragment).commit();
            }
        });

        return view;
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Uri uri = Uri.parse(text.toLowerCase());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}
