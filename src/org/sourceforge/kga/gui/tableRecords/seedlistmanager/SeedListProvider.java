package org.sourceforge.kga.gui.tableRecords.seedlistmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.gui.actions.PlantTableCell;
import org.sourceforge.kga.gui.tableRecords.RecordTableProvider;
import org.sourceforge.kga.translation.Translation;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

public class SeedListProvider implements RecordTableProvider<SeedEntry> {
	SeedList myList;

	public SeedListProvider(SeedList l) {
		myList=l;
	}

	@Override
	public Collection<SeedEntry> getAllRecords() {
		List<SeedEntry> entries = new ArrayList<SeedEntry>(myList.seedsEntries);
		entries.sort(new Comparator<SeedEntry>() {

			@Override
			public int compare(SeedEntry o1, SeedEntry o2) {
				if(o1.getPlant().compareTo(o2.getPlant())==0) {
					return o1.getVariety().toUpperCase().compareTo(o2.getVariety().toUpperCase());
				}
				else {
					return o1.getPlant().compareTo(o2.getPlant());
				}
			}

		});
		return entries;
	}

	@Override
	public void AddColumns(TableView table) {

		TableColumn<SeedEntry,PlantOrUnregistered> plantCol = new TableColumn<SeedEntry,PlantOrUnregistered>(Translation.getCurrent().name());
		plantCol.setCellValueFactory(new PropertyValueFactory<SeedEntry,PlantOrUnregistered>("plant"));
		plantCol.setCellFactory(PlantTableCell.getCallBack(/*, new PlantTableCell.plantCallback() {

		@Override
		public void selectedPlantChanged(PlantOrUnregistered newVal) {
			((SeedEntry)t.getTableView().getItems().get(
                  t.getTablePosition().getRow())).setType(t.getNewValue());
		}
	}*/));
		plantCol.setMinWidth(300);

		plantCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<SeedEntry, PlantOrUnregistered>>() {
					@Override public void handle(TableColumn.CellEditEvent<SeedEntry, PlantOrUnregistered> t) {
						((SeedEntry)t.getTableView().getItems().get(
								t.getTablePosition().getRow())).setType(t.getNewValue());
					}
				});		
		table.getColumns().add(plantCol);


		TableColumn<SeedEntry,String> VarietyCol = new TableColumn<SeedEntry,String>(Translation.getCurrent().variety());
		VarietyCol.setCellValueFactory(new PropertyValueFactory<SeedEntry,String>("variety"));
		VarietyCol.setCellFactory(TextFieldTableCell.forTableColumn());/*w->new TextFieldTableCell<SeedEntry,String>() {

	});*/
		table.getColumns().add(VarietyCol);

		VarietyCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<SeedEntry, String>>() {
					@Override public void handle(TableColumn.CellEditEvent<SeedEntry, String> t) {
						((SeedEntry)t.getTableView().getItems().get(
								t.getTablePosition().getRow())).setVariety(t.getNewValue());
					}
				});
		VarietyCol.setMinWidth(180);

		TableColumn<SeedEntry,SeedEntry.Quantity> quantityCol = new TableColumn<SeedEntry,SeedEntry.Quantity>(Translation.getCurrent().quantity());
		quantityCol.setCellValueFactory(new PropertyValueFactory<SeedEntry,SeedEntry.Quantity>("quantity"));

		TableColumn<SeedEntry,Double> qtyCol = new TableColumn<SeedEntry,Double>("#");
		qtyCol.setCellValueFactory(new PropertyValueFactory<SeedEntry,Double>("qty"));
		quantityCol.getColumns().add(qtyCol);
		qtyCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter() ));
		qtyCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<SeedEntry, Double>>() {
					@Override public void handle(TableColumn.CellEditEvent<SeedEntry, Double> t) {
						((SeedEntry)t.getTableView().getItems().get(
								t.getTablePosition().getRow())).setQuantity(t.getNewValue());
					}
				});

		qtyCol.setMinWidth(20);

		TableColumn<SeedEntry,String> unitCol = new TableColumn<SeedEntry,String>(Translation.getCurrent().unit());
		unitCol.setCellValueFactory(new PropertyValueFactory<SeedEntry,String>("qtyUnit"));
		quantityCol.getColumns().add(unitCol);
		unitCol.setCellFactory(ComboBoxTableCell.forTableColumn(Translation.getCurrent().measurement_unit_grams(),Translation.getCurrent().measurement_unit_pieces()));
		unitCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<SeedEntry, String>>() {
					@Override public void handle(TableColumn.CellEditEvent<SeedEntry, String> t) {
						((SeedEntry)t.getTableView().getItems().get(
								t.getTablePosition().getRow())).setQuantityUnits(t.getNewValue());
					}
				});
		unitCol.setMinWidth(30);

		table.getColumns().add(quantityCol);
		TableColumn<SeedEntry,String> commentCol = new TableColumn<SeedEntry,String>(Translation.getCurrent().comment());
		commentCol.setCellValueFactory(new PropertyValueFactory<SeedEntry,String>("comment"));
		commentCol.setCellFactory(TextFieldTableCell.forTableColumn());
		commentCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<SeedEntry, String>>() {
					@Override public void handle(TableColumn.CellEditEvent<SeedEntry, String> t) {
						((SeedEntry)t.getTableView().getItems().get(
								t.getTablePosition().getRow())).setComment(t.getNewValue());
					}
				});
		table.getColumns().add(commentCol);
		commentCol.setMinWidth(180);

	}

	@Override
	public SeedEntry addNew() {
		return myList.add(PlantTableCell.getSortedPlants().get(0).plant,myList.getDate());
	}

	@Override
	public void remove(SeedEntry toRemove) {
		myList.remove(toRemove, myList.getDate());		
	}

}
