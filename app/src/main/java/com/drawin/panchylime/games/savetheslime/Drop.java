package com.drawin.panchylime.games.savetheslime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.drawin.panchylime.R;

import java.util.Random;

/**
 * @author Jaekwin, Draco0503
 */
public class Drop {
    // ATTRIBUTES
    private Bitmap drop;

    private int dropX;
    private int dropY;
    private int dropSpeed;
    // THE ANIMATION OF THE DROP
    private FallenDrop fallenDrop;

    /**
     * Constructor of the Drop class
     * @param context where the drop is going to be
     * @param fallenDrop the drop's animation
     */
    public Drop(Context context, FallenDrop fallenDrop){
        this.fallenDrop = fallenDrop;
        drop = BitmapFactory.decodeResource(context.getResources(), R.drawable.gota_grande);
        resetPosition();
    }

    /**
     * Gets the drop width
     * @return drop width
     */
    public int getDropWidth(){
        return drop.getWidth();
    }

    /**
     * Gets the drop height
     * @return drop height
     */
    public int getDropHeight(){
        return drop.getHeight();
    }

    /**
     * Resets drop position to the top of the screen
     */
    public void resetPosition(){
        Random random = new Random();
        dropX = random.nextInt(SaveSlimeView.dWidth - getDropWidth());
        dropY = -200 + random.nextInt(600)* -1;
        dropSpeed = 35 + random.nextInt(16);
    }

    /**
     * Gets the drop image
     * @return drop image
     */
    public Bitmap getDrop() {
        return drop;
    }

    /**
     * Gets the drop X
     * @return drop X
     */
    public int getDropX() {
        return dropX;
    }

    /**
     * Gets the drop Y
     * @return drop Y
     */
    public int getDropY() {
        return dropY;
    }

    /**
     * Sets the Y of the drop to dropY param
     * @param dropY new drpY
     */
    public void setDropY(int dropY) {
        this.dropY = dropY;
    }

    /**
     * Gets the drop speed
     * @return drop speed
     */
    public int getDropSpeed() {
        return dropSpeed;
    }

    /**
     * Gets the drop animation
     * @return animation class of the drop
     */
    public FallenDrop getFallenDrop() {
        return fallenDrop;
    }
}
