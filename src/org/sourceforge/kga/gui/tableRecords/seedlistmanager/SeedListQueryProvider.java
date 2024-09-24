package org.sourceforge.kga.gui.tableRecords.seedlistmanager;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.sourceforge.kga.Resources;
import org.sourceforge.kga.SeedCollection;
import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.Query;
import org.sourceforge.kga.analyze.QueryProvider;
import org.sourceforge.kga.analyze.SortablePlant;
import org.sourceforge.kga.translation.Translation;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

public class SeedListQueryProvider implements QueryProvider<Entry<String,SeedEntry>,FXField<Entry<String,SeedEntry>,?>>  {
	SeedCollection collection;
	
	private List<FXField<Entry<String,SeedEntry>,?>> fields;

	public static final SeedListField FIELD_LIST = new SeedListField();
	public static final NameField FIELD_PLANT = new NameField();
	public static final VarietyField FIELD_VARIETY = new VarietyField();
	public static final QuantityUnitField FIELD_UNIT = new QuantityUnitField();
	public static final QuantityField FIELD_QTY = new QuantityField();
	public static final ValidFromField FIELD_VALID_FROM = new ValidFromField();
	public static final ValidToField FIELD_VALID_TO = new ValidToField();
	public static final ValidDatesField FIELD_VALID_RANGE = new ValidDatesField();
	public static final CommentField FIELD_COMMENT = new CommentField();
	
		
	public SeedListQueryProvider(SeedCollection collection) {
		this.collection=collection;
		fields = new LinkedList<>();
		fields.add(FIELD_LIST);
		fields.add(FIELD_PLANT);
		fields.add(FIELD_VARIETY);
		fields.add(FIELD_QTY);
		fields.add(FIELD_UNIT);
		fields.add(FIELD_COMMENT);
		fields.add(FIELD_VALID_RANGE);
		fields.add(FIELD_VALID_FROM);
		fields.add(FIELD_VALID_TO);
	}

	@Override
	public Collection<FXField<Entry<String, SeedEntry>, ?>> getAvailableFields() {
		return Collections.unmodifiableList(fields);
	}

	@Override
	public Stream<Entry<String, SeedEntry>> stream() {
		return collection.stream();
	}

	@Override
	public Query<Entry<String, SeedEntry>, FXField<Entry<String, SeedEntry>, ?>> getDefaultQuery() {
		Collection<FXField<Entry<String,SeedEntry>,?>> summarize = new HashSet<>();
		summarize.add(FIELD_QTY);
		Collection<FXField<Entry<String,SeedEntry>,?>> by = new HashSet<>();
		by.add(FIELD_PLANT);
		by.add(FIELD_VARIETY);
		by.add(FIELD_LIST);
		by.add(FIELD_VALID_RANGE);
		
		return new Query<Entry<String, SeedEntry>, FXField<Entry<String, SeedEntry>, ?>>(summarize,by,this,FIELD_PLANT,FIELD_LIST);
	}
}

class SeedListField extends FXField<Entry<String,SeedEntry>,String>{

	public SeedListField() {
		super(Translation.Key.name, ALLOWED_AGGREGATIONS.NONE, true);
	}

	@Override
	public Callback<TableColumn<Entry<String, SeedEntry>, String>, TableCell<Entry<String, SeedEntry>, String>> getCellFactory() {
		return TextFieldTableCell.forTableColumn();
	}

	@Override
	public String getValue(Entry<String, SeedEntry> point) {
		return point.getKey();
	}

	@Override
	public Entry<String, SeedEntry> updateValueforPivot(Entry<String, SeedEntry> initialPoint, String pivotedBy) {
		return new AbstractMap.SimpleEntry<>(pivotedBy,initialPoint.getValue());
	}
	
}

class NameField extends FXField<Entry<String,SeedEntry>,SortablePlant>{

	public NameField() {
		super(Translation.Key.plant, ALLOWED_AGGREGATIONS.NONE, true);
	}

	@Override
	public SortablePlant getValue(Entry<String, SeedEntry> point) {
		return new SortablePlant(point.getValue().getPlant().plant);
	}

	@Override
	public Entry<String, SeedEntry> updateValueforPivot(Entry<String, SeedEntry> initialPoint, SortablePlant pivotedBy) {
		if(Resources.plantList().hasVariety(pivotedBy.p,initialPoint.getValue().getVariety())) {		
			SeedEntry myEntry = new SeedEntry(new PlantOrUnregistered(pivotedBy.p), initialPoint.getValue().getVariety(),
					initialPoint.getValue().getQuantity(), initialPoint.getValue().getComment(), initialPoint.getValue().getValidFrom(),initialPoint.getValue().getValidTo(), null);	
			return new AbstractMap.SimpleEntry<>(initialPoint.getKey(),myEntry);
		}else {
			return null;
		}

	}

	@Override
	public Callback<TableColumn<Entry<String, SeedEntry>, SortablePlant>, TableCell<Entry<String, SeedEntry>, SortablePlant>> getCellFactory() {
		return SortablePlant.getCellFactory();
	}
}

