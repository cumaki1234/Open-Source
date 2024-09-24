package org.sourceforge.kga.gui.gardenplan;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.TaxonVariety;

public class MultiPointDependencySet {
	private HashMap<Point,MultiPointDependancy> deps;
	
	private HashMap <Point,Set<MultiPointDependancy>> affectingCache;
		
	public MultiPointDependencySet() {
		deps=new HashMap<>();
		affectingCache= new HashMap<>();
	}

	public Set<Point> GetRootParents(Point toCheck) {
		Set<MultiPointDependancy> parents=GetDependenciesAffectingPoint(toCheck);
		HashSet<Point> toRet = new HashSet<Point>();
		if(parents.size()==0) {
			toRet.add(toCheck);
		}
		else {
			for(MultiPointDependancy curr:parents) {
				toRet.addAll(GetRootParents(curr.getDrawingPoint()));
			}
		}
		return toRet;
	}
	
	public Set<MultiPointDependancy> GetDependenciesAffectingPoint(Point toCheck) {
		Set<MultiPointDependancy> toRet= affectingCache.get(toCheck);
		if(toRet==null) {
			return new HashSet<MultiPointDependancy>(0);		
		}
		else {
			return new HashSet<MultiPointDependancy>(toRet);
		}
	}
	
	public MultiPointDependancy getDependencyOriginatingAtPoint(Point toCheck) {
		return deps.get(toCheck);		
	}
	
	public MultiPointDependancy addDependencyIfNeeded(Point grid, Point size) {
		MultiPointDependancy existing = this.getDependencyOriginatingAtPoint(grid);
		if(size.x>1 || size.y>1) {
			MultiPointDependancy toAdd = new MultiPointDependancy(grid,size);
			if (existing==null || toAdd.size.x>existing.size.x) {
				deps.put(grid,toAdd);
				Set<Point> children = toAdd.getAllChildPoints();
				for(Point p : children) {
					if(!affectingCache.containsKey(p)) {
						affectingCache.put(p, new HashSet<MultiPointDependancy>());
					}
					affectingCache.get(p).add(toAdd);
				}
			}
			return deps.get(grid);
		}
		return existing;
	}
	
	public void clearDependencyOriginatingAtPoint(Point toClear) {
		//System.out.println("Removing dependency: "+toClear);
		MultiPointDependancy removed = deps.remove(toClear);
		if(removed !=null) {

			for(Point p : removed.getAllChildPoints()) {
				affectingCache.get(p).remove(removed);
				if(affectingCache.get(p).size()==0) {
					affectingCache.remove(p);
				}
			}
		}
	}
	
