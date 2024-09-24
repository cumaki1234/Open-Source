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


package org.sourceforge.kga.gui.gardenplan.plantSelection;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.util.Duration;

import org.sourceforge.kga.Animal;
import org.sourceforge.kga.KitchenGardenAid;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.Taxon;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.ClickableTooltip;
import org.sourceforge.kga.gui.actions.ScrollingTilePane;
import org.sourceforge.kga.gui.plants.PlantComponent;
import org.sourceforge.kga.gui.plants.PlantLabel;
import org.sourceforge.kga.gui.rules.HintListDisplay;
import org.sourceforge.kga.plant.Companion;
import org.sourceforge.kga.plant.PropertySource;
import org.sourceforge.kga.plant.Reference;
import org.sourceforge.kga.plant.SourceList;
import org.sourceforge.kga.rules.Hint;
import org.sourceforge.kga.rules.HintList;
import org.sourceforge.kga.rules.Rule;
import org.sourceforge.kga.translation.Translation;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;


/**
 * Window that displays companion and pest information for a plant.
 */
public class PlantRelationshipPane extends HBox
{

	private static final int spacing=20;
	private static final int padding=5;
    public PlantRelationshipPane(Plant plant)
    {
        setPadding(new Insets(padding));
        setSpacing(spacing);
        // setHgap(5);
        // setVgap(5);
        
        Translation t = Translation.getCurrent();

        FilterableCompanionGrid goodCompanions = new FilterableCompanionGrid(t.companion_good(), plant);
        FilterableCompanionGrid badCompanions = new FilterableCompanionGrid(t.companion_bad(), plant);
        
        ArrayList<PropertySource> sources = new ArrayList<>();
        Map <Boolean,Map<Plant,Set<Companion>>> plantpanions = new HashMap <Boolean,Map<Plant,Set<Companion>>>();
        plantpanions.put(true, new HashMap<Plant,Set<Companion>>());
        plantpanions.put(false, new HashMap<Plant,Set<Companion>>());
        TreeMap<Companion.Type, ArrayList<Companion>> companions = new TreeMap<>();
        Plant crt = plant;
        while (crt != null)
        {
            for (Companion companion : crt.getCompanions().get())
            {
                if (!companions.containsKey(companion.type))
                    companions.put(companion.type, new ArrayList<>());
                companions.get(companion.type).add(companion);
                Map<Plant,Set<Companion>> plants = plantpanions.get(companion.type.isBeneficial());
                if (!plants.containsKey(companion.plant)) {
                	plants.put(companion.plant, new HashSet<Companion>());
                }
                plants.get(companion.plant).add(companion);
            }
            crt = crt.getParent();
        }
        
        for (Map.Entry<Boolean,Map<Plant,Set<Companion>>> typeEntry : plantpanions.entrySet()) {
        	for (Map.Entry<Plant,Set<Companion>> plantEntry : typeEntry.getValue().entrySet()){
        		if(typeEntry.getKey()) {
        			goodCompanions.addCompanion(plantEntry.getKey(),plantEntry.getValue());
        		}
        		else {
        			badCompanions.addCompanion(plantEntry.getKey(),plantEntry.getValue());
        			
        		}
        	}
        }
        if(goodCompanions.size()>0) {
        	getChildren().add(goodCompanions);
        }
        if(badCompanions.size()>0) {
        	getChildren().add(badCompanions);
        }

        //goodheader.setMinWidth(3*(GardenView.PLANT_SIZE+6)+10);

        if(goodCompanions.size()>0 && badCompanions.size()>0) {
        	badCompanions.maxWidthProperty().bind(badCompanions.prefWidthProperty());
        	goodCompanions.prefWidthProperty().bind(widthProperty().divide(2).subtract(spacing/2+padding).add(10));
        	badCompanions.prefWidthProperty().bind(widthProperty().divide(2).subtract(spacing/2+padding));
        }

        this.setFillHeight(true);

    }

}



