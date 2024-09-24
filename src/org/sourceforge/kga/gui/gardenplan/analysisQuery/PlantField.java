package org.sourceforge.kga.gui.gardenplan.analysisQuery;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.QueryField;
import org.sourceforge.kga.analyze.SortablePlant;
import org.sourceforge.kga.gui.gardenplan.GardenView;
import org.sourceforge.kga.translation.Translation;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

class PlantField extends FXField<Entry<DatedPoint, TaxonVariety<Plant>>, SortablePlant>{

	public PlantField() {
		super(Translation.Key.plant, QueryField.ALLOWED_AGGREGATIONS.NONE,true);
	}
	 
	@Override
	public Callback<TableColumn<Entry<DatedPoint, TaxonVariety<Plant>>, SortablePlant>, TableCell<Entry<DatedPoint, TaxonVariety<Plant>>, SortablePlant>> getCellFactory() {
		return SortablePlant.getCellFactory();
	}

	@Override
	public SortablePlant getValue(Entry<DatedPoint, TaxonVariety<Plant>> point) {
		// TODO Auto-generated method stub
		if(point==null) {
			return null;
		}
		else
			return new SortablePlant(point.getValue().getTaxon());
	}

	@Override
	public Entry<DatedPoint, TaxonVariety<Plant>> updateValueforPivot(
			Entry<DatedPoint, TaxonVariety<Plant>> initialPoint, SortablePlant pivotedBy) {
		if(Resources.plantList().hasVariety(pivotedBy.p,initialPoint.getValue().getVariety())) {			
			return new AbstractMap.SimpleEntry<DatedPoint, TaxonVariety<Plant>>(initialPoint.getKey(),Resources.plantList().getVariety(pivotedBy.p, initialPoint.getValue().getVariety()));
		}else {
			return null;
		}
	}

}