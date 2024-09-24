package org.sourceforge.kga.gui.tableRecords;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.sourceforge.kga.JavaFXTest;
import org.sourceforge.kga.KGATest;
import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableColumn.CellEditEvent;

public abstract class tableRecordTest  <T> extends JavaFXTest{
	
	protected void ui_addRow(RecordTable<T> t) {
		int oldSize = t.table.getItems().size();
		t.addBut.getOnAction().handle(null);
		oldSize++;
		assertEquals(oldSize,t.table.getItems().size());	
	}
	
	protected TableColumn<T,?> ui_getCol(RecordTable<T> table, int index) {
		return (TableColumn<T,?>)table.table.getColumns().get(index);
	}
	
	protected TableColumn<T,?> ui_getCol(RecordTable<T> table, Enum index) {
		return ui_getCol(table,index.ordinal());
	}
	
	protected TableColumn<T,String> ui_getStringCol(RecordTable<T> table, int index) {
		return (TableColumn<T,String>)ui_getCol(table,index);
	}
	
	protected TableColumn<T,String> ui_getStringCol(RecordTable<T> table, Enum index) {
		return ui_getStringCol(table,index.ordinal());
	}
	
	protected TableColumn<T,Double> ui_getDoubleCol(RecordTable<T> table, int index) {
		return (TableColumn<T,Double>)ui_getCol(table,index);
	}
	
	protected TableColumn<T,Double> ui_getDoubleCol(RecordTable<T> table, Enum index) {
		return ui_getDoubleCol(table,index.ordinal());
	}
	
	protected <U> void ui_editCol(RecordTable<T> table, U val, int row, int colIndex) {
		TableColumn<T,U> col=(TableColumn<T,U>)ui_getCol(table,colIndex);
		ui_editCol(table,val,row,col);
	}
	
	protected <U> void ui_editCol(RecordTable<T> table, U val, int row, Enum colIndex) {
		ui_editCol(table,val,row,colIndex.ordinal());
	}
	
	protected <U> void ui_editCol(RecordTable<T> table, U val, int row, TableColumn<T,U> col) {
		col.getOnEditCommit().handle(new CellEditEvent<T,U>(table.table,new TablePosition(table.table,row,col),null,val));
	}
	
	protected int ui_rowCount(RecordTable<T> table) {
		return table.table.getItems().size();
	}
	
	protected T ui_getRow(RecordTable<T> table, int index) {
		return table.table.getItems().get(index);
	}

}