class VarietyField extends FXField<Entry<String,SeedEntry>,String>{

	public VarietyField() {
		super(Translation.Key.variety, ALLOWED_AGGREGATIONS.NONE, true);
	}

	@Override
	public String getValue(Entry<String, SeedEntry> point) {
		return point.getValue().getVariety();
	}

	@Override
	public Entry<String, SeedEntry> updateValueforPivot(Entry<String, SeedEntry> initialPoint, String pivotedBy) {
		if(Resources.plantList().hasVariety(initialPoint.getValue().getPlant().plant,pivotedBy)) {		
			SeedEntry myEntry = new SeedEntry(new PlantOrUnregistered(initialPoint.getValue().getPlant().plant), pivotedBy,
					initialPoint.getValue().getQuantity(), initialPoint.getValue().getComment(), initialPoint.getValue().getValidFrom(),initialPoint.getValue().getValidTo(), null);	
			return new AbstractMap.SimpleEntry<>(initialPoint.getKey(),myEntry);
		}else {
			return null;
		}

	}

	@Override
	public Callback<TableColumn<Entry<String, SeedEntry>, String>, TableCell<Entry<String, SeedEntry>, String>> getCellFactory() {
		return TextFieldTableCell.forTableColumn();
	}
}

class CommentField extends FXField<Entry<String,SeedEntry>,String>{

	public CommentField() {
		super(Translation.Key.comment, ALLOWED_AGGREGATIONS.NONE, true);
	}

	@Override
	public Callback<TableColumn<Entry<String, SeedEntry>, String>, TableCell<Entry<String, SeedEntry>, String>> getCellFactory() {
		return TextFieldTableCell.forTableColumn();
	}

	@Override
	public String getValue(Entry<String, SeedEntry> point) {
		return point.getValue().getComment();
	}

	@Override
	public Entry<String, SeedEntry> updateValueforPivot(Entry<String, SeedEntry> initialPoint, String pivotedBy) {
		SeedEntry myEntry = new SeedEntry(initialPoint.getValue().getPlant(), initialPoint.getValue().getVariety(),
				initialPoint.getValue().getQuantity(), pivotedBy, initialPoint.getValue().getValidFrom(),initialPoint.getValue().getValidTo(), null);
		return new AbstractMap.SimpleEntry<>(initialPoint.getKey(),myEntry);
	}
	
}

class QuantityUnitField extends FXField<Entry<String,SeedEntry>,String>{

	public QuantityUnitField() {
		super(Translation.Key.unit, ALLOWED_AGGREGATIONS.NONE, true);
	}

	@Override
	public Callback<TableColumn<Entry<String, SeedEntry>, String>, TableCell<Entry<String, SeedEntry>, String>> getCellFactory() {
		return TextFieldTableCell.forTableColumn();
	}

	@Override
	public String getValue(Entry<String, SeedEntry> point) {
		return point.getValue().getQtyUnit();
	}

	@Override
	public Entry<String, SeedEntry> updateValueforPivot(Entry<String, SeedEntry> initialPoint, String pivotedBy) {
		SeedEntry myEntry = new SeedEntry(initialPoint.getValue().getPlant(), initialPoint.getValue().getVariety(),
				new SeedEntry.Quantity(initialPoint.getValue().getQty(),pivotedBy), initialPoint.getValue().getComment(), initialPoint.getValue().getValidFrom(),initialPoint.getValue().getValidTo(), null);
		return new AbstractMap.SimpleEntry<>(initialPoint.getKey(),myEntry);
	}
	
}

class QuantityField extends FXField<Entry<String,SeedEntry>,Double>{

	public QuantityField() {
		super(Translation.Key.quantity, ALLOWED_AGGREGATIONS.SUM, false);
	}
	

	public double getNumericValue(Entry<String,SeedEntry> data) {
		return getValue(data);
	}

	@Override
	public Callback<TableColumn<Entry<String, SeedEntry>, Double>, TableCell<Entry<String, SeedEntry>, Double>> getCellFactory() {
		return TextFieldTableCell.forTableColumn(new DoubleStringConverter());
	}

	@Override
	public Double getValue(Entry<String, SeedEntry> point) {
		return point.getValue().getQuantity().quantity;
	}

	@Override
	public Entry<String, SeedEntry> updateValueforPivot(Entry<String, SeedEntry> initialPoint, Double pivotedBy) {
		/*SeedEntry myEntry = new SeedEntry(initialPoint.getValue().getPlant(), initialPoint.getValue().getVariety(),
				initialPoint.getValue().getQuantity(), initialPoint.getValue().getComment(), initialPoint.getValue().getValidFrom(),initialPoint.getValue().getValidTo(), null);*/	
		SeedEntry myEntry = new SeedEntry(initialPoint.getValue().getPlant(), initialPoint.getValue().getVariety(),
				new SeedEntry.Quantity(pivotedBy,initialPoint.getValue().getQtyUnit()), initialPoint.getValue().getComment(), initialPoint.getValue().getValidFrom(),initialPoint.getValue().getValidTo(), null);
		return new AbstractMap.SimpleEntry<>(initialPoint.getKey(),myEntry);
	}
	

