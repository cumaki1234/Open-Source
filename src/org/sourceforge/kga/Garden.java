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

import org.sourceforge.kga.Point;
import org.sourceforge.kga.Rectangle;
import org.sourceforge.kga.gui.gardenplan.MultiPointDependancy;
import org.sourceforge.kga.gui.gardenplan.MultiPointDependencySet;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;


/**
 * Garden is responsible for adding/removing squares, bounds and file IO.
 * This class represent the garden were all squares are.
 * Here you can add, get and remove squares.
 *
 * @author Christian Nilsson
 *
 */
public class Garden
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    static public class Coordinate
    {
        public boolean equals(Object obj)
        {
            return obj instanceof Coordinate &&
                ((Coordinate)obj).grid.equals(grid) && ((Coordinate)obj).year == year;
        }

        public Coordinate(int year, Point grid)
        {
            this.year = year;
            this.grid = grid;
        }

        public final int year;
        public final Point grid;
    }

    /**
     * The rectangle that bounds all the squares in the garden
     * It is computed when a garden is loaded.
     * For a new garden, it is an 1x1 rectangle.
     * It is automatically grown when a plant is added outside
     * of the bounds. It is not shrunk when squares are deleted.
     * x and y is the coordinates to origin.
     */
    private Rectangle bounds = new Rectangle(0, 0, 1, 1);
    /**
     * The squares in the garden.
     */
    private TreeMap<Integer, HashMap<Point, List<TaxonVariety<Plant>>>> squares;
    private List<GardenObserver> observers = new ArrayList<GardenObserver>();

    public MultiPointDependencySet deps;//TODO: the logic to maintain deps should really be in garden instead of garden canvas.

    /**
     * Creating a new empty garden
     */
    public Garden()
    {
        log.info("Creating new garden");
        squares = new TreeMap<Integer, HashMap<Point, List<TaxonVariety<Plant>>>>();
        deps = new MultiPointDependencySet();
    }
    
    public Stream<Entry<Point,List<TaxonVariety<Plant>>>> stream(int year) {
    	return squares.get(year).entrySet().stream();
    }
    
    public Stream<Entry<DatedPoint,List<TaxonVariety<Plant>>>> stream() {
    	Collection<Stream<Entry<DatedPoint,List<TaxonVariety<Plant>>>>> allStreams = new LinkedList<>();
    	for(int year : squares.keySet()) {
    		allStreams.add(stream(year).map(p->new java.util.AbstractMap.SimpleEntry<DatedPoint, List<TaxonVariety<Plant>>>(new DatedPoint(p.getKey(),year),p.getValue()) ));
    	}
    	Stream<Entry<DatedPoint,List<TaxonVariety<Plant>>>> result =  Stream.of(allStreams.toArray(new Stream[allStreams.size()])).flatMap(i -> i);
    	return result;
    }
    
    public Stream<Entry<DatedPoint,TaxonVariety<Plant>>> streamByPlant(){
    	return stream().flatMap(t->{
    		Collection<Entry<DatedPoint,TaxonVariety<Plant>>> summarized = new ArrayList<Entry<DatedPoint,TaxonVariety<Plant>>>(t.getValue().size());
    		for(TaxonVariety<Plant> plant : t.getValue()) {
    			summarized.add(new AbstractMap.SimpleEntry<DatedPoint,TaxonVariety<Plant>>(t.getKey(),plant));
    		}
    		Stream<Entry<DatedPoint,TaxonVariety<Plant>>> finalStream = summarized.stream();
    		return finalStream;
    	});
    }

    public void addObserver(GardenObserver observer)
    {
        observers.add(observer);
    }

    public void removeObserver(GardenObserver observer)
    {
        observers.add(observer);
    }


    /**
     * Add new year to garden. Copies items and perennials from latest year to
     * the new year.
     * @param newYear the year to be added
     * @return true if there are any items or perennials
     */
    public boolean addYear(int newYear)
    {
        log.info("Adding year " + Integer.toString(newYear));
        if (getYears().contains(newYear))
            return false;
        copyPermanentSquares(newYear);
        for (GardenObserver o : observers)
            o.yearAdded(this, newYear);
        return true;
    }

    /**
     * Copies square for one year to another. Copies only perennial and items.
     * Annuals are not copied.
     * @param newYear year where squares will be copied
     * @return always return true
     */
    private void copyPermanentSquares(int newYear)
    {
        HashMap<Point, List<TaxonVariety<Plant>>> newYearSquares = new HashMap<Point, List<TaxonVariety<Plant>>>();
        HashMap<Point, List<TaxonVariety<Plant>>> previousYearSquares = squares.get(newYear - 1);
        if (previousYearSquares != null)
            for (Map.Entry<Point, List<TaxonVariety<Plant>>> square : previousYearSquares.entrySet())
            {
                List<TaxonVariety<Plant>> perennial = getPermenantSquares(newYear,square.getKey(),square.getValue());
                if (perennial != null)
                    newYearSquares.put(square.getKey(), perennial);
            }
        squares.put(newYear, newYearSquares);
    }
    
    private List<TaxonVariety<Plant>> getPermenantSquares(int newYear, Point key, List<TaxonVariety<Plant>> priorYearPlants){

        List<TaxonVariety<Plant>> perennial = null;
        if(priorYearPlants!=null) {
        	for (TaxonVariety<Plant> plant : priorYearPlants)
        	{
        		if (plant.getTaxon().isItem() ||
        				findSquare(
        						newYear, key, 0,
        						plant.getTaxon().lifetime.getRepetitionYears(),
        						plant.getTaxon().lifetime.getRepetitionYears(),
        						plant.getTaxon(), true, false).isEmpty())
        		{
        			if (perennial == null)
        				perennial = new ArrayList<TaxonVariety<Plant>>();
        			perennial.add(plant);
        		}
        	}
        }
        return perennial;
    }

    /**
     * Returns a sorted set of all the year available in this garden.
     * @return a sorted set of all the years
     */
    public Set<Integer> getYears()
    {
        return squares.keySet();
    }

    public String toString()
    {
        return "Garden with " + squares.size() + " squares";
    }

    /**
     * Adds a square at year and grid. If there already is a square at this
     * location it will only return this square. If new square is outside
     * garden bounds, observers.boundsChanged will be notified.
     * @param year the year of square
     * @param grid location of square
     * @return the added or existing square
     */
    public boolean addPlant(int year, Point grid, TaxonVariety<Plant>  plant)
    {
        // log.fine("Add square at " + Integer.toString(year) + " " + grid.toString());
        HashMap<Point, List<TaxonVariety<Plant>>> yearMap = squares.get(year);
        if (yearMap == null)
        {
            yearMap = new HashMap<Point, List<TaxonVariety<Plant>>>();
            squares.put(year, yearMap);
        }
        List<TaxonVariety<Plant>> plantList = yearMap.get(grid);
        if (plantList == null)
        {
            plantList = new ArrayList<TaxonVariety<Plant>>();
            yearMap.put(grid, plantList);
        }

        if (plantList.contains(plant))
            return false;

        if (plantList.size() > 0 &&
            (plant.getTaxon().isItem() || plantList.get(0).getTaxon().isItem()))
        {
            plantList.clear();
        }
        plantList.add(plant);

        //checking bounds
        if (!bounds.contains(grid))
        {
            bounds.add(grid);
            if (!bounds.contains(grid))
            {
                // if the added Point falls on the right or bottom
                // edge of the enlarged Rectangle, contains returns false
                if (bounds.x + bounds.width == grid.x)
                    ++bounds.width;
                if (bounds.y + bounds.height == grid.y)
                    ++bounds.height;
            }
            log.info("Bounds changed " + bounds);
            for (GardenObserver o : observers)
                o.boundsChanged(bounds);
        }
        else
            squareChanged(year, grid);
        return true;
    }

    public TreeMap<Integer, HashMap<Point, List<TaxonVariety<Plant>>>> getAllSquares()
    {
        return squares;
    }

    public Rectangle getBounds()
    {
        return bounds;
    }

    public List<TaxonVariety<Plant>> getPlants(int year, Point grid)
    {
        HashMap<Point, List<TaxonVariety<Plant>>> yearMap = squares.get(year);
        if (yearMap == null || yearMap.get(grid)==null)
            return null;
        else
        	return Collections.unmodifiableList(new ArrayList<>(yearMap.get(grid)));
        
    }

    /**
     * Deletes whole year from garden.
     * @param year the year of which squares will be removed
     */
    public void deleteYear(int year)
    {
        squares.remove(year);
        for (GardenObserver o : observers)
            o.yearDeleted(this, year);
    }

    /**
     * Removes plant. Calls squareChanged().
     * @param plant the plant
     */
    public void removePlant(int year, Point grid, TaxonVariety<Plant> plant)
    {
        HashMap<Point, List<TaxonVariety<Plant>>> yearMap = squares.get(year);
        if (yearMap == null)
            return;
        List<TaxonVariety<Plant>> plants = yearMap.get(grid);
        if (plants == null)
            return;
        if (plant != null && plants.contains(plant))
        {
            plants.remove(plant);
            if (plants.size() == 0)
                yearMap.remove(grid);
            // log.fine("Remove plant " + plant + " at grid " + grid);
        }
        else
        {
        	removeIfPlantsAreSingleYear(yearMap, year,grid,plants);
            // log.fine("Remove at grid " + grid);
        }

        if (plants.size() == 0)
        {
            yearMap.remove(grid);
        }
        squareChanged(year, grid);
    }
    
    private void removeIfPlantsAreSingleYear(HashMap<Point, List<TaxonVariety<Plant>>> yearMap, int year, Point grid, Collection<TaxonVariety<Plant>> plants) {
    	boolean remove = yearMap.get(grid).size()==0&&yearMap.get(grid).get(0).getId()==Plant.ID_PLANT_SPACE_ERASER;
    	List<TaxonVariety<Plant>> willCopy=null;
    	if(squares.get(year-1)!=null) {
    		List<TaxonVariety<Plant>> previousYearSquares = squares.get(year - 1).get(grid);
    		willCopy = getPermenantSquares(year,grid,previousYearSquares);
    	}
        remove = remove||willCopy==null||willCopy.size()==0;
    	
    	if(remove)
    		yearMap.remove(grid);
    	else {
    		yearMap.get(grid).clear();
    		yearMap.get(grid).add(Resources.plantList().getVariety(Resources.plantList().getPlant(Plant.ID_PLANT_SPACE_ERASER),""));
    	}
    }
    
    public void plantVarietyChanged(TaxonVariety<Plant> p) {
    	for (Map.Entry<Integer,HashMap<Point,List<TaxonVariety<Plant>>>> curr :this.getAllSquares().entrySet()) {
    		int year = curr.getKey();
        	for (Map.Entry<Point,List<TaxonVariety<Plant>>> point :curr.getValue().entrySet()) {
        		if(point.getValue().contains(p)) {
        			this.squareChanged(year, point.getKey());
        		}        		
        	}    		
    	}
    }

    /**
     * This method is called when a plant is added or removed.
     * It notifies garden observer.squareChanged that this square has changed and
     * also notifies garden observer.hintschanged to itself and its nearest neighbor.
     */
    protected void squareChanged(int year, Point grid)
    {
    	//System.out.println("square changed");
        for (GardenObserver o : observers)
            o.plantsChanged(year, grid);

        //Updates hints for this year and all years beyond
        for (Map.Entry<Integer, HashMap<Point, List<TaxonVariety<Plant>>>> yearMap : squares.entrySet())
        {
            if (yearMap.getKey() < year)
                continue;
            for (int dx = -1; dx <= 1; ++dx)
                for (int dy = -1; dy <= 1; ++dy)
                {
                	if(dx==0 && dy==0) {
                		continue;//don't run hintschange on the square that did a plant changed.
                	}
                    Point neighbor = new Point(grid.x + dx, grid.y + dy);
                    List<TaxonVariety<Plant>> atSpace = this.getPlants(yearMap.getKey(), neighbor);
                    //if(atSpace!=null&&atSpace.size()>0) {//hints only exist if there are plants there....
                    	for (GardenObserver o : observers)
                    		o.hintsChanged(yearMap.getKey(), neighbor);
                    //}
                }
        }
    }

    public class FindResult
    {
        public FindResult(Coordinate coordinate, Plant plant)
        {
            this.coordinate = coordinate;
            this.plant = plant;
        }

        public final Coordinate coordinate;
        public final Plant plant;
    }

    private void checkSquare(int year, Point p, Collection<Plant> plant, boolean exactMatch, boolean findAll, ArrayList<FindResult> results)
    {
        java.util.List<TaxonVariety<Plant>> plantList = getPlants(year, p);
        Set<MultiPointDependancy> myDeps=deps.GetDependenciesAffectingPoint(p);
        if(myDeps!=null && myDeps.size()>0) {
        	java.util.List<TaxonVariety<Plant>> mine = plantList;
        	plantList = new LinkedList<>();
        	if(mine!=null) {
        		plantList.addAll(mine);
        	}
        	for (MultiPointDependancy curr : myDeps) {
        		List<TaxonVariety<Plant>> found = getPlants(year, curr.getDrawingPoint());
        		if(found!=null)
        			plantList.addAll(found);
        	}
        }
        if (plantList == null || plantList.size()==0)
            return;
        // log.finest("Check square " + p.toString() + " " + plant.toString());

        for (TaxonVariety<Plant> found : plantList)
        {
            Plant search = found.getTaxon();
            while (search != null)
            {
                if (plant.contains(search))
                {
                    // log.finest("Adding found " + Integer.toString(year) + " " + p.toString());
                    results.add(new FindResult(new Coordinate(year, p), found.getTaxon()));
                    break;
                }
                if (exactMatch)
                    break;
                search = search.getParent();
            }
            if (!findAll && !results.isEmpty())
                break;
        }
    }

    public ArrayList<FindResult> findSquare(
        int year, Point grid,
        int radius, int backYearsStart, int backYearsEnd,
        Plant plant, boolean exactMatch, boolean findAll)
    {
    	Collection<Plant> c = new HashSet<Plant>(1);
    	c.add(plant);
    	return findSquare(year,grid,radius,backYearsStart,backYearsEnd,c,exactMatch,findAll);
    }

    public ArrayList<FindResult> findSquare(
        int year, Point grid,
        int radius, int backYearsStart, int backYearsEnd,
        Collection<Plant> plant, boolean exactMatch, boolean findAll)
    {
        // log.finest("searchSquare " + grid.toString());
        ArrayList<FindResult> results = new ArrayList<>();
        for (int backYear = backYearsEnd; backYear >= backYearsStart; --backYear)
            for(int dx = -radius;dx<=radius;dx++)
            	for(int dy = -radius;dy<=radius;dy++) {
            		int widthAdjustment=0;//(dx>0)?sizeAtPoint.x-1:0;
            		int heightAdjustment=0;//(dy>0)?sizeAtPoint.y-1:0;
            		checkSquare(year - backYear, new Point(grid.x + widthAdjustment+dx, grid.y +heightAdjustment+dy), plant, exactMatch, findAll, results);
                    if (!findAll && !results.isEmpty())
                        return results;
            /*for (int r = 0; r <= radius; ++r)
                for (int i = -r + (r == 0 ? 0 : 1); i <= r; ++i)
                {
                    // log.finest("r=" + Integer.toString(r) + " i=" + Integer.toString(i));
                    checkSquare(year - backYear, new Point(grid.x + i, grid.y - r), plant, exactMatch, findAll, results);
                    if (!findAll && !results.isEmpty())
                        return results;
                    if (i == 0 && r == 0)
                        break;

                    checkSquare(year - backYear, new Point(grid.x - i, grid.y + r), plant, exactMatch, findAll, results);
                    if (!findAll && !results.isEmpty())
                        return results;

                    checkSquare(year - backYear, new Point(grid.x - r, grid.y - i), plant, exactMatch, findAll, results);
                    if (!findAll && !results.isEmpty())
                        return results;

                    checkSquare(year - backYear, new Point(grid.x + r, grid.y + i), plant, exactMatch, findAll, results);
                    if (!findAll && !results.isEmpty())
                        return results;*/
                }
        return results;
    }
}