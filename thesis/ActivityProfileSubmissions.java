package com.example.administrator.biodiversityapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityProfileSubmissions extends MenuBar {

    private Button btnMap;
    private Button btnRecord;
    private Button btnProfile;
    private ImageButton btnPending;
    private ImageButton btnSubmissions;
    private Spinner spinner;
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
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_profile_submissions);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title_bar);
        listOf_records = new ArrayList<Record>();
        SharedPreferences sp = getSharedPreferences("userEmail", Context.MODE_PRIVATE);
        currentEmail = sp.getString("loggedInUser", "");
        dbHandler = new DBHandler(this, null, null, 1);
        initializeArrayList(currentEmail);
        //initializeList();
        //listview
        noRecordLabel = (TextView) findViewById(R.id.noRecordLabel);
        tv_hikerName = (TextView) findViewById(R.id.hikerName);
        lView = (ListView) findViewById(R.id.submittedListView);
        lAdapter = new ListAdapterOfRecords(ActivityProfileSubmissions.this,listOf_records);
        //lAdapter = new ListAdapterOfPending(ActivityProfilePending.this,)
        lView.setAdapter(lAdapter);
        //lAdapter = new ListAdapterOfPending(ActivityProfilePending.this, listOf_speciesNames, listOf_commonNames, images,listOfDates);
//        tv_hikerName.setText("hello");
        if(listOf_records.size()==0){
            lView.setVisibility(View.GONE);
            //Toast.makeText(ActivityProfilePending.this, "NO SAVED RECORDS", Toast.LENGTH_LONG).show();
            noRecordLabel.setText("NO SUBMITTED RECORDS");
        }
        else{
            noRecordLabel.setVisibility(View.GONE);
        }

        try {

            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Toast.makeText(ActivityProfileSubmissions.this, listOf_records.get(i).get_datetime(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityProfileSubmissions.this, ViewRecordActivity.class);
                    intent.putExtra("positionOfRecordInList", i);
                    startActivity(intent);


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
        //spinner = (Spinner) findViewById(R.id.spinner);
        //String[] items = new String[]{"Download", "Email"};
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //spinner.setAdapter(adapter);

        //dbHandler.getPersonNameByEmail(currentEmail));

        //button listeners
        btnPending.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ActivityProfileSubmissions.this, ActivityProfilePending.class);

                startActivity(intent);
            }
        });

        btnSubmissions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ActivityProfileSubmissions.this, ActivityProfileSubmissions.class);

                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ActivityProfileSubmissions.this, MapActivity.class);

                startActivity(intent);
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ActivityProfileSubmissions.this, OpenCameraActivity.class);

                startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ActivityProfileSubmissions.this, ActivityProfilePending.class);

                startActivity(intent);
            }
        });

    }

    private void initializeArrayList(String email) {
        listOf_records = dbHandler.getAllRecordsByEmailStatus(email, "submitted");
        System.out.println("init list done");
        for (int i=0; i<listOf_records.size(); i++){
            System.out.println(listOf_records.get(i).get_commonName());
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}
