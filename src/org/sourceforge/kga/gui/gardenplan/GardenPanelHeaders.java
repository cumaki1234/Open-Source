/**
 * Kitchen garden aid is a planning tool for kitchengardeners.
 * Copyright (C) 2010 Christian Nilsson
 *
 * This file is part of Kitchen garden aid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 * Email contact: tiberius.duluman@gmail.com; christian1195@gmail.com
 */

package org.sourceforge.kga.gui.gardenplan;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;


public class GardenPanelHeaders extends ResizableCanvas
{
    private static final long serialVersionUID = 1L;
    public static final int COLUMNS = 0;
    public static final int ROWS = 1;

    public int orientation;

    private FontMetrics fm;
    private int zoomFactor = 100;
    int gridSize = GardenView.GRID_SIZE;

    public GardenPanelHeaders(int o)
    {
        orientation = o;

        Font font = Font.font("System", FontPosture.REGULAR, 10);
        fm = new FontMetrics(font);

        GraphicsContext g = getGraphicsContext2D();
        g.setStroke(Color.BLACK);
        g.setFill(Color.BLACK);
        g.setFont(font);

        float w = fm.computeStringWidth("999");
        setWidth(w);
        setHeight(w);
    }
    
    /**
     * from http://werner.yellowcouch.org/log/fontmetrics-jdk9/     *
     */
    private class FontMetrics
    {
     final private Text internal;
     public float ascent, descent, lineHeight;
     public FontMetrics(Font fnt)
     {
     internal =new Text();
     internal.setFont(fnt);
     Bounds b= internal.getLayoutBounds();
     lineHeight= (float) b.getHeight();
     ascent= (float) -b.getMinY();
     descent=(float) b.getMaxY();
     }

     public float computeStringWidth(String txt)
     {
     internal.setText(txt);
     return (float) internal.getLayoutBounds().getWidth();
     }

     public float getAscent() {return ascent;}
     public float getDescent() {return descent;}
     public float getLeading() {return lineHeight-ascent-descent;}
    }

    public void setZoomFactor(int zoomFactor)
    {
        this.zoomFactor = zoomFactor;
        paint();
    }

    int scrollX = 0;
    void setScrollX(int x)
    {
        scrollX = x;
        paint();
    }

    int scrollY = 0;
    void setScrollY(int y)
    {
        scrollY = y;
        paint();
    }

    public void paint()
    {
        GraphicsContext g = getGraphicsContext2D();
        g.clearRect(0, 0, getWidth(), getHeight());

        if (orientation == COLUMNS)
        {
            int startValue = toGridPosition(scrollX) - 1;
            int endValue = toGridPosition((int)(scrollX + getWidth()));
            int position =  - scrollX % fromGridPosition(1);
            paintHeader(position, startValue, endValue);
        }
        else
        {
            // for first empty row, startValue is -1, but only values >=0 are displayed
            int startValue = toGridPosition(scrollY) - 1;
            int endValue = toGridPosition((int)(scrollY + getHeight()));
            int position = - scrollY % fromGridPosition(1);
            paintHeader(position, startValue, endValue);
        }
    }

    public void paintHeader(int position, int startValue, int endValue)
    {
        GraphicsContext g = getGraphicsContext2D();
        if (orientation == COLUMNS)
        {
            double y = fm.getLeading() + fm.getAscent();
            for (int value = Math.max(0, startValue); value < endValue; ++value)
            {
                String text = valueToColumn(value);
                double width = fm.computeStringWidth(text.toString());
                double x = position + fromGridPosition(value - startValue) + (fromGridPosition(1) - width) / 2;
                g.fillText(text.toString(), x, y);
            }
        }
        else
        {
            double totalWidth = fm.computeStringWidth("999");
            double baseLine = (fromGridPosition(1) - fm.getAscent() - fm.getDescent()) / 2 + fm.getAscent();
            for (int value = Math.max(0, startValue); value < endValue; ++value)
            {
                String text = Integer.toString(value + 1);
                double x = (totalWidth - fm.computeStringWidth(text)) / 2;
                double y = position + fromGridPosition(value - startValue) + baseLine;
                g.fillText(text, x, y);
            }
        }
    }

    int fromGridPosition(int p)
    {
        return p * gridSize * zoomFactor / 100;
    }

    int toGridPosition(int p)
    {
        return p * 100 / zoomFactor / gridSize;
    }

    static String valueToColumn(int v)
    {
        StringBuffer text = new StringBuffer();
        while (v >= 0)
        {
            text.insert(0, (char)('A' + v % ('Z' - 'A' + 1)));
            v /= ('Z' - 'A' + 1);
            --v;
        }
        return text.toString();
    }
}
