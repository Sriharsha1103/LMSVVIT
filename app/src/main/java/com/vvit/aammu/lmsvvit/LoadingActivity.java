package com.vvit.aammu.lmsvvit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vvit.aammu.lmsvvit.utils.FirebaseUtils;
import com.vvit.aammu.lmsvvit.utils.PrefManager;

public class LoadingActivity extends AppCompatActivity {
    private FirebaseUtils firebaseUtils;

    public PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        prefManager = new PrefManager(this);
        String email = prefManager.getUserEmail();
        firebaseUtils = new FirebaseUtils(this);
        firebaseUtils.fetchData(email);

    }


}
