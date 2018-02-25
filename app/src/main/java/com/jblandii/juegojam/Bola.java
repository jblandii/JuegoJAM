package com.jblandii.juegojam;

import android.graphics.RectF;

import java.util.Random;

public class Bola {
    RectF rect;
    float xVelocity;
    float yVelocity;
    float anchoBola = 10;
    float altoBola = 10;


    public Bola(int screenX, int screenY){
        /*
        La pelota viajando a 100 pixeles por segundo.
        */
        xVelocity = 200;
        yVelocity = -400;
        /*
        Coloco la bola en el centro de la pantalla en la parte inferior, y le doy una forma de cuadrado de 10 pixeles por 10 pixeles
         */
        rect = new RectF();

    }

    public RectF getRect(){
        return rect;
    }

    public void update(long fps){
        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);
        rect.right = rect.left + anchoBola;
        rect.bottom = rect.top - altoBola;
    }

    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }


    public void reverseXVelocity(){
        xVelocity = - xVelocity;
    }

    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    public void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = y - altoBola;
    }

    public void clearObstacleX(float x){
        rect.left = x;
        rect.right = x + anchoBola;
    }

    public void reset(int x, int y){
        rect.left = x / 2;
        rect.top = y - 20;
        rect.right = x / 2 + anchoBola;
        rect.bottom = y - 20 - altoBola;
    }


}
