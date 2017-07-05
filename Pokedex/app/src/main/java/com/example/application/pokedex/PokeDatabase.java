package com.example.application.pokedex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class PokeDatabase {

    private static String DATABASE_NAME = "pokemonData";
    private static int DATABASE_VERSION = 1;
    private static String TABLE_NAME = "historyTable";
    private static String KEY_C_ID = "id";
    private static String KEY_NAME = "name";
    private static String KEY_IMAGE = "image";

    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " + KEY_C_ID + " INTEGER PRIMARY KEY, "
            + KEY_NAME + " TEXT, " + KEY_IMAGE + " TEXT );";

    Context context;

    public PokeDatabase(Context context) {
        this.context = context;
    }

    DbHelper dbHelper;


    class DbHelper extends SQLiteOpenHelper{

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TEABLE IF EXISTS" + TABLE_NAME);
            onCreate(db);
        }
    }

    public void createData(HistoryPokemon hp){
        dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_C_ID,hp.position);
        cv.put(KEY_NAME,hp.name);
        cv.put(KEY_IMAGE,hp.image);
        database.insert(TABLE_NAME,null,cv);
    }

    public ArrayList<HistoryPokemon> getData(){
        dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<HistoryPokemon> temp = new ArrayList<>();
        String[] columns = {KEY_C_ID,KEY_NAME,KEY_IMAGE};
        Cursor cursor = database.query(TABLE_NAME,columns,null,null,null,null,null);
        for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()){
            int iName = cursor.getColumnIndex(KEY_NAME);
            int iImage = cursor.getColumnIndex(KEY_IMAGE);
            int iPosition = cursor.getColumnIndex(KEY_C_ID);
            int position = cursor.getInt(iPosition);
            String name = cursor.getString(iName);
            String image = cursor.getString(iImage);
            HistoryPokemon temp_poke = new HistoryPokemon(position,name,image);
            temp.add(temp_poke);
        }
        cursor.close();
        return temp;
    }

    public void removeRow(HistoryPokemon poke){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int position = poke.position;
        database.delete(TABLE_NAME, KEY_C_ID + " = " + position, null);
    }

    public void removeData(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        database.delete(TABLE_NAME,null,null);
    }
}
