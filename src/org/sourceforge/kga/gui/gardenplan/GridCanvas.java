package org.sourceforge.kga.gui.gardenplan;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GridCanvas extends ResizableCanvas
{
    int zoomFactor = 100;

    public GridCanvas()
    {
        paint();
    }

    @Override
    public void paint()
    {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.setLineWidth(0.1);

        // for on-screen draw the grid on the entire panel
        for (int horizontal = 0; true; ++horizontal)
        {
            double x = horizontal * GardenView.GRID_SIZE * zoomFactor / 100 + 0.5;
            if (x >= getWidth())
                break;
            gc.strokeLine(x, 0, x, getHeight());
        }
        for (int vertical = 0; true; ++vertical)
        {
            double y = vertical * GardenView.GRID_SIZE * zoomFactor / 100 + 0.5;
            if (y >= getHeight())
                break;
            gc.strokeLine(0, y, getWidth(), y);
        }
    }

    public void setZoomFactor(int zoomFactor)
    {
        this.zoomFactor = zoomFactor;
        paint();
    }
}
