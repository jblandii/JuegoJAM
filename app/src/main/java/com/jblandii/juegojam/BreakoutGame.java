package com.jblandii.juegojam;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class BreakoutGame extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    BreakoutView breakoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);

    }

    /*
    Aquí está nuestra implementación de GameView.
    Implemento runnable para así tener un hilo y poder anular el método run.
     */
    class BreakoutView extends SurfaceView implements Runnable {

        Thread gameThread = null;
        /*
        Se necesita un SurfaceHolder.
        Cuando usamos Paint y Canvas en un hilo.
        */
        SurfaceHolder ourHolder;

        /*
        Boleano para saber si el juego está en ejecución o no.
         */
        volatile boolean jugando;

        /*
        El juego estará pausado en el inicio.
         */
        boolean pausado = true;

        /*
        Objetos canvas y paint.
         */
        Canvas canvas;
        Paint paint;

        /*
        Esta variable monitorea los cuadros por segundo del juego, y la siguiente es para monitorear los cuadros por segundo del juego.
         */
        long fps;
        private long timeThisFrame;

        /*
        El tamaño de la pantalla en píxeles.
         */
        int screenX;
        int screenY;

        /*
        La paleta de jugadores.
         */
        Paleta paleta;

        /*
        La bola.
         */
        Bola ball;

        // Up to 200 bricks
        Ladrillo[] bricks = new Ladrillo[200];
        int numBricks = 0;

        /*
        Puntuación, Vidas.
         */
        int puntuacion = 0;
        int vidas = 3;

        /*
        Cuando inicializo (call new()) en GameView, este método contructor empieza a ejecutarse.
         */
        public BreakoutView(Context context) {
            /*
            Pido a la clase SurfaceView que establezca nuestro objeto.
             */
            super(context);

            /*
            Inicializo los Objetos Holder y Paint.
             */
            ourHolder = getHolder();
            paint = new Paint();

            /*
            Obtengo un objeto de visualización para acceder a los detalles de la pantalla
             */
            Display display = getWindowManager().getDefaultDisplay();
            /*
            Cargo la resolución en un objeto Point.
             */
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            paleta = new Paleta(screenX, screenY);

            /*
            Creo una bola.
             */
            ball = new Bola(screenX, screenY);
            createBricksAndRestart();

        }

        public void createBricksAndRestart() {

            // Put the ball back to the start
            ball.reset(screenX, screenY);

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;

            /*
            Construyo una pared de ladrillos.
             */
            numBricks = 0;
            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 3; row++) {
                    bricks[numBricks] = new Ladrillo(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }

            /*
            Si el juego excede los puntajes y vidas
             */
            if (vidas == 0) {
                puntuacion = 0;
                vidas = 3;
            }

        }

        @Override
        public void run() {
            while (jugando) {

                /*
                Capturo la hora actual en milisegundos en startFrameTime.
                 */
                long startFrameTime = System.currentTimeMillis();

                /*
                Actualizo el marco
                 */
                if (!pausado) {
                    update();
                }

                /*
                Dibujo el marco
                 */
                draw();

                /*
                Calculo el fps este cuadro. Y uso el resultado para sincronizar animaciones y más.
                 */
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        /*
        Lo que necesita ser actualizado va aquí. Movimiento, detención de colisión, etc.
        */

        public void update() {

            /*
            Mueve la paleta si es necesario.
             */
            paleta.update(fps);

            ball.update(fps);

            /*
            Compruebo si la bola colisiona con un ladrillo
             */
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        puntuacion = puntuacion + 10;
                    }
                }
            }

            /*
            Compruebe si la bola colisiona con la paleta.
            */
            if (RectF.intersects(paleta.getRect(), ball.getRect())) {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(paleta.getRect().top - 2);
            }

            /*
            Rebota la pelota cuando golpea la parte inferior de la pantalla.
             */
            if (ball.getRect().bottom > screenY) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 2);

                /*
                Pierde vidas.
                 */
                vidas--;
                /*soundPool.play(loseLifeID, 1, 1, 0, 0, 1);*/

                if (vidas == 0) {
                    pausado = true;
                    createBricksAndRestart();
                }
            }

            /*
            Rebota la pelota cuando golpea la parte superior de la pantalla.
            */
            if (ball.getRect().top < 0) {
                ball.reverseYVelocity();
                ball.clearObstacleY(12);
            }

            /*
            Si la pelota golpea la pared izquierda rebota.
            */
            if (ball.getRect().left < 0) {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
            }

            /*
            Si la pelota golpea la pared derecha rebota.
             */
            if (ball.getRect().right > screenX - 10) {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 22);
            }


            /*
            Pausa el juego si se queda sin ladrillos.
             */
            if (puntuacion == numBricks * 10) {
                pausado = true;
                createBricksAndRestart();
            }

        }

        /*
        Dibuja la escena recien actualizada.
         */
        public void draw() {

            /*
            Me aseguro de que la superficie del dibujo sea válida o se colapsa.
             */
            if (ourHolder.getSurface().isValid()) {
                /*
                Bloqueo el lienzo listo para dibujar.
                 */
                canvas = ourHolder.lockCanvas();

                /*
                Dibujo el color de fondo.
                 */
                canvas.drawColor(Color.argb(255, 26, 128, 182));

                /*
                Elijo el color del pincel para dibujar.
                 */
                paint.setColor(Color.argb(255, 255, 255, 255));

                /*
                Dibujo la paleta
                 */
                canvas.drawRect(paleta.getRect(), paint);

                /*
                Dibujo la paleta
                 */
                canvas.drawRect(ball.getRect(), paint);


                /*
                Cambio el color del pincel para dibujar.
                 */
                paint.setColor(Color.argb(255, 249, 129, 0));

                /*
                Dibujo los ladrillos si son visibles.
                 */
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                /*
                Elijo el color del pincel para dibujar
                 */
                paint.setColor(Color.argb(255, 255, 255, 255));

                /*
                Dibujo la puntuación
                 */
                paint.setTextSize(40);
                canvas.drawText("Score: " + puntuacion + "   Lives: " + vidas, 10, 50, paint);

                /*
                Compruebo si el jugador ha eliminado todos los ladrillos.
                 */
                if (puntuacion == numBricks * 10) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!", 10, screenY / 2, paint);
                }

                /*
                Compruebo si el jugador ha perdido.
                 */
                if (vidas <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE LOST!", 10, screenY / 2, paint);
                }

                /*
                Dibujo en la pantalla.
                 */
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        /*
        Si SimpleGameEngine Activity está en pausa / detenido, cierra el hilo.
         */
        public void pause() {
            jugando = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        /*
        Si se inicia Simple Game Engine Activity, entonces, comienza el hilo.
         */
        public void resume() {
            jugando = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        /*
        La clase SurfaceView implementa onTouchListener. Así puedo anular este método y detectar toques en la pantalla.
         */
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                /*
                Si el jugador toca la pantalla.
                 */
                case MotionEvent.ACTION_DOWN:
                    pausado = false;
                    if (motionEvent.getX() > screenX / 2) {
                        paleta.setMovementState(paleta.RIGHT);
                    } else {
                        paleta.setMovementState(paleta.LEFT);
                    }
                    break;
                /*
                Si el jugador levanta el dedo de la pantalla.
                 */
                case MotionEvent.ACTION_UP:

                    paleta.setMovementState(paleta.STOPPED);
                    break;
            }
            return true;
        }

    }
    /*
    Este es el final de la clase interna BreakoutView. Este método se ejecuta cuando el jugador inicia el juego.
    */
    @Override
    protected void onResume() {
        super.onResume();

        /*
        Dice al metodo de reanudar gameView.
         */
        breakoutView.resume();
    }

    /*
    Este método se ejecuta cuando el jugador abandona el juego.
     */
    @Override
    protected void onPause() {
        super.onPause();
        /*
        Dice al método de pausa gameView que se ejecute
         */
        breakoutView.pause();
    }
}