package org.sourceforge.kga.gardenplan;

import java.util.LinkedList;
import java.util.List;

import org.sourceforge.kga.Garden;
import org.sourceforge.kga.GardenObserver;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.Rectangle;

public class testGardenObserver implements GardenObserver {
	
	public List<Integer> yearsAdded;
	public List<Integer> yearsRemoved;
	public List<Point> hintsChanged;
	public List<Point> plantsChanged;
	public List<Rectangle> boundsChanged;

	public testGardenObserver() {
		yearsAdded=new LinkedList<Integer>();
		yearsRemoved=new LinkedList<Integer>();
		hintsChanged=new LinkedList<Point>();
		plantsChanged=new LinkedList<Point>();
		boundsChanged=new LinkedList<Rectangle>();
	}

	@Override
	public void yearAdded(Garden sender, int year) {
		yearsAdded.add(year);

	}

	@Override
	public void yearDeleted(Garden sender, int year) {
		yearsRemoved.add(year);
	}

	@Override
	public void hintsChanged(int year, Point grid) {
		hintsChanged.add(grid);
	}

	@Override
	public void plantsChanged(int year, Point grid) {
		plantsChanged.add(grid);

	}

	@Override
	public void boundsChanged(Rectangle bounds) {
		boundsChanged.add(bounds);

	}

}
