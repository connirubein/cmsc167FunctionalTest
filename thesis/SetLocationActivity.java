package com.example.administrator.biodiversityapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.location.LocationManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.Manifest;
import android.widget.Button;
import android.widget.TextView;

import static android.support.constraint.Constraints.TAG;


public class SetLocationActivity extends MenuBar implements LocationListener {

    MapView mapView;
    private DBHandler db = new DBHandler(this);
    //    public MapActivity() {
//        mapView = null;
//    }
    private LocationManager lm;

    private MyLocationNewOverlay mLocationOverlay;
    private Double alt, lat, lng;
    private GeoPoint geoPoint;
    private Location l;
    TextView tv_alt, tv_lat, tv_lng;
    String dropPin_longitude;
    String dropPin_latitude;
    Button btn_cancel, btn_save;
    GeoPoint loc;
    Boolean gotPinned = false;
    String passed_spec, passed_comm, passed_rem;
    byte[] passed_img;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! on create !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        permissionAndSetupMap();
        System.out.println("permissions done");
        try {
            getPassedValues();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("got passed values");
    }

    private void getPassedValues() {
        Intent intent = new Intent(this, SetLocationActivity.class);

        Bundle fields = getIntent().getExtras();

        passed_spec = fields.getString("pass_specName");
        passed_comm = fields.getString("pass_commonName");
        passed_rem = fields.getString("pass_remarks");
        passed_img = fields.getByteArray("pass_img");

//        passed_spec = intent.getExtras().getString("pass_specName","");
//        passed_spec = intent.getStringExtra("pass_specName");
//        passed_comm = intent.getExtras().getString("pass_commonName","");
//        passed_rem = intent.getExtras().getString("pass_remarks","");
        //passed_img = intent.getByteArrayExtra("pass_img");
        System.out.println("----------------------------->get passed values:\n");
        System.out.println("-->"+passed_spec);
        System.out.println("-->"+passed_spec);
        System.out.println("-->"+passed_rem);
    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_save:
                    if(gotPinned){
                        openCameraActivity_withLocation();
                    }
                    else{
                        Snackbar.make(btn_save, "Pin a location before saving!", Snackbar.LENGTH_LONG).show();
                    }

                    break;
                case R.id.btn_cancel:
                    openCameraActivity_noLocation();
                    break;
            }
        }
    };


    @SuppressLint("RestrictedApi")
    private void permissionAndSetupMap() {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! permission !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        //handle permissions first, before map is created. not depicted here
        //System.out.println(Build.VERSION.SDK_INT + " <---------------------- build version");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //System.out.println("--------> so nag ask syag permission <---------");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            //    call ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission.
        }else {
            Log.v(TAG, "------------------> Permission is granted");
        }

        try {
            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //System.out.println(lm.getProviders(true).toString() + " walaaaaaaaaaaa <----------");

            AlertDialog.Builder builder;
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                builder = new AlertDialog.Builder(SetLocationActivity.this);

                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                final String message = "\nGo to Settings and enable GPS to find current location.";

                builder.setMessage(message)
                        .setTitle("GPS is not enabled")
                        .setPositiveButton("Settings",
                                (d, id) -> {
                                    startActivity(new Intent(action));
                                    d.dismiss();
                                })
                        .setNegativeButton("Ignore",
                                (d, id) -> d.cancel());
                builder.create().show();
            }

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            //get location
            l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (l != null) { //if gps provider is not null then it will return true already (meaning location is found)
                System.out.println("----------> gps provider used");
//                tv_alt.setText(new StringBuilder().append("Altitude: ").append(String.valueOf(l.getAltitude())));
//                tv_lat.setText(new StringBuilder().append("Latitude: ").append(String.valueOf(l.getLatitude())));
//                tv_lng.setText(new StringBuilder().append("Longitude: ").append(String.valueOf(l.getLongitude())));
                System.out.println(l.getLatitude() + " latitude");
                System.out.println(l.getLongitude() + " longitude");
                System.out.println(l.getAltitude() + " altitude");
            } else { //else if gps provider is null, try using the network provider instead.
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (l != null){
                    System.out.println("-----------> network provider used");
//                    tv_alt.setText(new StringBuilder().append("Altitude: ").append(String.valueOf(l.getAltitude())));
//                    tv_lat.setText(new StringBuilder().append("Latitude: ").append(String.valueOf(l.getLatitude())));
//                    tv_lng.setText(new StringBuilder().append("Longitude: ").append(String.valueOf(l.getLongitude())));
                    System.out.println(l.getLatitude() + " latitude");
                    System.out.println(l.getLongitude() + " longitude");
                    System.out.println(l.getAltitude() + " altitude");
                } else {
                    System.out.println("No Provider!--------------> wala na talaga");
                    builder = new AlertDialog.Builder(SetLocationActivity.this);
                    builder.setMessage("We could not find any location provider.\n App may not work properly.")
                            .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(SetLocationActivity.this, SetLocationActivity.class);
                                    startActivity(intent);
                                }
                            });
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        //load or initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_set_location);
        setUpUIViews();

        //mapView.setTileSource(new XYTileSource("tiles", 7, 13  , 256, ".png", new String[] {}));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
//        mapView.setTileSource(new OnlineTileSourceBase("USGS Topo", 0, 18, 256, "",
//                new String[] { "http://basemap.nationalmap.gov/ArcGIS/rest/services/USGSTopo/MapServer/tile/" }) {
//            @Override
//            public String getTileURLString(long pMapTileIndex) {
//                return getBaseUrl()
//                        + MapTileIndex.getZoom(pMapTileIndex)
//                        + "/" + MapTileIndex.getY(pMapTileIndex)
//                        + "/" + MapTileIndex.getX(pMapTileIndex)
//                        + mImageFilenameEnding;
//            }
//        });


        mapView.setUseDataConnection(false);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        //play around with these values
