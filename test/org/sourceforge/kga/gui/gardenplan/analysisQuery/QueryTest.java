package org.sourceforge.kga.gui.gardenplan.analysisQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sourceforge.kga.JavaFXTest;
import org.sourceforge.kga.KGATest;
import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.Query;
import org.sourceforge.kga.analyze.QueryField;
import org.sourceforge.kga.analyze.QueryProvider;
import org.sourceforge.kga.translation.Translation.Key;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public abstract class QueryTest <T> extends JavaFXTest {
	QueryProvider <T, FXField<T,?>> provider;
	Query<T,FXField<T,?>> initialQuery;
	
	@BeforeEach
	public void setupQuery(){
		provider=getProvider();
		 initialQuery = provider.getDefaultQuery();
	}
	
	public abstract QueryProvider <T, FXField<T,?>> getProvider();
	
	protected T getExampleRow(){
		return provider.getDefaultQuery().computeSortedDisplayedValues().get(0);
	}
	
	protected <K extends Comparable<K>> T updatePivot(T row,FXField<T, K> field, K newVal){
		T updated = field.updateValueforPivot(row, newVal);
		assertEquals(newVal,field.getValue(updated));
		assertEquals(0,newVal.compareTo(field.getValue(updated)));
		return updated;
	}
	
	@Test
	public void test_basicFieldTests() {
		for (FXField<T,?> field : provider.getAvailableFields()) {
			if(!field.getFieldName().equals(Key.cost)) {//this test does not currently put non-0 values in cost....
				testField(field);
			}
		}
	}
	
	private <K extends Comparable<K>> void testField(FXField<T,K> field ) {
		assertTrue(field.canAggregateBy() || field.getAllowedAggregations()!=QueryField.ALLOWED_AGGREGATIONS.NONE);
		TableView<T> view = new TableView<>();
		
		TableColumn<T,Entry<T,Double>> agg = new TableColumn<T,Entry<T,Double>>();
		view.getColumns().add(agg);
		agg.setCellFactory(field.getAggregatedCellFactory());
		
		TableColumn<T,K> direct = new TableColumn<T,K>();
		view.getColumns().add(direct);
		direct.setCellFactory(field.getCellFactory());
		
		view.getItems().add(getExampleRow());
		
		assertNotEquals(null,field.getFieldName());
		
		if(field.getAllowedAggregations()!=QueryField.ALLOWED_AGGREGATIONS.NONE) {
			assertNotEquals(0,field.getNumericValue(getExampleRow()));
		}
		
		assertNotEquals(null,field.getValue(getExampleRow()));
	}
}