	@Override
	public Collection<FXField<Entry<String,SeedEntry>,?>> includeGranularityWhenAggregating(){
		return Arrays.asList(new FXField[]{SeedListQueryProvider.FIELD_UNIT});
	}

	public Callback<TableColumn<Entry<String,SeedEntry>,Entry<Entry<String,SeedEntry>,Double>>, TableCell<Entry<String,SeedEntry>,Entry<Entry<String,SeedEntry>,Double>>> getAggregatedCellFactory(){
		return TextFieldTableCell.forTableColumn(new StringConverter<Entry<Entry<String,SeedEntry>,Double>>() {

			@Override
			public String toString(Entry<Entry<String,SeedEntry>, Double> object) {
				if(object.getValue()==null) {
					return "";
				}
				String dVal = object.getValue().toString();
				String uVal = (object.getKey().getValue()==null)?"":object.getKey().getValue().getQtyUnit();
				return dVal+" "+uVal;
			}

			@Override
			public Entry<Entry<String,SeedEntry>, Double> fromString(String string) {
				throw new UnsupportedOperationException("This converter is read only");//This is ok ONLY because we are read only, and necesary since we don't have a reference to the entry key anymore.
			}
			
		});
	}
	
}



class ValidFromField extends FXField<Entry<String,SeedEntry>,String>{

	public ValidFromField() {
		super(Translation.Key.valid_from, ALLOWED_AGGREGATIONS.NONE, true);
	}

	@Override
	public Callback<TableColumn<Entry<String, SeedEntry>, String>, TableCell<Entry<String, SeedEntry>, String>> getCellFactory() {
		return TextFieldTableCell.forTableColumn();
	}

	@Override
	public String getValue(Entry<String, SeedEntry> point) {
		return (point.getValue().getValidFrom()==null)?"":point.getValue().getValidFrom().toString();
	}

	@Override
	public Entry<String, SeedEntry> updateValueforPivot(Entry<String, SeedEntry> initialPoint, String pivotedBy) {
		LocalDate date = (pivotedBy==null)?null:LocalDate.parse(pivotedBy);
		SeedEntry myEntry = new SeedEntry(initialPoint.getValue().getPlant(), initialPoint.getValue().getVariety(),
				initialPoint.getValue().getQuantity(), initialPoint.getValue().getComment(), date,initialPoint.getValue().getValidTo(), null);
		return new AbstractMap.SimpleEntry<>(initialPoint.getKey(),myEntry);
	}

}

class ValidToField extends FXField<Entry<String,SeedEntry>,String>{

	public ValidToField() {
		super(Translation.Key.valid_to, ALLOWED_AGGREGATIONS.NONE, true);
	}

	@Override
	public Callback<TableColumn<Entry<String, SeedEntry>, String>, TableCell<Entry<String, SeedEntry>, String>> getCellFactory() {
		return TextFieldTableCell.forTableColumn();
	}

	@Override
	public String getValue(Entry<String, SeedEntry> point) {
		return (point.getValue().getValidTo()==null)?"":point.getValue().getValidTo().toString();
	}

	@Override
	public Entry<String, SeedEntry> updateValueforPivot(Entry<String, SeedEntry> initialPoint, String pivotedBy) {
		LocalDate date = (pivotedBy==null)?null:LocalDate.parse(pivotedBy);
		SeedEntry myEntry = new SeedEntry(initialPoint.getValue().getPlant(), initialPoint.getValue().getVariety(),
				initialPoint.getValue().getQuantity(), initialPoint.getValue().getComment(), initialPoint.getValue().getValidFrom(),date, null);
		return new AbstractMap.SimpleEntry<>(initialPoint.getKey(),myEntry);
	}

}

class ValidDatesField extends FXField<Entry<String,SeedEntry>,String>{

	public ValidDatesField() {
		super(Translation.Key.working_date, ALLOWED_AGGREGATIONS.NONE, true);
	}

	@Override
	public Callback<TableColumn<Entry<String, SeedEntry>, String>, TableCell<Entry<String, SeedEntry>, String>> getCellFactory() {
		return TextFieldTableCell.forTableColumn();
	}

	@Override
	public String getValue(Entry<String, SeedEntry> point) {
		if(point.getValue().getValidTo()==null) {
			return point.getValue().getValidFrom().toString();
		}
		else {
			return point.getValue().getValidFrom()+" -> "+point.getValue().getValidTo();
		}
	}

	@Override
	public Entry<String, SeedEntry> updateValueforPivot(Entry<String, SeedEntry> initialPoint, String pivotedBy) {
		String[] split = pivotedBy.split("\\s");
		LocalDate from = LocalDate.parse(split[0]);
		LocalDate to = (split.length<3)?null:LocalDate.parse(split[2]);
		SeedEntry myEntry = new SeedEntry(initialPoint.getValue().getPlant(), initialPoint.getValue().getVariety(),
				initialPoint.getValue().getQuantity(), initialPoint.getValue().getComment(), from,to, null);
		return new AbstractMap.SimpleEntry<>(initialPoint.getKey(),myEntry);
	}

}