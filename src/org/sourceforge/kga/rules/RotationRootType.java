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
import org.sourceforge.kga.plant.RootDeepness;
import org.sourceforge.kga.translation.Translation;

import java.util.Collection;
import java.util.List;


/**
 * Generates hints based on alternation of plant with deep and shallow roots
 */
public class RotationRootType extends Rule
{
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(RotationRootType.class.getName());

    public RotationRootType()
    {
    }

    class RootComparision
    {
        boolean sameRootLevel;
        boolean currentIsDeeper;
    };
    
    private RootComparision compareRoots(Plant plant, Plant previousPlant)
    {
        int root1 =
                (previousPlant.getRootDeepness().getMinimum() + previousPlant.getRootDeepness().getMaximum()) / 2;
        int root2 =
            (plant.getRootDeepness().getMinimum() + plant.getRootDeepness().getMaximum()) / 2;
        RootComparision result = new RootComparision();
        result.sameRootLevel  = root1 > root2 / 2 && root2 > root1 / 2;
        result.currentIsDeeper = root2 > root1;
        return result;
    }

    @Override
    public void getHints(HintList hints, Collection<TaxonVariety<Plant>>  extra)
    {
        Garden garden = hints.getGarden();
        int    year = hints.getYear();
        Point  grid = hints.getGrid();
        List<TaxonVariety<Plant>> previousPlantList = garden.getPlants(year - 1, grid);
        if (previousPlantList == null)
        	return;

        if(extra!=null) {
        	for (TaxonVariety<Plant> plant : extra)            
        		getSingleHint(hints,previousPlantList,plant.getTaxon());

        }

        
        List<TaxonVariety<Plant>> plantList = garden.getPlants(year, grid);
        if (plantList == null)
            return;
        
        for (TaxonVariety<Plant> plant : plantList)
        {
        	getSingleHint(hints,previousPlantList,plant.getTaxon());
        }
    }

    public void getSingleHint(HintList hints,List<TaxonVariety<Plant>> previousPlantList, Plant plant) {
        int    year = hints.getYear();
    	if (plant.isItem())
    		return;
    	RootDeepness rootDeepness = plant.getRootDeepness();
    	if (rootDeepness == null)
    		return;

    	TaxonVariety<Plant> good = null, bad = null;
    	for (TaxonVariety<Plant> previousPlant : previousPlantList)
    	{
    		if (previousPlant.getTaxon() == plant || previousPlant.getFamily() == plant.getFamily())
    			continue;

    		RootDeepness previousDeepness = previousPlant.getTaxon().getRootDeepness();
    		if (previousDeepness == null)
    			continue;

    		// bad root type alternation
    		if (compareRoots(plant, previousPlant.getTaxon()).sameRootLevel)
    			bad = previousPlant;
    		else
    			good = previousPlant;
    	}

    	// if in previous year both good and bad hints are found
    	// then intercalation happened in previous year and all soil levels ( deep and shallow ) have been used
    	if (bad != null && good == null || bad == null && good != null)
    	{
    		TaxonVariety<Plant> previousPlant = bad != null ? bad : good;
    		Hint.Value value = bad != null ? Hint.Value.BAD : Hint.Value.GOOD; 
    		hints.addRotationHint(value, plant, previousPlant.getTaxon(), year - 1, this);
    	}
    }
    
    @Override
    public String getDescription(Hint hint)
    {
        /*
        StringBuilder text = new StringBuilder();
        text.append(bad != null ? t.rotation_root_type_bad() : t.rotation_root_type_good());
        Hint hint = new Hint(this, garden,
                plant, new Garden.Coordinate(year, grid),
                previousPlant, new Garden.Coordinate(year - 1, grid),
                text.toString(), bad != null ? Hint.Value.BAD : Hint.Value.GOOD);
        hints.add(hint);
        */
        Translation t = Translation.getCurrent();
        StringBuilder text = new StringBuilder();
        if (hint.getValue() == Hint.Value.BAD)
        {
            text.append(t.rotation_root_type_same_level()).append(" ");
            text.append(" ( ").append(hint.getCurrentPlant().getRootDeepness().toString()).append(" ) ");
            text.append(t.rotation_after()).append(" ");
            text.append(" ( ").append(hint.getNeighborPlant().getRootDeepness().toString()).append(" )");
        }
        else
        {
            boolean currentIsDeeper = compareRoots(hint.getCurrentPlant(), hint.getNeighborPlant()).currentIsDeeper;
            text.append(currentIsDeeper ? t.rotation_root_type_deep() : t.rotation_root_type_shallow());
            text.append(" ( ").append(hint.getCurrentPlant().getRootDeepness().toString()).append(" ) ");
            text.append(t.rotation_after()).append(" ");
            text.append((!currentIsDeeper ? t.rotation_root_type_deep() : t.rotation_root_type_shallow()));
            text.append(" ( ").append(hint.getNeighborPlant().getRootDeepness().toString()).append(" )");
        }
        return text.toString();
    }

    @Override
    public void addReferencesToList(Hint hint, ReferenceList list)
    {
        list.add(hint.getNeighborPlant().getRootDeepness().references);
        list.add(hint.getCurrentPlant().getRootDeepness().references);
    }
}
