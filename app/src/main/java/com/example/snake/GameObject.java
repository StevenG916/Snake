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

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point newPosition) {
        this.position = newPosition;
    }

    // Abstract method to be implemented by subclasses
    public abstract void update();

    // Other common methods
}
