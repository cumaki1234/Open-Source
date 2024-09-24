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


package org.sourceforge.kga;

import java.util.*;
import java.io.*;

import org.sourceforge.kga.io.SerializableSpecies;
import org.sourceforge.kga.plant.PropertySource;


/**
 * This represents the parsed species.xml
 * @author Tiberius Duluman
 *
 */
public class PlantList
{
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(PlantList.class.getName());

    private static Map<Integer, Plant> plants;
    private static Map<Integer, Animal> animals;
    private static Map<Taxon,Map<String,TaxonVariety>> varieties; 
    

    public PlantList()
    {
        plants = new TreeMap<>();
        animals = new TreeMap<>();
        varieties=new TreeMap<>();
    }
    
    public boolean hasVariety(Plant p, String variety) {
    	if(variety==null || variety.length()==0) {
    		return true;
    	}
    	variety=variety.toUpperCase();
    	if (p==null) {
    		return varieties.containsKey(getPlant(Plant.ID_OTHER)) && varieties.get(getPlant(Plant.ID_OTHER)).containsKey(variety);
    	}
    	else
    		return varieties.containsKey(p) && varieties.get(p).containsKey(variety);  		
    	
    }
    
    public Set<TaxonVariety<Plant>> getVarieties(Plant p){
    	Set<TaxonVariety<Plant>> toReturn = new HashSet<TaxonVariety<Plant>>();
    	if(varieties.containsKey(p))
    		for(TaxonVariety<Plant> curr : varieties.get(p).values()) {
    			toReturn.add(curr);
    		}
    	return toReturn;
    }
    
    public TaxonVariety<Plant> getVariety(Plant p, String variety) {
    	if(variety==null) {
    		variety="";
    	}
    	if (p==null) {
    		return null;
    	}
    	return _getVariety(getPlant(p.getId()),variety);
    }
    
    
    public TaxonVariety<Animal>  getVariety(Animal p, String variety) {
    	return _getVariety(getAnimal(p.getId()),variety);
    }
    
    private void ensureHasMapSet(Taxon taxon, String variety) {
    	variety=variety.toUpperCase();
    	if (!varieties.containsKey(taxon)) {
    		varieties.put(taxon, new TreeMap<String,TaxonVariety>());
    	}
    }
    
    private <T extends Taxon> TaxonVariety<T>  _getVariety(T taxon, String variety) {
    	ensureHasMapSet(taxon,variety);
    	if(!varieties.get(taxon).containsKey(variety.toUpperCase())) {
        	varieties.get(taxon).put(variety.toUpperCase(), new TaxonVariety<T>(taxon,variety));
        }
    	return varieties.get(taxon).get(variety.toUpperCase());
    	
    }

    public Animal getAnimal(int id)
    {
        return animals.get(id);
    }

    public Collection<Animal> getAnimals()
    {
        return animals.values();
    }

    public void addTaxon(Taxon taxon)
    {
        int id = taxon.getId();
        if (plants.containsKey(id) || animals.containsKey(id))
            log.severe("Duplicate ID=" + Integer.toString(id) + " " + taxon.getName());
        if (taxon instanceof Plant)
            plants.put(id, (Plant)taxon);
        else if (taxon instanceof Animal)
            animals.put(id, (Animal)taxon);
    }
    
    void remapVariety(TaxonVariety v, String newVariety, Taxon newTaxon) {
    	if(newVariety==null) {
    		newVariety="";
    	}
    	ensureHasMapSet(newTaxon,newVariety);
    	if (varieties.get(newTaxon).containsValue(newVariety)) {
    		throw new Error("We do not support merging to varieties.");
    	}
		varieties.get(v.getTaxon()).remove(v.getVariety().toUpperCase());

    	v.updateAndNotify(newTaxon,newVariety);
    	
    	varieties.get(v.getTaxon()).put(newVariety,v);
    }

    public Plant getPlant(int id)
    {
        return plants.get(id);
    }

    public Collection<Plant> getPlants()
    {
        return Collections.unmodifiableCollection(plants.values());
    }



    // translations
    private Set<String> translations = new TreeSet<String>();
    public void setTranslation(int id, String language, String text)
    {
        translations.add(language);

        Plant plant = plants.get(id);
        Animal animal = animals.get(id);
        (plant != null ? plant : animal).setTranslation(language, text);
    }

    public Set<String> getTranslations()
    {
        return translations;
    }

    // sources
    private ArrayList<PropertySource> sources = new ArrayList<>();
    public PropertySource addSource(String name, String url)
    {
        log.info("addSource " + name);
        for (int i = 0; i < sources.size(); ++i)
            if (sources.get(i).name.compareTo(name) == 0)
            {
                log.info("source already added");
                return sources.get(i);
            }
        log.info("new source");
        sources.add(new PropertySource(sources.size() + 1, name, url));
        return sources.get(sources.size() - 1);
    }

    public PropertySource reserveSource(int i)
    {
        i = i - 1;
        while (i >= sources.size())
            sources.add(new PropertySource(sources.size() + 1, "", ""));
        return sources.get(i);
    }

    public PropertySource getSource(int i)
    {
        i = i - 1;
        if (i >= sources.size())
            return null;
        return sources.get(i);
    }
}