package com.example.application.unit;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.data.BarData;

public class HomeActivity extends AppCompatActivity {

    Button screenButton, graphButton, conservationButton, searchActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        screenButton = findViewById(R.id.screenButton);
        screenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,TaskActivity.class));
            }
        });

        searchActivityButton = findViewById(R.id.search_activity_button);
        searchActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,SearchActivity.class));
            }
        });

        graphButton = findViewById(R.id.graphButton);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,BarActivity.class));
            }
        });

        conservationButton = findViewById(R.id.conservationButton);
        conservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,ConservationActivity.class));
            }
        });
    }
}
