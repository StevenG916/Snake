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
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.io.IOException;

class SnakeGame extends SurfaceView implements Runnable {

    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    // for playing sound effects
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;

    // How many points does the player have
    private int mScore;

    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    // A snake ssss
    private Snake mSnake;
    // And an apple
    private Apple mApple;

    private Bitmap background;

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        // Work out how many pixels each block is
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;
        background = BitmapFactory.decodeResource(getResources(), R.drawable.hornets);

        textPaint.setColor(Color.MAGENTA); // Set the text color
        textPaint.setTextSize(50); // Set the text size
        textPaint.setTextAlign(Paint.Align.RIGHT); // Align text to the right

        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes
                                    .CONTENT_TYPE_SONIFICATION)
                            .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
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
            // Error
        }

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Call the constructors of our two game objects
        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

    }

    // Called to start a new game
    public void newGame() {

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Get the apple ready for dinner
        mApple.spawn();

        // Reset the mScore
        mScore = 0;

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();
    }

    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused) {
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

        // Run at 10 frames per second
        final long TARGET_FPS = 10;
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // Are we due to update the frame
        if(mNextFrameTime<= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }

        return false;
    }

    // Update all the game objects
    public void update() {
        // Move the snake
        mSnake.move();

        // Did the head of the snake eat the apple?
        if(mSnake.checkDinner(mApple.getLocation())){
            // This reminds me of Edge of Tomorrow.
            // One day the apple will be ready!

            mApple.spawn(true);

            // Add to  mScore
            mScore = mScore + 1;

            // Play a sound
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        // Did the snake die?
        if (mSnake.detectDeath()) {
            // Pause the game ready to start again
            mSP.play(mCrashID, 1, 1, 0, 0, 1);

            mPaused =true;
        }
    }

    // Do all the drawing
    public void draw() {
        // Get a lock on the mCanvas

        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            // Fill the screen with a color
            mCanvas.drawBitmap(background, null, new Rect(0, 0, getWidth(), getHeight()), null);

            // Set the size and color of the mPaint for the text
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(120);

            // Draw the score
            mCanvas.drawText("" + mScore, 20, 120, mPaint);

            // Draw the apple and the snake
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);

            // Draw some text while paused
            if(mPaused){

                // Set the size and color of mPaint for the text
                mPaint.setColor(Color.BLACK);
                mPaint.setTextSize(250);

                // Draw the message
                // We will give this an international upgrade soon
                mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
                /*mCanvas.drawText(getResources().
                                getString(R.string.tap_to_play),
                        200, 700, mPaint);*/

            }
            String name1 = "Savannah";
            String name2 = "Steven";

            float margin = 30; // Adjust margin as needed
            float x = getWidth() - margin; // Align to the right with margin

            float y1 = textPaint.getTextSize() + margin; // Position from the top with margin
            mCanvas.drawText(name1, x, y1, textPaint);

            float gap = 10; // Gap between names
            float y2 = y1 + textPaint.getTextSize() + gap;

            mCanvas.drawText(name2, x, y2, textPaint);


            // Unlock the Canvas to show graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        Log.d("SnakeGame", "Touch event X: " + motionEvent.getX() + ", Y: " + motionEvent.getY() + ", Halfway Point: " + mSnake.getHalfWayPoint());
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

        if(mThread != null) {
            try {
                mThread.join();
            }
            catch (InterruptedException e) {
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

    public void addPauseButton(Context context) {
        ImageButton pauseButton = new ImageButton(context);
        pauseButton.setImageResource(R.drawable.ic_action_pause); // Your pause button drawable
        pauseButton.setBackgroundColor(Color.TRANSPARENT); // Optionally make the button background transparent

        // Define layout parameters to position your button - let's say at the top-right corner
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.END
        );
        int margin = getResources().getDimensionPixelSize(R.dimen.pause_button_margin); // Define a margin in your dimens.xml
        layoutParams.setMargins(margin, margin, margin, margin);

        // Add this view to the SurfaceView's parent (which should be a FrameLayout or similar)
        ((ViewGroup) this.getParent()).addView(pauseButton, layoutParams);

        // Set the click listener to pause the game
        pauseButton.setOnClickListener(v -> {
            if(mPlaying && !mPaused) {
                // Game is currently playing, so pause it
                pause();
                pauseButton.setImageResource(R.drawable.ic_action_pause); // Change icon to play/resume
            } else {
                // Game is paused, so resume it
                resume();
                pauseButton.setImageResource(R.drawable.ic_action_pause); // Change back to pause icon
            }
        });
    }

    }





