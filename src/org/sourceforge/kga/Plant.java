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
import org.sourceforge.kga.plant.*;


/**
 * This represent a family, genus, species or subspecies
 */
public class Plant extends Taxon<Plant>
{
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Plant.class.getName());
    
    public static final int ID_OTHER=99;
    
    public static final int ID_PATH=116;
    public static int ID_PLANT_SPACE_ERASER=999;
    
    public static final Point LEGACY_DEFAULT_SIZE=new Point(1,1);

    private static Plant plantae = null;
    public static Plant getKingdom()
    {
        if (plantae == null)
            plantae = new Plant(Type.KINGDOM, 0, "Plantae", null);
        return plantae;
    }

    public Lifetime lifetime = null;
    NutritionalNeeds nutritionalNeeds = null;
    RootDeepness rootDeepness = null;
    WeedControl weedControl = null;
    LightRequirement lightRequirement = null;
    CompanionList companions = new CompanionList(this);

    public Plant(int id)
    {
        super(id);
    }

    // constructor
    public Plant(Type type, int id, String name, Plant parent)
    {
        super(type, id, name, parent);
        this.lifetime = new Lifetime(this);
    }

    // inherited getters
    public NutritionalNeeds getNutritionalNeeds() { return nutritionalNeeds == null && parent != null ? getParent().getNutritionalNeeds() : nutritionalNeeds; }
    public RootDeepness     getRootDeepness()     { return rootDeepness == null && parent != null ? getParent().getRootDeepness() : rootDeepness; }
    public WeedControl      getWeedControl()      { return weedControl == null && parent != null ? getParent().getWeedControl() : weedControl; }
    public LightRequirement getLightRequirement() { return lightRequirement == null && parent != null ? getParent().getLightRequirement() : lightRequirement; }

    public boolean hasNutritionalNeeds() { return nutritionalNeeds != null; }
    public boolean hasRootDeepness()     { return rootDeepness != null; }
    public boolean hasWeedControl()      { return weedControl != null; }

    public void setNutritionalNeeds(NutritionalNeeds nutritionalNeeds)
    {
        this.nutritionalNeeds = nutritionalNeeds;
    }

    public void setRootDeepness(RootDeepness rootDeepness)
    {
        this.rootDeepness = rootDeepness;
    }

    public void setWeedControl(WeedControl weedControl)
    {
        this.weedControl = weedControl;
    }

    public CompanionList getCompanions()
    {
        return companions;
    }

    public Plant getParent()
    {
        return (Plant)parent;
    }
    
    public Set<TaxonVariety<Plant>> getVarieties(){
    	return Resources.plantList().getVarieties(this);
    } 
    
    public int getLargestVarietySize() {
    	int largest = 1;
    	for(TaxonVariety<Plant> curr : getVarieties()) {
    		largest=(largest>curr.getSize().x)?largest:curr.getSize().x;
    	}
    	return largest;
    }
    
    public List<Point> getCommonSizes(){
    	List<Point> points = new LinkedList<Point>();
    	points.add(LEGACY_DEFAULT_SIZE);
    	if(this.isItem()) {
    		return points;
    	}
    	points.add(new Point(2,2));
    	points.add(new Point(3,3));
    	points.add(new Point(4,4));
    	points.add(new Point(5,5));
    	points.add(new Point(6,6));
    	points.add(new Point(7,7));
    	points.add(new Point(8,8));
    	points.add(new Point(9,9));
    	points.add(new Point(10,10));
    	return points;
    }
}