package org.sourceforge.kga.gui.tableRecords;

import java.util.Collection;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public interface RecordTableProvider <T> {
	
	public enum Column_TYPE {PLANT,STRING,DOUBLE,QUANTITY};
	
	public Collection<T> getAllRecords();
	
	public void AddColumns(TableView<T> table);
	
	public T addNew();
	
	public void remove(T toRemove);

}
