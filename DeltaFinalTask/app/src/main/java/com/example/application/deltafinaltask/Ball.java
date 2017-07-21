package com.example.application.deltafinaltask;


import android.content.Context;
import android.graphics.Rect;

import java.util.Random;

public class Ball {

    Rect rect;
    int xVelocity;
    int yVelocity;
    int ballWidth = 15;
    int ballHeight = 15;

    public Ball(Context context,int screenX, int screenY){

        xVelocity = 10;
        yVelocity = -10;

        rect = new Rect(screenX/2 ,screenY-150,(screenX/2)+ballWidth,(screenY/2)+ballHeight);
    }

    public Rect getRect(){
        return rect;
    }

    public void update(){

        rect.left = rect.left + (xVelocity);
        rect.top = rect.top + (yVelocity);
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top + ballHeight;
    }

    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = - xVelocity;
    }

    public void setRandomXVelocity(){
        Random generator = new Random();
        int rX = generator.nextInt(2);

        if(rX == 0){
            reverseXVelocity();
        }
    }


    public void clearObstacleY(int y){
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    public void clearObstacleX(int x){
        rect.left = x;
        rect.right = x + ballWidth;
    }



    public void reset(int x, int y){
        rect.left = x / 2;
        rect.top = y - 150;
        rect.right = x / 2 + ballWidth;
        rect.bottom = y - 150 + ballHeight;
    }
}


