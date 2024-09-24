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

import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.sourceforge.kga.Plant;

import java.util.Collection;


/**
 * This represent a tag for the plant
 * @author Tiberius Duluman
 */
public class Tag
{
    private final SimpleStringProperty name;
    private final SimpleSetProperty<Plant> plants;

    public Tag(String name, Collection<Plant> plantList)
    {
        this.name = new SimpleStringProperty(name);
        this.plants = new SimpleSetProperty(FXCollections.observableSet());
        if (plantList != null)
        {
            this.plants.addAll(plantList);
        }
    }

    public String getName()
    {
        return name.get();
    }

    public void setName(String name)
    {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty()
    {
        return name;
    }

    public void setSpecies(Collection<Plant> plantList)
    {
        this.plants.clear();
        if (plantList != null)
            this.plants.addAll(plantList);
    }

    public ObservableSet<Plant> getSpecies()
    {
        return plants;
    }

    public SimpleSetProperty<Plant> speciesProperty()
    {
        return plants;
    }
}