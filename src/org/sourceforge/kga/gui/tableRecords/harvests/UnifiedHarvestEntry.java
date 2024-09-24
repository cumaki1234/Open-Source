package org.sourceforge.kga.gui.tableRecords.harvests;

import java.util.HashMap;
import java.util.Map;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Project;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.translation.Translation;

public class UnifiedHarvestEntry {
	private Map<Integer,HarvestEntry> persistedHarvests;
	private PlantInfoEntry persistedInfo;
	private Project project;
	TaxonVariety<Plant> myPlant;

	public UnifiedHarvestEntry(TaxonVariety<Plant> myPlant, Project proj) {
		project = proj;
		this.myPlant=myPlant;
		persistedHarvests = new HashMap<>();
		for(HarvestEntry curr : proj.getHarvestEntries()) {
			if(!curr.plant.equals(myPlant)) {
				continue;
			}
			if(persistedHarvests.containsKey(curr.year)) {
				throw new Error("Multiple harvest entries for a single taxon in a single year. this should never happen...");
			}
			persistedHarvests.put(curr.year, curr);
		}
		for(PlantInfoEntry curr : proj.getPlantInfoEntries()) {
			if(!curr.plant.equals(myPlant)) {
				continue;
			}
			if(persistedInfo!=null) {
				throw new Error("Multiple harvest entries for a single taxon in a single year. this should never happen...");
			}
			persistedInfo=curr;
		}
	}
	
	public String getUnit() {
		if(persistedInfo==null) {
			return Translation.getCurrent().measurement_unit_pieces();
		}else {
			return persistedInfo.harvestUnit;
		}
	}
	
	private PlantInfoEntry materializePlantInfo() {
		if(persistedInfo==null) {
			persistedInfo = new PlantInfoEntry(myPlant);
			project.getPlantInfoEntries().add(persistedInfo);			
		}
		return persistedInfo;
	}
	
	public void setUnit(String value) {
		materializePlantInfo().harvestUnit=value;	
	}
	
	public Double getUnitValue() {
		if(persistedInfo==null) {
			return null;
		}else {
			return persistedInfo.unitValue;
		}
	}
	
	public Double getHarvest(int year) {
		if(persistedHarvests.containsKey(year)) {
			return persistedHarvests.get(year).qty;
		}
		else {
			return null;
		}
	}
	
	public void setHarvest(int year,Double value) {
		if(value==null && persistedHarvests.containsKey(year)) {
			project.getHarvestEntries().remove(persistedHarvests.get(year));
		}
		else {
			if(!persistedHarvests.containsKey(year)) {
				persistedHarvests.put(year, new HarvestEntry(myPlant,year));
				project.getHarvestEntries().add(persistedHarvests.get(year));
			}
			persistedHarvests.get(year).qty=value;
		}
	}
	
	public void setUnitValue(Double value) {
		materializePlantInfo().unitValue=value;	
	}

	public PlantOrUnregistered getMyPlant() {
		return new PlantOrUnregistered(myPlant.getTaxon());
	}

	public String getMyVariety() {
		return (myPlant.getVariety());
	}
	
	
}
