package com.vvit.aammu.lmsvvit.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvit.aammu.lmsvvit.HomeActivity;
import com.vvit.aammu.lmsvvit.LoadingActivity;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.model.Leave;
import com.vvit.aammu.lmsvvit.model.Leaves;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtils {

    private final DatabaseReference mFirebaseDatabase;
    private Employee employee;
    private Activity activity;
    private long count=0;
    private long employeeCount;

    public FirebaseUtils(Activity activity,DatabaseReference databaseReference){

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        this.activity = activity;
    }
    public void updatePersonalInfo(final Employee employee1){
        if(checkNetwork()){
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Uploading Data....");
            progressDialog.show();
            employee1.toString();
            mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                Leaves leaves;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.getChildrenCount();
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        employee = data.getValue(Employee.class);
                        if(employee1.getName().equals(employee.getName())){
                            employee.setName(employee1.getName());
                            employee.setGender(employee1.getGender());
                            employee.setDesignation(employee1.getDesignation());
                            data.getRef().setValue(employee);
                            break;
                        }

                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
            Toast.makeText(activity, "No Internet, Check Network Connection", Toast.LENGTH_SHORT).show();
    }
    public void addLeave(final Employee employee1,final Leave leave){
        Log.i("Fragment","Connected to Firebase");
        if(checkNetwork()){
            mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        Log.i("Fragment","Checking");
                        employee = data.getValue(Employee.class);
                        employee.toString();
                        if(employee1.getName().equals(employee.getName())) {
                            if(data.hasChild("leaves/leave")){
                                long childCount = data.child("leaves").child("leave").getChildrenCount();
                                Log.i("Children Count",""+childCount);
                                childCount++;
                                Log.i("Children Count++",""+childCount);
                                data.getRef().child("leaves/leave/"+String.valueOf(childCount)).setValue(leave);
                            }
                            data.getRef().child("leaves/leave/1").setValue(leave);
                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
            Toast.makeText(activity, "No Internet, Check Network Connection", Toast.LENGTH_SHORT).show();
    }
    public void fetchData(final String emailId){
        Log.i("Fetch Data","");
        if(checkNetwork()) {
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Fetching Data....");
            progressDialog.show();
            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                Leaves leaves;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i=0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        employee = data.getValue(Employee.class);
                        assert employee != null;
                        if (emailId.equals(employee.getEmailId())) {
                            leaves = data.child("leaves").getValue(Leaves.class);
                            employee.setLeaves(leaves);
                            Intent intent = new Intent(activity, HomeActivity.class);
                            intent.putExtra("employee",employee);
                            activity.startActivity(intent);
                            activity.finish();
                            break;
                        }
                        i++;
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else{
            Toast.makeText(activity, "No Internet, Check Network Connection", Toast.LENGTH_SHORT).show();
        }
    }
    public void checkLogin(final String email, final String password){
        Log.i("check Loginchc","");
        if(checkNetwork()) {
            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                Leaves leaves;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    employeeCount = dataSnapshot.getChildrenCount();
                    Log.i("EmployeeCount"," "+employeeCount);
                    int i=1;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Log.i("Employee",""+data.getValue());
                        employee = data.getValue(Employee.class);
                        if (employee.getEmailId().equals(email) && employee.getPassword().equals(password)) {
                            leaves = data.child("leaves").getValue(Leaves.class);
                            employee.setLeaves(leaves);
                            Intent intent = new Intent(activity, LoadingActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                            break;
                        }
                        i++;
                        count++;
                    }
                    if (count == employeeCount)
                        Toast.makeText(activity, "Invalid Username/Password", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
            Toast.makeText(activity, "No Internet, Check Network Connection", Toast.LENGTH_SHORT).show();
    }
    public List<Leave> getLeaves(final Employee employee){
        final List<Leave> leavesList = new ArrayList<>();
        Log.i("Leave Data","");
        if(checkNetwork()) {
            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                Leaves leaves;
                Employee employee1;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i=0;
                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                        employee1 = data.getValue(Employee.class);
                        assert employee1 != null;
                        if (employee1.getEmailId().equals(employee.getEmailId())) {
                            if(data.hasChild("leaves/leave")){
                                data.getRef().child("leaves/leave").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int childCount=1;
                                        Log.i("LeaveCount",""+data.getChildrenCount());
                                        Log.i("LeaveCount",""+dataSnapshot.getChildrenCount());
                                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                            Leave leave = data.child("leaves/leave").child(String.valueOf(childCount)).getValue(Leave.class);
                                            leave.display();
                                            leavesList.add(leave);
                                            childCount++;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            break;
                        }
                        i++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else{
            Toast.makeText(activity, "No Internet, Check Network Connection", Toast.LENGTH_SHORT).show();
        }
        return leavesList;
    }
    public boolean checkNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if(connectivityManager!=null){
            info = connectivityManager.getActiveNetworkInfo();
        }
        if(info == null){
            return false;
        }
        return true;

    }
    public int checkSpeed(){
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        int linkSpeed = wifiManager.getConnectionInfo().getRssi();
        int level = WifiManager.calculateSignalLevel(linkSpeed, 5);
        return level;
    }
}
