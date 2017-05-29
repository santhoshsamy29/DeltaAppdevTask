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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
        {
            text1 = (TextView)findViewById(R.id.text1);
            text2 = (TextView)findViewById(R.id.text2);
            text3 = (TextView)findViewById(R.id.text3);
            x=savedInstanceState.getInt("R");
            y=savedInstanceState.getInt("G");
            z=savedInstanceState.getInt("B");
            text1.setText(Integer.toString(x));
            text2.setText(Integer.toString(y));
            text3.setText(Integer.toString(z));
        }

        myLayout = (RelativeLayout) findViewById(R.id.myLayout);
        myLayout.setBackgroundColor(Color.rgb(x,y,z));

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x>255)
                    x=0;
                x+=5;
                RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.myLayout);
                myLayout.setBackgroundColor(Color.rgb(x,y,z));
                TextView text1 = (TextView)findViewById(R.id.text1);
                text1.setText(Integer.toString(x));
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(y>255)
                    y=0;
                y+=5;
                RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.myLayout);
                myLayout.setBackgroundColor(Color.rgb(x,y,z));
                TextView text2 = (TextView)findViewById(R.id.text2);
                text2.setText(Integer.toString(y));
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(z>255)
                    z=0;
                z+=5;
                RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.myLayout);
                myLayout.setBackgroundColor(Color.rgb(x,y,z));
                TextView text3 = (TextView)findViewById(R.id.text3);
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
                RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.myLayout);
                myLayout.setBackgroundColor(Color.rgb(x,y,z));
                TextView text1 = (TextView)findViewById(R.id.text1);
                text1.setText(Integer.toString(x));
                TextView text2 = (TextView)findViewById(R.id.text2);
                text2.setText(Integer.toString(y));
                TextView text3 = (TextView)findViewById(R.id.text3);
                text3.setText(Integer.toString(z));
            }
        });
    }

   public  void onSaveInstanceState (Bundle savedInstanceState)
    {
        savedInstanceState.putInt("R",x);
        savedInstanceState.putInt("G",y);
        savedInstanceState.putInt("B",z);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("state", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("R",x);
        editor.putInt("G",y);
        editor.putInt("B",z);
        editor.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("state",Context.MODE_PRIVATE);
        x= preferences.getInt("R",0);
        y= preferences.getInt("G",0);
        z= preferences.getInt("B",0);
        text1 = (TextView)findViewById(R.id.text1);
        text2 = (TextView)findViewById(R.id.text2);
        text3 = (TextView)findViewById(R.id.text3);
        text1.setText(Integer.toString(x));
        text2.setText(Integer.toString(y));
        text3.setText(Integer.toString(z));
        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.myLayout);
        myLayout.setBackgroundColor(Color.rgb(x,y,z));
    }
}
