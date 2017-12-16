package com.example.application.unit;

import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class CongratsActivity extends AppCompatActivity {

    TextView congratsTv,consumerNameTv,thankYouTv,hereTv,receiverTimerTv;
    Button button,moreShoppingButton,lastStoreButton;
    DatabaseReference rootRef,congratsRef;
    int minutes;
    ProgressBar pb;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrats);

        ll = findViewById(R.id.ll);
        pb = findViewById(R.id.timer_pb);
        congratsTv = findViewById(R.id.congrats_tv);
        consumerNameTv = findViewById(R.id.consumer_name_tv);
        thankYouTv = findViewById(R.id.thankyou_tv);
        hereTv = findViewById(R.id.here_tv);
        receiverTimerTv = findViewById(R.id.recieve_timer);
        button = findViewById(R.id.button);
        moreShoppingButton = findViewById(R.id.more_shopping_button);
        lastStoreButton = findViewById(R.id.last_store_button);


        rootRef = FirebaseDatabase.getInstance().getReference();
        congratsRef = rootRef.child("congrats");

        congratsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                consumerNameTv.setText(dataSnapshot.child("name").getValue().toString());
                minutes = dataSnapshot.child("time").getValue(Integer.class);
                pb.setVisibility(View.INVISIBLE);
                ll.setVisibility(View.VISIBLE);
                new CountDownTimer(60000 * minutes, 1) {

                    public void onTick(long millisUntilFinished) {
                        receiverTimerTv.setText("Received In : "+"00 : " +String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                ((millisUntilFinished/1000)%60)));
                    }

                    public void onFinish() {

                    }
                }.start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Typeface customFontBold = Typeface.createFromAsset(getAssets(),"fonts/DancingScript-Bold.ttf");

        consumerNameTv.setTypeface(customFontBold);
        congratsTv.setTypeface(customFontBold);
        thankYouTv.setTypeface(customFontBold);
        hereTv.setTypeface(customFontBold);
        receiverTimerTv.setTypeface(customFontBold);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"New Activity Created",Toast.LENGTH_SHORT).show();
            }
        });

        moreShoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"New Activity Created",Toast.LENGTH_SHORT).show();
            }
        });

        lastStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"New Activity Created",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
