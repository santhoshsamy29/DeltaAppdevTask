package com.example.application.task1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int x=0,y=0,z=0;
    RelativeLayout myLayout;
    TextView text1,text2,text3;
    public static final String PREF = "state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myLayout = (RelativeLayout) findViewById(R.id.myLayout);
        text1 = (TextView)findViewById(R.id.text1);
        text2 = (TextView)findViewById(R.id.text2);
        text3 = (TextView)findViewById(R.id.text3);

        myLayout.setBackgroundColor(Color.rgb(x,y,z));

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x+=5;
                if(x>255)
                    x=0;
                myLayout.setBackgroundColor(Color.rgb(x,y,z));
                text1.setText(Integer.toString(x));
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                y+=5;
                if(y>255)
                    y=0;
                myLayout.setBackgroundColor(Color.rgb(x,y,z));
                text2.setText(Integer.toString(y));
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                z+=5;
                if(z>255)
                    z=0;
                myLayout.setBackgroundColor(Color.rgb(x,y,z));
                text3.setText(Integer.toString(z));
            }
        });

        Button button4 = (Button)findViewById(R.id.reset);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x=0;
                y=0;
                z=0;
                myLayout.setBackgroundColor(Color.rgb(x,y,z));
                text1.setText(Integer.toString(x));
                text2.setText(Integer.toString(y));
                text3.setText(Integer.toString(z));
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("R",x);
        editor.putInt("G",y);
        editor.putInt("B",z);
        editor.apply();
    }

   @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(PREF,Context.MODE_PRIVATE);
        x= preferences.getInt("R",0);
        y= preferences.getInt("G",0);
        z= preferences.getInt("B",0);

        text1 = (TextView)findViewById(R.id.text1);
        text2 = (TextView)findViewById(R.id.text2);
        text3 = (TextView)findViewById(R.id.text3);
        myLayout = (RelativeLayout) findViewById(R.id.myLayout);

        text1.setText(Integer.toString(x));
        text2.setText(Integer.toString(y));
        text3.setText(Integer.toString(z));
        myLayout.setBackgroundColor(Color.rgb(x,y,z));
    }
}
