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

import java.util.ArrayList;
import java.util.Collections;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.sourceforge.kga.*;
import org.sourceforge.kga.gui.gardenplan.GardenView;
import org.sourceforge.kga.plant.Companion;
import org.sourceforge.kga.translation.Translation;

public class PlantComponent extends Label
{
    private static final long serialVersionUID = 1L;
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Garden.class.getName());

    public Plant plant;

    public PlantComponent()
    {
        super();
    }
    
    public PlantComponent(Plant s)
    {
    	this(s,true);
    }
    
    public PlantComponent(Plant s,boolean selectable)
    {
        super();
        setPlant(s,selectable);
    }

    /**
     * Decrease font size from a label until the text width is smaller
     * than the size of the plant icon.
     * Font is never decreased less then the 8 size, because it would
     * become unreadable.
     */
    static private double resizeFont(Label label)
    {
        String plantName = label.getText();
        Text text = new Text(plantName);
        double fontSize = text.getFont().getSize();
        boolean resizeDone = false;
        while (true)
        {
            resizeDone = text.getLayoutBounds().getWidth() <= GardenView.PLANT_SIZE;
            if (resizeDone || fontSize <= 8.)
            {
                break;
            }
            fontSize -= 0.5;
            text.setFont(new Font(text.getFont().getName(), fontSize));
        }
        label.setFont(new Font(text.getFont().getName(), fontSize));
        if (!resizeDone)
        {
            // find the space which is closer to the middle
            int bestSpaceIdx = -1;
            int spaceIdx = -1;
            while (true)
            {
                spaceIdx = plantName.indexOf(' ', spaceIdx + 1);
                if (spaceIdx == -1)
                    break;
                if (Math.abs(    spaceIdx - plantName.length() / 2) <
                    Math.abs(bestSpaceIdx - plantName.length() / 2))
                {
                    bestSpaceIdx = spaceIdx;
                }
            }
            if (bestSpaceIdx != -1)
            {
                label.setText(plantName.substring(0, bestSpaceIdx) + "\n" + plantName.substring(bestSpaceIdx + 1));
            }
        }
        return fontSize;
    }

    public void setPlant(Plant s) {
    	setPlant(s,true);
    }
    
    public void setPlant(Plant s, boolean selectable)
    {
        this.plant = s;
        try
        {
            ImageView imageView = s.createImageview(GardenView.PLANT_SIZE);
            this.setGraphic(imageView);
        }
        catch (Exception e)
        {
            System.err.println("PlantComponent: Cannot set icon for " + s.getName() + " " + e.toString());
        }
        String plantName = Translation.getCurrent().translate(s);
        setText(plantName);
        resizeFont(this);
        setContentDisplay(ContentDisplay.TOP);
        setAlignment(Pos.BOTTOM_CENTER);
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
        if(selectable)
        	setSelected(false);
        else
            setStyle("-fx-padding: 1 1 1 3; -fx-border-insets: 0; -fx-border-width: 1;");
        	
        /* TODO:
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        setToolTipText(getHtmlToolTipText()); */
    }

    public Plant getPlant()
    {
        return plant;
    }

    public void setSelected(boolean isSelected)
    {
        if (isSelected)
        {
            setStyle("-fx-padding: 1 1 1 3; -fx-border-insets: 0; -fx-border-width: 5; -fx-border-color: green green green green");
        }
        else
        {
            setStyle("-fx-padding: 1 3 1 1; -fx-border-insets: 0; -fx-border-width: 5; -fx-border-color: white grey grey white");
        }
    }


    /**
     * A HTML-coded text with a description of the plant.
     * @return HTML-coded text with a description of the plant.
     */
    private String getHtmlToolTipText()
    {
        Translation t = Translation.getCurrent();
        // header with translated name and scientific name
        StringBuilder html = new StringBuilder();
        html.append("<html><center><h2>");
        html.append(t.translate(plant));
        html.append("</h2><i>");
        html.append(plant.getName());
        html.append("</i></center>");

        ArrayList<Plant> goodList = new ArrayList<>();
        ArrayList<Plant> badList = new ArrayList<>();
        Plant p = plant;
        do
        {
            getCompanionPlants(p, goodList, badList);
            p = p.getParent();
        } while (p != null && p.getType() != Plant.Type.GENUS && p.getType() != Plant.Type.FAMILY);

        Plant genus = p.getType() == Plant.Type.GENUS ? p : null;
        ArrayList<Plant> genusGoodList = new ArrayList<>();
        ArrayList<Plant> genusBadList = new ArrayList<>();
        if (genus != null)
        {
            getCompanionPlants(genus, genusGoodList, genusBadList);
            removeDuplicated(genusGoodList, goodList);
            removeDuplicated(genusBadList, badList);
        }

        Plant family = (Plant)plant.getFamily();
        ArrayList<Plant> familyGoodList = new ArrayList<>();
        ArrayList<Plant> familyBadList = new ArrayList<>();
        getCompanionPlants(family, familyGoodList, familyBadList);
        removeDuplicated(familyGoodList, goodList);
        removeDuplicated(familyBadList, badList);
        removeDuplicated(familyGoodList, genusGoodList);
        removeDuplicated(familyBadList, genusBadList);

        html.append("<table border='0'>");
        if (!goodList.isEmpty() || !badList.isEmpty() ||
            !genusGoodList.isEmpty() || !genusBadList.isEmpty() ||
            !familyGoodList.isEmpty() || !familyBadList.isEmpty())
        {
            html.append("<tr><th>");
            html.append(t.companion_good());
            html.append("</th><th>");
            html.append(t.companion_bad());
            html.append("</th></tr>");
        }
        if (!goodList.isEmpty() || !badList.isEmpty())
        {
            html.append("<tr align='center'><td colspan='2'><hr/></td></tr>");
            getTooltips(html, goodList, badList);
        }

        if (!genusGoodList.isEmpty() || !genusBadList.isEmpty())
        {
            html.append("<tr align='center'><td colspan='2'><hr/></td></tr>");
            html.append("<tr align='center'><td colspan='2'><b>");
            html.append(genus.getName());
            html.append("</b></td></tr>");
            getTooltips(html, genusGoodList, genusBadList);
        }

        html.append("<tr align='center'><td colspan='2'><hr/></td></tr>");
        html.append("<tr><td colspan='2' align='center'>");
        html.append("<b>").append(t.translate(family)).append("</b> - ").append(family.getName());
        html.append("</td></tr>");
        getTooltips(html, familyGoodList, familyBadList);

        html.append("</table>");
        return html.toString();
    }

    private static void removeDuplicated(ArrayList<Plant> intoList, ArrayList<Plant> fromList)
    {
        for (Plant plant : fromList)
        {
            for (Plant find : intoList)
                if (find == plant || plant.isParentOf(find) || find.isParentOf(plant))
                {
                    intoList.remove(find);
                    break;
                }
        }
    }

    private static void getTooltips(StringBuilder html, ArrayList<Plant> goodList, ArrayList<Plant> badList)
    {
        if (goodList.isEmpty() && badList.isEmpty())
            return;

        html.append("<tr><td align='left' valign='top'>");
        listToTooltip(html, goodList);
        html.append("</td>");

        html.append("<td align='left' valign='top'>");
        listToTooltip(html, badList);
        html.append("</td></tr>");
    }

    private static void getCompanionPlants(Plant plant, ArrayList<Plant> goodList, ArrayList<Plant> badList)
    {
        for (Companion companion : plant.getCompanions().get())
        {
            ArrayList<Plant> list = companion.type.isBeneficial() ? goodList : badList;

            boolean found = false;
            for (Plant p : list)
            {
                if (p == companion.plant || p.isParentOf(companion.plant))
                {
                    found = true;
                    break;
                }
            }
            if (found)
                continue;

            ArrayList<Plant> toRemove = new ArrayList<>();
            for (Plant p : list) {
            	Plant pAsPlant = p;
                if (companion.plant.isParentOf(pAsPlant))
                    toRemove.add(p);
            }
            for (Plant p : toRemove)
                list.remove(p);

            list.add(companion.plant);
        }
        Collections.sort(goodList, new TaxonComparatorByName());
        Collections.sort(badList, new TaxonComparatorByName());
    }

    private static void listToTooltip(StringBuilder html, ArrayList<Plant> list)
    {
        Translation t = Translation.getCurrent();
        boolean first = true;
        for (Plant plant : list)
        {
            if (first)
                first = false;
            else
                html.append("<br>");
            if (plant.getChildren().size() == 1)
            {
                html.append(t.translate(plant.getChildren().iterator().next()));
                continue;
            }
            html.append(t.translate(plant));
            if (plant.getChildren().size() != 0)
            {
                ArrayList<Plant> children = new ArrayList<>();
                getAllChildren(plant, children);
                Collections.sort(children, new TaxonComparatorByName());
                for (Plant child : children)
                {
                    html.append("<br>");
                    html.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    html.append(t.translate(child));
                }
            }
        }
    }

    private static void getAllChildren(Plant plant, ArrayList<Plant> children)
    {
        if (plant.getChildren().size() == 0)
            children.add(plant);
        else
            for (Taxon child : plant.getChildren())
                getAllChildren((Plant)child, children);
    }

    public void setUnregisteredPlant(String plantName)
    {
        setText(plantName);
        setGraphic(null);
        // TODO: setToolTipText(null);
    }

    public void strikeText()
    {
        setText("<html><strike><i>" + getText() + "</i><stike></html");
    }
}
