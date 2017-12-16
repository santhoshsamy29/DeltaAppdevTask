package com.example.application.unit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BarActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner_year,spinner_month;
    TextView totalTv,moneyTv;
    String year, month;
    DatabaseReference rootRef, dataRef,yearRef,monthRef,orderRef;
    BarChart barChart;
    ArrayList<Integer> tempOrder;
    ProgressBar progressBar;
    boolean selected ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);

        rootRef = FirebaseDatabase.getInstance().getReference();
        dataRef = rootRef.child("data");
        selected = false;

        spinner_month = findViewById(R.id.spinner_month);
        spinner_year = findViewById(R.id.spinner_year);
        totalTv = findViewById(R.id.total_tv);
        moneyTv = findViewById(R.id.money_tv);
        barChart = findViewById(R.id.bar_graph);
        progressBar = findViewById(R.id.graph_progress);



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
        spinner_month.setAdapter(month_adapter);
        spinner_year.setOnItemSelectedListener(this);
        spinner_month.setOnItemSelectedListener(this);
    }

    //Fetching the graph data from database
    private void fetchGraph() {
        tempOrder = new ArrayList<>();

        yearRef = dataRef.child(year);
        monthRef = yearRef.child(month);
        orderRef = monthRef.child("order");

        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    int val = snap.getValue(int.class);
                    tempOrder.add(val);
                    Log.e("SAN",String.valueOf(val));
                }
                displayGraph();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    //Displaying the fetched data to the screen
    private void displayGraph() {

        int total =0 ;
        ArrayList<BarEntry> order = new ArrayList<>();
        Log.d("SAN",String.valueOf(tempOrder.size()));

        for (int i=0; i<tempOrder.size(); i++){
            total = total + tempOrder.get(i);
            order.add(new BarEntry(tempOrder.get(i),i));
            Log.d("SAN",String.valueOf(order.get(i)));
        }

        BarDataSet dataset = new BarDataSet(order, "# of Orders");

        ArrayList<String> xValues = new ArrayList<>();
        for (int i=0; i<order.size(); i++){
            xValues.add(String.valueOf(i+1));
        }
        BarData barData = new BarData(xValues,dataset);
        barChart.setData(barData);
        barChart.invalidate();

        totalTv.setText("Total : " + String.valueOf(total));
        moneyTv.setText("Money : " + String.valueOf(total * 10));
    }

    //Onclick for spinners
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
       switch(adapterView.getId()){
           case R.id.spinner_year:
               year = adapterView.getItemAtPosition(position).toString();
               if(selected){
                   progressBar.setVisibility(View.VISIBLE);
                   fetchGraph();
               }
               selected = true;
               break;
           case R.id.spinner_month:
               month = adapterView.getItemAtPosition(position).toString().toLowerCase().substring(0,3);
               progressBar.setVisibility(View.VISIBLE);
               fetchGraph();
               break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
