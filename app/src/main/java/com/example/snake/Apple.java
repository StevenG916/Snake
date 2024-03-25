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

    // The location of the apple on the grid
    // Not in pixels
    //private Point mLocation = new Point();

    // The range of values we can choose from
    // to spawn an apple
    // private Point mSpawnRange;
    // private int mSize;

    // An image to represent the apple
    private Bitmap mBitmapApple;

    /// Set up the apple in the constructor
    Apple(@NonNull Context context, Point spawnRange, int size){
        
        super(spawnRange, size);

        // Make a note of the passed in spawn range
        this.spawnRange = spawnRange;

        /*// Make a note of the size of an apple
        mSize = s;*/
        // Hide the apple off-screen until the game starts
        //position.x = -10;

        // Load the image to the bitmap
        mBitmapApple = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.apple);

        // Resize the bitmap
        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, size, size, false);
    }

    // This is called every time an apple is eaten
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

    // Overloaded spawn method with random characteristics
    void spawn(int size) {
        Random random = new Random();
        position.x = random.nextInt(spawnRange.x) + 1;
        position.y = random.nextInt(spawnRange.y - 1) + 1;
        this.size = size; // Update size based on the parameter

    }

   // Let SnakeGame know where the apple is
    // SnakeGame can share this with the snake
    Point getLocation(){
        return position;
    }

    // Draw the apple

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(mBitmapApple,
                position.x * size, position.y * size, paint);

    }
    @Override
    public void update() {
        // Update logic here, if necessary for the apple (likely empty)
    }

    //@Override
    /*public void draw(Canvas canvas, Paint paint) {
        if (mBitmapApple == null){

        }
        canvas.drawBitmap(mBitmapApple, position.x * size, position.y * size, paint);

    }*/
}