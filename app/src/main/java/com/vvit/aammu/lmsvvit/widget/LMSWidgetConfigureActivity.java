package com.vvit.aammu.lmsvvit.widget;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvit.aammu.lmsvvit.R;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.utils.FirebaseUtils;
import com.vvit.aammu.lmsvvit.utils.PrefManager;

import java.util.Date;

/**
 * The configuration screen for the {@link LMSWidget LMSWidget} AppWidget.
 */
public class LMSWidgetConfigureActivity extends Activity implements LoaderManager.LoaderCallbacks<String> {

    private static final String PREFS_NAME = "com.vvit.aammu.lmsvvit.widget.LMSWidget";
    private static final String PREF_LEAVE = "pref_leaves";
    private static final String PREF_TITLE = "pref_title";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    FirebaseUtils firebaseUtils;
    PrefManager prefManager;
    static String result;
    ProgressDialog dialog;
    static String name;

    public LMSWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveLeavePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_LEAVE,result);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadLeavePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String result = prefs.getString(PREF_LEAVE,"");
        return result;
    }

    static void deleteLeavePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_LEAVE);
        prefs.apply();
    }

    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_TITLE,name);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String result = prefs.getString(PREF_TITLE,"");
        return result;
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_TITLE);
        prefs.apply();
    }
    public void loadWidget(){
        final Context context = LMSWidgetConfigureActivity.this;

        saveTitlePref(context, mAppWidgetId, name);
        saveLeavePref(context,mAppWidgetId,result);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        LMSWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
// Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();

    }
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        dialog = new ProgressDialog(this);
        firebaseUtils = new FirebaseUtils(this, (DatabaseReference) null);
        // Find the widget id from the intent.

        prefManager=new PrefManager(this);
        getLoaderManager().initLoader(23,null,this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        //mAppWidgetText.setText(loadTitlePref(LMSWidgetConfigureActivity.this, mAppWidgetId));
    }

    private void getData() {
        final String emailId = prefManager.getUserEmail();
        if (firebaseUtils.checkNetwork()) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        StringBuilder builder = new StringBuilder();
                        Employee employee = dataSnapshot1.getValue(Employee.class);
                        if (emailId.equals(employee.getEmailId())) {

                            if (employee.getDesignation().equalsIgnoreCase("HOD")) {
                                Log.i("Widget", "Hod");
                                builder.append("List of Staff with Balance Leaves:\n");
                                for(DataSnapshot dataSnapshot2:dataSnapshot.getChildren()) {
                                    Employee employee1 = dataSnapshot2.getValue(Employee.class);
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
                        }
                        else {
                            Log.i("Widget","Not Hod");

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

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
            result = getString(R.string.no_internet);
    }

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