	/**
	 * returns back a map, that maps square sizes to the plant at that point in the square.
	 * @param plants the list of plants to process
	 * @return a map, in order from largest to smallest
	 */
	public static final TreeMap<Point,List<TaxonVariety<Plant>>> groupBySize(Collection<TaxonVariety<Plant>> plants){
		TreeMap <Point,java.util.List<TaxonVariety<Plant>>> map = new TreeMap <Point,java.util.List<TaxonVariety<Plant>>>(new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				if(o1==null && o2==null) {
					return 0;
				}
				else if (o1==null || o2==null) {
					return (o1==null)?1:-1;
				}
				else
					return o2.x-o1.x;
			}
			
		});
		
		if(plants!=null) {
			for (TaxonVariety<Plant> plant : plants) {
				if(!map.containsKey(plant.getSize())){
					map.put(plant.getSize(), new LinkedList<TaxonVariety<Plant>>());
				}
				map.get(plant.getSize()).add(plant);
			}
		}
		
		return map;
		
	}
	
	/**
	 * returns back a map, that maps square sizes to the plant at that point in the square.
	 * @param points the list of plants to process
	 * @return a map, in order from largest to smallest
	 */
	public final TreeMap<Point,Set<Point>> groupBySizeFromPoints(Collection<Point> points, GridDrawer drawer){
		TreeMap <Point,Set<Point>> map = new TreeMap <Point,Set<Point>>(new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				if(o1==null && o2==null) {
					return 0;
				}
				else if (o1==null || o2==null) {
					return (o1==null)?1:-1;
				}
				else
					return o2.x-o1.x;
			}
			
		});
		
		if(points!=null) {
			for (Point curr : points) {
				Point size = getLargestPlantAt(curr,drawer);
				if(!map.containsKey(size)){
					map.put(size, new HashSet<Point>());
				}
				map.get(size).add(curr);
			}
		}
		
		return map;
		
	}
	
	private Point getLargestPlantAt(Point p, GridDrawer drawer) {
		int largest = 1;
		List<TaxonVariety<Plant>> plants = drawer.getPlantsAtPoint(p);
		if(plants!=null) {
			for (TaxonVariety<Plant> plant : drawer.getPlantsAtPoint(p)){
				largest=(largest<plant.getSize().x)?plant.getSize().x:largest;
			}
		}
		return new Point(largest,largest);
	}
	
	public int size() {
		return deps.size();
	}

	public void draw(Point grid, java.util.List<TaxonVariety<Plant>> plantList, boolean clear, GridDrawer drawer) {
		draw(grid, plantList, clear, drawer,true, new HashSet<Point>()); 
	}
	
	@SafeVarargs
	private static final  Set<Point> union(Set<Point>... toSkip){
		Set<Point> toReturn = new HashSet<>();
		for(Set<Point> curr:toSkip) {
			toReturn.addAll(curr);
		}
		return toReturn;
	}
	
	public void draw(Point grid, java.util.List<TaxonVariety<Plant>> plantList, boolean clear, GridDrawer drawer, boolean paintParentSquares, Set<Point> skip) {
        Map<Point,java.util.List<TaxonVariety<Plant>>>  sizeMap = MultiPointDependencySet.groupBySize(plantList);
        Point mySize = sizeMap.size()>0?sizeMap.keySet().iterator().next():Plant.LEGACY_DEFAULT_SIZE;
    	skip.add(grid);

        Set<MultiPointDependancy> myDeps = GetDependenciesAffectingPoint(grid);
        Set<Point> childrenToRedraw = new HashSet<Point>();
        Set<Point> peersToRedraw = new HashSet<Point>();
        MultiPointDependancy originatingAtMe = addDependencyIfNeeded(grid, mySize);
        if(mySize.x<2) {
        	//the new value for this doesn't have a dependency, only the old value had one, if any.
        	this.clearDependencyOriginatingAtPoint(grid);
        }

    	if(originatingAtMe!=null) {
    		childrenToRedraw.addAll(originatingAtMe.getAllDirectChildren(this));
    		peersToRedraw.addAll(originatingAtMe.getAllChildParents(this));
    		peersToRedraw.remove(grid);
    	}  
    	
    	Set<Point> childSkipList = union(skip,peersToRedraw,childrenToRedraw);
        
    	/*
    	 * TODO: myDeps.size()==0 causes 1 bug, and fixes a different bug.
    	 * UGGGGGH Specifically, having it makes other grids display instead of erase, but also results in nodes not clearing if this grid has both a parent AND children.
    	 * the originatingAtMe!=null check is a (not perfect) bandaid to this problem. It handles cases properly
    	 * UNLESS you try to have a size>1 plant start in a grid that depends on a different size >1 plant..... Which I don't have a strong usecase for.....
    	 */
        if (clear) // && (myDeps.size()==0 || originatingAtMe!=null))
        {
        	if(originatingAtMe!=null && mySize.x<originatingAtMe.size.x) {
        		drawer.clearRectangle(grid, originatingAtMe.size);
        	}
        	else {
        		drawer.clearRectangle(grid, mySize);
        	}
        }

        if(clear) {//ensure any child squares that I do not share are cleared, we will redraw them after this square draws.
        	for(Point renderAfter : childrenToRedraw) {
        		if(!skip.contains(renderAfter)) {
        			draw(renderAfter, null, clear, drawer, false,childSkipList);
        		}
        	}        	
        	for(Point renderAfter : peersToRedraw) {
        		if(!skip.contains(renderAfter)) {
        			draw(renderAfter, null, clear, drawer, true,childSkipList);
        		}
        	}
        }

        if(myDeps.size()>0 && paintParentSquares) {
        	for(MultiPointDependancy curr : myDeps) {
        		Point toDraw = curr.getDrawingPoint();
        		peersToRedraw.remove(toDraw);
        		List<TaxonVariety<Plant>> parentList = drawer.getPlantsAtPoint(toDraw);
        		if(parentList==null)
        			parentList=new LinkedList<>();
        		
        		if(!skip.contains(toDraw) && parentList.size()>0)
        			draw(toDraw,parentList,clear,drawer,true, childSkipList);
        	}        	
        }

       //finally draw my squares.
        for(Map.Entry<Point,List<TaxonVariety<Plant>>> curr : sizeMap.entrySet()) {        	
        	drawer.draw(grid, curr.getKey().x, curr.getValue());
        }
        //draw my peer squares
        for(Point renderAfter : peersToRedraw) {
        	if(!skip.contains(renderAfter)) {
        		//TODO: This NEEDS to clear spaces the plant draws that are not drawn by this parent.
        		draw(renderAfter, drawer.getPlantsAtPoint(renderAfter), false, drawer, true,childSkipList);
        	}
        }

        //draw my child squares
        for(Point renderAfter : childrenToRedraw) {
        	if(!skip.contains(renderAfter)) {
        		//TODO: This NEEDS to clear spaces the plant draws that are not drawn by this parent.
        		draw(renderAfter, drawer.getPlantsAtPoint(renderAfter), false, drawer, false,childSkipList);
        	}
        }
        skip.remove(grid);


	}

}
