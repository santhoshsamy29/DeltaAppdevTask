package com.example.application.deltatasktwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;

public class MyDataBase {

    private static String DATABASE_NAME = "deltaTwo";
    private static int DATABASE_VERSION = 1;
    private static String TABLE_NAME = "image_list";
    private static String KEY_C_ID = "id";
    private static String KEY_CAPTION = "caption";
    private static String KEY_IMAGE = "image";

    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " + KEY_C_ID + " INTEGER PRIMARY KEY, "
            + KEY_CAPTION + " TEXT, " + KEY_IMAGE + " TEXT );";

    Context context;
    dbHelper mdbhelper;

    public MyDataBase(Context context) {
        this.context = context;
    }

    class dbHelper extends SQLiteOpenHelper{
        public dbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
            onCreate(db);
        }
    }

    public long createData(CardView card){
        mdbhelper = new dbHelper(context);
        SQLiteDatabase database = mdbhelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_C_ID,card.position);
        cv.put(KEY_CAPTION,card.caption);
        cv.put(KEY_IMAGE,card.image.toString());
        return database.insert(TABLE_NAME,null,cv);
    }

    public ArrayList<CardView> getData(){
        mdbhelper = new dbHelper(context);
        SQLiteDatabase database = mdbhelper.getReadableDatabase();
        ArrayList<CardView> temp_array = new ArrayList<>();
        String[] columns = {KEY_C_ID,KEY_CAPTION,KEY_IMAGE};
        Cursor cursor = database.query(TABLE_NAME,columns,null,null,null,null,null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                int i1 = cursor.getColumnIndex(KEY_C_ID);
                int i2 = cursor.getColumnIndex(KEY_CAPTION);
                int i3 = cursor.getColumnIndex(KEY_IMAGE);
                int _id = cursor.getInt(i1);
                String caption = cursor.getString(i2);
                String img_str = cursor.getString(i3);
                Uri img_uri = Uri.parse(img_str);
                CardView card = new CardView(_id,caption,img_uri);
                temp_array.add(card);

            }
        cursor.close();
        return temp_array;
    }

    public void updateData(CardView card,Uri img_uri){
        mdbhelper = new dbHelper(context);
        SQLiteDatabase database = mdbhelper.getWritableDatabase();
        ContentValues cv_update = new ContentValues();
        cv_update.put(KEY_IMAGE,img_uri.toString());
        cv_update.put(KEY_CAPTION,card.caption);
        database.update(TABLE_NAME,cv_update,KEY_C_ID + "=?",new String[]{String.valueOf(card.position)});
    }

    public void removeRow(CardView card){
        SQLiteDatabase database = mdbhelper.getWritableDatabase();
        int pos = card.position;
        database.delete(TABLE_NAME, KEY_C_ID + " = " + pos, null);
    }

    public void close(){
        mdbhelper.close();
    }
}
