package org.sourceforge.kga.gui.gardenplan.analysisQuery;

import java.util.Map.Entry;

import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.QueryField;
import org.sourceforge.kga.translation.Translation;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

class PlantCountField extends FXField<Entry<DatedPoint, TaxonVariety<Plant>>, Integer>{

	public PlantCountField() {
		super(Translation.Key.plant_count, QueryField.ALLOWED_AGGREGATIONS.SUM,false);
	}

	@Override
	public Callback<TableColumn<Entry<DatedPoint, TaxonVariety<Plant>>, Integer>, TableCell<Entry<DatedPoint, TaxonVariety<Plant>>, Integer>> getCellFactory() {
		return TextFieldTableCell.forTableColumn(new IntegerStringConverter());
	}

	@Override
	public Integer getValue(Entry<DatedPoint, TaxonVariety<Plant>> point) {
		return 1;
	}

	@Override
	public Entry<DatedPoint, TaxonVariety<Plant>> updateValueforPivot(
			Entry<DatedPoint, TaxonVariety<Plant>> initialPoint, Integer pivotedBy) {
		return initialPoint;
	}
	
	@Override
	public double getNumericValue(Entry<DatedPoint, TaxonVariety<Plant>> data) {
		return 1;
	}
	
	@Override
	public Callback<TableColumn<Entry<DatedPoint, TaxonVariety<Plant>>,Entry<Entry<DatedPoint, TaxonVariety<Plant>>,Double>>, TableCell<Entry<DatedPoint, TaxonVariety<Plant>>,Entry<Entry<DatedPoint, TaxonVariety<Plant>>,Double>>> getAggregatedCellFactory(){
		return FXField.DoubleAsIntCellFactory();
	}
	
	private void getTotalCosts() {
		
	}
	
}