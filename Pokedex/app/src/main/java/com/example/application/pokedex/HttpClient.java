package com.example.application.pokedex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpClient {

    public static String BASE_URL = "http://pokeapi.co/api/v2/pokemon/";

    public String getPokemon(String pokemon) {

        HttpURLConnection conn = null;
        InputStream is = null;


        try {
            conn = (HttpURLConnection) (new URL(BASE_URL + pokemon)).openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            StringBuffer pokeBuffer = new StringBuffer();
            is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line = null;
            while((line = br.readLine()) != null){
                pokeBuffer.append(line + "\n");
            }

            is.close();
            conn.disconnect();
            return pokeBuffer.toString();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
