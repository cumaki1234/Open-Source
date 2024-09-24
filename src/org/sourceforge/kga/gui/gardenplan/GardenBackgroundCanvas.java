package org.sourceforge.kga.gui.gardenplan;


import javafx.geometry.Dimension2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.rules.Rule;

import org.sourceforge.kga.Point;
import org.sourceforge.kga.TaxonVariety;

import java.util.Map;

public class GardenBackgroundCanvas extends Canvas
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(GardenCanvas.class.getName());

    public static final int PLANT_SIZE = 48;
    public static final int PADDING = Rule.IMAGE_SIZE;
    public static final int GRID_SIZE = PLANT_SIZE + 2 * PADDING;
    EditableGarden garden = null;
    int year;
    static final int COLORS_COUNT = 4;
    Color colors[][] = new Color[COLORS_COUNT][COLORS_COUNT];

    public GardenBackgroundCanvas()
    {
        for (int good = 0; good < COLORS_COUNT; ++good)
            for (int bad = 0; bad < COLORS_COUNT; ++bad)
                colors[good][bad] = new Color(
                    (bad >= good ? COLORS_COUNT : bad) / (float)COLORS_COUNT,
                    (good >= bad ? COLORS_COUNT : good) / (float)COLORS_COUNT,
                    0f,
                    (bad > good ? bad : good) / (float)COLORS_COUNT);
    }

    private Dimension2D getImageSize()
    {
        return new Dimension2D((garden.getBounds().width + 2) * GRID_SIZE, (garden.getBounds().height + 2) * GRID_SIZE);
    }

    public void repaint()
    {
        if (garden == null)
            return;

        // TODO: getTopLevelAncestor().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Dimension2D imageSize = getImageSize();
        setWidth(imageSize.getWidth() * garden.getZoomFactor() / 100.);
        setHeight(imageSize.getHeight() * garden.getZoomFactor() / 100.);
        GraphicsContext g = getGraphicsContext2D();

        // clear
        org.sourceforge.kga.Rectangle bounds = garden.getBounds();
        Affine affineBackup = g.getTransform();
        g.setTransform(garden.gridToImage(bounds.getLocation()));
        g.clearRect(-GRID_SIZE, -GRID_SIZE, (bounds.width + 2) * GRID_SIZE, (bounds.height + 2) * GRID_SIZE);

        // draw backgrounds
        TaxonVariety<Plant> selectedPlant = garden.getSelectedPlant();
        if (selectedPlant != null && !selectedPlant.isItem())
        {
            log.info("Draw backgrounds " + bounds.toString());
            org.sourceforge.kga.Point p = new org.sourceforge.kga.Point(bounds.x + 1, bounds.y + 1);
            for (p.x = bounds.x - 1; p.x <= bounds.x + bounds.width; ++p.x)
                for (p.y = bounds.y - 1; p.y <= bounds.y + bounds.height; ++p.y)
                    paintSquare(g, p);
        }
        g.setTransform(affineBackup);
        // TODO: getTopLevelAncestor().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /* Affine transformToImage(GraphicsContext g, Point p)
    {
        Affine affineBackup = g.getTransform();
        org.sourceforge.kga.Rectangle bounds = garden.getBounds();
        g.scale(garden.getZoomFactor() / 100., garden.getZoomFactor() / 100.);
        g.translate((p.x - bounds.x + 1) * GRID_SIZE, (p.y - bounds.y + 1) * GRID_SIZE);
        return affineBackup;
    }  */

    void paintSquare(GraphicsContext g, Point grid)
    {
        // log.fine("Draw background for position " + grid.toString());

        // draw background
        Map.Entry<Integer, Integer> hints = garden.getPreviewHints(year, grid);
        if (hints == null )
            return;

        int good = hints.getKey(), bad = hints.getValue();
        // log.fine("getHintsForSpecies " + grid.toString() + " good=" + Integer.toString(good) + " bad=" + Integer.toString(bad));
        if (good == 0 && bad == 0)
            return;

        if (bad < 0)
        {
            good += -bad;
            bad = 0;
        }
        if (good < 0)
        {
            bad += -good;
            good = 0;
        }
        if (good > 3)
            good = 3;
        if (bad > 3)
            bad = 3;

        g.setTransform(garden.gridToImage(grid));
        g.setFill(colors[good][bad]);
        g.fillRect(0, 0, GRID_SIZE, GRID_SIZE);
    }
    /*
    public EditableGarden getGarden()
    {
        return garden;
    }

    public int getYear()
    {
        return year;
    }*/

    public void setGardenAndYear(EditableGarden garden, int year)
    {
        this.garden = garden;
        this.year = year;
        repaint();
    }
}
