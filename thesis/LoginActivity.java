package com.example.administrator.biodiversityapplication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.Html;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {


    //Declaration EditTexts
    private EditText editTextEmail;
    private EditText editTextPassword;

    //Declaration TextInputLayout
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    //Declaration Button
    private Button buttonLogin;

    //Declaration DataBaseHelper
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initListeners();
        initObjects();
    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.buttonLogin:        //go directly to map activity after logging in
                    openMapActivity();
                    break;
            }
        }
    };

    //This method is used to validate input given by user
    public boolean validate() {
        boolean valid = false;

        //Get values from EditText fields
        String Email = editTextEmail.getText().toString();
        String Password = editTextPassword.getText().toString();

        //Handling validation for Email field
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            valid = false;
            textInputLayoutEmail.setError("Please enter valid email!");
        } else {
            valid = true;
            textInputLayoutEmail.setError(null);
        }

        //Handling validation for Password field
        if (Password.isEmpty()) {
            valid = false;
            textInputLayoutPassword.setError("Please enter valid password!");
        } else {
            if (Password.length() > 5) {
                valid = true;
                textInputLayoutPassword.setError(null);
            } else {
                valid = false;
                textInputLayoutPassword.setError("Password is to short!");
            }
        }

        return valid;
    }

    private void openMapActivity(){

        if (validate()) {

            //Get values from EditText fields
            String Email = editTextEmail.getText().toString();
            String Password = editTextPassword.getText().toString();

            //Authenticate user
            Person currentUser = dbHandler.Authenticate(new Person(null, null, Email, Password));

            //Check Authentication is successful or not
            if (currentUser != null) {
                //User Logged in Successfully Launch map activity
                Intent intent = new Intent(LoginActivity.this, MapActivity.class);

//                intent.putExtra("LoggedInUserId",dbHandler.getPersonIDByEmail(editTextEmail.getText().toString()));
                SharedPreferences sharedPreferences = getSharedPreferences("userEmail", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("loggedInUser", Email);
                Snackbar.make(buttonLogin, "Logged in successfully! Loading map...", Snackbar.LENGTH_LONG).show();
                editor.apply();
                new Handler().postDelayed(() -> startActivity(intent), 2000);
                startActivity(intent);
                finish();
            } else {

                //User Logged in Failed
                Snackbar.make(buttonLogin, "Failed to log in , please try again", Snackbar.LENGTH_LONG).show();

            }
        }

        dbHandler.close();

    }

    private void initViews(){
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
    }

    private void initListeners(){
        buttonLogin.setOnClickListener(btnOnClickListener);
    }

    private void initObjects(){
        dbHandler = new DBHandler(this);
    }

    //This method is for handling fromHtml method deprecation
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

           result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}