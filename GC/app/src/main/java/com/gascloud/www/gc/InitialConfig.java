package com.gascloud.www.gc;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class InitialConfig extends AppCompatActivity {

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_config);

        manager = getSupportFragmentManager();

        PhoneFragment phoneFragment = new PhoneFragment();
        manager.beginTransaction().replace(R.id.container, phoneFragment).commit();
    }
}
