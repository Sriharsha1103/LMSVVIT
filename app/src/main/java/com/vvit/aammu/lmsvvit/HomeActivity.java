package com.vvit.aammu.lmsvvit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.utils.PrefManager;

import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Employee employee;
    private PrefManager prefManager;
    private Bundle bundle;

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

        Menu menu = navigationView.getMenu();
        if(employee.getDesignation().equalsIgnoreCase("HOD")){
            menu.findItem(R.id.nav_leave_applications).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.nav_leave_applications).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(this);
        Fragment fragment = HomeFragment.newInstance(employee);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.id_frame_layout,fragment);
        fragmentTransaction.commit();
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
            return true;
        }

        if(id==R.id.action_logout){
            prefManager.setFirstTimeLogin(true);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_applied_leaves) {
           // Toast.makeText(this, "Yet to implement", Toast.LENGTH_SHORT).show();
            fragment = AppliedLeavesFragment.newInstance(employee);
        }
        else if(id==R.id.nav_home){
            fragment = HomeFragment.newInstance(employee);
        }
        else if (id == R.id.nav_leave_applications) {
            //Toast.makeText(this, "Yet to implement", Toast.LENGTH_SHORT).show();
            fragment = ApplyLeaveFragment.newInstance(employee);
        } else if (id == R.id.nav_leaves_summary) {

            Toast.makeText(this, "Yet to implement", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_personal_info) {
            //Toast.makeText(this, "Yet to implement", Toast.LENGTH_SHORT).show();
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
}
