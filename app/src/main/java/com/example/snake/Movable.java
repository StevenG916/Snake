package com.example.snake;

import android.graphics.Path;

public interface Movable {

    void move();
    void changeDirection(Path.Direction newDirection);
}
