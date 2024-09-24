package org.sourceforge.kga.gui.gardenplan;

import javafx.scene.canvas.Canvas;


public abstract class ResizableCanvas extends Canvas
{

    @Override
    public boolean isResizable() { return true; }

    @Override
    public double minHeight(double width)
    {
        return 0;
    }

    @Override
    public double maxHeight(double width)
    {
        return Double.MAX_VALUE;
    }

    /* @Override
    public double prefHeight(double width)
    {
        return minHeight(width);
    } */

    @Override
    public double minWidth(double height)
    {
        return 0;
    }

    /*@Override
    //public double prefWidth(double width)
    {
        return minWidth(width);
    } */

    @Override
    public double maxWidth(double height)
    {
        return Double.MAX_VALUE;
    }


    @Override
    public void resize(double width, double height)
    {
        super.setWidth(width);
        super.setHeight(height);
        paint();
    }

    public abstract void paint();
}
