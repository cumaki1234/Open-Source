package org.sourceforge.kga.gui.gardenplan;

import java.util.Collection;
import java.util.List;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.TaxonVariety;

public interface GridDrawer {
	
	public void clearRectangle(Point grid, Point size);
	
	public void draw(Point grid, int size, List<TaxonVariety<Plant>> toDraw);
	
	public List<TaxonVariety<Plant>> getPlantsAtPoint(Point grid);

}
