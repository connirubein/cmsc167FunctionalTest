package com.example.administrator.biodiversityapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.Manifest;
import android.widget.ImageView;
import android.widget.TextView;

import static android.support.constraint.Constraints.TAG;

public class ViewRecordActivity extends AppCompatActivity{


    DBHandler dbHandler;
    String currentEmail;
    int recordPosition;

    TextView view_specName;
    TextView view_comName;
    TextView view_remarks;
    TextView view_location;
    TextView view_datetime;
    ImageView view_image;
    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_record);
        initPassedValues();
        initViews();
        initListeners();
        fillInFields();

    }

    private void initPassedValues() {
        dbHandler = new DBHandler(this, null, null, 1);
        SharedPreferences sp = getSharedPreferences("userEmail", Context.MODE_PRIVATE);
        currentEmail = sp.getString("loggedInUser", "");
        Intent intent = getIntent();
        recordPosition = intent.getIntExtra("positionOfRecordInList", -1);
    }

    private void initListeners(){
        btn_back.setOnClickListener(btnOnClickListener);
    }

    private void initViews(){
        view_specName = findViewById(R.id.view_specName);
        view_comName = findViewById(R.id.view_comName);
        view_remarks = findViewById(R.id.view_remarks);
        view_location = findViewById(R.id.view_location);
        view_datetime = findViewById(R.id.view_datetime);
        view_image = (ImageView) findViewById(R.id.view_image);
        btn_back = findViewById(R.id.btn_back);
    }

    private void fillInFields() {
        ArrayList<Record> records = dbHandler.getAllRecordsByEmailStatus(currentEmail,"submitted");
        Record record = records.get(recordPosition);
        LatLong location = dbHandler.getLocationByRecordID(record.get_id());

        byte[] recordImage = record.get_imageData();
        Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage, 0, recordImage.length);

        try {
            view_specName.setText(record.get_speciesName());
            view_comName.setText(record.get_commonName());
            view_remarks.setText(record.get_remarks());
            view_datetime.setText(record.get_datetime());
            view_image.setImageBitmap(bitmap);
            view_location.setText("Longitude: " + location.getLongitude() + "\n" + "Latitude: " + location.getLatitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.btn_back:           //open record activity
                    Intent intent = new Intent(ViewRecordActivity.this, ActivityProfileSubmissions.class);

                    startActivity(intent);
                    break;
            }
        }
    };



}
