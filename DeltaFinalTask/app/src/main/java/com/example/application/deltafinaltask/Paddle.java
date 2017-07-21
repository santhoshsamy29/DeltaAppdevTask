package com.example.application.deltafinaltask;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

public class Paddle {


    Rect rect;
    private Bitmap bitmap;

    private int x;
    private int y;
    private int maxX,maxY;
    private int paddleSpeed;
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    private int paddleMoving = STOPPED;


    public Paddle(Context context,int screenX, int screenY){

        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.paddle);
        x = screenX / 2;
        y = screenY - 100;
        maxX = screenX;
        maxY = screenY;
        //Log.d("SAN","Paddle width : " + bitmap.getWidth());

        rect = new Rect(x, y, x+ bitmap.getWidth(), y + bitmap.getHeight());
        paddleSpeed = 30;
    }

    public void update(){

        if(paddleMoving == LEFT){
           x-=paddleSpeed;
        }

        if(paddleMoving == RIGHT){
            x+= paddleSpeed;

        }

        if(x > maxX-bitmap.getWidth()){
           x = maxX - bitmap.getWidth();
        }

        if(x < 0){
            x = 0;
        }
        rect.left = x;
        rect.top = y;
        rect.right = x + bitmap.getWidth();
        rect.bottom = y + bitmap.getHeight();

    }



    public Rect getRect(){
        return rect;
    }

    public void setMovementState(int state){
        paddleMoving = state;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
