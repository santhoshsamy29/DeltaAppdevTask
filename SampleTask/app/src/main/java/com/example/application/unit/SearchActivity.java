package com.example.application.unit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    AutoCompleteTextView searchBar;
    RelativeLayout searchLayout;
    LinearLayout didYouMeanLayout;
    ImageButton micButton,closeButton,searchButton;
    ImageView unitPic,logo;
    TextView title,desc,didYouMean;
    ProgressBar progressBar;
    View separator;
    private Boolean firstTouch = false;
    ArrayList<String> predictions = new ArrayList<>();
    ArrayList<Integer> maxLcs;
    String name,description,lcsName;
    ArrayAdapter prediction_adapter;
    DatabaseReference rootRef,prodRef,nameRef;
    CardView cardView;
    Boolean dataAvailable = false;
    Boolean slideMiddleBool = false;

    private final int REQ_CODE_SPEECH_INPUT = 9002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        logo = findViewById(R.id.logo);
        searchButton = findViewById(R.id.ic_search);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        didYouMean = findViewById(R.id.did_you_mean);
        searchBar = findViewById(R.id.search_bar);
        searchLayout = findViewById(R.id.search_layout);
        didYouMeanLayout = findViewById(R.id.did_you_mean_layout);
        micButton = findViewById(R.id.ic_mic);
        closeButton = findViewById(R.id.ic_close);
        unitPic = findViewById(R.id.unit_pic);
        progressBar = findViewById(R.id.desc_progress);
        cardView = findViewById(R.id.dataCard);
        separator = findViewById(R.id.separator);

        cardView.setVisibility(View.INVISIBLE);
        didYouMean.setPaintFlags(didYouMean.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.setText("");
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                didYouMeanLayout.setVisibility(View.GONE);
                displayData();
            }
        });

        didYouMean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                searchBar.setText(lcsName);
                searchBar.dismissDropDown();
                didYouMeanLayout.setVisibility(View.GONE);
                showData(lcsName);
            }
        });

        rootRef = FirebaseDatabase.getInstance().getReference();
        prodRef = rootRef.child("products");

        prodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String value = snapshot.getKey();
                    predictions.add(value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(predictions != null){
            prediction_adapter = new ArrayAdapter(this,R.layout.single_prediction,predictions);
            searchBar.setAdapter(prediction_adapter);
        }

        searchBar.addTextChangedListener(filterTextWatcher);

        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!firstTouch){
                    slideToAbove();
                    logo.setVisibility(View.INVISIBLE);
                    unitPic.setVisibility(View.GONE);
                    firstTouch = true;
                    return true;
                }else {
                    return false;
                }
            }
        });

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                        keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    cardView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    didYouMeanLayout.setVisibility(View.GONE);
                    searchBar.dismissDropDown();
                    displayData();
                    return true;
                }
                return false;
            }
        });

        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                didYouMeanLayout.setVisibility(View.GONE);
                displayData();
            }
        });

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void displayData(){
        name = searchBar.getText().toString().toLowerCase();
        prodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.e("SAN",snapshot.getKey().toString());
                    if(snapshot.getKey().toString().equals(name)){
                        dataAvailable = true;
                        showData(name);
                    }
                }
                if(!dataAvailable){
                    //Toast.makeText(getApplicationContext(),"No such data",Toast.LENGTH_SHORT).show();

                    Log.e("SAN",name.toCharArray().toString());
                    maxLcs = new ArrayList<>();
                    for(int i =0 ; i<predictions.size() ; i++){
                        Log.d("SAN",i + " : " +predictions.get(i));
                        maxLcs.add(i,lcs(name.toCharArray(),predictions.get(i).toCharArray(),name.length(),predictions.get(i).length()));
                        Log.d("SAN",maxLcs.get(i).toString());
                    }

                    int largestLCSIndex = largest(maxLcs,maxLcs.size());
                    Log.e("SAN",String.valueOf(largestLCSIndex));

                    lcsName = predictions.get(largestLCSIndex);
                    didYouMeanLayout.setVisibility(View.VISIBLE);
                    didYouMean.setText(lcsName);

                    cardView.setVisibility(View.VISIBLE);
                    showData(lcsName);

                    //searchButton.setVisibility(View.VISIBLE);
                    //separator.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private int largest(ArrayList<Integer> maxLcs, int size) {

        int largest = 0;
        for ( int i = 1; i < maxLcs.size(); i++ )
        {
            if(maxLcs.get(i) > maxLcs.get(largest)){
                largest = i;
            }
        }
        return largest;
    }

    int lcs( char[] X, char[] Y, int m, int n )
    {
        if (m == 0 || n == 0)
            return 0;
        if (X[m-1] == Y[n-1])
            return 1 + lcs(X, Y, m-1, n-1);
        else
            return max(lcs(X, Y, m, n-1), lcs(X, Y, m-1, n));
    }

    int max(int a, int b)
    {
        return (a > b)? a : b;
    }

    private void showData(String name) {
        nameRef = prodRef.child(name);
        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals("desc")){
                        description = snapshot.getValue(String.class);
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
                title.setText(nameRef.getKey().toUpperCase());
                desc.setText(description);
                cardView.setVisibility(View.VISIBLE);
                separator.setVisibility(View.VISIBLE);
                separator.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.VISIBLE);
                searchBar.dismissDropDown();

                //RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(searchButton.getWidth(),searchButton.getHeight());
                RelativeLayout.LayoutParams q = new RelativeLayout.LayoutParams(closeButton.getWidth(),
                        closeButton.getHeight());

                q.addRule(RelativeLayout.LEFT_OF,separator.getId());
                q.addRule(RelativeLayout.CENTER_VERTICAL);
                closeButton.setLayoutParams(q);

                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                dataAvailable = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                dataAvailable = false;
            }
        });
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


            closeButton.setVisibility(View.VISIBLE);
            micButton.setVisibility(View.INVISIBLE);
            //separator.setVisibility(View.VISIBLE);
            //searchButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void slideToAbove() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -5.0f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        searchLayout.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                searchLayout.clearAnimation();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        searchLayout.getWidth(), searchLayout.getHeight());
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.setMargins(0,5,0,5);
                searchLayout.setLayoutParams(lp);
                slideMiddleBool = true;
            }

        });

    }

    public void slideToMiddle() {
        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 5.0f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        searchLayout.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                unitPic.setVisibility(View.VISIBLE);
                logo.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.INVISIBLE);
                searchButton.setVisibility(View.INVISIBLE);
                separator.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                searchLayout.clearAnimation();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        searchLayout.getWidth(), searchLayout.getHeight());

                int dpValue = 15;
                float d = getApplicationContext().getResources().getDisplayMetrics().density;
                int margin = (int)(dpValue * d);

                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                lp.setMargins(margin,5,margin,5);
                searchLayout.setLayoutParams(lp);
                slideMiddleBool = false;
                firstTouch = false;

                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(micButton.getWidth(),
                        micButton.getHeight());
                p.addRule(RelativeLayout.CENTER_VERTICAL);
                p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                micButton.setLayoutParams(p);
                searchBar.setText("");
                RelativeLayout.LayoutParams q = new RelativeLayout.LayoutParams(closeButton.getWidth(),
                        closeButton.getHeight());
                q.addRule(RelativeLayout.CENTER_VERTICAL);
                q.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                closeButton.setLayoutParams(q);
                closeButton.setVisibility(View.INVISIBLE);
                micButton.setVisibility(View.VISIBLE);
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchBar.setText(result.get(0));
                    slideToAbove();
                    logo.setVisibility(View.INVISIBLE);
                    unitPic.setVisibility(View.GONE);
                    firstTouch = true;
                    displayData();
                }
                break;
            }

        }
    }

    @Override
    public void onBackPressed() {
        if(slideMiddleBool){
            slideToMiddle();
        }else{
            super.onBackPressed();
        }
    }
}
