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
import org.sourceforge.kga.translation.Translation;

import java.util.Collection;
import java.util.List;


/**
 * This rule gives a hint when a plant or related plant is planted at the same spot within a given
 * period of years.
 */
public class RotationRepetition extends Rule
{
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Garden.class.getName());

    public RotationRepetition() {}

    @Override
    public void getHints(HintList hints, Collection<TaxonVariety<Plant>>  extra)
    {
        Garden garden = hints.getGarden();
        int    year = hints.getYear();
        Point  grid = hints.getGrid();
        
        if(extra!=null) {
        	for (TaxonVariety<Plant> curr: extra)
        		 getSingleHint(hints,curr.getTaxon());
        }
        
        List<TaxonVariety<Plant>> plantList = garden.getPlants(year, grid);
        if (plantList == null)
            return;

        for (TaxonVariety<Plant> plant : plantList)
        {
        	getSingleHint(hints,plant.getTaxon());
        }
    }
    
    public void getSingleHint(HintList hints, Plant plant) {
        Garden garden = hints.getGarden();
        int    year = hints.getYear();
        Point  grid = hints.getGrid();
            if (plant.isItem())
                return;

            // determine for how many years the plant already has been cultivated
            int repetitionYears = plant.lifetime.getRepetitionYears();
            int backYear = 1;
            if (repetitionYears != Integer.MAX_VALUE)
                while (backYear < repetitionYears)
                {
                    java.util.List<TaxonVariety<Plant>> searchList = garden.getPlants(year - backYear, grid);
                    if (searchList == null || !searchList.contains(plant))
                        break;
                    ++backYear;
                }

            // find the plant or the family in previous years
            TaxonVariety<Plant> foundRepetition = null;
            int   foundYear = 0;
            boolean speciesRepetition = false;
            for (int gapYear = 0; gapYear < plant.lifetime.getRepetitionGap() && !speciesRepetition; ++gapYear)
            {
                int checkYear = year - backYear - gapYear;
                java.util.List<TaxonVariety<Plant>> searchList = garden.getPlants(checkYear, grid);
                if (searchList != null)
                {
                    for (TaxonVariety<Plant> s : searchList)
                    {
                        if (repetitionYears != Integer.MAX_VALUE && s.getTaxon() == plant)
                        {
                            foundRepetition = s;
                            speciesRepetition = true;
                            foundYear = checkYear;
                            break;
                        }
                        if (s.getFamily() == plant.getFamily())
                        {
                            foundRepetition = s;
                            foundYear = checkYear;
                        }
                    }
                }
            }

            // check if repetition found and generate hint
            if (foundRepetition != null)
            {
                hints.addRotationHint(Hint.Value.BAD, plant, foundRepetition.getTaxon(), foundYear, this);
            }
    }
    
    @Override
    public String getDescription(Hint hint)
    {
        Translation t = Translation.getCurrent();
        
        StringBuilder description = new StringBuilder();
        if (hint.getNeighborPlant() == hint.getCurrentPlant())
        {
            description.append(t.rotation_repetition_species());
            description.append(" ");
            description.append(t.translate(hint.getCurrentPlant()));
        }
        else
        {
            description.append(t.rotation_repetition_family());
            description.append(" ");
            description.append(t.translate(hint.getCurrentPlant().getFamily()));
        }
        description.append(" ");
        description.append(t.in());
        description.append(" ");
        description.append(hint.getNeighborYear());
        return description.toString();
    }

    @Override
    public void addReferencesToList(Hint hint, ReferenceList list)
    {
        list.add(hint.getCurrentPlant().lifetime.references);
    }
}
