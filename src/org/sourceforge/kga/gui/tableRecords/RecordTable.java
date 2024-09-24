package org.sourceforge.kga.gui.tableRecords;

import java.util.Comparator;

import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.gui.actions.PlantTableCell;
import org.sourceforge.kga.gui.localization.LocalizedTableView;
import org.sourceforge.kga.translation.Translation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class RecordTable <T> extends BorderPane {
	public Button addBut;
	public Button delBut;
	public TableView<T> table;
	RecordTableProvider <T> provider;
	ObservableList<T> entries;
	private HBox leftBottomSection;

	
	public void updateView() {
		entries = FXCollections.observableArrayList(provider.getAllRecords());
		table.setItems( entries);	
		for (TableColumn<?,?> curr : table.getColumns()) {
			curr.setVisible(false);
			curr.setVisible(true);
		}
	}
	

	public static TableColumn getNextColumn(TableView<?> table, boolean forward) {
		ObservableList<TablePosition> tp = table.getSelectionModel().getSelectedCells();
		if(tp.size()!=1) {
			return null;
		}
		int col = tp.get(0).getColumn();
		int delta = (forward)?1:-1;
		int target=col+delta;
		if(target<0 || target>=table.getColumns().size()) {
			return null;
		}
		else {
			return table.getColumns().get(target);
		}
	}
	

	private TableColumn getNextColumn(boolean forward) {
		return getNextColumn(table,forward);
	}
	
	public static <T> void moveToNextTab(TableView<T> table, boolean forward) {
		int delta = (forward)?1:-1;
		ObservableList<TablePosition> tp = table.getSelectionModel().getSelectedCells();
		if(tp.size()!=1) {
			return;
		}
		int col = tp.get(0).getColumn();
		int target=col+delta;
		if(target<0 || target>=table.getColumns().size()) {
			return;
		}
		else {
			table.layout();
			table.getSelectionModel().select(tp.get(0).getRow(),table.getColumns().get(target));
			Platform.runLater(()->{
			table.edit(tp.get(0).getRow(),table.getColumns().get(target));
			});
		}
	}
	
	private void handleTab(boolean forward) {
		moveToNextTab(table,forward);
	}

	
	public RecordTable(RecordTableProvider <T> provider){

		table = new LocalizedTableView<T>();
		table.getSelectionModel().setCellSelectionEnabled(true);
		/*table.setOnKeyPressed(new EventHandler<KeyEvent>() {

	        @Override
	        public void handle(KeyEvent event) {
	        	System.out.println(event.getCode().getName());	 
	        	if(event.getCode() == KeyCode.TAB) {
	        		handleTab(!event.isShiftDown());
	        		
	        	}
	        }
	    });*/
		this.provider=provider;
		updateView();
		 BorderPane bottomsection = new BorderPane();
	     leftBottomSection = new HBox();
	     bottomsection.setLeft(leftBottomSection);
	     table.setEditable(true);
	     provider.AddColumns(table);
	     
	     BorderPane box = new BorderPane();
	        box.setCenter(table);
	        HBox buttons = new HBox();
	        addBut=new Button(Translation.getCurrent().add());
	        addBut.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					entries.add(provider.addNew());
				}
	        });
	        buttons.getChildren().add(addBut);

	        delBut=new Button(Translation.getCurrent().delete());
	        delBut.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					T toRemove=table.getSelectionModel().getSelectedItem();
					entries.remove(toRemove);
					provider.remove(toRemove);
				}
	        });
	        table.getSelectionModel().selectedItemProperty().addListener((w,o,n)->{
	        	delBut.setDisable(n==null);
	        	});
	        delBut.setDisable(true);
	        buttons.getChildren().add(delBut);

	        HBox.setMargin(addBut, new Insets(5));
	        HBox.setMargin(delBut, new Insets(5));
	                
	        bottomsection.setRight(buttons);
	        box.setBottom(bottomsection);
			this.setCenter(box);
	}
	
	public void addToLeftBottomScetion(Node n) {
        HBox.setMargin(n, new Insets(5));
		leftBottomSection.getChildren().add(n);
	}

}
