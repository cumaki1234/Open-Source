package org.sourceforge.kga.analyze;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Map.Entry;

import org.sourceforge.kga.translation.Translation.Key;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

public abstract class FXField <T,K extends Comparable<K>> extends QueryField<T,FXField<T,?>, K> {
	
	 
	public FXField(Key name, ALLOWED_AGGREGATIONS t, boolean allowAggregateBy) {
		super(name,t, allowAggregateBy);
	}
	
	public abstract Callback<TableColumn<T,K>, TableCell<T,K>> getCellFactory();
	
	public Callback<TableColumn<T,Entry<T,Double>>, TableCell<T,Entry<T,Double>>> getAggregatedCellFactory(){
		return DoubleCellFactory();
	}

	public static <T> Callback<TableColumn<T,Entry<T,Double>>, TableCell<T,Entry<T,Double>>> DoubleCellFactory() {
		return TextFieldTableCell.forTableColumn(new StringConverter<Entry<T,Double>>() {

			@Override
			public String toString(Entry<T, Double> object) {
				if(object.getValue()==null) {
					return null;
				}else {
					return object.getValue().toString();
				}
			}

			@Override
			public Entry<T, Double> fromString(String string) {
				throw new UnsupportedOperationException("This converter is read only");//This is ok ONLY because we are read only, and necesary since we don't have a reference to the entry key anymore.
			}
			
		});
	}

	public static <T> Callback<TableColumn<T,Entry<T,Double>>, TableCell<T,Entry<T,Double>>> DoubleAsCurrencyCellFactory() {
		return TextFieldTableCell.forTableColumn(new StringConverter<Entry<T,Double>>() {

			@Override
			public String toString(Entry<T, Double> object) {
				if(object.getValue()==null || object.getValue()==0) {
					return "-";
				}else {
					
					return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(object.getValue());
				}
			}

			@Override
			public Entry<T, Double> fromString(String string) {
				throw new UnsupportedOperationException("This converter is read only");//This is ok ONLY because we are read only, and necesary since we don't have a reference to the entry key anymore.
			}
			
		});
	}
	
	public static <T> Callback<TableColumn<T,Entry<T,Double>>, TableCell<T,Entry<T,Double>>> DoubleAsIntCellFactory() {
		return TextFieldTableCell.forTableColumn(new StringConverter<Entry<T,Double>>() {

			@Override
			public String toString(Entry<T,Double> object) {
				if(object.getValue()==null) {
					return null;
				}else {
					return Integer.toString((int)object.getValue().doubleValue());
				}
			}

			@Override
			public Entry<T,Double> fromString(String string) {
				throw new UnsupportedOperationException("This converter is read only");//This is ok ONLY because we are read only, and necesary since we don't have a reference to the entry key anymore.
			}
			
		});
	}
	
	/*private static interface FormattedNumberConverter {
		public String toString(T data, Double aggregatedNumber);

		public Double fromString(T data, String number);
	}
	
	public static <T> Callback<TableColumn<T,Double>, TableCell<T,Double>> FormattedNumberCellFactory() {
	}*/

}
