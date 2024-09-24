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
import org.sourceforge.kga.gui.FileWithChanges;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.SeedFileWithChanges;
import org.sourceforge.kga.translation.Translation;

import java.util.*;


public class TagInInventory extends Tag implements FileWithChanges.Listener, SeedList.Listener
{
    private TagInInventory()
    {
        super("", null);
    }

    @Override public String getName()
    {
        return Translation.getCurrent().species_in_inventory();
    }

    ProjectFileWithChanges seedFile = null;
    SeedCollection inventory = null;
    public void setSeedFile(ProjectFileWithChanges seedFile)
    {
    	if (this.seedFile != null)
    		this.seedFile.removeListener(this);
    	this.seedFile = seedFile;
    	this.seedFile.addListener(this);
    }

    @Override
    public ObservableSet<Plant> getSpecies()
    {
    	//ObservableSet species = FXCollections.observableSet();

    	Set<Plant> species = new HashSet<Plant>();
    	if (inventory != null) {
    		for (SeedList seedList : inventory)
    			for (SeedEntry entry : seedList.getSeedView())
    				if (entry.getPlant().plant != null)
    					species.add(entry.getPlant().plant);
    	}
    	return FXCollections.observableSet(species);
    }

    static TagInInventory instance = new TagInInventory();
    public static TagInInventory getInstance()
    {
        return instance;
    }

    @Override
    public void viewChanged()
    {
        //for (TagListener listener : TagList.getInstance().getListeners())
        //    listener.tagChanged(this);
    }

    @Override
    public void listChanged()
    {
        //for (TagListener listener : TagList.getInstance().getListeners())
        //    listener.tagChanged(this);
    }

    @Override
    public void objectChanged()
    {
         this.inventory = seedFile.getProject().getSeedCollection();
    }

    @Override
    public void unsavedChangesChanged()
    {
    }
}
