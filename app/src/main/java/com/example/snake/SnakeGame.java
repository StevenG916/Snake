/*
Savannah Birdsong-See
Steven Graham

CSC 133 - Assignment 3
Snake Game
 */

package com.example.snake;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import java.io.IOException;

class SnakeGame extends SurfaceView implements Runnable {
    /*
    Objects for the game loop/thread
     */
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    /*
    For playing sound
     */
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;

    // How many points does the player have
    private int mScore;

    /*
    Objects for drawing
     */
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    // A snake ssss
    private Snake mSnake;

    // And an apple
    private Apple mApple;

    private Bitmap background;
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /*
     This is the constructor method that gets called from SnakeActivity
     */
    public SnakeGame(Context context, Point size) {
        super(context);

        /*
        Determine how many pixels each block is and how many blocks
        can fit
         */
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mNumBlocksHigh = size.y / blockSize;

        /*
        Set the background image,
        text colour, text size, and text alignment
         */
        background = BitmapFactory.decodeResource(getResources(), R.drawable.hornets);
        textPaint.setColor(Color.MAGENTA);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.RIGHT);

        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage
                            (AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.
                    CONTENT_TYPE_SONIFICATION).build();
            mSP = new SoundPool.Builder().setMaxStreams(5).setAudioAttributes(audioAttributes)
                    .build();
        }
        else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        } catch (IOException e) {
            Log.d("mSP", "Sound not found");
        }

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Call the constructors of our two game objects
        mApple = new Apple(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        mSnake = new Snake(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
    }

    // Called to start a new game
    public void newGame() {
        /*
        Reset the Snake,
        spawn an apple,
        reset the player's score,
        and set mNextFrameTime to allow an update to be triggered
         */
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        mApple.spawn();
        mScore = 0;
        mNextFrameTime = System.currentTimeMillis();
    }

    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if (!mPaused) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }
            draw();
        }
    }

    // Check to see if it is time for an update
    public boolean updateRequired() {
        final long TARGET_FPS = 10;
        final long MILLIS_PER_SECOND = 1000; // 1000 ms in 1 second

        if (mNextFrameTime <= System.currentTimeMillis()) {
            /*
            If 1/10 a second has passed - update the frame
            Setup when the next update will be triggered
             */
            mNextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / TARGET_FPS;
            return true;
        }
        return false;
    }

    // Updates all the game objects
    public void update() {
        // Move the snake
        mSnake.move();

        // Did the head of the snake eat the apple?
        if (mSnake.checkDinner(mApple.getLocation())) {
            mApple.spawn(true);
            mScore = mScore + 1;
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        if (mSnake.detectDeath()) {
            mSP.play(mCrashID, 1, 1, 0, 0, 1);
            mPaused = true;
        }
    }

    // Performs all the drawing
    public void draw() {
        /*
        Lock onto mCanvas:
        Fill canvas with colour, set size and text colour,
        Draw the score, apple, snake, and paused text
         */
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawBitmap(background, null, new Rect(0, 0, getWidth(), getHeight()), null);

            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(120);
            mCanvas.drawText("" + mScore, 20, 120, mPaint);

            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);

            if (mPaused) {
                mPaint.setColor(Color.BLACK);
                mPaint.setTextSize(250);
                mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
            }
            String name1 = "Savannah";
            String name2 = "Steven";

            float margin = 30;
            float x = getWidth() - margin; // Align to the right with margin

            float y1 = textPaint.getTextSize() + margin; // Position from the top with margin
            mCanvas.drawText(name1, x, y1, textPaint);

            float gap = 10;
            float y2 = y1 + textPaint.getTextSize() + gap;

            mCanvas.drawText(name2, x, y2, textPaint);

            // Unlock the Canvas to show graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        Log.d("SnakeGame", "Touch event X: " + motionEvent.getX() + ", Y: " +
                motionEvent.getY() + ", Halfway Point: " + mSnake.getHalfWayPoint());
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (mPaused) {
                mPaused = false;
                newGame();
                performClick();

                // Don't want to process snake direction for this tap
                return true;
            }

            // Let the Snake class handle the input
            mSnake.switchHeading(motionEvent);
        }
        return true;
    }

    // Stop the thread
    public void pause() {
        mPlaying = false;

        if (mThread != null) {
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mThread = null;
        }
    }

    // Start the thread
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    // Method for button functionality
    public void addPauseButton(Context context) {
        ImageButton pauseButton = new ImageButton(context);
        pauseButton.setImageResource(R.drawable.baseline_pause_circle_24); // Your pause button drawable
        pauseButton.setBackgroundColor(Color.TRANSPARENT);

        // Lock the button to the bottom-right
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.END);
        int margin = getResources().getDimensionPixelSize(R.dimen.pause_button_margin);
        layoutParams.setMargins(margin, margin, margin, margin);

        // Add view to the SurfaceView's parent
        ((ViewGroup) this.getParent()).addView(pauseButton, layoutParams);

        // Set the click listener to pause the game
        pauseButton.setOnClickListener(v -> {
            if (mPlaying && !mPaused) {
                // Game is currently playing, so pause it
                pause();
                pauseButton.setImageResource(R.drawable.ic_action_pause); // Change icon to play/resume
            } else {
                // Game is paused, so resume it
                resume();
                pauseButton.setImageResource(R.drawable.baseline_pause_circle_24); // Change back to pause icon
            }
        });
    }
}
