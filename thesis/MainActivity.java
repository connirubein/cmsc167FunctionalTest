package com.example.administrator.biodiversityapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUIViews();
        dbHandler = new DBHandler(this, null, null, 1);

    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_hmlogin:          //open log in activity
                    openLogInActivity();
                    break;
                case R.id.btn_hmsignup:         //open sign up activity
                    openSignUpActivity();
                    break;
            }
        }
    };

    private void openSignUpActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void openLogInActivity(){  //open LogInActivity page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void setUpUIViews(){
        findViewById(R.id.btn_hmlogin).setOnClickListener(btnOnClickListener);
        findViewById(R.id.btn_hmsignup).setOnClickListener(btnOnClickListener);
    }
}
