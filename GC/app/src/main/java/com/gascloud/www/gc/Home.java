package com.gascloud.www.gc;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager manager;
    public FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private boolean is_virgin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = getSupportFragmentManager();

        DashboardFragment dashboardFragment = new DashboardFragment();
        manager.beginTransaction().replace(R.id.homecontent, dashboardFragment, dashboardFragment.getTag()).commit();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            final String name = user.getDisplayName();
            final Uri photo = user.getPhotoUrl();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View hView =  navigationView.getHeaderView(0);
            final TextView nav_user = (TextView)hView.findViewById(R.id.profileNameFacebook);
            final ImageView nav_picture = (ImageView) hView.findViewById(R.id.profilePhotoFacebook);

            nav_user.setText(name);
            Picasso.get().load(photo).into(nav_picture);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        String title = "Panel de control";

        if (id == R.id.nav_panel) {
            DashboardFragment dashboardFragment = new DashboardFragment();
            manager.beginTransaction().replace(R.id.homecontent, dashboardFragment, dashboardFragment.getTag()).commit();
            title = "Panel de control";
        } else if (id == R.id.nav_stat) {
            StatisticsFragment statisticsFragment = new StatisticsFragment();
            manager.beginTransaction().replace(R.id.homecontent, statisticsFragment, statisticsFragment.getTag()).commit();
            title = "Estadísticas";
        }  else if (id == R.id.nav_config) {
            SettingsFragment settingsFragment = new SettingsFragment();
            manager.beginTransaction().replace(R.id.homecontent, settingsFragment, settingsFragment.getTag()).commit();
            title = "Ajustes";
        } else if (id == R.id.nav_tips) {
            TipsFragment tipsFragment = new TipsFragment();
            manager.beginTransaction().replace(R.id.homecontent, tipsFragment, tipsFragment.getTag()).commit();
            title = "Tips";
        } else if (id == R.id.nav_comp) {
            ShareFragment shareFragment = new ShareFragment();
            manager.beginTransaction().replace(R.id.homecontent, shareFragment, shareFragment.getTag()).commit();
            title = "Compartir GasCloud";
        }

        getSupportActionBar().setTitle(title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
