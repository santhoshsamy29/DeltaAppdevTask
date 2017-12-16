package com.example.application.unit;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.application.unit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    DatabaseReference rootRef, nameRef;
    CustomAdapter customAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> names;
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        names = new ArrayList<>();
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        recyclerView = (RecyclerView)findViewById(R.id.recycle_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(names!=null){
            customAdapter = new CustomAdapter(this,names);
            recyclerView.setAdapter(customAdapter);
        }

        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchData();
            }
        });

        customAdapter.setClickListener(new CustomAdapter.ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
               itemclicks(view,position);
            }
        });
    }

    //Function for onclicks of different views
    private void itemclicks(View view, int position) {
        switch (view.getId()){
            case R.id.call :
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:0123456789"));
                startActivity(callIntent);
                break;
            case R.id.location :
               /* try {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=Aroma+Hotel+%26+Restaurant");
                    Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException e){
                    Toast.makeText(getApplicationContext(),"No maps activity found",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }*/
                Intent mapsIntent = new Intent(TaskActivity.this,MapsActivity.class);
                Bundle b = new Bundle();
                b.putDouble("latitude", 30.730267);
                b.putDouble("longitude",76.773582);
                mapsIntent.putExtras(b);
                startActivity(mapsIntent);
                /*manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    askPerm();
                } else {
                    startActivity(new Intent(TaskActivity.this,MapsActivity.class));
                }
*/
                break;
            case R.id.new_activity :
                Toast.makeText(TaskActivity.this,"To open a New Acitivity",Toast.LENGTH_SHORT).show();
                break;
            case R.id.ok_button :
                openDialog();
                break;
        }
    }

    /*void askPerm(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Permission Required");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(TaskActivity.this,"Permission Required",Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }*/

    //Function to open dialog onclick of clickhere
    void openDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hello");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void onRefresh() {
        if(names != null){
            swipeRefreshLayout.setRefreshing(false);
        }else{
            fetchData();
        }
    }

    //Function to fetch data from database
    private void fetchData() {
        names.clear();
        rootRef = FirebaseDatabase.getInstance().getReference();
        nameRef = rootRef.child("names");

        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    String val = snap.getValue(String.class);
                    Log.e("SAN",snap.getKey());
                    names.add(val);
                }
                customAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
