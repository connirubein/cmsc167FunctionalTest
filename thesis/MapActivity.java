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
import android.widget.TextView;

import static android.support.constraint.Constraints.TAG;


public class MapActivity extends MenuBar implements LocationListener {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! on create !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        permissionAndSetupMap();
        renderLocations();


    }

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_record:           //open record activity
                    openCameraActivity();
                    break;
                case R.id.btn_profile:
                    openActivityProfilePending();
                    break;
            }
        }
    };

    public void renderLocations(){

        List<GeoPoint> points = new ArrayList<>();

        ArrayList<LatLong> latLongs = db.getLatLong();

        for (int i = 0; i < latLongs.size(); i++){
            //points.add(new LabelledGeoPoint(Double.parseDouble(latitude.get(i)), Double.parseDouble(longitude.get(i))));
            points.add(new GeoPoint(Double.parseDouble(latLongs.get(i).getLatitude()), Double.parseDouble(latLongs.get(i).getLongitude())));
            //System.out.println(points.get(i) + " hello");
        }

        ArrayList<OverlayItem> item = new ArrayList<>();
        for (int i = 0; i < points.size(); i++){
            item.add(new OverlayItem(null, " ", points.get(i)));
        }

        // add overlay
        ItemizedOverlayWithFocus<OverlayItem> iconOverlay = new ItemizedOverlayWithFocus<>(item, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            //onClick callback
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return true;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                return false;
            }
        }, this);
        iconOverlay.setFontSize(12);
        //iconOverlay.setFocusItemsOnTap(true);

        mapView.getOverlays().add(iconOverlay);
        db.close();
    }

    private String getAddress(double latitude, double longitude){
        String stringAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null){
                Address returnedAddress = addresses.get(0);
                StringBuilder strRtrndAddr = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++){
                    strRtrndAddr.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                stringAdd = strRtrndAddr.toString();
                Log.w("My current location", strRtrndAddr.toString());
            }
            else {
                Log.w("My current location", "No address returned!");
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.w("My current location", "Cannot get address!");
        }
        return stringAdd;
    }

    @SuppressLint("RestrictedApi")
    private void permissionAndSetupMap() {
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
                builder = new AlertDialog.Builder(MapActivity.this);

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
                    builder = new AlertDialog.Builder(MapActivity.this);
                    builder.setMessage("We could not find any location provider.\n App may not work properly.")
                            .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MapActivity.this, MapActivity.class);
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
        setContentView(R.layout.activity_map);
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

    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().save(this, prefs);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up


    }

    @Override
    public void onLocationChanged(Location location) {
        tv_alt.setText(new StringBuilder().append("Altitude: ").append(String.valueOf(location.getAltitude())));
        tv_lat.setText(new StringBuilder().append("Latitude: ").append(String.valueOf(location.getLatitude())));
        tv_lng.setText(new StringBuilder().append("Longitude: ").append(String.valueOf(location.getLongitude())));
//        tv_lat.setText(new StringBuilder().append("Latitude: ").append(dropPin_latitude));
//        tv_lng.setText(new StringBuilder().append("Longitude: ").append(dropPin_longitude));

    }


    private void openCameraActivity(){
        Intent intent = new Intent(this, OpenCameraActivity.class);
        startActivity(intent);
    }

    private void openActivityProfilePending(){
        Intent intent = new Intent(this, ActivityProfilePending.class);
        startActivity(intent);
    }

    private void setUpUIViews(){
        findViewById(R.id.btn_map).setOnClickListener(btnOnClickListener);
        findViewById(R.id.btn_record).setOnClickListener(btnOnClickListener);
        findViewById(R.id.btn_profile).setOnClickListener(btnOnClickListener);
        mapView = findViewById(R.id.map);
        tv_alt = findViewById(R.id.tv_alt);
        tv_lat = findViewById(R.id.tv_lat);
        tv_lng = findViewById(R.id.tv_lng);
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