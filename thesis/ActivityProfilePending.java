package com.example.administrator.biodiversityapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class ActivityProfilePending extends MenuBar {

    int passed_UserId;
    private Button btnMap;
    private Button btnRecord;
    private Button btnProfile;
    private ImageButton btnPending;
    private ImageButton btnSubmissions;
    DBHandler dbHandler;
    TextView tv_hikerName;

    ListView lView;
    TextView noRecordLabel;
    ListAdapterOfRecords lAdapter;
    private ArrayList<Record> listOf_records;
    String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pending);
        listOf_records = new ArrayList<Record>();
        SharedPreferences sp = getSharedPreferences("userEmail", Context.MODE_PRIVATE);
        currentEmail = sp.getString("loggedInUser", "");
        dbHandler = new DBHandler(this, null, null, 1);
        initializeArrayList(currentEmail);
        //initializeList();
        //listview
        noRecordLabel = (TextView) findViewById(R.id.noRecordLabel);
        tv_hikerName = (TextView) findViewById(R.id.hikerName);
        lView = (ListView) findViewById(R.id.pendingListView);
        lAdapter = new ListAdapterOfRecords(ActivityProfilePending.this,listOf_records);
        lView.setAdapter(lAdapter);
        if(listOf_records.size()==0){
            lView.setVisibility(View.GONE);
            noRecordLabel.setText("NO SAVED RECORDS");
        }
        else{
            noRecordLabel.setVisibility(View.GONE);
        }

        try {

            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    //Toast.makeText(ActivityProfilePending.this, listOf_records.get(i).get_datetime(), Toast.LENGTH_SHORT).show();
                    //shared preference to form activity for editing

                    //User Logged in Successfully Launch map activity
                    Intent intent = new Intent(ActivityProfilePending.this, FormActivity.class);

//                intent.putExtra("LoggedInUserId",dbHandler.getPersonIDByEmail(editTextEmail.getText().toString()));
                    SharedPreferences sharedPreferences = getSharedPreferences("updateRecordInfo", Context.MODE_PRIVATE);
                    //SharedPreferences.Editor editor = sharedPreferences.edit();
                    //editor.putString("datetimeOfRecord", listOf_records.get(i).get_datetime());
                    //editor.putInt("positionOfRecordInList",i);
                    //editor.putBoolean("editAndUpdateARecord", true);
                    //editor.apply();
                    intent.putExtra("positionOfRecordInList", i);
                    intent.putExtra("editAndUpdateARecord", true);
                    //intent.putExtra("datetimeWhenRecorded", listOf_records.get(i).get_datetime());
                    startActivity(intent);
                    finish();


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        //buttons
        btnPending = (ImageButton) findViewById(R.id.btnPending);
        btnSubmissions = (ImageButton) findViewById(R.id.btnSubmissions);
        btnMap = (Button) findViewById(R.id.btn_map);
        btnRecord = (Button) findViewById(R.id.btn_record);
        btnProfile = (Button) findViewById(R.id.btn_profile);

        //dbHandler.getPersonNameByEmail(currentEmail));

        //button listeners
        btnPending.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ActivityProfilePending.this, ActivityProfilePending.class);

                startActivity(intent);
            }
        });

        btnSubmissions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ActivityProfilePending.this, ActivityProfileSubmissions.class);

                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ActivityProfilePending.this, MapActivity.class);

                startActivity(intent);
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ActivityProfilePending.this, OpenCameraActivity.class);

                startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ActivityProfilePending.this, ActivityProfilePending.class);

                startActivity(intent);
            }
        });

    }

    private void initializeArrayList(String email) {
        listOf_records = dbHandler.getAllRecordsByEmailStatus(email, "pending");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
