/*
Savannah Birdsong-See
Steven Graham

CSC 133 - Assignment 3
Snake Game
 */

package com.example.snake;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

public class SnakeActivity extends Activity {
    // Declare an instance of SnakeGame
    SnakeGame mSnakeGame;
    boolean mWasGamePaused;

    /*
    Setting up the game
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create a new instance of the SnakeEngine class
        mSnakeGame = new SnakeGame(this, size);

        // Make snakeEngine the view of the Activity
        setContentView(mSnakeGame);
        mSnakeGame.addPauseButton(this);
    }

    /*
     Start the thread in SnakeEngine
     */
    @Override
    protected void onResume() {
        Log.d("SnakeActivity.java", "onResume()");
        super.onResume();
        // Optionally resume the game

        if (mSnakeGame != null && mWasGamePaused) {
            mSnakeGame.resume();
            mWasGamePaused = false;
        }
    }

    /*
     Stop the thread in SnakeEngine
     */
    @Override
    protected void onPause() {
        Log.d("SnakeActivity.java", "onPause()");
        super.onPause();
        if (mSnakeGame != null) {
            mSnakeGame.pause();
            mWasGamePaused = true;
        }
    }
}
