package com.example.application.unit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrialActivity extends AppCompatActivity {

    private static final int LOCATION_ENABLE_REQUEST_CODE = 9000;
    private static final int PERMISSION_REQUEST_CODE = 9001;
    String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private Boolean isPermissionGiven = false;
    private Boolean isLocationEnabled = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Location currentLocation;
    double latitude,longitude;


    RecyclerView horizontalRv, productsRv;
    HorizontalImagesAdapter horizontalImagesAdapter;
    ProductDetailsAdapter productDetailsAdapter;
    ArrayList<Product> productArrayList = new ArrayList<>();
    EditText deliveryAddressEt, writeDownEt;
    TextView totalPriceTv;
    DatabaseReference rootRef, prodRef;
    int totalPrice = 0;
    ProgressBar progressBar;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial);

        progressBar = findViewById(R.id.prod_det_prog);
        deliveryAddressEt = findViewById(R.id.delivery_add_et);
        writeDownEt = findViewById(R.id.write_down_et);
        totalPriceTv = findViewById(R.id.total_price_tv);

        enableEditTextScrolling(deliveryAddressEt);
        enableEditTextScrolling(writeDownEt);

        if (isPermissionGiven) {
            if (isLocationEnabled) {
                getDeviceLocation();
            } else {
                enableLocationDialog();
            }
        } else {
            callPermissionRequest();
        }

        rootRef = FirebaseDatabase.getInstance().getReference();
        prodRef = rootRef.child("products");
        prodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Product temp = new Product();
                    temp.setProdName(snapshot.getKey());
                    temp.setColor(snapshot.child("color").getValue().toString());
                    temp.setPrice(Integer.parseInt(snapshot.child("price").getValue().toString()));
                    temp.setQuantity(Integer.parseInt(snapshot.child("quantity").getValue().toString()));
                    temp.setSize(Integer.parseInt(snapshot.child("size").getValue().toString()));
                    temp.setStoreName(snapshot.child("store").getValue().toString());
                    temp.setTransactionId(Long.parseLong(snapshot.child("id").getValue().toString()));
                    temp.setImg_url(snapshot.child("img_url").getValue().toString());
                    productArrayList.add(temp);
                    i++;
                    if(i == 4){
                        break;
                    }
                }
                Log.d("SAN",String.valueOf(productArrayList.size()));
                productDetailsAdapter.notifyDataSetChanged();
                horizontalImagesAdapter.notifyDataSetChanged();
                for (int j = 0; j < productArrayList.size(); j++) {
                    totalPrice += productArrayList.get(j).getPrice();
                }
                totalPriceTv.setText("Total Price : " + totalPrice);
                deliveryAddressEt.setText(address);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /*productArrayList.add(new Product("danaerys", "white", "targaryen targaryen targaryen targaryen targaryen targaryen targaryen targaryen targaryen targaryen", 200, 5, 1, 1000l));
        productArrayList.add(new Product("jonsnow", "white", "targaryen", 200, 5, 1, 1001l));
        productArrayList.add(new Product("nedstard", "white", "stark", 200, 5, 1, 1002l));
        productArrayList.add(new Product("tyrion", "white", "lannister", 200, 5, 1, 1003l));
        productArrayList.add(new Product("sansa", "white", "stark", 200, 5, 1, 1004l));*/

        //int[] drawables = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4};

        horizontalRv = findViewById(R.id.horizontal_rv);
        productsRv = findViewById(R.id.product_det_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horizontalRv.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        productsRv.setLayoutManager(layoutManager1);
        productDetailsAdapter = new ProductDetailsAdapter(this, productArrayList);
        horizontalImagesAdapter = new HorizontalImagesAdapter(this, productArrayList);
        productsRv.setAdapter(productDetailsAdapter);
        horizontalRv.setAdapter(horizontalImagesAdapter);

        horizontalImagesAdapter.setOnClickListener(new HorizontalImagesAdapter.onClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                switch (view.getId()){
                    case R.id.image_close_button:
                        productArrayList.remove(position);
                        horizontalImagesAdapter.notifyDataSetChanged();
                        productDetailsAdapter.notifyDataSetChanged();
                        totalPrice = 0;
                        for (int j = 0; j < productArrayList.size(); j++) {
                            totalPrice += productArrayList.get(j).getPrice();
                        }
                        totalPriceTv.setText("Total Price : " + totalPrice);
                }
            }
        });
    }

    //Function to enable scrolling over edittexts
    private void enableEditTextScrolling(EditText editText) {

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        productDetailsAdapter.notifyDataSetChanged();
        horizontalImagesAdapter.notifyDataSetChanged();
    }

    //Function to get current address of user
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
                            latitude = currentLocation.getLatitude() ;
                            longitude = currentLocation.getLongitude();

                            Log.d("SAN",latitude + " " + longitude);

                            Geocoder geocoder;
                            List<Address> addresses = null;
                            geocoder = new Geocoder(TrialActivity.this, Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //address = addresses.get(0).getAddressLine(0);// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String s = addresses.get(0).getSubLocality();
                            String locale =addresses.get(0).getLocale().toString();
                            String premises = addresses.get(0).getSubAdminArea();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();

                            Log.e("SAN",city + " fdoomo "+ s + " ndsknd " + premises);

                            //fetchAddress();
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

    //Helper function to check if location is enabled
    private boolean checkLocationEnabled() {
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //Function to open dialog check location
    private void enableLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrialActivity.this);

        builder.setTitle("Enable Location?")
                .setMessage("You need to enable location to show your location on map")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, LOCATION_ENABLE_REQUEST_CODE);
                        } catch (ActivityNotFoundException e){

                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        isLocationEnabled = false;
                        dialogInterface.dismiss();
                    }
                });
        builder.show();

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
                    getDeviceLocation();
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
                            getDeviceLocation();
                        }
                    } else {
                        //Toast.makeText(TrialActivity.this, "Location needed for navigation", Toast.LENGTH_SHORT).show();
                        try{
                            setResult(RESULT_CANCELED);
                        }catch (RuntimeException e){
                            e.printStackTrace();
                        }


                    }
                }
        }
    }

}
