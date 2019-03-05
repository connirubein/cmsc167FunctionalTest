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
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.Manifest;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import static android.support.constraint.Constraints.TAG;

public class FormActivity extends AppCompatActivity implements LocationListener {

    EditText input_specName;
    EditText input_comName;
    EditText input_remarks;
    byte[] imageAsBytes;
    DBHandler dbHandler;
    String currentEmail;
    int recordPosition;
    boolean updateARecord;
    LocationManager lm;
    Location l;
    String provider;
    Button btn_geopoint, btn_save, btn_submit;
    Double lat = 0.0;
    Double lng = 0.0;
    String datetimeRecorded;
    ImageView image_view;
    RadioButton gps, map;
    TextView alti, lati, lngi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_form);
            initViews();
            initListeners();
            System.out.println("edit and update boolean 1 ----------------> "+updateARecord);
            initPassedValues();
            System.out.println("edit and update boolean 2 ----------------> "+updateARecord);
            initdbHandlerAndBtns();
            System.out.println("edit and update boolean----------------> "+updateARecord);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initdbHandlerAndBtns() {
        dbHandler = new DBHandler(this, null, null, 1);
        if(updateARecord){
            fillInTextFields(recordPosition);
        }
        else{
            btn_save.setEnabled(false);
            btn_submit.setEnabled(false);

        }
    }

    private void initPassedValues() {
        SharedPreferences sp = getSharedPreferences("userEmail", Context.MODE_PRIVATE);
        currentEmail = sp.getString("loggedInUser", "");

        Intent intent = getIntent();
        //imageAsBytes = intent.getByteArrayExtra("byteArray");


        updateARecord = intent.getBooleanExtra("editAndUpdateARecord", false);
        recordPosition = intent.getIntExtra("positionOfRecordInList", -1);
        //datetimeRecorded = intent.getStringExtra("datetimeWhenRecorded");
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void initListeners(){
        btn_save.setOnClickListener(btnOnClickListener);
        btn_submit.setOnClickListener(btnOnClickListener);
    }

    private void initViews(){
        input_specName = findViewById(R.id.et_specName);
        input_comName = findViewById(R.id.et_comName);
        input_remarks = findViewById(R.id.et_remarks);
        btn_save = findViewById(R.id.btn_save);
        btn_submit = findViewById(R.id.btn_submit);

        image_view = findViewById(R.id.image_view);

        updateARecord = false;
        recordPosition = -1;
        gps = findViewById(R.id.rb_gps);
        map = findViewById(R.id.rb_map);

        alti = findViewById(R.id.tv_alti);
        lati = findViewById(R.id.tv_lati);
        lngi = findViewById(R.id.tv_lngi);
        gps.setEnabled(false);
        map.setEnabled(false);
    }

    private void fillInTextFields(int position) {
        ArrayList<Record> records = dbHandler.getAllRecordsByEmailStatus(currentEmail,"pending");
        Record record = records.get(position);
        LatLong location = dbHandler.getLocationByRecordID(record.get_id());

        byte[] recordImage = record.get_imageData();
        Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage, 0, recordImage.length);

        try {
            image_view.setImageBitmap(bitmap);
            input_specName.setText(record.get_speciesName());
            input_comName.setText(record.get_commonName());
            input_remarks.setText(record.get_remarks());
            lati.setText(location.getLatitude());
            lngi.setText(location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(updateARecord){
            new AlertDialog.Builder(this).setTitle("Unsaved Changes")
                    .setMessage("You have unsaved changes.\n\nAre you sure you want to leave this activity?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> openSavedRecords()).create().show();
        }
        else{
            new AlertDialog.Builder(this).setTitle("Unsaved Changes")
                    .setMessage("You have unsaved changes.\n\nAre you sure you want to leave this activity?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> openCamera()).create().show();
        }
    }

    public void openSavedRecords(){
        Intent intent = new Intent(FormActivity.this, ActivityProfilePending.class);
        startActivity(intent);
    }

    public void openCamera(){
        Intent intent = new Intent(FormActivity.this, OpenCameraActivity.class);
        startActivity(intent);
    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.btn_save:           //open record activity
                    openSaveSubmitRecord("pending");
                    break;
                case R.id.btn_submit:
                    if(areAllFieldsFilled()){
                        if(updateARecord){
                            //no need to insert another location
                            updateRecordSubmit();
                        }
                        else{
                            openSaveSubmitRecord("submitted");

                        }
                        openMapActivity();
                        break;
                    }

                    else{
                        Snackbar.make(btn_submit, "Fill out all fields before submitting", Snackbar.LENGTH_LONG).show();
                    }

                    break;
//                case R.id.btn_geoPoint:
//                    if (hasLocation()) {
//                        Snackbar.make(btn_geopoint, "Location found!", Snackbar.LENGTH_LONG).show();
//                        btn_save.setEnabled(true);
//                        btn_submit.setEnabled(true);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                            }
//                        }, 2000);
//                    }else {
//                        Snackbar.make(btn_geopoint, "No location found!", Snackbar.LENGTH_LONG).show();
//                        btn_save.setEnabled(false);
//                        btn_submit.setEnabled(false);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                            }
//                        }, 2000);
//                    }
//                    break;
            }
        }
    };

    private boolean areAllFieldsFilled() {
        System.out.println("------------ARE ALL FIELDS FILLED---------------");
        System.out.println("---->" + input_specName.getText());
        System.out.println("---->" + input_comName.getText());
        System.out.println("---->" + input_remarks.getText());
        if (input_specName.getText().length() == 0 || input_comName.getText().length() == 0 || input_remarks.getText().length() == 0){
            System.out.println("--------------------------->not all fields are filled");
            return false;
        }
        System.out.println("all fields are filled");
        return true;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void updateRecordSubmit(){
        ArrayList<Record> records = dbHandler.getAllRecordsByEmailStatus(currentEmail,"pending");
        Record oldRecord = records.get(recordPosition);

        Record newRecord  = new Record(dbHandler.getPersonIDByEmail(currentEmail), input_specName.getText().toString(), input_comName.getText().toString(),
                input_remarks.getText().toString(), oldRecord.get_imageData(), "submitted", oldRecord.get_datetime());
        System.out.println("new record:");
        System.out.println(newRecord.get_id());
        dbHandler.updateRecordSubmit(oldRecord, newRecord);
    }

    private boolean hasLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //System.out.println("--------> so nag ask syag permission <---------");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            //    call ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }else {
            Log.v(TAG, "------------------> Permission is granted");
        }

        try {
            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //System.out.println(lm.getProviders(true).toString() + " walaaaaaaaaaaa <----------");
            //if (lm != null) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

            //get location
            l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (l != null) { //if network provider is not null then it will return true already (meaning location is found)
                System.out.println(l.getLatitude() + " latitude");
                System.out.println(l.getLongitude() + " longitude");
                return true;
            } else { //else if network provider is null, try using the gps provider instead.
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (l != null){
                    //System.out.println("-----------> gps provider");
                    System.out.println(l.getLatitude() + " latitude");
                    System.out.println(l.getLongitude() + " longitude");
                    return true;
                } else {
                    System.out.println("No Provider!--------------> wala na talaga");
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void openSaveSubmitRecord(String record_status){
        System.out.println("will make record");
//        ImageView imageView = (ImageView) findViewById(R.id.iv_image);
//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (updateARecord) {
            ArrayList<Record> records = dbHandler.getAllRecordsByEmailStatus(currentEmail,record_status);
            Record oldRecord = records.get(recordPosition);
            System.out.println("passed to oldrecord:");
            System.out.println("record id = " + records.get(recordPosition).get_id());
            System.out.println("old record:");
            System.out.println(oldRecord.get_id());
            Record newRecord  = new Record(dbHandler.getPersonIDByEmail(currentEmail), input_specName.getText().toString(), input_comName.getText().toString(),
                    input_remarks.getText().toString(), oldRecord.get_imageData(), record_status, oldRecord.get_datetime());
            System.out.println("new record:");
            System.out.println(newRecord.get_id());
            dbHandler.updateRecordSave(oldRecord,newRecord);
        }

        //else if making a new record
        else {
            byte[] imageInByte = imageAsBytes;
            Record record  = new Record(dbHandler.getPersonIDByEmail(currentEmail), input_specName.getText().toString(), input_comName.getText().toString(),
                    input_remarks.getText().toString(), imageInByte, record_status, getDateTime());
            System.out.println("just made");
            record = dbHandler.addRecord(record);
            System.out.println("record id now is: "+record.get_id());
            //record.set_id(dbHandler.setRecordIDFromDB(record));
            System.out.println("record status was: "+record.get_status());
            lat = l.getLatitude();
            lng = l.getLongitude();
            dbHandler.addLocation(new LatLong(null,record.get_id(),lat.toString(),lng.toString()));
        }
        //dbHandler.addProduct( new Record (input_specName.getText().toString(), input_comName.getText().toString(),
                //input_remarks.getText().toString(), input_location.getText().toString(), "pending"));
        Intent intent = new Intent(this, ActivityProfilePending.class);
        //intent.putExtra("LoggedInUserId",passed_UserId);
        startActivity(intent);
    }

    private void openSubmitRecord(){
        lat = l.getLatitude();
        lng = l.getLongitude();
        dbHandler.addLocation(new LatLong(null,null,lat.toString(),lng.toString()));
        dbHandler.close();
    }

    public void openMapActivity() {
        Intent intent = new Intent(FormActivity.this, MapActivity.class);
        startActivity(intent);
    }

    public void onLocationChanged(Location location) {
        //lat = location.getLatitude();
        //lng = location.getLongitude();
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderEnabled(String s) {

    }

    public void onProviderDisabled(String s) {

    }

}
