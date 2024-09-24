package org.sourceforge.kga.gui.gardenplan.analysisQuery;

import java.util.AbstractMap;
import java.util.List;
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
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

class YearField extends FXField<Entry<DatedPoint, TaxonVariety<Plant>>, Integer>{

	public YearField() {
		super(Translation.Key.year, QueryField.ALLOWED_AGGREGATIONS.NONE,true);
	}

	@Override
	public Callback<TableColumn<Entry<DatedPoint, TaxonVariety<Plant>>, Integer>, TableCell<Entry<DatedPoint, TaxonVariety<Plant>>, Integer>> getCellFactory() {
		return TextFieldTableCell.forTableColumn(new IntegerStringConverter());
	}

	@Override
	public Integer getValue(Entry<DatedPoint, TaxonVariety<Plant>> point) {
		return point.getKey().getYear();
	}

	@Override
	public Entry<DatedPoint, TaxonVariety<Plant>> updateValueforPivot(
			Entry<DatedPoint, TaxonVariety<Plant>> initialPoint, Integer pivotedBy) {
		DatedPoint pivotPoint = new DatedPoint(initialPoint.getKey(),pivotedBy);
		return new AbstractMap.SimpleEntry<DatedPoint, TaxonVariety<Plant>>(pivotPoint,initialPoint.getValue());
	}
	
	@Override
	public double getNumericValue(Entry<DatedPoint, TaxonVariety<Plant>> data) {
		return getValue(data);
	}
	
}