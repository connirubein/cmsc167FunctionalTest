package com.connirubein.cmsc167;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    private Button buttonLogout;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private TextView textViewNotif;

    //Declaration DatabaseHelper
    DBHandler databaseHelper;

    //Declaration TextInputLayout
    TextInputLayout textInputLayoutUserName;
    TextInputLayout textInputLayoutPassword;

    ListView lView;
    TextView noRecordLabel;
    //ListAdapterOfRecords lAdapter;
    TextView textViewTextUserlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        initObjects();
        initViews();
        displayInit();
        initListeners();

    }

    private void initObjects(){
        databaseHelper = new DBHandler(this);
    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnLogout:        //go directly to map activity after logging in
                    openMainActivity();
                    break;
            }
        }
    };

    private void initViews(){
        buttonLogout = findViewById(R.id.btnLogout);
        textViewTextUserlist = findViewById(R.id.tv_userlist);

    }

    private void displayInit(){
        String[] allNamesList = databaseHelper.getAllNames();
        String allNames = "LIST OF USERS:\n";
        //editTextUserlist.setText("LIST OF USERS:\n"+databaseHelper.getAllNames());
        //System.out.println(allNamesList);
        for(int i=0; i<allNamesList.length;i++){
            allNames = allNames + allNamesList[i] + "\n";
        }
        textViewTextUserlist.setText(allNames);
        //System.out.println("HEEEEEEEEEEEEEY");

    }

    private void initListeners(){
        buttonLogout.setOnClickListener(btnOnClickListener);
    }

    private void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
