/*
Savannah Birdsong-See
Steven Graham

CSC 133 - Assignment 3
Snake Game
 */

package com.example.snake;

import android.graphics.Point;

abstract class GameObject {
    protected Point position;
    protected Point spawnRange;
    protected int size;

    public GameObject(Point position, int size) {
        this.position = new Point();
        this.size = size;
        this.spawnRange = new Point();
    }

    /*
    Abstract method to be implemented by subclasses.
    It is called when the the GameObject needs to be updated
     */
    public abstract void update();

}
