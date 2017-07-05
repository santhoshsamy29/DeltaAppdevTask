package com.example.application.pokedex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {

    public Pokemon getPokemonData(String data) throws JSONException{

        Pokemon poke = new Pokemon();

        ArrayList<String> poke_ability = new ArrayList<>();
        ArrayList<String> tStat_name = new ArrayList<>();
        ArrayList<Integer> tBase_stat = new ArrayList<>();
        ArrayList<String> tType = new ArrayList<>();

        JSONObject jObj = new JSONObject(data);

        poke.setName(getString("name",jObj));
        poke.setBase_experience(getInt("base_experience",jObj));
        poke.setHeight(getInt("height",jObj));
        poke.setWeight(getInt("weight",jObj));

        JSONObject sprite_obj = getObject("sprites",jObj);
        poke.setImage(getString("front_default",sprite_obj));

        JSONArray abilities_arr = jObj.getJSONArray("abilities");

        for(int i=0 ; i<abilities_arr.length() ; i++){
            JSONObject abilities_obj = abilities_arr.getJSONObject(i);

            JSONObject ability_obj = getObject("ability",abilities_obj);
            poke_ability.add(getString("name",ability_obj));
        }
        String[] poke_ability_arr = new String[poke_ability.size()];
        poke_ability_arr = poke_ability.toArray(poke_ability_arr);
        poke.setAbilities(poke_ability_arr);

        JSONArray stats_arr = jObj.getJSONArray("stats");

        for(int i=0 ; i<stats_arr.length() ; i++){
            JSONObject stats_obj = stats_arr.getJSONObject(i);
            tBase_stat.add(getInt("base_stat",stats_obj));

            JSONObject stat_obj = getObject("stat",stats_obj);
            tStat_name.add(getString("name",stat_obj));
        }
        String[] tStat_name_arr = new String[tStat_name.size()];
        tStat_name_arr = tStat_name.toArray(tStat_name_arr);
        poke.setStat_name(tStat_name_arr);
        Integer[] tBase_stat_arr = new Integer[tBase_stat.size()];
        tBase_stat_arr = tBase_stat.toArray(tBase_stat_arr);
        poke.setBase_stat(tBase_stat_arr);

        JSONArray types_arr = jObj.getJSONArray("types");
         for(int i=0; i<types_arr.length() ; i++){
             JSONObject types_obj = types_arr.getJSONObject(i);

             JSONObject type_obj = getObject("type",types_obj);
             tType.add(getString("name",type_obj));
         }
         String[] tType_arr = new String[tType.size()];
         tType_arr = tType.toArray(tType_arr);
         poke.setTypes(tType_arr);

        return poke;

    }

    private static JSONObject getObject(String tag, JSONObject jObject) throws JSONException {
        JSONObject subObj = jObject.getJSONObject(tag);
        return subObj;
    }

    private static String getString(String tag, JSONObject jObject) throws JSONException {
        return jObject.getString(tag);
    }

    private static int getInt(String tag, JSONObject jObject) throws JSONException {
        return jObject.getInt(tag);
    }

}
