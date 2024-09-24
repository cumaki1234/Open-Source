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
 * Author: tiberius.duluman
 * Date: 6/27/13
 */
public class NotTagged extends Tag implements TagListener
{
	
	TagList myList;
    public NotTagged(TagList myList)
    {
        super("", null);
        this.myList=myList;
    }

    @Override public String getName()
    {
        return Translation.getCurrent().not_tagged();
    }

    public ObservableSet<Plant> getSpecies()
    {
        Set<Plant> species = new HashSet<Plant>();

        Set<Plant> tagged = new HashSet<Plant>();
        for( Tag curr : myList.getTags()) {
        	tagged.addAll(curr.getSpecies());
        }
        
        for (Plant s : Resources.plantList().getPlants())
        {
            if (!tagged.contains(s))
                species.add(s);
        } 

        return FXCollections.observableSet(species);
    }

    @Override
    public void tagAdded(Tag tag)
    {
        tagChanged(null);
    }

    @Override
    public void tagDeleted(Tag tag)
    {
        tagChanged(null);
    }

    @Override
    public void tagChanged(Tag tag)
    {
        if (tag == TagInGarden.getInstance() || tag == this)
            return;
        /* for (TagListener listener : TagList.getInstance().getListeners())
            if (listener != this)
                listener.tagChanged(this); */
    }
}
