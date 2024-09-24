/**
 * Kitchen garden aid is a planning tool for kitchengardeners.
 * Copyright (C) 2013 Tiberius Duluman
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
 * Email contact: tiberius.duluman@gmail.com
 */


package org.sourceforge.kga;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.sourceforge.kga.plant.NotTagged;
import org.sourceforge.kga.plant.Tag;
import org.sourceforge.kga.plant.TagInGarden;
import org.sourceforge.kga.plant.TagInInventory;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Translation;

import java.util.*;
import java.util.prefs.BackingStoreException;

public class TagList
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(PlantList.class.getName());

    private ObservableList<Tag> tags = FXCollections.observableArrayList(tag ->
        new Observable[] { tag.nameProperty(), tag.speciesProperty() });
    private HashMap<Plant, HashSet<Tag>> plantToTags = new HashMap<>();
    
    public NotTagged notTagged = new NotTagged(this);

	List<Tag> frontOrder = Arrays.asList(new Tag[]{notTagged,TagInGarden.getInstance(),TagInInventory.getInstance()});

    public ObservableList<Tag> getTags()
    {
        return tags;
    }


    
    public class tagSorter implements java.util.Comparator<Tag>
    {
    	
        @Override
        public int compare(Tag o1, Tag o2)
        {
        	if(frontOrder.contains(o1) || frontOrder.contains(o2)) {
        		if (frontOrder.contains(o1) && frontOrder.contains(o2)) {
        			return frontOrder.indexOf(o1)-frontOrder.indexOf(o2);
        		}
        		if(frontOrder.contains(o1)) {
        			return -1;
        		}else {
        			return 1;
        		}
        	}
        	Translation t = Translation.getCurrent();
        	return t.getCollator().compare(o1.getName().toUpperCase(), o2.getName().toUpperCase());
        }
    }
    
    public tagSorter getSorter() {
    	return new tagSorter();
    }

    public Tag addTag(String name)
    {
        Tag tag = new Tag(name, null);
        tags.add(tag);
        return tag;
    }
    
    public Tag getTag(String name) {
    	for(Tag curr: frontOrder) {
    		if(curr.getName().toUpperCase().equals(name.toUpperCase())) {
    			return curr;
    		}    		
    	}
    	
    	for(Tag curr:tags) {
    		if(curr.getName().toUpperCase().equals(name.toUpperCase())) {
    			return curr;
    		}    		
    	}
    	return addTag(name);
    }

    void addTagToPlants(Tag tag)
    {
        // add the tag to the plant to tags mapping
        for (Plant plant : tag.getSpecies())
        {
            if (!plantToTags.containsKey(plant))
                plantToTags.put(plant, new HashSet<>());
            plantToTags.get(plant).add(tag);
        }
    }

    void removeTagFromPlants(Tag tag)
    {
        // add the tag to the plant to tags mapping
        for (Plant plant : tag.getSpecies())
        	if(plantToTags.containsKey(plant)) {
        		plantToTags.get(plant).remove(tag);
        	}
    }

    public void removeTag(Tag tag)
    {
        removeTagFromPlants(tag);
        tags.remove(tag);
    }

    public void renameTag(Tag tag, String name)
    {
        tag.setName(name);
    }

    public void modifyPlants(Tag tag, Collection<Plant> plants)
    {
        removeTagFromPlants(tag);
        tag.setSpecies(plants);
        addTagToPlants(tag);
    }
}
