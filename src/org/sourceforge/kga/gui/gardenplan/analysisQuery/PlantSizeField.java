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
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

class PlantSizeField extends FXField<Entry<DatedPoint, TaxonVariety<Plant>>, Integer>{

	public PlantSizeField() {
		super(Translation.Key.size, QueryField.ALLOWED_AGGREGATIONS.SUM,false);
	}

	@Override
	public Callback<TableColumn<Entry<DatedPoint, TaxonVariety<Plant>>, Integer>, TableCell<Entry<DatedPoint, TaxonVariety<Plant>>, Integer>> getCellFactory() {
		return TextFieldTableCell.forTableColumn(new IntegerStringConverter());
	}

	@Override
	public Integer getValue(Entry<DatedPoint, TaxonVariety<Plant>> point) {
		return point.getValue().getSize().x*point.getValue().getSize().x;
	}

	@Override
	public Entry<DatedPoint, TaxonVariety<Plant>> updateValueforPivot(
			Entry<DatedPoint, TaxonVariety<Plant>> initialPoint, Integer pivotedBy) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public double getNumericValue(Entry<DatedPoint, TaxonVariety<Plant>> data) {
		return getValue(data);
	}
	
	@Override
	public Callback<TableColumn<Entry<DatedPoint, TaxonVariety<Plant>>,Entry<Entry<DatedPoint, TaxonVariety<Plant>>,Double>>, TableCell<Entry<DatedPoint, TaxonVariety<Plant>>,Entry<Entry<DatedPoint, TaxonVariety<Plant>>,Double>>> getAggregatedCellFactory(){
		return FXField.DoubleAsIntCellFactory();
	}
	
}