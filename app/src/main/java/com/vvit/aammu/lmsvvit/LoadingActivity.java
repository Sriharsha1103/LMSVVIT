package com.vvit.aammu.lmsvvit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.database.FirebaseDatabase;
import com.vvit.aammu.lmsvvit.utils.FirebaseUtils;
import com.vvit.aammu.lmsvvit.utils.PrefManager;

public class LoadingActivity extends AppCompatActivity {
    private FirebaseUtils firebaseUtils;

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        prefManager = new PrefManager(this);
        String email = prefManager.getUserEmail();
        Log.i("Email",email);
        firebaseUtils = new FirebaseUtils(LoadingActivity.this, FirebaseDatabase.getInstance().getReference("id"));
        firebaseUtils.fetchData(email);
    }


}
