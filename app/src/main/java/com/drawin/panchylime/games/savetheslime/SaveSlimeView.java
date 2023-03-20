package com.drawin.panchylime.games.savetheslime;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.drawin.panchylime.R;

import java.util.ArrayList;

/**
 * @author Jaekwin, Draco0503
 */
public class SaveSlimeView extends View {

    // DISPLAY DIMENSIONS
    public static int dWidth;
    public static int dHeight;

    // THREAD
    private final long UPDATE_MILLIS = 60; // THREAD REFRESH TIME
    private Handler handler;
    private Runnable runnable;

    // DATABASE PKs
    private String _username;
    private String _pet;

    // WHOLE BACKGROUND
    private Bitmap background;
    private Bitmap ground;
    // SOME OTHER DIMENSIONS NEEDED
    private Rect rectBackground;
    private Rect rectGround;

    private Context context; // THE CONTAINER OF THE VIEW

    // WHAT THE PLAYER MOVES
    private Bitmap slime;
    private float slimeX;
    private float slimeY;
    private float oldX;
    private float oldSlimeX;

    // SOME
    private final Paint textPaint = new Paint();
    private final Paint healthPaint = new Paint();
    private float TEXT_SIZE = 80;

    // PLAYER ATTRIBUTES
    private int points = 0;
    private int life = 3;

    // THE DROPS OF THE GAME
    private ArrayList<Drop> drops;

    /**
     * Constructor of the SaveSlimeView class, initializes the variables of the game
     * @param context mostly the caller activity
     * @param username the player
     * @param pet the selected pet to play with
     */
    public SaveSlimeView(Context context, String username, String pet){
        super(context);
        this.context = context;
        this._username = username;
        this._pet = pet;
        background = BitmapFactory.decodeResource(getResources(), R.drawable.fondo_nochewuenox2);
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.suelo);
        slime = BitmapFactory.decodeResource(getResources(), R.drawable.slime_azul_oscuro);

        Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        rectBackground = new Rect(0,0,dWidth,dHeight);
        rectGround = new Rect(0,dHeight-ground.getHeight(),dWidth,dHeight);
        // Thread
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };

        textPaint.setColor(Color.rgb(255,165,0));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface((ResourcesCompat.getFont(context, R.font.pixeled)));
        healthPaint.setColor(Color.GREEN);
        slimeX = (dWidth / 2) - (slime.getWidth() / 2);
        slimeY = dHeight - ground.getHeight() - slime.getHeight();

        // Creates 3 drops and its animations
        drops = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            FallenDrop fallenDrop = new FallenDrop(context);
            Drop drop = new Drop(context, fallenDrop);

            drops.add(drop);
        }
    }

    /**
     * What is drawn in every step of the loop created by the thread
     * @param canvas
     */
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground, null, rectGround, null);
        canvas.drawBitmap(slime, slimeX, slimeY, null);
        for(int i = 0; i < drops.size(); i++){
            canvas.drawBitmap(drops.get(i).getDrop(), drops.get(i).getDropX(), drops.get(i).getDropY(), null);

            drops.get(i).setDropY(drops.get(i).getDropY() + drops.get(i).getDropSpeed());
            if(drops.get(i).getDropY() + drops.get(i).getDropHeight() >= dHeight - ground.getHeight() + 100){
                points += 10;
                drops.get(i).getFallenDrop().setFallenDropY(drops.get(i).getDropY());
                drops.get(i).getFallenDrop().setFallenDropX(drops.get(i).getDropX());
                for (int j = 0; j < 3 ; j++) {
                    canvas.drawBitmap(drops.get(i).getFallenDrop().getFallenDrop(j),
                            drops.get(i).getFallenDrop().getFallenDropX(),
                            drops.get(i).getFallenDrop().getFallenDropY(),
                            null);
                }
                drops.get(i).resetPosition();
            }
        }
        // The end game logic
        for(int i = 0; i< drops.size(); i++){
            if(drops.get(i).getDropX() + drops.get(i).getDropWidth() >= slimeX
            && drops.get(i).getDropX() <= slimeX + slime.getWidth()
            && drops.get(i).getDropY() + drops.get(i).getDropWidth() >= slimeY
            && drops.get(i).getDropY() + drops.get(i).getDropWidth() <= slimeY + slime.getHeight()){
                life--;
                drops.get(i).resetPosition();
                if(life == 0){
                    @SuppressLint("DrawAllocation")
                    Intent intent = new Intent(context, GameOverActivity.class);
                    intent.putExtra("points", points);
                    intent.putExtra("user", _username);
                    intent.putExtra("pet", _pet);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }

        }

        if(life == 2){
            healthPaint.setColor(Color.YELLOW);
        }else if(life == 1){
            healthPaint.setColor(Color.RED);
        }
        canvas.drawRect(dWidth-200,30,dWidth-200+60*life,80, healthPaint);
        canvas.drawText(""+points, 50, TEXT_SIZE, textPaint);
        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    /**
     * The movement of the players pet
     * @param event
     * @return always true
     */
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event){
        float touchX = event.getX();
        float touchY = event.getY();
        if(touchY >= slimeY){
            int action = event.getAction();
            if(action == MotionEvent.ACTION_DOWN){
                oldX = event.getX();
                oldSlimeX = slimeX;
            }
            if(action == MotionEvent.ACTION_MOVE){
                float shift = oldX - touchX;
                float newSlimeX = oldSlimeX - shift;
                if(newSlimeX <= 0){
                    slimeX = 0;
                }else if(newSlimeX >= dWidth - slime.getWidth()){
                    slimeX = dWidth - slime.getWidth();
                }else{
                    slimeX = newSlimeX;
                }
            }
        }
        return true;
    }
}
