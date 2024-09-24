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
import org.sourceforge.kga.plant.NutritionalNeeds;
import org.sourceforge.kga.plant.ReferenceList;
import org.sourceforge.kga.translation.Translation;

import java.util.*;


/**
 * Generates hints based on nutritional needs.
 * High -> Low -> Soil improver -> High
 */
public class RotationNutritionalNeeds extends Rule
{
    public RotationNutritionalNeeds()
    {
    }
    
    private boolean goodRotation(NutritionalNeeds.Type now, NutritionalNeeds.Type previous)
    {
        // good crop rotation is High -> Low -> SoilImprover -> High
        return
            previous == NutritionalNeeds.Type.HIGH          && now == NutritionalNeeds.Type.LOW ||
            previous == NutritionalNeeds.Type.LOW           && now == NutritionalNeeds.Type.SOIL_IMPROVER ||
            previous == NutritionalNeeds.Type.SOIL_IMPROVER && now == NutritionalNeeds.Type.HIGH;
    }

    private boolean badRotation(NutritionalNeeds.Type now, NutritionalNeeds.Type previous)
    {
        // bad crop rotation: High -> High, Low -> High, Low -> Low
        return
            previous == NutritionalNeeds.Type.HIGH && now == NutritionalNeeds.Type.HIGH ||
            previous == NutritionalNeeds.Type.LOW  && now == NutritionalNeeds.Type.HIGH ||
            previous == NutritionalNeeds.Type.LOW  && now == NutritionalNeeds.Type.LOW;
    }
    
    @Override
    public void getHints(HintList hints, Collection<TaxonVariety<Plant>> extra)
    {
        Garden garden = hints.getGarden();
        int    year = hints.getYear();
        Point  grid = hints.getGrid();
        
        List<TaxonVariety<Plant>> plantList = garden.getPlants(year, grid);

        if (plantList == null&&extra==null)
            return;
        List<TaxonVariety<Plant>> previousPlantList = garden.getPlants(year - 1, grid);
        if (previousPlantList == null)
            return;
        
        // determine the plants with highest nutritional needs from previous year
        NutritionalNeeds.Type previousNutritionalNeeds = NutritionalNeeds.Type.SOIL_IMPROVER;
        ArrayList<TaxonVariety<Plant>> found = new ArrayList<TaxonVariety<Plant>>();
        for (TaxonVariety<Plant> p : previousPlantList)
        {
            if (p.getTaxon().getNutritionalNeeds() == null)
                continue;
            if (p.getTaxon().getNutritionalNeeds().type.ordinal() > previousNutritionalNeeds.ordinal())
            {
                previousNutritionalNeeds = p.getTaxon().getNutritionalNeeds().type; 
                found.clear();
            }
            if (p.getTaxon().getNutritionalNeeds().type == previousNutritionalNeeds)
            {
                found.add(p);
            }
        }
        previousPlantList = found;

        if(extra!=null) {
        	for(TaxonVariety<Plant> curr : extra)
        		getSingleHint(hints,curr.getTaxon(),previousPlantList);        	
        }

        if (plantList == null)
            return;
        for (TaxonVariety<Plant> plant : plantList)
        {
        	getSingleHint(hints,plant.getTaxon(),previousPlantList);
        }
    }
    

    public void getSingleHint(HintList hints, Plant plant, List<TaxonVariety<Plant>> previousPlantList) {

        int    year = hints.getYear();
    	if (plant.isItem())
            return;
        if (plant.getNutritionalNeeds() == null)
            return;

        for (TaxonVariety<Plant> previousPlant : previousPlantList)
        {
            if (previousPlant.getTaxon() == plant || previousPlant.getFamily() == plant.getFamily())
                continue;
            Hint.Value value;
            
            // bad crop rotation: High -> High, Low -> High, Low -> Low
            if (badRotation(plant.getNutritionalNeeds().type, previousPlant.getTaxon().getNutritionalNeeds().type))
               value = Hint.Value.BAD;
            else if (goodRotation(plant.getNutritionalNeeds().type, previousPlant.getTaxon().getNutritionalNeeds().type))
                value = Hint.Value.GOOD;
            else
                value = Hint.Value.TIP;

            if (hints.addRotationHint(value, plant, previousPlant.getTaxon(), year - 1, this))
                break;                                    
        }
    }
    
    @Override
    public String getDescription(Hint hint)
    {
        Translation t = Translation.getCurrent();
        StringBuilder text = new StringBuilder();
        text.append(hint.getValue() == Hint.Value.GOOD ? t.rotation_good() : t.rotation_bad());
        text.append(" ");
        text.append(hint.getCurrentPlant().getNutritionalNeeds().translate());
        text.append(" ");
        text.append(t.rotation_after());
        text.append(" ");
        text.append(hint.getNeighborPlant().getNutritionalNeeds().translate());
        return text.toString();
    }

    @Override
    public void addReferencesToList(Hint hint, ReferenceList list)
    {
        list.add(hint.getNeighborPlant().getNutritionalNeeds().references);
        list.add(hint.getCurrentPlant().getNutritionalNeeds().references);
    }
}
