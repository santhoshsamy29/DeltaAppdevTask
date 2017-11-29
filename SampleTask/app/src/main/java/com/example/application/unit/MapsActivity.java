package com.example.application.unit;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_ENABLE_REQUEST_CODE = 9000;
    private static final int PERMISSION_REQUEST_CODE = 9001;
    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private Boolean isPermissionGiven = false;
    private Boolean isLocationEnabled = false;
    private GoogleMap map;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Location currentLocation;
    CircleImageView getCurrent;
    CircleImageView back;
    LatLng origin, dest;

    MarkerOptions originMark, destMark;
    PolylineOptions lineOptions = null;
    Marker oMarker = null, dMarker;
    Polyline polyline = null;
    Handler mHandler;
    private Boolean firstCam = false;
    private Boolean firstDial = false;
    LocationManager manager;
    AlertDialog askPermDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getCurrent = findViewById(R.id.get_current);
        back = findViewById(R.id.back);

        getCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f));
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (isPermissionGiven) {
            if (isLocationEnabled) {
                getDeviceLocation();
            } else {
                enableLocationDialog();
            }
        } else {
            callPermissionRequest();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler = new Handler();
        findIt.run();
    }


    Runnable findIt = new Runnable() {
        @Override
        public void run() {
            if (isPermissionGiven) {
                if (isLocationEnabled) {
                    getDeviceLocation();
                } else {
                    enableLocationDialog();
                }
            } else {
                callPermissionRequest();
            }
            mHandler.postDelayed(findIt, 5000);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (!hasPermission()) {
            callPermissionRequest();
        } else if (checkLocationEnabled()) {
            isLocationEnabled = true;
        } else {
            isLocationEnabled = false;
            enableLocationDialog();
        }

        if (isPermissionGiven && isLocationEnabled) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            getDeviceLocation();
        }

    }


    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(isPermissionGiven && isLocationEnabled){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){

                            currentLocation = (Location)task.getResult();
                            origin = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()) ;
                            dest =  new LatLng(30.730267, 76.773582);

                            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                            boundsBuilder.include(origin);
                            boundsBuilder.include(dest);
                            int routePadding = 100;
                            LatLngBounds latLngBounds = boundsBuilder.build();

                            Drawable originDrawable = getResources().getDrawable(R.drawable.red);
                            BitmapDescriptor originIcon = getMarkerIconFromDrawable(originDrawable);

                            Drawable destDrawable = getResources().getDrawable(R.drawable.blue);
                            BitmapDescriptor destIcon = getMarkerIconFromDrawable(destDrawable);


                            if(oMarker != null){
                                oMarker.remove();
                            }
                            originMark = new MarkerOptions().position(origin)
                                    .icon(originIcon);

                            destMark = new MarkerOptions().position(dest)
                                    .icon(destIcon);

                            oMarker = map.addMarker(originMark);

                            dMarker = map.addMarker(destMark);
                            if(!firstCam){
                                map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,routePadding));
                                firstCam = true;
                            }


                            final String url = getDirectionsUrl(origin, dest);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    DownloadTask downloadTask = new DownloadTask();
                                    downloadTask.execute(url);

                                }
                            },1000);

                        }
                    }
                });
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public boolean hasPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            isPermissionGiven = false;
            isLocationEnabled = false;
            return false;
        } else {
            return true;
        }
    }

    private boolean checkLocationEnabled() {
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void enableLocationDialog() {
        if(!firstDial){
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            askPermDialog = builder.create();
            firstDial = true;
            builder.setTitle("Enable Location?")
                    .setMessage("You need to enable location to show your location on map")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firstDial = false;
                            try {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, LOCATION_ENABLE_REQUEST_CODE);
                            } catch (ActivityNotFoundException e){
                                Log.e("SAN",e.getMessage());
                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firstDial = false;
                            isLocationEnabled = false;
                            dialogInterface.dismiss();
                        }
                    });
            builder.show();
        }

    }

    private void callPermissionRequest() {
        ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                PERMISSION_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOCATION_ENABLE_REQUEST_CODE:
                if (checkLocationEnabled()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isLocationEnabled = true;
                        }
                    },1000);

                } else {
                    isLocationEnabled = false;
                }
                if (isPermissionGiven && isLocationEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                    }
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length >= 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        isPermissionGiven = true;
                        if (checkLocationEnabled()) {
                            isLocationEnabled = true;
                        } else {
                            isLocationEnabled = false;
                            enableLocationDialog();
                        }
                        if (isPermissionGiven && isLocationEnabled) {
                            if (ActivityCompat.checkSelfPermission(this,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                            }
                            map.setMyLocationEnabled(true);
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    } else {
                        Toast.makeText(MapsActivity.this, "Location needed for navigation", Toast.LENGTH_SHORT).show();
                        try{
                            setResult(RESULT_CANCELED);
                        }catch (RuntimeException e){
                            e.printStackTrace();
                        }


                    }
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(findIt);
    }



    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        @Override
        protected List<List<HashMap<String,String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String,String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String,String>>> result) {
            ArrayList points = null;

            if(polyline != null){
                polyline.remove();
            }

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();
                List<HashMap<String,String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLACK);
                lineOptions.geodesic(true);
            }
            if(lineOptions != null){
                polyline =  map.addPolyline(lineOptions);
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}