//world
//        mapController.setCenter(new GeoPoint(10.3156992, 123.88543660000005));
        // cebu city
        //BoundingBox bounds = new BoundingBox(5.621452591118825, -5.6304931640625,5.621452591118825,-5.6304931640625);
        BoundingBox bounds = mapView.getBoundingBox();
        mapController.setCenter(new GeoPoint(10.309802, 123.910031));
        //mapView.setScrollableAreaLimit(mapView.getBoundingBox());


        mapView.setMinZoomLevel(10.0);
        CompassOverlay mCompassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), mapView);
        mCompassOverlay.enableCompass();
        mapView.getOverlays().add(mCompassOverlay);
        //mapView.zoomToBoundingBox(bounds, false);

        // mapView.setScrollableAreaLimitDouble(mapView.getBoundingBox());

        mLocationOverlay = new MyLocationNewOverlay(mapView);
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(mLocationOverlay);

//        mRotationGestureOverlay = new RotationGestureOverlay(mapView);
//        mRotationGestureOverlay.setEnabled(true);
//        mapView.setMultiTouchControls(true);
//        mapView.getOverlays().add(this.mRotationGestureOverlay);

        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);

        //ITileSource tileSource = new XYTileSource("maps", null, 15, 17, 256, "", "/assets    ");

        //play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(500, 50);
        mapView.getOverlays().add(mScaleBarOverlay);
        //mapView.setScrollableAreaLimitDouble(bounds);
        //System.out.println("------------------------------------------------->");
        //System.out.println(mapView.getBoundingBox());
        //mapView.setMinZoomLevel(2.0);
        //mapView.on

        Overlay touchOverlay = new Overlay(this){
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {

                final Drawable marker = getApplicationContext().getResources().getDrawable(R.drawable.droppin);
                Projection proj = mapView.getProjection();
                loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());

                //dropPin_longitude = Double.toString(((double)loc.getLongitude())/1000000000);
                //dropPin_latitude = Double.toString(((double)loc.getLatitude())/1000000000);
                System.out.println("- Latitude = " + loc.getLatitude() + ", Longitude = " + loc.getLongitude() + ", Altitude = " + loc.getAltitude() );
                //btn_save.setEnabled(true);
                gotPinned = true;
                ArrayList<OverlayItem> overlayArray = new ArrayList<OverlayItem>();
                OverlayItem mapItem = new OverlayItem("", "", new GeoPoint((((double)loc.getLatitudeE6())/1000000), (((double)loc.getLongitudeE6())/1000000)));
                mapItem.setMarker(marker);
                overlayArray.add(mapItem);
                if(anotherItemizedIconOverlay==null){
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                    mapView.invalidate();
                }else{
                    mapView.getOverlays().remove(anotherItemizedIconOverlay);
                    mapView.invalidate();
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                }
                //      dlgThread();
                return true;
            }
        };
        mapView.getOverlays().add(touchOverlay);

    }

    public void onPause(){
        super.onPause();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! on pause !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().save(this, prefs);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onResume(){
        super.onResume();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! on resume !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up


    }

    @Override
    public void onLocationChanged(Location location) {


    }


    private void openCameraActivity_withLocation(){         //clicked btn_save
        Intent intent = new Intent(this, OpenCameraActivity.class);
        intent.putExtra("fromMap",true);
        intent.putExtra("savedALocation", true);

        Bundle fields = new Bundle();
        fields.putString("pass_specName",passed_spec);
        fields.putString("pass_commonName",passed_comm);
        fields.putString("pass_remarks",passed_rem);
        fields.putByteArray("pass_img", passed_img);
        fields.putDouble("map_long", loc.getLongitude());
        fields.putDouble("map_lat", loc.getLatitude());
        intent.putExtras(fields);
//        intent.putExtra("pass_specName", passed_spec);
//        intent.putExtra("pass_commonName", passed_comm);
//        intent.putExtra("pass_remarks", passed_rem);
//        intent.putExtra("pass_img", passed_img);
        startActivity(intent);
    }

    private void openCameraActivity_noLocation(){           //clicked btn_cancel
        Intent intent = new Intent(this, OpenCameraActivity.class);
        intent.putExtra("fromMap",true);
        Bundle fields = new Bundle();
        fields.putString("pass_specName",passed_spec);
        fields.putString("pass_commonName",passed_comm);
        fields.putString("pass_remarks",passed_rem);
        fields.putByteArray("pass_img", passed_img);
        intent.putExtras(fields);
        System.out.println("finish setlocation activity");
//        intent.putExtra("pass_specName", passed_spec);
//        intent.putExtra("pass_commonName", passed_comm);
//        intent.putExtra("pass_remarks", passed_rem);
//        intent.putExtra("pass_img", passed_img);
        startActivity(intent);
    }       //clicked btn_cancel


    private void setUpUIViews(){
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! on set up !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        findViewById(R.id.btn_cancel).setOnClickListener(btnOnClickListener);
        System.out.println("done btn cancel");
        findViewById(R.id.btn_save).setOnClickListener(btnOnClickListener);
        btn_save = findViewById(R.id.btn_save);
        System.out.println("assigned btn save");
//        btn_save.setEnabled(false);
//        System.out.println("btn save disabled");
        mapView = findViewById(R.id.map);
        System.out.println("map done");
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
}