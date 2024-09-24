package org.sourceforge.kga.gui.gardenplan;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.TaxonVariety;

public class PlantedSquare {
	private TaxonVariety<Plant> plant;
	private Point plantSize;

	public Point getPlantSize() {
		return plantSize;
	}

	public TaxonVariety<Plant> getTaxonVariety() {
		return plant;
	}
	
	public Plant getTaxon() {
		return plant.getTaxon();
	}
	
	public int getId() {
		return plant.getId();
	}
	
	public boolean isItem() {
		return plant.isItem();
	}

	public PlantedSquare(TaxonVariety<Plant> plant) {
		this.plant=plant;
		this.plantSize=new Point(1,1);
	}

	public PlantedSquare(TaxonVariety<Plant> plant, Point plantSize) {
		this.plant=plant;
		this.plantSize=plantSize;
	}
}
