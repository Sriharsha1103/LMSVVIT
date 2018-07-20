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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvit.aammu.lmsvvit.HomeActivity;
import com.vvit.aammu.lmsvvit.LoadingActivity;
import com.vvit.aammu.lmsvvit.R;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.model.Leave;
import com.vvit.aammu.lmsvvit.model.Leaves;
import com.vvit.aammu.lmsvvit.widget.WidgetService;

import java.util.List;

public class FirebaseUtils {

    private static final String KEY_EMPLOYEE = "employee";
    private static final String KEY_CLS = "cls";
    private static final String KEY_SLS = "sls";
    private static final String KEY_MLS = "mls";
    private final DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private Employee employee;
    private Activity activity;
    private long count = 0;
    private long employeeCount;
    private static final String KEY_NAME = "name";
    private static final String KEY_DESIGNATION = "designation";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_LEAVE = "leave";
    private static final String KEY_LEAVES = "leaves";
    private static final String KEY_LEAVES_LIST = "leaves/leave";

    public FirebaseUtils() {
    }

    Boolean flag;
    private MyAdapter adapter;

   

    private ApplicantAdapter applicantAdapter;

    public FirebaseUtils(Activity activity, MyAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
    }

    public FirebaseUtils(Activity activity, DatabaseReference databaseReference) {
        this.activity = activity;
    }


    public FirebaseUtils(FragmentActivity activity, ApplicantAdapter adapter) {
    }

    public FirebaseUtils(Activity activity) {
        this.activity=activity;
    }

    public void updatePersonalInfo(final Employee employee1) {
        if (checkNetwork()) {
            employee1.toString();
            mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                Leaves leaves;

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        employee = data.getValue(Employee.class);
                        if (employee1.getName().equals(employee.getName())) {
                            data.getRef().child(KEY_NAME).setValue(employee1.getName());
                            data.getRef().child(KEY_DESIGNATION).setValue(employee1.getDesignation());
                            data.getRef().child(KEY_DEPARTMENT).setValue(employee1.getDepartment());
                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else
            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show();
    }

    public void addLeave(final Employee employee1, final Leave leave) {
        if (checkNetwork()) {
            mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        employee = data.getValue(Employee.class);
                        employee.toString();
                        if (employee1.getName().equals(employee.getName())) {
                            if (data.hasChild(KEY_LEAVES_LIST)) {
                                long childCount = data.child(KEY_LEAVES).child(KEY_LEAVE).getChildrenCount();
                                childCount++;
                                data.getRef().child(KEY_LEAVES_LIST + "/" + String.valueOf(childCount)).setValue(leave);
                            } else
                                data.getRef().child(KEY_LEAVES_LIST + "/1").setValue(leave);
                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else
            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show();
    }

    public void fetchData(final String emailId) {
        Log.i("LMSApp","FirebaseUtils - fetchData");
        if (checkNetwork()) {
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(activity.getString(R.string.fetching_data));
            progressDialog.show();
            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                Leaves leaves;

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        employee = data.getValue(Employee.class);
                        assert employee != null;
                        if (emailId.equals(employee.getEmailId())) {
                            leaves = data.child(KEY_LEAVES).getValue(Leaves.class);
                            employee.setLeaves(leaves);
                            Intent intent = new Intent(activity, HomeActivity.class);
                            intent.putExtra(KEY_EMPLOYEE, employee);
                            activity.finish();
                            activity.startActivity(intent);
                            activity.startService(new Intent(activity, WidgetService.class));
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
        } else {
            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    public void checkLogin(final String email, final String password) {
        if (checkNetwork()) {
            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                Leaves leaves;

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    employeeCount = dataSnapshot.getChildrenCount();
                    int i = 1;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        employee = data.getValue(Employee.class);
                        if (employee.getEmailId().equals(email) && employee.getPassword().equals(password)) {
                            leaves = data.child(KEY_LEAVES).getValue(Leaves.class);
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
                        Toast.makeText(activity, R.string.invalid_auth, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else
            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show();
    }

    public void updateStatus(final Employee employee, final Leave.Status status) {
        if (checkNetwork()) {
            mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i = 0;
                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                        final Employee employee1 = data.getValue(Employee.class);
                        assert employee1 != null;
                        if (employee1.getName().equals(employee.getName())) {
                            if (data.hasChild(KEY_LEAVES_LIST)) {
                                data.getRef().child(KEY_LEAVES_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int childCount = 1;
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            Leave leave = data.child(KEY_LEAVES_LIST).child(String.valueOf(childCount)).getValue(Leave.class);
                                           if (leave.getStatus().equals(Leave.Status.APPLIED) && status.equals(Leave.Status.ACCEPTED)) {
                                                if (leave.getLeaveType().equalsIgnoreCase("Casual Leaves")) {
                                                    int count = employee1.getLeaves().getcls();
                                                    count = count - leave.getNoOfDays();
                                                    data.getRef().child(KEY_LEAVES).child(KEY_CLS).setValue(count);

                                                } else if (leave.getLeaveType().equalsIgnoreCase("Sick Leaves")) {
                                                    count = employee1.getLeaves().getsls();
                                                    count -= leave.getNoOfDays();
                                                    data.getRef().child(KEY_LEAVES).child(KEY_SLS).setValue(count);

                                                } else if(leave.getLeaveType().equalsIgnoreCase("Maternity Leaves")){
                                                    count = employee1.getLeaves().getmls();
                                                    count -= leave.getNoOfDays();
                                                    data.getRef().child(KEY_LEAVES).child(KEY_MLS).setValue(count);

                                                }
                                                data.getRef().child(KEY_LEAVES_LIST).child(String.valueOf(childCount)).child("status").setValue(status);

                                            }
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
                    flag = true;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    flag = false;
                }
            });
        } else {
            flag = false;
            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    public void getAppliedLeaves() {
        if (checkNetwork()) {
            mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int j = 1;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Employee employee = dataSnapshot1.getValue(Employee.class);
                        List<Leave> leaves = employee.getLeaves().getLeave();
                        if (leaves != null) {
                            for (int i = 1; i < leaves.size(); i++) {
                                if (leaves.get(i).getStatus().equals(Leave.Status.APPLIED)) {
                                    String title = employee.getName();
                                    String body = activity.getString(R.string.applied_leave_for) +" "+ String.valueOf(leaves.get(i).getNoOfDays()) +" " +activity.getString(R.string.days);
                                    MyNotificationManager.getInstance(activity, employee).displayNotification(title, body);
                                }
                            }
                        }
                        j++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else
            Toast.makeText(activity, R.string.no_internet, Toast.LENGTH_SHORT).show();

    }

    public boolean checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (connectivityManager != null) {
            info = connectivityManager.getActiveNetworkInfo();
        }
        if (info == null) {
            return false;
        }
        return true;

    }

    public int checkSpeed() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        int linkSpeed = wifiManager.getConnectionInfo().getRssi();
        int level = WifiManager.calculateSignalLevel(linkSpeed, 5);
        return level;
    }
}
