package com.example.application.pokedex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    long startTime, elapsedTime;
    HistoryActivity historyActivity = new HistoryActivity();
    PokeDatabase pokeDatabase = new PokeDatabase(this);
    String[] pokemon_list;
    CardView cardView;
    AutoCompleteTextView pokeEnter;
    Button pokeSearch;
    ImageView pokeImage;
    TextView pokeName, pokeType, pokeHeight, pokeWeight, pokeExperience, pokeAbility, pokeSpeed, pokeSd, pokeSa, pokeDefence, pokeAttack, pokeHp;
    String selected_poke= null;
    ProgressBar progressBar;
    HttpClient httpClient = new HttpClient();
    JSONParser jsonParser = new JSONParser();

    public static int POSITION=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("STATE",Context.MODE_PRIVATE);
        POSITION = preferences.getInt("ID",0);

        pokemon_list = getResources().getStringArray(R.array.Pokemon);
        pokeEnter = (AutoCompleteTextView)findViewById(R.id.pokeEnter);
        pokeSearch = (Button) findViewById(R.id.pokeSearch);
        cardView = (CardView)findViewById(R.id.cardView);
        progressBar = (ProgressBar) findViewById(R.id.pBar);

        pokeImage = (ImageView)findViewById(R.id.poke_img);
        pokeName = (TextView)findViewById(R.id.poke_name);
        pokeType = (TextView)findViewById(R.id.poke_type);
        pokeHeight = (TextView)findViewById(R.id.poke_height);
        pokeWeight = (TextView)findViewById(R.id.poke_weight);
        pokeExperience = (TextView)findViewById(R.id.poke_experience);
        pokeAbility = (TextView)findViewById(R.id.poke_ability);
        pokeSpeed = (TextView)findViewById(R.id.poke_speed);
        pokeSd = (TextView)findViewById(R.id.poke_sd);
        pokeSa = (TextView)findViewById(R.id.poke_sa);
        pokeDefence = (TextView)findViewById(R.id.poke_defence);
        pokeAttack = (TextView)findViewById(R.id.poke_attack);
        pokeHp = (TextView)findViewById(R.id.poke_hp);

        ArrayAdapter text_adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,pokemon_list);
        pokeEnter.setAdapter(text_adapter);
        selected_poke = pokeEnter.getText().toString();

        pokeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_poke = pokeEnter.getText().toString();
                startTime = SystemClock.uptimeMillis();
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pokeEnter.getWindowToken(), 0);

                if(isNetworkAvailable()) {
                    if (!(selected_poke.isEmpty())) {
                            PokeTask pokeTask = new PokeTask();
                            pokeTask.execute(selected_poke);
                            pokeEnter.setText("");

                    }
                    else {
                        Toast.makeText(MainActivity.this, "Enter a Pokemon", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this,"NO INTERNET CONNECTION",Toast.LENGTH_SHORT).show();
                }

            }
        });

        pokeEnter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                selected_poke = pokeEnter.getText().toString();
                startTime = SystemClock.uptimeMillis();
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pokeEnter.getWindowToken(),0);
                    if(isNetworkAvailable()) {
                        if (!(selected_poke.isEmpty())) {
                            PokeTask pokeTask = new PokeTask();
                            pokeTask.execute(selected_poke);
                            pokeEnter.setText("");

                        }
                        else {
                            Toast.makeText(MainActivity.this, "Enter a Pokemon", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this,"NO INTERNET CONNECTION",Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public class PokeTask extends AsyncTask<String,Void,Pokemon>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cardView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            pokeName.setVisibility(View.INVISIBLE);
            pokeImage.setVisibility(View.INVISIBLE);
            pokeType.setVisibility(View.INVISIBLE);
            pokeHeight.setVisibility(View.INVISIBLE);
            pokeWeight.setVisibility(View.INVISIBLE);
            pokeExperience.setVisibility(View.INVISIBLE);
            pokeAbility.setVisibility(View.INVISIBLE);
            pokeSpeed.setVisibility(View.INVISIBLE);
            pokeSd.setVisibility(View.INVISIBLE);
            pokeSa.setVisibility(View.INVISIBLE);
            pokeDefence.setVisibility(View.INVISIBLE);
            pokeAttack.setVisibility(View.INVISIBLE);
            pokeHp.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Pokemon doInBackground(String... params) {
            Pokemon pokemon = new Pokemon();

                String data = httpClient.getPokemon(params[0]);
                elapsedTime = SystemClock.uptimeMillis() - startTime;

                if (data == null) {
                    Log.e("SAN","Null Data Recieved");
                    if(elapsedTime > 19000) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Connection Timeout", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "No such Pokemon found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    return null;
                }

                try {
                    pokemon = jsonParser.getPokemonData(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pokeDatabase.createData(new HistoryPokemon(POSITION,pokemon.getName(),pokemon.getImage()));


                return pokemon;


        }

        @Override
        protected void onPostExecute(Pokemon pokemon) {
            super.onPostExecute(pokemon);

            if(pokemon != null) {
                POSITION++;
                progressBar.setVisibility(View.INVISIBLE);

                pokeName.setVisibility(View.VISIBLE);
                pokeImage.setVisibility(View.VISIBLE);
                pokeType.setVisibility(View.VISIBLE);
                pokeHeight.setVisibility(View.VISIBLE);
                pokeWeight.setVisibility(View.VISIBLE);
                pokeExperience.setVisibility(View.VISIBLE);
                pokeAbility.setVisibility(View.VISIBLE);
                pokeSpeed.setVisibility(View.VISIBLE);
                pokeSd.setVisibility(View.VISIBLE);
                pokeSa.setVisibility(View.VISIBLE);
                pokeDefence.setVisibility(View.VISIBLE);
                pokeAttack.setVisibility(View.VISIBLE);
                pokeHp.setVisibility(View.VISIBLE);

                //pokeDatabase.createData(pokemon);

                pokeName.setText(pokemon.getName());
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < pokemon.getTypes().length; i++) {
                    if (i < (pokemon.getTypes().length - 1))
                        builder.append(pokemon.getTypes()[i] + ", ");
                    else
                        builder.append(pokemon.getTypes()[i]);
                }
                pokeType.setText(builder.toString());

                pokeHeight.setText("Height : " + String.valueOf(pokemon.getHeight()));
                pokeWeight.setText("Weight : " + String.valueOf(pokemon.getWeight()));
                pokeExperience.setText("Experience : " + String.valueOf(pokemon.getBase_experience()));

                StringBuilder builder1 = new StringBuilder();
                for (int i = 0; i < pokemon.getAbilities().length; i++) {
                    if (i < (pokemon.getAbilities().length - 1))
                        builder1.append(pokemon.getAbilities()[i] + ", ");
                    else
                        builder1.append(pokemon.getAbilities()[i]);
                }
                pokeAbility.setText("Ability : " + builder1.toString());

                pokeSpeed.setText(pokemon.getStat_name()[0] + " : " + Integer.valueOf(pokemon.getBase_stat()[0]));
                pokeSd.setText(pokemon.getStat_name()[1] + " : " + Integer.valueOf(pokemon.getBase_stat()[1]));
                pokeSa.setText(pokemon.getStat_name()[2] + " : " + Integer.valueOf(pokemon.getBase_stat()[2]));
                pokeDefence.setText(pokemon.getStat_name()[3] + " : " + Integer.valueOf(pokemon.getBase_stat()[3]));
                pokeAttack.setText(pokemon.getStat_name()[4] + " : " + Integer.valueOf(pokemon.getBase_stat()[4]));
                pokeHp.setText(pokemon.getStat_name()[5] + " : " + Integer.valueOf(pokemon.getBase_stat()[5]));
                if(pokemon.getImage() == "null"){
                    Glide.with(getApplicationContext())
                            .load(R.drawable.pokeball)
                            .into(pokeImage);
                }else {
                    Glide.with(getApplicationContext())
                            .load(pokemon.getImage())
                            .into(pokeImage);
                }

            }
            else {
                progressBar.setVisibility(View.INVISIBLE);
                cardView.setVisibility(View.INVISIBLE);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.history:
                Intent hIntent = new Intent(MainActivity.this,HistoryActivity.class);
                startActivity(hIntent);
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("STATE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("ID",POSITION);
        editor.apply();
    }
}
