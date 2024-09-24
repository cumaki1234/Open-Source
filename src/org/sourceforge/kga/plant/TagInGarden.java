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


package org.sourceforge.kga.plant;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.sourceforge.kga.*;
import org.sourceforge.kga.translation.Translation;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tiberius.duluman
 * Date: 6/27/13
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagInGarden extends Tag implements GardenObserver
{
    private TagInGarden()
    {
        super("", null);
    }

    @Override public String getName()
    {
        return Translation.getCurrent().species_in_garden();
    }

    Garden garden = null;
    public void setGarden(Garden garden)
    {
        if (this.garden != null)
            this.garden.removeObserver(this);
        this.garden = garden;
        this.garden.addObserver(this);
    }

    @Override
    public ObservableSet<Plant> getSpecies()
    {
        //ObservableSet species = FXCollections.observableSet();
    	Set<Plant> species = new HashSet<Plant>();
    	if (garden != null){

    		for (Map.Entry<Integer, HashMap<Point, java.util.List<TaxonVariety<Plant>>>> yearMap : garden.getAllSquares().entrySet())
    		{
    			for (Map.Entry<Point, java.util.List<TaxonVariety<Plant>>> s : yearMap.getValue().entrySet())
    			{
    				List<TaxonVariety<Plant>> inSquare = s.getValue();
    				for(TaxonVariety<Plant> curr : inSquare) {
    					species.add(curr.getTaxon());
    				}
    			}
    		}
    	}

        return FXCollections.observableSet(species);
    }

    @Override
    public void yearAdded(Garden garden, int year) {}

    @Override
    public void yearDeleted(Garden garden, int year)
    {
        //for (TagListener listener : TagList.getInstance().getListeners())
        //    listener.tagChanged(this);
    }

    @Override public void hintsChanged(int year, Point grid) {}

    @Override
    public void plantsChanged(int year, Point grid)
    {
        //for (TagListener listener : TagList.getInstance().getListeners())
        //    listener.tagChanged(this);
    }

    @Override public void boundsChanged(Rectangle bounds) {}

    static TagInGarden instance = new TagInGarden();
    public static TagInGarden getInstance()
    {
        return instance;
    }
}
