package com.example.application.deltafinaltask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    int count;
    private boolean isPlaying;
    private boolean gameWon;
    Canvas canvas;
    Paint paint;
    private Thread gameThread = null;
    private SurfaceHolder surfaceHolder;
    private Context context;
    int screenX,screenY;
    boolean paused = true;

    Paddle paddle;
    Ball ball;
    Brick[] bricks = new Brick[200];
    int numBricks = 0;

    int score = 0;
    int brickWidth,brickHeight;

    public GameView(Context context,int screenX,int screenY) {
        super(context);

        this.context = context;
        this.screenX = screenX;
        this.screenY = screenY;
        count = 0;

        surfaceHolder = getHolder();
        paint = new Paint();
        gameWon = false;

        paddle = new Paddle(context,screenX,screenY);
        ball = new Ball(context,screenX,screenY);

        Restart();
        createBrick();
        score = 0;
    }

    public void Restart(){

        ball.reset(screenX, screenY);

        brickWidth = screenX / 8;
        brickHeight = screenY / 12;
    }

    public void createBrick(){
        numBricks = 0;

        for(int column = 0; column < 8; column ++ ){
            for(int row = 1; row < 6; row ++ ){
                Random generator = new Random();
                boolean x = generator.nextBoolean();
                if(x){
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks ++;
                    Log.e("SAN", "Brick No: " + numBricks);
                }else{
                    continue;
                }
            }
        }
        int score = 0;

    }

    @Override
    public void run() {
        while(isPlaying){
            if(!paused) {
                update();
            }
            draw();
            control();
        }
    }

    public void update() {

        paddle.update();
        ball.update();
        
        if(Rect.intersects(paddle.getRect(),ball.getRect())) {
                //ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top - 2);

        }

        for(int i = 0; i < numBricks; i++){
            if (bricks[i].getVisibility()){
                if(Rect.intersects(bricks[i].getRect(),ball.getRect())) {
                    float wy = (ball.ballWidth + brickWidth) * (ball.getRect().centerY() - bricks[i].getRect().centerY());
                    float hx = (ball.ballHeight + brickHeight) * (ball.getRect().centerX() - bricks[i].getRect().centerX());

                    if(wy > hx){
                        if (wy > -hx){
                            bricks[i].setInvisible();
                            ball.reverseYVelocity();
                            score = score + 10;
                        }else{
                            bricks[i].setInvisible();
                            ball.reverseXVelocity();
                            score = score + 10;
                        }
                    } else{
                        if (wy > -hx){
                            bricks[i].setInvisible();
                            ball.reverseXVelocity();
                            score = score + 10;
                        } else{
                            bricks[i].setInvisible();
                            ball.reverseYVelocity();
                            score = score + 10;
                        }
                    }
                }
            }
        }

        if(ball.getRect().bottom > screenY){
            ball.reverseYVelocity();
            ball.clearObstacleY(screenY - 2);
            paused = true;
            Restart();
            lifeLineDialog();

        }

        if(ball.getRect().top < 0){
            ball.reverseYVelocity();
            ball.clearObstacleY(12);
        }

        if(ball.getRect().left < 0){
            ball.reverseXVelocity();
            ball.clearObstacleX(2);
        }

        if(ball.getRect().right > screenX - 10){
            ball.reverseXVelocity();
            ball.clearObstacleX(screenX - 22);
        }


        if(score == numBricks * 10){
            gameWon = true;
        }

        if(gameWon){
            retrydialog();
        }


    }

    public void draw(){

        if(surfaceHolder.getSurface().isValid()){
            canvas = surfaceHolder.lockCanvas();

            canvas.drawColor(Color.BLACK);

            paint.setColor(Color.rgb(255,255,255));
            canvas.drawRect(ball.getRect(), paint);

            canvas.drawBitmap(paddle.getBitmap(),paddle.getX(),paddle.getY(),paint);

            paint.setTextSize(40);
            canvas.drawText("Score: " + score, 10,50, paint);

            paint.setColor(Color.rgb(160,160,160));
            for(int i = 0; i < numBricks; i++){
                if(bricks[i].getVisibility()) {
                    canvas.drawRect(bricks[i].getRect(), paint);
                }
            }

            if(score == numBricks * 10){
                paused = true;
                //Restart();
                //createBrick();
            }



            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    private void control(){
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause(){
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(){
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if(gameWon){
            Intent gIntent = new Intent(context,GameActivity.class);
            gIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(gIntent);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {


            case MotionEvent.ACTION_DOWN:

                paused = false;

                /*
                if(event.getX() > screenX / 2){
                    paddle.setMovementState(paddle.RIGHT);
                }
                else{
                    paddle.setMovementState(paddle.LEFT);
                }
                */

                break;
            case MotionEvent.ACTION_UP:
                //paddle.setMovementState(paddle.STOPPED);
                break;
        }


        return super.onTouchEvent(event);
    }

    public void lifeLineDialog(){


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(count != 0){
                    gameOverDialog();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.activity_life,null,false);
                builder.setView(view);
                builder.setCancelable(false);

                final AlertDialog lifeDialog = builder.create();
                lifeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final ImageButton life,quit;
                life =(ImageButton)view.findViewById(R.id.life);
                quit =(ImageButton)view.findViewById(R.id.quit_button);

                life.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lifeDialog.dismiss();
                        paused = false;
                        paddle.setX(screenX/2);
                        paddle.setY(screenY-100);
                        count++;
                    }
                });

                quit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gameOverDialog();
                    }
                });
                lifeDialog.show();
            }
        });
    }

    public void gameOverDialog(){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.activity_gameover,null);
                builder.setView(view);
                builder.setCancelable(false);
                final AlertDialog over_dialog = builder.create();
                view.findViewById(R.id.gameover_tv);
                TextView scoreTv = (TextView)view.findViewById(R.id.score_tv);
                scoreTv.setText("Score : " + score);

                view.findViewById(R.id.gameover_replay).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gIntent = new Intent(context,GameActivity.class);
                        gIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(gIntent);

                    }
                });

                over_dialog.show();
            }
        });
    }

    public void retrydialog(){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.activity_gameover,null);
                builder.setView(view);
                builder.setCancelable(false);
                final AlertDialog over_dialog = builder.create();
                TextView rTv = (TextView)view.findViewById(R.id.gameover_tv);
                rTv.setText("GAME WON");

                TextView scoreTv = (TextView)view.findViewById(R.id.score_tv);
                scoreTv.setText("Score : " + score);

                view.findViewById(R.id.gameover_replay).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gIntent = new Intent(context,GameActivity.class);
                        gIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(gIntent);

                    }
                });

                over_dialog.show();
            }
        });

    }


   public void ySpeed(int yS){
       paddle.setMovementState(yS);
   }

   public void setP()
   {
       paused = false;
   }


}
