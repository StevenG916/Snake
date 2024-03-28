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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;

class Snake {

    // The location in the grid of all the segments
    private ArrayList<Point> segmentLocations;

    // The size of each snake segment
    private int mSegmentSize;

    // Total grid size
    private Point mMoveRange;

    // The center of the screen horizontally in pixels
    private int halfWayPoint;

    // For tracking movement Heading
    private enum Heading {
        UP, RIGHT, DOWN, LEFT
    }
    private Heading heading = Heading.RIGHT;

    // Bitmaps for possible head directions and the body
    private Bitmap mBitmapHeadRight;
    private Bitmap mBitmapHeadLeft;
    private Bitmap mBitmapHeadUp;
    private Bitmap mBitmapHeadDown;
    private Bitmap mBitmapBody;

    /*
    Constructor
     */
    Snake(Context context, Point mr, int ss) {
        segmentLocations = new ArrayList<>();
        mSegmentSize = ss;
        mMoveRange = mr;

        /*
        Create a matrix for scaling
         */
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);

        /*
        Create and scale bitmap headings for the head
         */
        mBitmapHeadRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);
        mBitmapHeadLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);
        mBitmapHeadUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);
        mBitmapHeadDown = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);

        /*
        Ensure that the head of the snake always faces the correct direction
         */
        mBitmapHeadRight = Bitmap.createScaledBitmap(mBitmapHeadRight, ss, ss, false);
        mBitmapHeadLeft = Bitmap.createBitmap(mBitmapHeadRight, 0, 0, ss, ss, matrix, true);

        // A matrix for rotating
        matrix.preRotate(-90);
        mBitmapHeadUp = Bitmap.createBitmap(mBitmapHeadRight, 0, 0, ss, ss, matrix, true);

        /*
        Multiply matrix by 180 so it faces down
         */
        matrix.preRotate(180);
        mBitmapHeadDown = Bitmap.createBitmap(mBitmapHeadRight, 0, 0, ss, ss, matrix, true);

        // Create and scale the body
        mBitmapBody = BitmapFactory.decodeResource(context.getResources(), R.drawable.body);
        mBitmapBody = Bitmap.createScaledBitmap(mBitmapBody, ss, ss, false);

        /*
        Use the halfway point to detect which side of the screen was pressed
         */
        halfWayPoint = mr.x * ss / 2;
    }

    /*
    Get the Snake ready for a new game
     */
    void reset(int w, int h) {
        /*
        Reset the heading
         */
        heading = Heading.RIGHT;

        /*
        Clear the ArrayList of old contents
         */
        segmentLocations.clear();

        /*
        Add a single segment to the snake
         */
        segmentLocations.add(new Point(w / 2, h / 2));
    }

    /*
    Controls movement
     */
    void move() {
        /*
        Start at the back and move it to the position of the segment in front of it.
        Then make it the same value as the next segment going forwards towards the head
         */
        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            segmentLocations.get(i).x = segmentLocations.get(i - 1).x;
            segmentLocations.get(i).y = segmentLocations.get(i - 1).y;
        }

        // Get the existing head position
        Point p = segmentLocations.get(0);

        // Use the existing head position to move appropriately
        switch (heading) {
            case UP:
                p.y--;
                break;
            case RIGHT:
                p.x++;
                break;
            case DOWN:
                p.y++;
                break;
            case LEFT:
                p.x--;
                break;
        }

        //Insert the adjusted point back into position 0
        segmentLocations.set(0, p);
    }

    boolean detectDeath() {
        boolean dead = segmentLocations.get(0).x == -1 ||
                segmentLocations.get(0).x > mMoveRange.x ||
                segmentLocations.get(0).y == -1 ||
                segmentLocations.get(0).y > mMoveRange.y;
        /*
        Determine if the edge of the screen has been hit
         */
        /*
        Determine if the snake has eaten itself via collision
         */
        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            if (segmentLocations.get(0).x ==
                    segmentLocations.get(i).x &&
                    segmentLocations.get(0).y ==
                            segmentLocations.get(i).y) {
                dead = true;
                break;
            }
        }
        return dead;
    }

    boolean checkDinner(Point l) {
        if (segmentLocations.get(0).x == l.x && segmentLocations.get(0).y == l.y) {
            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!segmentLocations.isEmpty()) {
            switch (heading) {
                case RIGHT:
                    canvas.drawBitmap(mBitmapHeadRight, segmentLocations.get(0).x * mSegmentSize,
                            segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
                case LEFT:
                    canvas.drawBitmap(mBitmapHeadLeft, segmentLocations.get(0).x * mSegmentSize,
                            segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
                case UP:
                    canvas.drawBitmap(mBitmapHeadUp, segmentLocations.get(0).x * mSegmentSize,
                            segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
                case DOWN:
                    canvas.drawBitmap(mBitmapHeadDown, segmentLocations.get(0).x * mSegmentSize,
                            segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
            }

            // Draw the snake body one block at a time
            for (int i = 1; i < segmentLocations.size(); i++) {
                canvas.drawBitmap(mBitmapBody, segmentLocations.get(i).x * mSegmentSize,
                        segmentLocations.get(i).y * mSegmentSize, paint);
            }
        }
    }

    // Handle changing direction
    void switchHeading(MotionEvent motionEvent) {
        // Is the tap on the right hand side? If so --> rotate right
        if (motionEvent.getX() >= halfWayPoint) {
            heading = rotateClockwise(heading);
        }
         // rotate left
        else {
            heading = rotateCounterClockwise(heading);
        }
    }

    /*
    Ordinal values:
    UP - 0
    DOWN - 1
    RIGHT - 2
    LEFT - 3
     */

    private Heading rotateClockwise(Heading currDirection) {
        //grab the current ordinal value and convert it into an int
        int index = currDirection.ordinal();

        //rotate clockwise: increment the ordinal by one. Modulo is to keep within array boundaries
        index = (index + 1) % 4;

        //convert back to enum
        return Heading.values()[index];
    }

    public int getHalfWayPoint() {
        return halfWayPoint;
    }

    private Heading rotateCounterClockwise(Heading currDirection) {
        //grab the current ordinal value and convert it into an int
        int index = currDirection.ordinal();

        /*
        Rotate counter clockwise:
        Deincrement the ordinal by one, and add by 4 to ensure the value is always positive.
        Modulo is used to keep within array boundaries.
         */
        index = (index - 1 + 4) % 4;
        return Heading.values()[index];
    }
}