package com.vvit.aammu.lmsvvit.widget;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvit.aammu.lmsvvit.R;
import com.vvit.aammu.lmsvvit.model.Employee;

public class WidgetService extends Service implements LoaderManager.LoaderCallbacks<String>{

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private String result;
    private String name;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        getData();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1,new Notification());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void getData() {
        SharedPreferences preferences1 = getSharedPreferences(getString(R.string.my_pref), 0);
        final String emailId = preferences1.getString(getString(R.string.email), "");
        if (emailId != null) {
            if (checkNetwork()) {
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            StringBuilder builder = new StringBuilder();
                            Employee employee = dataSnapshot1.getValue(Employee.class);
                            if (emailId.equals(employee.getEmailId())) {
                                if (employee.getDesignation().equalsIgnoreCase(getString(R.string.hod))) {

                                    name = employee.getName();
                                    builder.append(getString(R.string.leaves_staff_balance));
                                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                        Employee employee1 = dataSnapshot2.getValue(Employee.class);
                                        if (!employee1.getDesignation().equalsIgnoreCase(getString(R.string.hod)))
                                            {
                                                builder.append(employee1.getName());
                                                builder.append(":-\n");
                                                int leave_count = employee1.getLeaves().getcls();
                                                int sick_count = employee1.getLeaves().getsls();
                                                builder.append(getString(R.string.cl_balance) + leave_count);
                                                builder.append("\n");
                                                builder.append(getString(R.string.sl_balance) + sick_count);
                                                builder.append("\n");
                                                if (employee1.getGender().equals(getString(R.string.female))) {
                                                    int maternity_count = employee1.getLeaves().getsls();
                                                    builder.append(getString(R.string.ml_balance) + maternity_count);
                                                    builder.append("\n");
                                                }
                                            }
                                        }
                                    
                                        result = builder.toString();
                                        loadWidget();
                                        break;
                                    } else {
                                        name = employee.getName();
                                        int leave_count = employee.getLeaves().getcls();
                                        int leave_balance = 15 - leave_count;
                                        int sick_count = employee.getLeaves().getsls();
                                        int sick_balance = 10 - sick_count;
                                        builder.append(getString(R.string.cl_balance) + leave_count);
                                        builder.append("\n");
                                        builder.append(getString(R.string.cl_used) + leave_balance);
                                        builder.append("\n");
                                        builder.append(getString(R.string.sl_balance) + sick_count);
                                        builder.append("\n");
                                        builder.append(getString(R.string.sl_used) + sick_balance);
                                        if (employee.getGender().equals(getString(R.string.female))) {
                                            int maternity_count = employee.getLeaves().getsls();
                                            int maternity_balance = 10 - sick_count;
                                            builder.append("\n");
                                            builder.append(getString(R.string.ml_balance) + maternity_count);
                                            builder.append("\n");
                                            builder.append(getString(R.string.ml_used) + maternity_balance);
                                        }
                                        result = builder.toString();
                                        loadWidget();
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else
                result = getString(R.string.no_internet);
            }
            else
            {
            }
        }

        private void loadWidget() {
            Intent widgetUpdateIntent = new Intent();
            widgetUpdateIntent.setAction(LMSWidget.WIDGET_UPDATE_ACTION);
            widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);

            widgetUpdateIntent.putExtra(getString(R.string.name),name);
            widgetUpdateIntent.putExtra(getString(R.string.result),result);
            sendBroadcast(widgetUpdateIntent);
            LMSWidget.updateAppWidget(this,widgetUpdateIntent);
        }


        public boolean checkNetwork() {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = null;
            if (connectivityManager != null) {
                info = connectivityManager.getActiveNetworkInfo();
            }
            if (info == null) {
                return false;
            }
            return true;

        }

        @SuppressLint("StaticFieldLeak")
        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<String>(getApplicationContext()) {
                @Override
                public String loadInBackground() {
                    getData();
                    return result;
                }

                @Override
                protected void onStartLoading() {
                    if(result==null)
                        forceLoad();
                    else
                        deliverResult(result);

                }

                @Override
                public void deliverResult(String data) {
                    result = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
            loadWidget();
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }
    }