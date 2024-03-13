package com.example.snake;

public interface Collidable {

    boolean checkCollision(GameObject other);
    void onCollision(GameObject other);
}
