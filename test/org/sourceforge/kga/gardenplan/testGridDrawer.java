package org.sourceforge.kga.gardenplan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opentest4j.AssertionFailedError;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.gardenplan.GridDrawer;
import org.sourceforge.kga.gui.gardenplan.MultiPointDependencySet;

public class testGridDrawer implements GridDrawer {
	
	List<TaxonVariety<Plant>>[][] spaces;
	Map<Point,List<TaxonVariety<Plant>>> garden;
	MultiPointDependencySet toTest;
	
	@SuppressWarnings("unchecked")
	public testGridDrawer(int xSize, int ySize, MultiPointDependencySet toTest) {
		spaces = new List[xSize][ySize];
		this.toTest=toTest;
		garden = new HashMap<Point,List<TaxonVariety<Plant>>>();
	}

	@Override
	public void clearRectangle(Point grid, Point size) {
		for(int x=grid.x;x<grid.x+size.x;x++) {
			for(int y=grid.y;y<grid.y+size.y;y++) {
				spaces[x][y]=null;
			}
		}
	}

	@Override
	public void draw(Point grid, int size, List<TaxonVariety<Plant>> toDraw) {
		for(int x=grid.x;x<grid.x+size;x++) {
			for(int y=grid.y;y<grid.y+size;y++) {
				if(spaces[x][y]==null) {
					spaces[x][y]=new LinkedList<TaxonVariety<Plant>>();
				}
				spaces[x][y].addAll(toDraw);
			}
		}
	}

	@Override
	public List<TaxonVariety<Plant>> getPlantsAtPoint(Point grid) {
		return garden.get(grid);
	}
	
	public void addPlant(Point p, List<TaxonVariety<Plant>> toAdd) {
		garden.put(p, toAdd);
		clearRectangle(new Point(0,0),new Point(spaces.length,spaces[0].length));
		for (Point curr : garden.keySet()) {
			toTest.draw(curr, garden.get(curr), true, this);			
		}
	}
	
	public void assertEmptyExceptRect(Point tl,Point br, List<TaxonVariety<Plant>> expected){
		assertEmptyExcept(tl, br);
		assertRectEquals(tl, br,expected);		
	}
	
	public void assertRectEquals(Point tl,Point br, List<TaxonVariety<Plant>> expected){
		try {
		assertTrue(br.x>=tl.x);
		assertTrue(br.y>=tl.y);
		for(int x = tl.x;x<br.x;x++) {
			for(int y = tl.y;y<br.y;y++) {
				if(expected==null||expected.size()==0) {
					assertTrue(spaces[x][y]==null || spaces[x][y].size()==0);
				}
				else {
					if(spaces[x][y]==null) {
						spaces[x][y]=new LinkedList<TaxonVariety<Plant>>();
					}
					assertEquals(expected.size(),spaces[x][y].size());
					for(TaxonVariety<Plant> curr:expected) {
						assertTrue(spaces[x][y].contains(curr));
					}
				}
			}
		}
		}
		catch (AssertionFailedError e) {
			System.out.println(this);
			throw e;
		}
	}
	
	public void assertEmptyExcept(Point tl,Point br){
		if(tl.x!=0)
			assertRectEquals(new Point(0,0),new Point(tl.x-1,getLowerLeft().y),null);
		if(tl.y!=0)
			assertRectEquals(new Point(0,0),new Point(getLowerLeft().x,tl.y-1),null);
		if(br.x!=getLowerLeft().x)
			assertRectEquals(new Point(br.x+1,0),getLowerLeft(),null);
		if(br.y!=getLowerLeft().y)
			assertRectEquals(new Point(br.y+1,0),getLowerLeft(),null);		
	}
	
	public Point getLowerLeft() {
		return new Point(spaces.length-1,spaces[0].length-1);
	}
	

	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Current Display State:");
		for(int x=0;x<spaces.length;x++) {
			b.append("\n[");
			for (int y=0;y<spaces[x].length;y++) {
				if(spaces[x][y]==null) {
					spaces[x][y]=new LinkedList<TaxonVariety<Plant>>();
				}
				b.append((y==0)?spaces[x][y].size():","+spaces[x][y].size());
			}
			b.append("]");
		}
		return b.toString();
		
	}

}
