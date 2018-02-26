package com.jblandii.juegojam;

import android.graphics.RectF;

public class Paleta {

    /*
    RectF es un objeto que tiene cuatro coordenadas.
     */
    private RectF rect;

    /*
    El ancho que tendrá la paleta.
     */
    private float longitud;
    private float altura;

    /*
     X es el extremo izquierdo del rectángulo que forma nuestra paleta.
     */
    private float x;

    /*
     Y es la coordenada superior.
     */
    private float y;

    /*
    Esto mantendrá los píxeles por segundo de velocidad que la paleta se moverá.
     */
    private float velocidadPaleta;

    /*
    Formas de mover la paleta.
     */
    public final int PARADO = 0;
    public final int IZQUIERDA = 1;
    public final int DERECHA = 2;

    /*
    Para saber si se mueve la paleta y en que dirección.
     */
    private int paletaMoviendose = PARADO;

    /*
    Este es el método constructor.
    Cuando creo un objeto de esta clase, paso el ancho y la altura de la pantalla.
     */
    public Paleta(int screenX, int screenY){
        /*
        130 píxeles de ancho y 20 de alto
         */
        longitud = 130;
        altura = 20;

        /*
        Aparece aproximadamente en el medio de la pantalla.
         */
        x = screenX / 2;
        y = screenY - 20;

        // Initialize rectangle
        rect = new RectF(x, y, x + longitud, y + altura);

        /*
        La rapidez de la paleta en pixeles por segundo.
         */
        velocidadPaleta = 600;
    }


    /*
    Este es un método getter para hacer el rectángulo que define la paleta disponible en la clase BreakoutView.
     */
    public RectF getRect(){
        return rect;
    }

    /*
    Este método se usará para cambiar / configurar si la paleta va hacia la izquierda, a la derecha o hacia la nada.
     */
    public void setMovementState(int state){
        paletaMoviendose = state;
    }

    /*
    Se llamará a este método de actualización desde la actualización en BreakoutView.
    Determina si la paleta necesita moverse y cambia las coordenadas contenido en rect si es necesario.
     */
    public void update(long fps){
        if(paletaMoviendose == IZQUIERDA){
            x = x - velocidadPaleta / fps;
        }

        if(paletaMoviendose == DERECHA){
            x = x + velocidadPaleta / fps;
        }
        rect.left = x;
        rect.right = x + longitud;
    }

}

