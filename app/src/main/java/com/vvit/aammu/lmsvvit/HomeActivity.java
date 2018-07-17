package com.vvit.aammu.lmsvvit;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.mediation.AbstractAdViewAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.utils.FirebaseUtils;
import com.vvit.aammu.lmsvvit.utils.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Employee employee;
    private PrefManager prefManager;
    private Bundle bundle;
    private Fragment fragment;
    private FirebaseUtils firebaseUtils;
    private boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        prefManager = new PrefManager(this);
        bundle = getIntent().getExtras();
        employee=bundle.getParcelable("employee");
        String notificationFragment = getIntent().getStringExtra("notificationFragment");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("1", "Notification", importance);
            mChannel.setDescription("Description");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            TextView emailId = (TextView) headerView.findViewById(R.id.id_nav_header_title);
            TextView username = headerView.findViewById(R.id.nav_header_subtitle);
            username.setText(employee.getName());
            emailId.setText(employee.getEmailId());
            firebaseUtils = new FirebaseUtils(this, (DatabaseReference) null);
            Menu menu = navigationView.getMenu();
            if (employee.getDesignation().equalsIgnoreCase("HOD")) {
                menu.findItem(R.id.nav_leave_applications).setVisible(true);
                menu.findItem(R.id.nav_applied_leaves).setVisible(false);
                firebaseUtils.getAppliedLeaves();
            } else {
                menu.findItem(R.id.nav_leave_applications).setVisible(false);
            }
            navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState!=null){
            if(fragment!=null){
                if(fragment instanceof AppliedLeavesFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState,"LeavesFragment");
                else if(fragment instanceof ApplyLeaveFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState,"ApplyFragment");
                else if(fragment instanceof HomeFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState,"HomeFragment");
                else if(fragment instanceof PersonalFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState,"PersonalFragment");
                else if(fragment instanceof LeaveApplicantsFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState,"ApplicantsFragment");
                else if(fragment instanceof LeavesSummaryFragment)
                    getSupportFragmentManager().getFragment(savedInstanceState,"SummaryFragment");

            }
        }
        else if(notificationFragment==null){
            fragment = HomeFragment.newInstance(employee);
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.id_frame_layout, fragment);
            fragmentTransaction.commit();
        }
        else{
            fragment = LeaveApplicantsFragment.newInstance();
           // fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.id_frame_layout, fragment);
            fragmentTransaction.commit();

        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            changePassword();
            return true;
        }

        if(id==R.id.action_logout){
            prefManager.setFirstTimeLogin(true);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void changePassword() {
        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        dialog.setTitle("Change Password");
        View view = LayoutInflater.from(this).inflate(R.layout.change_password,null);
        final EditText password = view.findViewById(R.id.id_change_password);
        final EditText password2 = view.findViewById(R.id.id_change_password2);
        dialog.setView(view);
        dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(password2.getText().toString()))
                    Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                if(password.getText().toString().equals(password2.getText().toString())){
                    if(firebaseUtils.checkNetwork()){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int i=1;
                                for(DataSnapshot data:dataSnapshot.getChildren()){
                                    Employee employee = data.getValue(Employee.class);
                                    if(employee.getEmailId().equals(prefManager.getUserEmail())){
                                        data.getRef().child("password").setValue(password.getText().toString());
                                        flag = true;
                                        break;
                                    }
                                    i++;
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                flag = false;
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toast.makeText(HomeActivity.this, "Retype password should be same as password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_applied_leaves) {
            fragment = AppliedLeavesFragment.newInstance(employee);
        }
        else if(id==R.id.nav_home){
            fragment = HomeFragment.newInstance(employee);
        }
        else if (id == R.id.nav_leave_applications) {
            fragment = LeaveApplicantsFragment.newInstance();
        } else if (id == R.id.nav_leaves_summary) {
            fragment = LeavesSummaryFragment.newInstance(employee);
        } else if (id == R.id.nav_personal_info) {
            fragment = PersonalFragment.newInstance(employee);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.id_frame_layout,fragment);
        fragmentTransaction.addToBackStack("Fragment");
        fragmentTransaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(fragment!=null){
            if(fragment instanceof AppliedLeavesFragment)
                getSupportFragmentManager().putFragment(outState,"LeavesFragment",fragment);
            else if(fragment instanceof ApplyLeaveFragment)
                getSupportFragmentManager().putFragment(outState,"ApplyFragment",fragment);
            else if(fragment instanceof HomeFragment)
                getSupportFragmentManager().putFragment(outState,"HomeFragment",fragment);
            else if(fragment instanceof PersonalFragment)
                getSupportFragmentManager().putFragment(outState,"PersonalFragment",fragment);
            else if(fragment instanceof LeaveApplicantsFragment)
                getSupportFragmentManager().putFragment(outState,"ApplicantsFragment",fragment);
            else if(fragment instanceof LeavesSummaryFragment)
                getSupportFragmentManager().putFragment(outState,"SummaryFragment",fragment);

        }
    }
}
