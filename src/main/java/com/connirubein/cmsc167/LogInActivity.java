package com.connirubein.cmsc167;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LogInActivity extends AppCompatActivity {

    private Button buttonLogin;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private TextView textViewNotif;

    //Declaration DatabaseHelper
    DBHandler databaseHelper;

    //Declaration TextInputLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
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
                case R.id.btnLogin:        //go directly to map activity after logging in
                    openLogInActivity();
                    break;
            }
        }
    };

    private void initViews(){
        buttonLogin = findViewById(R.id.btnLogin);
        editTextUsername = findViewById(R.id.et_name);
        editTextPassword = findViewById(R.id.et_password);
        textViewNotif = findViewById(R.id.tv_notif);

    }

    private void initListeners(){
        buttonLogin.setOnClickListener(btnOnClickListener);
    }

    public boolean validate() {
        boolean valid = false;

        //Get values from EditText fields
        String UserName = editTextUsername.getText().toString();
        String Password = editTextPassword.getText().toString();

        //Handling validation for Email field
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(UserName).matches()) {
            valid = false;
            textViewNotif.setText("Please enter valid email!");
        } else {
            valid = true;
            textViewNotif.setText("");
        }

        //Handling validation for Password field
        if (Password.isEmpty()) {
            valid = false;
            textViewNotif.setText("Please enter valid password!");
        } else {
            if (Password.length() > 5) {
                valid = true;
                textViewNotif.setText("");
            } else {
                valid = false;
                textViewNotif.setText("Password is to short!");
            }
        }

        return valid;
    }

    private void openLogInActivity(){

        if (validate()) {

            //Get values from EditText fields
            String UserName = editTextUsername.getText().toString();
            String Password = editTextPassword.getText().toString();

            //Authenticate user
            Person currentUser = databaseHelper.Authenticate(new Person(null, UserName, Password));

            //Check Authentication is successful or not
            if (currentUser != null) {
                //User Logged in Successfully Launch map activity
                Intent intent = new Intent(LogInActivity.this, MainMenu.class);

//                intent.putExtra("LoggedInUserId",dbHandler.getPersonIDByEmail(editTextEmail.getText().toString()));
                SharedPreferences sharedPreferences = getSharedPreferences("userEmail", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("loggedInUser", UserName);
                Snackbar.make(buttonLogin, "Logged in successfully! Loading map...", Snackbar.LENGTH_LONG).show();
                editor.apply();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
                startActivity(intent);
                finish();
            } else {

                //User Logged in Failed
                Snackbar.make(buttonLogin, "Failed to log in , please try again", Snackbar.LENGTH_LONG).show();

            }
        }

        databaseHelper.close();

    }
}
