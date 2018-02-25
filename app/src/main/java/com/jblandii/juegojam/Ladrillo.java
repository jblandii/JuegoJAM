package com.jblandii.juegojam;

import android.graphics.RectF;

public class Ladrillo {

    private RectF rect;

    private boolean esVisible;

    public Ladrillo(int fila, int columna, int ancho, int alto) {
        esVisible = true;
        int padding = 1;
        rect = new RectF(columna * ancho + padding,
                fila * alto + padding,
                columna * ancho + ancho - padding,
                fila * alto + alto - padding);
    }

    public RectF getRect() {
        return this.rect;
    }

    public void setInvisible() {
        esVisible = false;
    }

    public boolean getVisibility() {
        return esVisible;
    }
}
