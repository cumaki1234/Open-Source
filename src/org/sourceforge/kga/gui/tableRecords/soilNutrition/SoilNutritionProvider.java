package org.sourceforge.kga.gui.tableRecords.soilNutrition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sourceforge.kga.gui.tableRecords.ComboBoxCell;
import org.sourceforge.kga.gui.tableRecords.DatePickerCell;
import org.sourceforge.kga.gui.tableRecords.RecordTable;
import org.sourceforge.kga.gui.tableRecords.RecordTableProvider;
import org.sourceforge.kga.translation.Translation;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

public class SoilNutritionProvider implements RecordTableProvider<SoilNutritionEntry> {
	Collection<SoilNutritionEntry> entries;
	
	public SoilNutritionProvider(Collection<SoilNutritionEntry> items) {
		entries = items;
	}

	@Override
	public Collection<SoilNutritionEntry> getAllRecords() {
		List<SoilNutritionEntry> entries = new ArrayList<SoilNutritionEntry>(this.entries);
		return entries;
	}
	
	private class nutritionCol extends TableColumn<SoilNutritionEntry,String>{
		public nutritionCol(String name, String propName) {
			super(name);
			try {
				String nameHelper = "et"+propName.substring(0, 1).toUpperCase() + propName.substring(1);

				Method getter = SoilNutritionEntry.class.getMethod("g"+nameHelper, null);
				Method setter = SoilNutritionEntry.class.getMethod("s"+nameHelper, Double.class);
			setCellValueFactory(s->{
				try {
					return new ReadOnlyStringWrapper(s.getValue().fertNumberToString((Double)getter.invoke(s.getValue(),null)));
				} catch (Exception e1) {
					throw new Error(e1);
				} 
			});
			setCellFactory(TextFieldTableCell.forTableColumn());//TabbableTextFieldTableCell.tabableTableColumn(this));
			setOnEditCommit(
					new EventHandler<TableColumn.CellEditEvent<SoilNutritionEntry, String>>() {
						@Override public void handle(TableColumn.CellEditEvent<SoilNutritionEntry, String> t) {
							try {
								Double fertNumber = t.getRowValue().stringTofertNumber(t.getNewValue());
								setter.invoke(t.getRowValue(), fertNumber);
							}
							catch (Exception e) {
								e.printStackTrace();
								t.consume();
							}
							finally {
								t.getTableView().refresh();
								RecordTable.moveToNextTab(getTableView(), true);
							}
						}
					});
			}
			catch (Exception e) {
				throw new Error(e);
			}
			setMinWidth(Control.USE_PREF_SIZE);
		}
	}

	@Override
	public void AddColumns(TableView<SoilNutritionEntry> table) {

		TableColumn<SoilNutritionEntry,LocalDate> dateCol = new TableColumn<SoilNutritionEntry,LocalDate>(Translation.getCurrent().working_date());
		dateCol.setCellValueFactory(t->new ObservableValue<LocalDate>(){
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
		});
		
		dateCol.setCellFactory(w-> {DatePickerCell picker = new DatePickerCell<SoilNutritionEntry>();
		//picker.setOnAction(w->{w.});
		return picker;		
		});
		dateCol.setMinWidth(100);

		dateCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<SoilNutritionEntry, LocalDate>>() {
					@Override public void handle(TableColumn.CellEditEvent<SoilNutritionEntry, LocalDate> t) {
						if (t==null)
							t.getRowValue().setDate(null);
						else
							t.getRowValue().setDate(t.getNewValue());
					}
				});		
		table.getColumns().add(dateCol);

		table.getColumns().add(new nutritionCol(Translation.getCurrent().nitrogen(),"nitrogen"));
		table.getColumns().add(new nutritionCol(Translation.getCurrent().phosphorus(),"phosphorus"));
		table.getColumns().add(new nutritionCol(Translation.getCurrent().potassium(),"potassium"));
		table.getColumns().add(new nutritionCol(Translation.getCurrent().magnesium(),"magnesium"));
		table.getColumns().add(new nutritionCol(Translation.getCurrent().calcium(),"calcium"));
		table.getColumns().add(new nutritionCol(Translation.getCurrent().zinc(),"zinc"));
		table.getColumns().add(new nutritionCol(Translation.getCurrent().ph(),"PH"));
		
		TableColumn<SoilNutritionEntry,Double> phCol = new TableColumn<SoilNutritionEntry,Double>(Translation.getCurrent().ph());
		phCol.setCellValueFactory(new PropertyValueFactory<SoilNutritionEntry,Double>("ph"));
		phCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		phCol.setOnEditCommit(t-> {t.getRowValue().setPH(t.getNewValue());});
		phCol.setMinWidth(180);
		
		TableColumn<SoilNutritionEntry,String> commentCol = new TableColumn<SoilNutritionEntry,String>(Translation.getCurrent().comment());
		commentCol.setCellValueFactory(new PropertyValueFactory<SoilNutritionEntry,String>("comment"));
		commentCol.setCellFactory(TextFieldTableCell.forTableColumn());
		commentCol.setOnEditCommit( t -> {t.getRowValue().setComment(t.getNewValue());});
		table.getColumns().add(commentCol);
		commentCol.setMinWidth(300);
		
	}

	@Override
	public SoilNutritionEntry addNew() {
		SoilNutritionEntry ne = new SoilNutritionEntry();
		entries.add(ne);
		return ne;
	}

	@Override
	public void remove(SoilNutritionEntry toRemove) {
		entries.remove(toRemove);
	}

}
