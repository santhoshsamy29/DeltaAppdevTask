package com.example.application.unit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConservationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final int DEFAULT_CAR_SPEED = 40;
    final int DEFAULT_WALK_SPEED = 5;
    final int MILEAGE_PETROL = 12;
    final int MILEAGE_DIESEL = 14;
    final float PETROL_RATE = 66.87f;
    final float DIESEL_RATE = 56.61f;
    DatabaseReference rootRef,dataRef,yearRef,monthRef,distanceRef;
    Spinner spinner_year,spinner_month;
    String year,month;
    int distance;
    ProgressBar progressBar;
    boolean selected;
    TextView fuelPetrolTv,fuelDieselTv,timeWalkTv,timeCarTv,moneyTv,energyTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conservation);

        spinner_year = findViewById(R.id.spinner_year1);
        spinner_month = findViewById(R.id.spinner_month1);
        fuelPetrolTv = findViewById(R.id.fuel_petrol);
        fuelDieselTv = findViewById(R.id.fuel_diesel);
        timeCarTv = findViewById(R.id.time_car);
        timeWalkTv = findViewById(R.id.time_walk);
        moneyTv = findViewById(R.id.money_tv2);
        energyTv = findViewById(R.id.energy_tv);
        progressBar = findViewById(R.id.info_progress);

        rootRef = FirebaseDatabase.getInstance().getReference();
        dataRef = rootRef.child("data");
        selected = false;

        ArrayList<String> list_year = new ArrayList<>();
        list_year.add("2017");

        ArrayList<String> list_month = new ArrayList<>();
        list_month.add("January");
        list_month.add("February");
        list_month.add("March");
        list_month.add("April");
        list_month.add("May");
        list_month.add("June");
        list_month.add("July");
        list_month.add("August");
        list_month.add("September");
        list_month.add("October");
        list_month.add("November");
        list_month.add("December");

        ArrayAdapter<String> year_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list_year);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> month_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list_month);
        month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_year.setAdapter(year_adapter);
        spinner_year.setOnItemSelectedListener(this);
        spinner_month.setAdapter(month_adapter);
        spinner_month.setOnItemSelectedListener(this);
    }

    //Fetch data to display from database
    private void fetchResults() {
        yearRef = dataRef.child(year);
        monthRef = yearRef.child(month);
        distanceRef = monthRef.child("distance");

        distanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                distance = dataSnapshot.getValue(int.class);
                showResults();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    //Display the data fetched from database
    private void showResults() {
        int petrol_consumed,diesel_consumed;
        petrol_consumed = distance/MILEAGE_PETROL;
        diesel_consumed = distance/MILEAGE_DIESEL;
        fuelPetrolTv.setText("Petrol : " + String.valueOf(petrol_consumed)+ " litres");
        fuelDieselTv.setText("Diesel : " + String.valueOf(diesel_consumed)+ " litres");
        timeCarTv.setText("Car : " + String.valueOf(distance/DEFAULT_CAR_SPEED)+ " hours");
        timeWalkTv.setText("Walk : " + String.valueOf(distance/DEFAULT_WALK_SPEED)+ " hours");
        moneyTv.setText("Money : " + String.format("%.2f",(petrol_consumed * PETROL_RATE) + (diesel_consumed * DIESEL_RATE)));
        energyTv.setText("Energy : " + String.valueOf(276 * (distance/DEFAULT_WALK_SPEED)));
    }

    //Onclick for spinners
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(adapterView.getId()){
            case R.id.spinner_year1:
                year = adapterView.getItemAtPosition(i).toString();
                if(selected){
                    progressBar.setVisibility(View.VISIBLE);
                    fetchResults();
                }
                selected = true;
                break;
            case R.id.spinner_month1:
                month = adapterView.getItemAtPosition(i).toString().toLowerCase().substring(0,3);
                progressBar.setVisibility(View.VISIBLE);
                fetchResults();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
