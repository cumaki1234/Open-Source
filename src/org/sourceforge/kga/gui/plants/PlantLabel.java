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


package org.sourceforge.kga.gui.plants;

import java.awt.*;
import javax.swing.*;

import javafx.embed.swing.SwingFXUtils;
import org.sourceforge.kga.*;
import org.sourceforge.kga.translation.*;


public class PlantLabel extends JLabel
{
    /**
     * Decrease font size from a label until the text width is smaller
     * than the size of the plant icon.
     * Font is never decreased less then the 8 size, because it would
     * become unreadable.
     */
    public static int ResizeFont(JLabel label)
    {
        Font f = label.getFont();
        int fontSize = f.getSize();
        Font newFont = null;
        int stringWidth = 0;
        while (true)
        {
            newFont = new Font(f.getName(), Font.PLAIN, fontSize);
            FontMetrics fm = label.getFontMetrics(newFont);
            stringWidth = fm.stringWidth(label.getText());
            if (stringWidth < org.sourceforge.kga.gui.gardenplan.GardenView.PLANT_SIZE)
            {
                break;
            }
            if (fontSize <= 8)
                break;
            --fontSize;
        }
        label.setFont(newFont);
        return stringWidth;
    }

    public static Dimension getDefaultSize()
    {
        JLabel label = new JLabel("x");
        label.setBorder(defaultBorder);

        Font f = label.getFont();
        int fontSize = f.getSize();
        Font newFont = new Font(f.getName(), Font.PLAIN, fontSize);
        FontMetrics fm = label.getFontMetrics(newFont);

        return new Dimension(
            defaultBorder.getBorderInsets(label).left +
            defaultBorder.getBorderInsets(label).right +
            Math.max(ResizeFont(label), org.sourceforge.kga.gui.gardenplan.GardenView.PLANT_SIZE),

            fm.getHeight() + label.getIconTextGap() +
            org.sourceforge.kga.gui.gardenplan.GardenView.PLANT_SIZE +
            defaultBorder.getBorderInsets(label).top +
            defaultBorder.getBorderInsets(label).bottom);
    }

    public static javax.swing.border.Border defaultBorder = BorderFactory.createCompoundBorder(
        BorderFactory.createEtchedBorder(),
        BorderFactory.createEmptyBorder(2, 2, 2, 2));

    int computedWidth;
    public PlantLabel(Plant plant)
    {
        Translation t = Translation.getCurrent();
        setText(t.translate(plant));
        computedWidth = Math.max(
            ResizeFont(this), org.sourceforge.kga.gui.gardenplan.GardenView.PLANT_SIZE);
        if (plant.getImage() != null)
            setIcon(new ImageIcon(SwingFXUtils.fromFXImage(plant.getImage(), null)));
        setHorizontalAlignment(SwingConstants.CENTER);
        setHorizontalTextPosition(JLabel.CENTER);
        setVerticalTextPosition(JLabel.BOTTOM);
        setBorder(defaultBorder);
        computedWidth +=
            defaultBorder.getBorderInsets(this).left +
            defaultBorder.getBorderInsets(this).right;
        Plant family = (Plant)plant.getFamily();
        if (family != null)
            setToolTipText(
                "<html>" +
                "<b>" + t.name() + ":</b> " + plant.getName() + "<br>" +
                "<b>" + t.family() + ":</b> " + t.translate(plant.getFamily()) +
                " ( " + plant.getFamily().getName() + " )");
        else
            setToolTipText("<html><b>" + t.name() + ":</b> " + plant.getName() + "<br>");
    }

    public int getComputedWidth()
    {
        return computedWidth;
    }
}
