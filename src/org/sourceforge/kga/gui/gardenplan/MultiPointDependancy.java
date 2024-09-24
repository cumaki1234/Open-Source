package org.sourceforge.kga.gui.gardenplan;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.sourceforge.kga.Point;

public class MultiPointDependancy {
	
	Point grid;
	Point size;
	
	public MultiPointDependancy(Point grid, Point size) {
		this.grid=grid;
		this.size=size;
	}
	
	public Point getDrawingPoint() {
		return grid;
	}
	
	public boolean affectsPoint(Point toCheck) {
		if(toCheck.x >=grid.x && toCheck.x<grid.x+size.x) {
			if(toCheck.y >=grid.y&&toCheck.y<grid.y+size.y) {
				return !grid.equals(toCheck);
			}
		}return false;
	}
	
	public Set<Point> getAllChildPoints(){
		Set<Point> toReturn = new HashSet<Point>();
		for (int dx=0;dx<size.x;dx++) {
			for (int dy=(dx==0)?1:0;dy<size.y;dy++) {
				toReturn.add(new Point(grid.x+dx,grid.y+dy));
			}
		}
		return toReturn;
	}
	
	/**
	 * returns direct children. If a child is also a child of a different child, then it is not returned.
	 * @return
	 */
	public Set<Point> getAllDirectChildren(MultiPointDependencySet graph){
		Set<Point> possibleChildren = getAllChildPoints();
        Set<Point> childrenToRedraw = new HashSet<Point>();
		for(Point curr:possibleChildren) {
			boolean skipThisChild=false;
			for (MultiPointDependancy otherDep: graph.GetDependenciesAffectingPoint(curr)) {
				if(possibleChildren.contains(otherDep.grid)) {
					skipThisChild=true;
					break;
				}
			}
			if(!skipThisChild) {
				childrenToRedraw.add(curr);
			}
		}
		return childrenToRedraw;
	}
	
	/**
	 * returns direct children. If a child is also a child of a different child, then it is not returned.
	 * @return
	 */
	public Set<Point> getAllChildParents(MultiPointDependencySet graph){
		Set<Point> possibleChildren = getAllDirectChildren(graph);
        Set<Point> parents = new HashSet<Point>();
		for(Point child:possibleChildren) {
			Set<MultiPointDependancy> thisChildsParents = graph.GetDependenciesAffectingPoint(child);
			for(MultiPointDependancy curr : thisChildsParents) {
				parents.add(curr.grid);
			}
		}
		return parents;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof MultiPointDependancy) {
			return grid.equals(((MultiPointDependancy)other).grid)&&size.equals(((MultiPointDependancy)other).size);
		}
		else {
		return false;
		}		
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(grid,size);
	}

}
