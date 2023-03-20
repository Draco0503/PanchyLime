package com.drawin.panchylime.games.savetheslime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.drawin.panchylime.R;

/**
 * @author Jaekwin, Draco0503
 */
public class FallenDrop {
    // ATTRIBUTES
    private final Bitmap[] fallenDrop = new Bitmap[3];
    private int fallenDropX;
    private int fallenDropY;

    /**
     * Constructor of the FallenDrop class, loads all the animation states of a drop
     * @param context where the class is going to be shown
     */
    public FallenDrop(Context context){
        fallenDrop[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.gota_apachurra);
        fallenDrop[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.gota_apachurra_gota);
        fallenDrop[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.gota_apachurra_gotasuelo);
    }

    /**
     * Gets the image of the index given
     * @param dropFrame index of the array given
     * @return image selected
     */
    public Bitmap getFallenDrop(int dropFrame){
        return fallenDrop[dropFrame];
    }

    /**
     * Gets FallenDrop X
     * @return FallenDrop X
     */
    public int getFallenDropX() {
        return fallenDropX;
    }

    /**
     * Sets the FallenDrop X
     * @param fallenDropX new fallenDropX
     */
    public void setFallenDropX(int fallenDropX) {
        this.fallenDropX = fallenDropX;
    }

    /**
     * Gets FallenDrop Y
     * @return FallenDrop Y
     */
    public int getFallenDropY() {
        return fallenDropY;
    }

    /**
     * Sets the FallenDrop Y
     * @param fallenDropY new fallenDropY
     */
    public void setFallenDropY(int fallenDropY) {
        this.fallenDropY = fallenDropY;
    }
}
