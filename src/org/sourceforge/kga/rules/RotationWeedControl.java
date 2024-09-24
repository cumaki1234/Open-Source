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

import org.sourceforge.kga.*;
import org.sourceforge.kga.plant.ReferenceList;
import org.sourceforge.kga.plant.WeedControl;
import org.sourceforge.kga.translation.Translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Generates hints based on alternation of plant witch leaves the soil weedy or clear
 * because of their large/dense foliage
 */
public class RotationWeedControl extends Rule
{
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Garden.class.getName());

    public RotationWeedControl()
    {
    }

    
    @Override
    public void getHints(HintList hints, Collection<TaxonVariety<Plant>>  extra)
    {
        Garden garden = hints.getGarden();
        int    year = hints.getYear();
        Point  grid = hints.getGrid();
        List<TaxonVariety<Plant>> previousPlantList = garden.getPlants(year - 1, grid);
        if (previousPlantList == null || previousPlantList.isEmpty())
            return;

        // get clear plants from previous year
        ArrayList<TaxonVariety<Plant>> checkPlants = new ArrayList<TaxonVariety<Plant>>();
        WeedControl.Type previousWeedControl = WeedControl.Type.WEEDY;
        for (TaxonVariety<Plant> s : previousPlantList)
        {
            if (s.getTaxon().getWeedControl() == null)
                continue;

            if (s.getTaxon().getWeedControl().type == WeedControl.Type.CLEAR &&
                    previousWeedControl == WeedControl.Type.WEEDY)
            {
                previousWeedControl = WeedControl.Type.CLEAR;
                checkPlants.clear();
            }
            checkPlants.add(s);
        }
        
        if(extra!=null) {
        	for (TaxonVariety<Plant> plant : extra)     
        		getSingleHint(hints,checkPlants,previousWeedControl,plant.getTaxon());
        	
        }
        
        List<TaxonVariety<Plant>> plantList = garden.getPlants(year, grid);
        if (plantList == null || plantList.isEmpty())
            return;

        for (TaxonVariety<Plant> plant : plantList)
        {
        	getSingleHint(hints,checkPlants,previousWeedControl,plant.getTaxon());
        }
    }

    public void getSingleHint(HintList hints, ArrayList<TaxonVariety<Plant>> checkPlants,WeedControl.Type previousWeedControl, Plant plant) {
        int    year = hints.getYear();
    	if (plant.getWeedControl() == null)
    		return;
    	WeedControl.Type weedControl = plant.getWeedControl().type; 

    	for (TaxonVariety<Plant> previousPlant : checkPlants)
    	{
    		if (previousPlant.getTaxon() == plant || previousPlant.getFamily() == plant.getFamily())
    			continue;

    		// check repetition by weed control
    		Hint.Value hintValue;
    		if (weedControl != previousWeedControl)
    		{
    			// weedy after clear or clear after weedy
    			hintValue = Hint.Value.GOOD;
    		}
    		else if (weedControl == WeedControl.Type.WEEDY)
    		{
    			// weedy after weedy
    			hintValue = Hint.Value.BAD;
    		}
    		else
    		{
    			// clear after clear
    			hintValue = Hint.Value.TIP;
    		}

    		hints.addRotationHint(hintValue, plant, previousPlant.getTaxon(), year - 1, this);
    	}
    }

    @Override
    public String getDescription(Hint hint)
    {
        Translation t = Translation.getCurrent();
        StringBuilder text = new StringBuilder();
        text.append(hint.getValue() == Hint.Value.GOOD ? t.rotation_weed_control_good() : t.rotation_weed_control_bad());
        text.append(" ");
        text.append(hint.getCurrentPlant().getWeedControl().translate());
        text.append(" ");
        text.append(t.rotation_after());
        text.append(" ");
        text.append(hint.getNeighborPlant().getWeedControl().translate());
        return text.toString();
    }

    @Override
    public void addReferencesToList(Hint hint, ReferenceList list)
    {
        list.add(hint.getNeighborPlant().getWeedControl().references);
        list.add(hint.getCurrentPlant().getWeedControl().references);
    }
}
