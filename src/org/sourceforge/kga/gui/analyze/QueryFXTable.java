package org.sourceforge.kga.gui.analyze;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.Query;
import org.sourceforge.kga.gui.localization.LocalizedTableView;
import org.sourceforge.kga.translation.Translation;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;

public class QueryFXTable <T> extends LocalizedTableView<T>{
	
	Collection<ForkJoinTask> tasks;

	public QueryFXTable(Query<T,FXField<T,?>> q) {
		super.getItems().addAll(q.computeSortedDisplayedValues());
		for (FXField<T,?> currDef : q.getAggregateBy()) {
			if(q.getPivotBy()!=null && currDef.equals(q.getPivotBy())){
				continue;
			}
			getColumns().add(getTableColumn(currDef));
		}
		if(q.getPivotBy()!=null) {
			addPivotColumns(q,q.getPivotBy());
		}
		tasks = new HashSet<ForkJoinTask>();
	}
	
	private <K extends Comparable<K>> void addPivotColumns(Query<T,FXField<T,?>> q, FXField<T,K> pivotBy){
		List<K> values = q.computeSortedUniqueValues(pivotBy);
		for (K kurr : values) {
			TableColumn<T,K> pCol = getTableColumn(pivotBy,kurr.toString(), t->kurr);
			for(FXField<T,?> measure : q.getToAggregate()) {
				pCol.getColumns().add(getPivotChildColumn(q,pivotBy,measure,kurr));	
			}			
			getColumns().add(pCol);
		}
	}
	
	private synchronized void startParallelTask(Runnable r) {
		ForkJoinTask running = ForkJoinPool.commonPool().submit(r);
	}
	
	public synchronized void waitForTasks() {
		for (ForkJoinTask curr:tasks) {
			curr.join();
		}
		tasks.clear();
	}
	
	private <K extends Comparable<K>,M extends Comparable<M>> TableColumn<T,Entry<T,Double>> getPivotChildColumn(Query<T,FXField<T,?>> q, FXField<T,K> pivotBy, FXField<T,M> measure, K pivotedValue){
		Map<T,Double> computed = new ConcurrentHashMap<T,Double>(super.getItems().size());
		for (T curr : super.getItems()) {
			startParallelTask(()->computed.put(curr,q.computeAggregatedValue(pivotBy.updateValueforPivot(curr, pivotedValue), measure)));
			//computed.put(curr,q.computeAggregatedValue(pivotBy.updateValueforPivot(curr, pivotedValue), measure));
		}
		TableColumn<T,Entry<T,Double>> col = new TableColumn<>(Translation.getCurrent().translate(measure.getFieldName()));
		col.setCellValueFactory(t->new ObservableValue<Entry<T,Double>>() {

			@Override
			public void addListener(InvalidationListener listener) {}

			@Override
			public void removeListener(InvalidationListener listener) {}

			@Override
			public void addListener(ChangeListener<? super Entry<T,Double>> listener) {}

			@Override
			public void removeListener(ChangeListener<? super Entry<T,Double>> listener) {}

			@Override
			public Entry<T,Double> getValue() {
				waitForTasks();
				return new AbstractMap.SimpleEntry<>(t.getValue(),computed.get(t.getValue()));//q.computeAggregatedValue(pivotBy.updateValueforPivot(t.getValue(), pivotedValue), measure);
			}
		});
		col.setCellFactory(measure.getAggregatedCellFactory());
		return col;
	}
	
	private <K extends Comparable<K>> TableColumn<T,K> getTableColumn(FXField<T,K> def){
		return getTableColumn(def,Translation.getCurrent().translate(def.getFieldName()),t->def.getValue(t));
	}
	
	private <K extends Comparable<K>> TableColumn<T,K> getTableColumn(FXField<T,K> def, String name, Callback<T,K> cellValueFactoryGetValue){
		TableColumn<T,K> col = new TableColumn<>(name);
		col.setCellValueFactory(t->new ObservableValue<K>() {

			@Override
			public void addListener(InvalidationListener listener) {}

			@Override
			public void removeListener(InvalidationListener listener) {}

			@Override
			public void addListener(ChangeListener<? super K> listener) {}

			@Override
			public void removeListener(ChangeListener<? super K> listener) {}

			@Override
			public K getValue() {return cellValueFactoryGetValue.call(t.getValue());}//def.getValue(t.getValue());}
			
		});
		col.setCellFactory(def.getCellFactory());
		return col;
	}

}


/**
(t->new ObservableValue<LocalDate>(){
public void addListener(InvalidationListener listener) {
}

@Override
public void removeListener(InvalidationListener listener) {
}

@Override
public void addListener(ChangeListener<? super LocalDate> listener) {
}

@Override
public void removeListener(ChangeListener<? super LocalDate> listener) {
}

@Override
public LocalDate getValue() {
	return t.getValue().getDate();
}
});*/