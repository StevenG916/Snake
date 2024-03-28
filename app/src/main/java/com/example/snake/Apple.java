/*
Savannah Birdsong-See
Steven Graham

CSC 133 - Assignment 3
Snake Game
 */

package com.example.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Random;

class Apple extends GameObject implements Drawable{
    private Bitmap mBitmapApple;

    /*
    Constructor
     */
    Apple(@NonNull Context context, Point spawnRange, int size){
        
        super(spawnRange, size);

        // Make a note of the passed in spawn range
        this.spawnRange = spawnRange;

        // Load the image to the bitmap
        mBitmapApple = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.apple);

        // Resize the bitmap
        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, size, size, false);
    }

    /*
    Spawn() is called when the first apple is spawned into the game; i.e. when the game is first
    loaded
     */
    void spawn() {
        // Choose two random values and place the apple
        Random random = new Random();
        if (spawnRange.x > 0 && spawnRange.y > 0) {
            position.x = random.nextInt(spawnRange.x) + 1;
            position.y = random.nextInt(spawnRange.y - 1) + 1;
        } else {
            // Log an error or set default values if spawnRange is not correctly initialized
            Log.e("Apple", "spawnRange must be positive, but was: " + spawnRange);
            // Optionally set default spawn position or handle the error as appropriate
        }
    }

    /*
    Whenever an apple is eaten, the overloaded spawn method is called and handles
    every subsequent apple spawn
     */
    void spawn(boolean isEaten) {
        //spawn a new apple
        Random random = new Random();
        if (spawnRange.x > 0 && spawnRange.y > 0) {
            position.x = random.nextInt(spawnRange.x) + 1;
            position.y = random.nextInt(spawnRange.y - 1) + 1;
        } else {
            Log.e("Apple", "spawnRange must be positive, but was: " + spawnRange);
        }
    }

    //Gets the current location of the apple
    Point getLocation(){
        return position;
    }

    //Draws the apple
    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(mBitmapApple,
                position.x * size, position.y * size, paint);
    }
    @Override
    public void update() {
        // Update logic here, if necessary
    }
}