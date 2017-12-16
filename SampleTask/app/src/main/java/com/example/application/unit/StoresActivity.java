package com.example.application.unit;

import android.content.Intent;
import android.graphics.Rect;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StoresActivity extends AppCompatActivity {

    RecyclerView storesRv;
    StoreAdapter storeAdapter;
    ImageButton backHomeButton,middleButton,rightButton;
    BottomSheetBehavior bottomSheetBehavior;
    View bottomSheet;
    DatabaseReference rootRef,storeRef;
    ArrayList<Store> storesArrayList = new ArrayList<>();
    ProgressBar progressBar;
    RelativeLayout rl;
    TextView bottomStoreNameTv;
    ImageView storeImage,ownImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

        storeImage = findViewById(R.id.store_images);
        ownImage = findViewById(R.id.store_own_image);
        bottomStoreNameTv = findViewById(R.id.bottom_store_name_tv);
        backHomeButton = findViewById(R.id.back_home_button);
        middleButton = findViewById(R.id.middle_button);
        rightButton = findViewById(R.id.right_button);
        progressBar = findViewById(R.id.stores_pb);
        rl = findViewById(R.id.stores_layout);

        rootRef = FirebaseDatabase.getInstance().getReference();
        storeRef = rootRef.child("stores");
        storeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Store stores = new Store();
                    stores.setName(snapshot.getKey());
                    stores.setProdName(snapshot.child("prodname").getValue(String.class));
                    stores.setLandmark(snapshot.child("landmark").getValue(String.class));
                    stores.setLati(snapshot.child("latitude").getValue(Double.class));
                    stores.setLongi(snapshot.child("longitude").getValue(Double.class));
                    stores.setTimer(snapshot.child("timer").getValue(Integer.class));
                    stores.setStoreImgUrl(snapshot.child("store_img").getValue(String.class));
                    stores.setOwnImgUrl(snapshot.child("own_img").getValue(String.class));
                    storesArrayList.add(stores);
                }

                Log.e("SAN","" + storesArrayList.size());
                Log.d("SAN","setting adapter");
                storeAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
                rl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                rl.setVisibility(View.VISIBLE);
            }
        });

        storesRv = findViewById(R.id.stores_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        storesRv.setLayoutManager(layoutManager);
        storeAdapter = new StoreAdapter(StoresActivity.this,storesArrayList);
        storesRv.setAdapter(storeAdapter);

        storeAdapter.setOnClickListener(new StoreAdapter.OnClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                switch (view.getId()){
                    case R.id.gps_button:
                        Intent mapsIntent = new Intent(StoresActivity.this,MapsActivity.class);
                        Bundle b = new Bundle();
                        b.putDouble("latitude", storesArrayList.get(position).getLati());
                        b.putDouble("longitude",storesArrayList.get(position).getLongi());
                        mapsIntent.putExtras(b);
                        startActivity(mapsIntent);
                        break;
                    case R.id.store_name_tv:
                        bottomStoreNameTv.setText(storesArrayList.get(position).getName().toUpperCase());
                        Glide.with(getApplicationContext())
                                .load(storesArrayList.get(position).getStoreImgUrl())
                                .into(storeImage);
                        Glide.with(getApplicationContext())
                                .load(storesArrayList.get(position).getOwnImgUrl())
                                .into(ownImage);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case R.id.productName_tv:
                        Toast.makeText(getApplicationContext(),"New Activity Opened",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(0);

        backHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        middleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"New Activity Opened",Toast.LENGTH_SHORT).show();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"New Activity Opened",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Function to hide bottomsheet
    @Override public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);

                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY())){
                    bottomSheetBehavior.setPeekHeight(0);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }


            }
        }

        return super.dispatchTouchEvent(event);
    }
}
