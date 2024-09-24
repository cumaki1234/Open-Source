package org.sourceforge.kga.gui.gardenplan.analysisQuery;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.QueryField;
import org.sourceforge.kga.gui.gardenplan.GardenView;
import org.sourceforge.kga.translation.Translation;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

class VarietyField extends FXField<Entry<DatedPoint, TaxonVariety<Plant>>, String>{

	public VarietyField() {
		super(Translation.Key.variety, QueryField.ALLOWED_AGGREGATIONS.NONE,true);
	}

	@Override
	public Callback<TableColumn<Entry<DatedPoint, TaxonVariety<Plant>>, String>, TableCell<Entry<DatedPoint, TaxonVariety<Plant>>, String>> getCellFactory() {
		return TextFieldTableCell.forTableColumn();
		/*return TextFieldTableCell.forTableColumn(new StringConverter<TaxonVariety<Plant>>(){

			@Override
			public String toString(TaxonVariety<Plant> object) {
				return Translation.getCurrent().translate(object.getTaxon())+" - "+object.getVariety();
			}

			@Override
			public TaxonVariety<Plant> fromString(String string) {
				//should never happen since this is read only
				return null;
			}
			
		});*/
	}

	@Override
	public String getValue(Entry<DatedPoint, TaxonVariety<Plant>> point) {
		// TODO Auto-generated method stub
		return point.getValue().getVariety();
	}

	@Override
	public Entry<DatedPoint, TaxonVariety<Plant>> updateValueforPivot(
			Entry<DatedPoint, TaxonVariety<Plant>> initialPoint, String pivotedBy) {
		if(Resources.plantList().hasVariety(initialPoint.getValue().getTaxon(),pivotedBy)) {			
			return new AbstractMap.SimpleEntry<DatedPoint, TaxonVariety<Plant>>(initialPoint.getKey(),Resources.plantList().getVariety(initialPoint.getValue().getTaxon(),pivotedBy));
		}else {
			return null;
		}
	}

}