package com.example.application.unit;

import android.*;
import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.microedition.khronos.opengles.GL;

public class UnitTialActivity extends AppCompatActivity {


    private static final int PERM_ID = 9000;
    private static final int REQUEST_CAMERA = 1;

    RecyclerView horImgRv,horNumRv,horPayRv, productsRv;
    HorizontalImageAdapterUt horizontalImageAdapter;
    HorizontalNumbersAdapter horizontalNumbersAdapter;
    HorizontalPayAdapter horizontalPayAdapter;
    ProductDetailsAdapter productDetailsAdapter;
    ArrayList<Product> productArrayList = new ArrayList<>();
    ArrayList<Integer> numberArrayList = new ArrayList<>();
    ArrayList<Boolean> selectedArrayList;
    ArrayList<String> paymentArrayList = new ArrayList<>();
    TextView totalPriceTv,totalQuantityTv,paybackTv;
    EditText recievedEt;
    DatabaseReference rootRef, prodRef,payRef;
    int totalPrice = 0;
    ProgressBar progressBar;
    String address;
    Button selfieButton,calculateButton,doneButton,submitButton;
    boolean permissionGranted= false, uriAvailable = false,zoomed = false,cashed = false;
    String timeStamp,imgname;
    RelativeLayout unitTrialRl;
    RelativeLayout zoomLayout,cashlayout;
    ImageView zoomQrImage;
    StorageReference rootReference,imageReference;
    Uri downloadUrl=null,imageUri=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_tial);

        progressBar = findViewById(R.id.prod_det_prog);
        totalPriceTv = findViewById(R.id.total_price_tv);
        totalQuantityTv = findViewById(R.id.total_quantity_tv);
        selfieButton = findViewById(R.id.selfie_button);
        unitTrialRl = findViewById(R.id.unit_trial_layout);
        zoomLayout = findViewById(R.id.qr_zoom_layout);
        zoomQrImage = findViewById(R.id.qr_zoom);
        cashlayout = findViewById(R.id.cash_layout);
        calculateButton = findViewById(R.id.calculate_button);
        doneButton = findViewById(R.id.done_button);
        recievedEt = findViewById(R.id.recieved_et);
        submitButton = findViewById(R.id.submit_button);
        paybackTv = findViewById(R.id.payback_amount_tv);

        horImgRv = findViewById(R.id.unit_trial_hor_img_rv);
        productsRv = findViewById(R.id.product_det_rv);
        LinearLayoutManager horImgLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horImgRv.setLayoutManager(horImgLayout);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        productsRv.setLayoutManager(layoutManager1);
        productDetailsAdapter = new ProductDetailsAdapter(this, productArrayList);
        horizontalImageAdapter = new HorizontalImageAdapterUt(this, productArrayList);
        productsRv.setAdapter(productDetailsAdapter);
        horImgRv.setAdapter(horizontalImageAdapter);


        horNumRv = findViewById(R.id.ut_hor_numbers_rv);
        LinearLayoutManager horNumLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horNumRv.setLayoutManager(horNumLayout);
        horizontalNumbersAdapter = new HorizontalNumbersAdapter(this,numberArrayList);
        horNumRv.setAdapter(horizontalNumbersAdapter);

        horPayRv = findViewById(R.id.unit_trial_hor_pay_rv);
        LinearLayoutManager horPayLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horPayRv.setLayoutManager(horPayLayout);
        horizontalPayAdapter = new HorizontalPayAdapter(this,paymentArrayList);
        horPayRv.setAdapter(horizontalPayAdapter);

        rootRef = FirebaseDatabase.getInstance().getReference();
        rootReference = FirebaseStorage.getInstance().getReference();

        payRef = rootRef.child("payment");
        payRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    paymentArrayList.add(snapshot.getValue(String.class));
                    horizontalPayAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                    numberArrayList.add(i);
                    if(i == 4){
                        break;
                    }
                }

                productDetailsAdapter.notifyDataSetChanged();
                horizontalImageAdapter.notifyDataSetChanged();
                horizontalNumbersAdapter.notifyDataSetChanged();

                selectedArrayList = new ArrayList<>(numberArrayList.size());
                for(int x=0 ; x<numberArrayList.size() ; x++){
                    selectedArrayList.add(false);
                }

                unitTrialRl.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        zoomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomLayout.setVisibility(View.INVISIBLE);
                zoomed = false;
            }
        });

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(recievedEt.getText().toString()) || !TextUtils.isDigitsOnly(recievedEt.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Enter Recieved Amount",Toast.LENGTH_SHORT).show();
                    return;
                }
                int payback = Integer.parseInt(recievedEt.getText().toString()) - totalPrice;
                if(payback < 0){
                    Toast.makeText(getApplicationContext(),"Amount recieved less than total price",Toast.LENGTH_LONG).show();
                    return;
                }
                paybackTv.setText(String.valueOf(payback));
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashlayout.setVisibility(View.INVISIBLE);
                cashed = false;
            }
        });


        horizontalPayAdapter.setOnClickListener(new HorizontalPayAdapter.onClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                if(position == 0){
                    cashlayout.setVisibility(View.VISIBLE);
                    cashed = true;
                } else {
                    zoomLayout.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext())
                            .load(paymentArrayList.get(position))
                            .into(zoomQrImage);
                    zoomed = true;
                }
            }
        });



        horizontalNumbersAdapter.setOnClickListener(new HorizontalNumbersAdapter.onClickListener() {
            @Override
            public void itemClicked(View view, int position) {

                if(selectedArrayList.get(position)){
                    selectedArrayList.set(position,false);
                } else if(!selectedArrayList.get(position)){
                    selectedArrayList.set(position,true);
                }

                totalPrice = 0;
                for (int j = 0; j < productArrayList.size(); j++) {
                    if(selectedArrayList.get(j)){
                        totalPrice += productArrayList.get(j).getPrice();
                    }

                }
                totalPriceTv.setText("Total Price : " + totalPrice);

                int totalQuantity =0;
                for(int i=0 ;  i< productArrayList.size() ; i++){
                    if(selectedArrayList.get(i)){
                        totalQuantity += 1;
                    }
                }
                totalQuantityTv.setText("Total Quantity : " + totalQuantity);

            }
        });


        selfieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permissionGranted){
                    cameraIntent();
                } else{
                    askPermission();
                }

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(imageUri);
            }
        });

    }

    //Function to open camera
    private void cameraIntent() {
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imgname = "IMG_" + timeStamp + ".jpg";
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(camIntent.resolveActivity(getPackageManager())!= null){
            File img_path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"/Unit");
            if(!img_path.exists()){
                if(img_path.mkdirs()){
                    Log.d("SAN","Failed to create Directory");
                }
            }
            File image_name = new File(img_path,imgname);
            Uri img_uri = Uri.fromFile(image_name);
            Log.e("SAN","hello uri      "+img_uri.toString());
            camIntent.putExtra(MediaStore.EXTRA_OUTPUT,img_uri);
            camIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            startActivityForResult(camIntent,REQUEST_CAMERA);
        }
    }

    //Helper function to ask for permissions
    private void askPermission() {
        String[] cam_permission = new String[]{android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,cam_permission,PERM_ID);
        }  else{
            permissionGranted = true;
            cameraIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "GRANT ALL PERMISSIONS", Toast.LENGTH_SHORT).show();
                    permissionGranted = false;
                    return;
                }
            }
            cameraIntent();
        }
        switch (requestCode){
            case PERM_ID: permissionGranted = true;
                break;
            default:super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    File img_path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/Unit");
                    File image_name = new File(img_path, imgname);

                    Bitmap bm = decodebitmap(image_name,700,1000);

                    fOut(bm,image_name);

                    imageUri = getContent(this, image_name);
                    if(imageUri != null){
                        uriAvailable = true;
                    }

                    break;
            }
        }
    }

    //Function to upload image to database
    private void uploadImage(Uri imageUri) {
        if(imageUri != null){
            imageReference = rootReference.child("images/" + UUID.randomUUID().toString());
            Log.d("SAN",imageUri.toString());
            imageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("SAN","Sucess uploading");
                            Toast.makeText(getApplicationContext(),"Failllllllllllllll",Toast.LENGTH_SHORT).show();
                            downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.e("SAN","Download url : " + downloadUrl.toString());
                            int j=0;
                            DatabaseReference orderRef = rootRef.child("orders").push();
                            for(int i=0 ; i<productArrayList.size() ; i++){
                                if(selectedArrayList.get(i)){
                                    String itemNum = "item"+String.valueOf(j);
                                    orderRef.child(itemNum).setValue(productArrayList.get(i).getProdName());
                                    j++;
                                }
                            }

                            /*if(downloadUrl!=null){
                                orderRef.child("selfie_url").setValue(downloadUrl);
                            }else {
                                orderRef.child("selfie_url").setValue(null);
                            }*/

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    Log.d("SAN","FAil uploading");
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Log.d("SAN","Completed uploading");
                }
            });
        }else {
            int j=0;
            DatabaseReference orderRef = rootRef.child("orders").push();
            for(int i=0 ; i<productArrayList.size() ; i++){
                if(selectedArrayList.get(i)){
                    String itemNum = "item"+String.valueOf(j);
                    orderRef.child(itemNum).setValue(productArrayList.get(i).getProdName());
                    j++;
                }
            }
            orderRef.child("selfie_url").setValue("");
        }

    }

    //Helper function to decode bitmap
    public static Bitmap decodebitmap(File image, int rWidth, int rHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getAbsolutePath(),options);

        final int height = options.outHeight;
        final int width = options.outWidth;
        int samplesize = 1;

        if(height > rHeight)
        {
            samplesize = Math.round((float)height/(float)rHeight);
        }
        int expecWidth = width/samplesize;
        if(expecWidth > rWidth)
        {
            samplesize = Math.round((float)width/(float)rWidth);
        }

        options.inSampleSize = samplesize;

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(image.getAbsolutePath(),options);
    }

    //Helper function to write image to outputstream
    public void fOut(Bitmap bm, File image){
        try {
            OutputStream oStream = new FileOutputStream(image);
            bm.compress(Bitmap.CompressFormat.PNG,100,oStream);
            try {
                oStream.flush();
                oStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Helper function to get content uri from filepath
    private Uri getContent(Context context, File image) {

        String imagePath = image.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                new String[] { MediaStore.Files.FileColumns._ID },
                MediaStore.Files.FileColumns.DATA + "=? ",
                new String[] { imagePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), "" + id);
        } else {
            if (image.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.DATA, imagePath);
                return context.getContentResolver().insert(
                        MediaStore.Files.getContentUri("external"), values);
            } else {
                return null;
            }
        }
    }

    @Override
    public void onBackPressed() {

        if(!zoomed && !cashed){
            super.onBackPressed();
        }else if(zoomed && !cashed){
            zoomLayout.setVisibility(View.INVISIBLE);
            zoomed = false;
        } else if(cashed && !zoomed){
            cashlayout.setVisibility(View.INVISIBLE);
            cashed = false;
        }
    }
}
