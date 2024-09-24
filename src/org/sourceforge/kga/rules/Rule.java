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


package org.sourceforge.kga.rules;

import javafx.scene.image.Image;
import org.sourceforge.kga.*;
import org.sourceforge.kga.gui.gardenplan.EditableGarden;
import org.sourceforge.kga.gui.gardenplan.EditableGarden.Operation;
import org.sourceforge.kga.plant.ReferenceList;

import java.util.*;


/**
 * Rules are applied to plant. Rules is the way to tell the system
 * how a plant should or shouldn't be used. If you break a warning type rule or
 * follow a benefit type rule you will get a hint. Each rule has a level of impact
 * called effect.
 * @author Christian Nilsson
 */
public abstract class Rule
{
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Garden.class.getName());

    public static Image GOOD;
    public static Image BAD;
    public static Image EQUAL;
    public static Image TIP;
    public static Image[][] GOOD_ARROWS = new Image[3][3];
    public static Image[][] BAD_ARROWS = new Image[3][3];

    public static final int ONE_YEAR_BACK = 1;
    public static final int CLOSEST_NEIGHBOURS = 1;

    public static final int IMAGE_SIZE = 9;

    public Rule() {}

    public abstract void getHints(HintList hints, Collection<TaxonVariety<Plant>> extraPlantToInclude);

    public abstract String getDescription(Hint hint);

    public abstract void addReferencesToList(Hint hint, ReferenceList list);

    public ReferenceList getReferences(Hint hint)
    {
        ReferenceList list = new ReferenceList();
        addReferencesToList(hint, list);
        return list;
    }

    /**
     * Returns the hints for this square. The collection does not contain null elements.
     * @return the hints for this square
     */
    public static HintList getHints(EditableGarden garden, int year, Point grid, boolean detailed) {
    	return getHints(garden,year,grid,detailed,false);
    }

    /**
     * Returns the hints for this square. The collection does not contain null elements.
     * @return the hints for this square
     */
    public static HintList getHints(EditableGarden garden, int year, Point grid, boolean detailed, boolean includeSelectedPlantIfAny)
    {
    	if (includeSelectedPlantIfAny&&garden.getOperation()==Operation.AddPlant && garden.getSelectedPlant()!=null) {
    		TaxonVariety<Plant> extra = garden.getSelectedPlant();
    		ArrayList<TaxonVariety<Plant>> xList = new ArrayList<>(1);
    		xList.add(extra);
    		return getHints(garden,year,grid,detailed,xList);
    	}
    	else
    		return getHints(garden,year,grid,detailed,new ArrayList<>(0));
    }

    /**
     * Returns the hints for this square. The collection does not contain null elements.
     * @return the hints for this square
     */
    public static HintList getHints(EditableGarden garden, int year, Point grid, boolean detailed, List<TaxonVariety<Plant>> extra)
    {
        HintList hints = new HintList(garden, year, grid, detailed);
        for (Rule r : rules)
            r.getHints(hints,extra);
        return hints;
    }

    static ArrayList<Rule> rules = new ArrayList<Rule>();
    static
    {
        rules.add(new RotationRepetition());
        rules.add(new RotationNutritionalNeeds());
        rules.add(new RotationRootType());
        rules.add(new RotationWeedControl());
        rules.add(new Companion());
    }
}
