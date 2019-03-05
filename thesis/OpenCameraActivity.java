package com.example.administrator.biodiversityapplication;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class OpenCameraActivity extends AppCompatActivity implements LocationListener {

    int passed_UserId;
    static final int REQUEST_PICTURE_CAPTURE = 1;
    private String pictureFilePath;
    private File pictureFile;
    private Uri image_data;

    EditText input_specName;
    EditText input_comName;
    EditText input_remarks;
    byte[] imageAsBytes;
    DBHandler dbHandler;
    String currentEmail;
    int recordPosition;
    boolean rb_map_clicked, gotLocationAlready;
    LocationManager lm;
    Location l;
    String provider;
    Button btn_save, btn_submit;
    Double lat = 0.0;
    Double lng = 0.0;
    String datetimeRecorded;
    ImageView image_view;
    RadioButton gps, map;
    TextView alti, lati, lngi;
    Double loc_lat, loc_long;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        initPassedValues();
        System.out.println("passed values done");
        initViews();
        System.out.println("init views done");
        initListeners();
        System.out.println("init listeners done");
        System.out.println("edit and update boolean 2 ----------------> "+rb_map_clicked);
        initdbHandlerAndBtns();
        //image.setVisibility(View.INVISIBLE);

        if (rb_map_clicked){
            //xml
        }
        else {
            takePictureIntent();
        }
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
        recordPosition = -1;
        alti = findViewById(R.id.tv_alti);
        lati = findViewById(R.id.tv_lati);
        lngi = findViewById(R.id.tv_lngi);
        gps = findViewById(R.id.rb_gps);
        map = findViewById(R.id.rb_map);

        if(gotLocationAlready) {
            map.setChecked(true);
            gps.setEnabled(false);
            map.setEnabled(false);
        }

        try {
            if(rb_map_clicked){
                Bundle fields = getIntent().getExtras();
                input_specName.setText(fields.getString("pass_specName"));
                input_comName.setText(fields.getString("pass_commonName"));
                input_remarks.setText(fields.getString("pass_remarks"));
                Bitmap bitmap = BitmapFactory.decodeByteArray(fields.getByteArray("pass_img"), 0, fields.getByteArray("pass_img").length);
                image_view.setImageBitmap(bitmap);
                loc_long = fields.getDouble("map_long",0.0);
                loc_lat = fields.getDouble("map_lat",0.0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void initdbHandlerAndBtns() {
        dbHandler = new DBHandler(this, null, null, 1);
//        btn_save.setEnabled(false);
//        btn_submit.setEnabled(false);


    }

    private void initPassedValues() {
        SharedPreferences sp = getSharedPreferences("userEmail", Context.MODE_PRIVATE);
        currentEmail = sp.getString("loggedInUser", "");

        try {
            Intent intent = getIntent();
            rb_map_clicked = intent.getBooleanExtra("fromMap", false);
            gotLocationAlready = intent.getBooleanExtra("savedALocation", false);
//            loc_lat = intent.getDoubleExtra("map_lat", 0.0);
//            loc_long = intent.getDoubleExtra("map_long", 0.0);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //System.out.println("--------> so nag ask syag permission <---------");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
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

        try {

            Bitmap bitmap = ((BitmapDrawable) image_view.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageAsBytes = baos.toByteArray();

            Record record  = new Record(dbHandler.getPersonIDByEmail(currentEmail), input_specName.getText().toString(), input_comName.getText().toString(),
                    input_remarks.getText().toString(), imageAsBytes, record_status, getDateTime());
            System.out.println("img in byte: ----------------> "+imageAsBytes);
            System.out.println("just made");
            record = dbHandler.addRecord(record);
            System.out.println("record id now is: "+record.get_id());
            //record.set_id(dbHandler.setRecordIDFromDB(record));
            System.out.println("record status was: "+record.get_status());
            if(gotLocationAlready){
                lat = loc_lat;
                lng = loc_long;
            }
            else{
                lat = l.getLatitude();
                lng = l.getLongitude();
            }
            dbHandler.addLocation(new LatLong(null,record.get_id(),lat.toString(),lng.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //dbHandler.addProduct( new Record (input_specName.getText().toString(), input_comName.getText().toString(),
        //input_remarks.getText().toString(), input_location.getText().toString(), "pending"));
        Intent intent = new Intent(this, ActivityProfilePending.class);
        //intent.putExtra("LoggedInUserId",passed_UserId);
        startActivity(intent);
    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.btn_save:           //open record activity
                    if(gps.isChecked() || map.isChecked()){
                        openSaveSubmitRecord("pending");
                    }
                    else{
                        Snackbar.make(btn_save, "Set a location before saving!", Snackbar.LENGTH_LONG).show();
                    }
                    break;
                case R.id.btn_submit:
                    if(areAllFieldsFilled()){
                        if(gps.isChecked() || map.isChecked()){
                            openSaveSubmitRecord("submitted");
                            openMapActivity();
                        }
                        else{
                            Snackbar.make(btn_save, "Set a location before submitting!", Snackbar.LENGTH_LONG).show();
                        }

                        break;
                    }

                    else{
                        Snackbar.make(btn_submit, "Fill out all fields before submitting", Snackbar.LENGTH_LONG).show();
                    }

                    break;
            }
        }
    };

    private void openFormActivity(){
        Intent intent = null;
        try {
            intent = new Intent(this, FormActivity.class);
            //ByteArrayOutputStream byteArrayOutPutStream = new ByteArrayOutputStream();
            //bitmap_img.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutPutStream);
            //byte[] byteImg = byteArrayOutPutStream.toByteArray();
            //String imageEncoded = Base64.encodeToString(byteImg, Base64.DEFAULT);
            //Log.d("Image Log:", imageEncoded);
            //intent.putExtra("byteArray",bbytes);
            intent.putExtra("pictureTaken", pictureFile);
            intent.putExtra("img_uri", image_data.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ablie form activity plsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
        }


        startActivity(intent);
    }

    private void openMapActivity(){
        Intent intent = new Intent(OpenCameraActivity.this, MapActivity.class);
        startActivity(intent);
    }

    private void saveToGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pictureFilePath);
        Uri picUri = Uri.fromFile(f);
        System.out.println("picUri -------------> " + picUri.toString());
        galleryIntent.setData(picUri);
        this.sendBroadcast(galleryIntent);
        image_data = picUri;


    }

    private void takePictureIntent(){
        //System.out.println("nisulod sya sa takePictureIntent() <------------------");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
        //cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null){
            //startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            try{
                pictureFile = getPictureFile();
                //System.out.println("pictureFile <----------- " + pictureFile.toString());
            } catch (IOException e){
                Toast.makeText(this, "Photo file can't be created, please try again", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pictureFile != null){
                //Uri photoURI = FileProvider.getUriForFile(this, "com.example.administrator.biodiversityapp", pictureFile);
                //System.out.println("photoURI -----------> " + photoURI.toString());
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            }
        }
    }

    private File getPictureFile() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        //System.out.println("image -------------> " + image.getAbsolutePath());
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Exit")
                .setMessage("Are you sure you want to close camera?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        takePictureIntent();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openMapActivity();
                    }
                }).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK){
                File imgFile = new File(pictureFilePath);
                //System.out.println("imgFile -----------------> " + imgFile.toString());
                if (imgFile.exists()){

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions. inJustDecodeBounds = false;
                    bmOptions. inSampleSize = 4;
                    bmOptions. inPurgeable = true ;

                    // by this point we have the camera photo on disk
                    Bitmap takenImage = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
                    // RESIZE BITMAP, see section below
                    // Load the taken image into a preview
                    image_view.setImageBitmap(takenImage);
                }
            }
            Bitmap bitmap = ((BitmapDrawable) image_view.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageAsBytes = baos.toByteArray();
            try {
                baos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            saveToGallery();

            //openFormActivity();

        }else {
            if (resultCode == RESULT_CANCELED){
                onBackPressed();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        alti.setText(new StringBuilder().append("Altitude: ").append(String.valueOf(location.getAltitude())));
        lati.setText(new StringBuilder().append("Latitude: ").append(String.valueOf(location.getLatitude())));
        lngi.setText(new StringBuilder().append("Longitude: ").append(String.valueOf(location.getLongitude())));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void onRadioBtnClicked(View view){
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rb_gps:
                if (checked)
                    if (hasLocation()) {
                        Snackbar.make(gps, "Location found!", Snackbar.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, 2000);
                    } else {
                        Snackbar.make(map, "No location found!", Snackbar.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, 2000);
                    }
                break;
            case R.id.rb_map:
                if (checked) {
                    // Ninjas rule
                    Intent intent = new Intent(this, SetLocationActivity.class);
                    Bundle fields = new Bundle();
                    fields.putString("pass_specName",input_specName.getText().toString());
                    fields.putString("pass_commonName",input_comName.getText().toString());
                    fields.putString("pass_remarks",input_remarks.getText().toString());
                    fields.putByteArray("pass_img", imageAsBytes);
                    System.out.println("pin to map intent");
//                    intent.putExtra("pass_specName", input_specName.getText());
//                    System.out.println("--->"+input_specName.getText());
//                    intent.putExtra("pass_commonName", input_comName.getText());
//                    intent.putExtra("pass_remarks", input_remarks.getText());
//                    intent.putExtra("pass_img", imageAsBytes);
                    intent.putExtras(fields);
                    startActivity(intent);
                    break;
                }
        }
    }
}
