package com.connirubein.signuplogin;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.os.Handler;
//import android.support.design.widget.Snackbar;
//import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    private Button buttonSignup;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private TextView textViewNotif;

    //Declaration DatabaseHelper
    DBHandler databaseHelper;

    //Declaration TextInputLayout
    //TextInputLayout textInputLayoutUserName;
    //TextInputLayout textInputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        System.out.println("INIT:::::::::::::::::::");
        initViews();
        initListeners();
        initObjects();
    }

    private void initObjects(){
        databaseHelper = new DBHandler(this);
    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnSignup:        //go directly to map activity after logging in
                    openSignUpActivity();
                    break;
            }
        }
    };

    private void initViews(){
        buttonSignup = findViewById(R.id.btnSignup);
        editTextUsername = findViewById(R.id.et_name);
        editTextPassword = findViewById(R.id.et_password);
        textViewNotif = findViewById(R.id.tv_notif);
        System.out.println("initviews done");
    }

    private void initListeners(){
        buttonSignup.setOnClickListener(btnOnClickListener);
        System.out.println("initlisteners done");
    }

    private void openSignUpActivity(){
//        System.out.println("intent!!");
        if (validate()) {
            String UserName = editTextUsername.getText().toString();
            System.out.println("username: "+UserName);
            String Password = editTextPassword.getText().toString();
            System.out.println("password: "+ Password);

            //Check in the database if there is any person associated with  this email
            if (!databaseHelper.isUsernameExists(UserName)) {

                //Email does not exist now add new person to database
                databaseHelper.addPerson(new Person(null, UserName, Password));
                //Snackbar.make(buttonSignup, "User created successfully! Please Login.", Snackbar.LENGTH_LONG).show();
                textViewNotif.setText("SIGN-UP SUCCESSFUL");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);

            }else {

                //Email exists with email input provided so show error user already exist
                //Snackbar.make(buttonSignup, "User already exists with same username.", Snackbar.LENGTH_LONG).show();
                textViewNotif.setText("SIGN-UP FAILED");
            }

            //Check in database if there is any person associated with this username
            //TODO


        }
//        Intent intent = new Intent(this, SignUpActivity.class);
//        startActivity(intent);
    }

    public boolean validate() {
        boolean valid = false;

        //Get values from EditText fields
        String UserName = editTextUsername.getText().toString();
        String Password = editTextPassword.getText().toString();

        //Handling validation for UserName field
        if (UserName.isEmpty()) {
            valid = false;
            textViewNotif.setText("Please enter valid username!");
        } else {
            if (UserName.length() > 5) {
                valid = true;
                textViewNotif.setText("");
            } else {
                valid = false;
                textViewNotif.setText("Username is to short!");
            }
        }

        //Handling validation for Password field
        if (Password.isEmpty()) {
            valid = false;
            textViewNotif.setText("Please enter valid password!");
        } else {
            if (Password.length() > 5) {
                valid = true;
                //textInputLayoutPassword.setError(null);
            } else {
                valid = false;
                textViewNotif.setText("Password must be at least 6 characters.");
            }
        }


        return valid;
    }
}
