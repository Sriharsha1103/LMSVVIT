package com.vvit.aammu.lmsvvit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.vvit.aammu.lmsvvit.utils.FirebaseUtils;
import com.vvit.aammu.lmsvvit.utils.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.email)
    EditText inputEmail;
    @BindView(R.id.password)
    EditText inputPassword;
    @BindView(R.id.progressBar)
    ProgressBar progress;
    @BindView(R.id.btn_login)
    Button login;
    private FirebaseUtils firebaseUtils;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLogin()) {
            launchHomeScreen();
            finish();
        }

        firebaseUtils = new FirebaseUtils(this,FirebaseDatabase.getInstance().getReference("id"));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                    Toast.makeText(LoginActivity.this, R.string.type_password,Toast.LENGTH_SHORT).show();
                else {
                    prefManager.setUserEmail(email);
                    prefManager.setFirstTimeLogin(false);
                    firebaseUtils.checkLogin(email, password);
                }
            }
        });



    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLogin(false);
        startActivity(new Intent(LoginActivity.this, LoadingActivity.class));
        finish();
    }

}
