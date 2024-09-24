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


import org.sourceforge.kga.Animal;
import org.sourceforge.kga.Garden;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.plant.ReferenceList;
import org.sourceforge.kga.translation.Translation;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.TaxonVariety;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * This rule gives a hint when planting a beneficial plant next to each other. For
 * instance onion next to carrot.
 * @author Christian Nilsson
 *
 */

public class Companion extends Rule
{
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Companion.class.getName());

    @Override
    public void getHints(HintList hints, Collection<TaxonVariety<Plant>> extra)
    {
        if(extra!=null) {
        	for (TaxonVariety<Plant> curr : extra)
        		getSingleHint(hints,curr);        	
        }
    	Garden garden = hints.getGarden();
    	int    year = hints.getYear();
    	Point  grid = hints.getGrid();

        // log.finest("Evaluate companion " + grid.toString());
        java.util.List<TaxonVariety<Plant>> plantList = garden.getPlants(year, grid);
        if (plantList == null)
            return;

        for (TaxonVariety<Plant> plant : plantList)
        {
        	getSingleHint(hints,plant);
        }
    }
    
    public void getSingleHint(HintList hints, TaxonVariety<Plant> plant) {
    	Garden garden = hints.getGarden();
    	int    year = hints.getYear();
    	Point  grid = hints.getGrid();
    	// TODO: log.finest(plant.getName() + " has " + plant.getCompanions().size() + " companions of type " + companionType);
    	 Collection<org.sourceforge.kga.plant.Companion> companions = plant.getTaxon().getCompanions().getInherited();
    	 //Set<Plant> companionPlants = new HashSet<Plant>(companions.size());
    	 Map<Plant,org.sourceforge.kga.plant.Companion> compToPlant = new HashMap<>();
    	 for (org.sourceforge.kga.plant.Companion companion : plant.getTaxon().getCompanions().getInherited())
    	 {
    		 //companionPlants.add(companion.plant);
    		 compToPlant.put(companion.plant, companion);
    	 }
    	 ArrayList<Garden.FindResult> results =
    			 garden.findSquare(year, grid, CLOSEST_NEIGHBOURS, 0, 0, compToPlant.keySet(), false, true);

    	 for (Garden.FindResult found : results) {
    		 if(found.coordinate.grid.instersectsAtSize(grid, found.plant.getLargestVarietySize(), 1)) {
    			 for(Plant p = found.plant;p!=null;p=p.getParent()){
    				 if(compToPlant.containsKey(p)) {
    	    			 hints.addCompanionHint(compToPlant.get(p), plant.getTaxon(), found.plant, found.coordinate.grid, this); 
    	    			 break;
    				 }
    			 }    			 
    		 }
    	 }
    }


    static private void appendAnimals(StringBuilder text, TreeSet<Animal> animals)
    {
        boolean first = true;
        for (Animal animal : animals)
        {
            if (!first)
                text.append(", ");
            text.append(Translation.getCurrent().translate(animal));
            first=false;
        }
    }

    @Override
    public String getDescription(Hint hint)
    {
        // TODO: verify if attract pest and other references are correctly implemented
        Translation t = Translation.getCurrent();
        StringBuilder text = new StringBuilder();
        org.sourceforge.kga.plant.Companion companion = hint.getCompanion(); 
        switch (companion.type)
        {
            case GOOD:
            case BAD:
                break;
            default:
                text.append(companion.type.translate());
        }
        if (companion.type == org.sourceforge.kga.plant.Companion.Type.IMPROVE || companion.type == org.sourceforge.kga.plant.Companion.Type.INHIBIT)
        {
            if(companion.improve.size()>0) {
            	text.append(" ");
            }
            boolean first = true;
            for (org.sourceforge.kga.plant.Companion.Improve improve : companion.improve)
            {
                if (!first)
                    text.append(", ");
                first = false;
                text.append(improve.translate());
            }
        }

        if (companion.type.withAnimals()) {
        	text.append(": ");
            appendAnimals(text, companion.animals);
        }

        return text.toString();
    }

    @Override
    public void addReferencesToList(Hint hint, ReferenceList list)
    {
        list.add(hint.getCompanion().references);
    }
}
