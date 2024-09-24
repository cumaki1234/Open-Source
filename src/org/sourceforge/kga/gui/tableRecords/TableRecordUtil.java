package org.sourceforge.kga.gui.tableRecords;

import java.util.Set;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.gui.actions.PlantTableCell;
import org.sourceforge.kga.translation.Translation;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class TableRecordUtil {
	
	public static <K> TableColumn<K,String> addStringColumn(TableView<K> table, Translation.Key columnName, String property, EventHandler<CellEditEvent<K,String>> handler) {
		TableColumn<K,String> descCol = new TableColumn<K,String>(Translation.getCurrent().translate(columnName));
		descCol.setCellValueFactory(new PropertyValueFactory<K,String>(property));
		descCol.setCellFactory(TextFieldTableCell.forTableColumn());
		descCol.setOnEditCommit( handler);
		table.getColumns().add(descCol);
		descCol.setMinWidth(10);
		if(handler==null) {
			descCol.setEditable(false);
		}
		return descCol;
	}
	
	public static <K> TableColumn<K,String> addStringComboColumn(TableView<K> table, Translation.Key columnName, String property, EventHandler<CellEditEvent<K,String>> handler, ObservableList<String> options, boolean editable) {
		return addStringComboColumn(table,columnName,new PropertyValueFactory<K,String>(property), handler,options,editable);
	}
	public static <K> TableColumn<K,String> addStringComboColumn(TableView<K> table, Translation.Key columnName, Callback<CellDataFeatures<K,String>, ObservableValue<String>> propertyGetter, EventHandler<CellEditEvent<K,String>> handler, ObservableList<String> options, boolean editable) {
		TableColumn<K,String> descCol = new TableColumn<K,String>(Translation.getCurrent().translate(columnName));
		descCol.setCellValueFactory(propertyGetter);
		if (editable) {
			descCol.setCellFactory(col->{
				ComboBoxTableCell<K, String> ct= new ComboBoxTableCell<>(options);
		           ct.setComboBoxEditable(true);
		           return ct;
			});
		}
		else {
			descCol.setCellFactory(ComboBoxTableCell.forTableColumn(options));
		}
		/*descCol.setCellFactory(col -> {
	        TableCell<K, String> c = new TableCell<>();
	        final ComboBox<String> comboBox = new ComboBox<>(options);
	        c.itemProperty().addListener((observable, oldValue, newValue) -> {
	        	handler.handle(new CellEditEvent<K,String>(table,new TablePosition<K,String>(table,c.getTableRow().getIndex(),descCol),TableColumn.editCommitEvent(),newValue));
	        });
	        //c.graphicProperty().bind(Bindings.when(c.emptyProperty()).then((Node) null).otherwise(comboBox));
	        return c;
	    });*/
		descCol.setOnEditCommit( handler);
		table.getColumns().add(descCol);
		descCol.setMinWidth(10);
		return descCol;
	}

	public static <K> TableColumn<K,Double> addDoubleColumn(TableView<K> table, Translation.Key columnName, String property, EventHandler<CellEditEvent<K,Double>> handler) {
		return addDoubleColumn(table,Translation.getCurrent().translate(columnName),new PropertyValueFactory<K,Double>(property),handler);
	}
	public static <K> TableColumn<K,Double> addDoubleColumn(TableView<K> table, String columnName, Callback<CellDataFeatures<K,Double>, ObservableValue<Double>> propertyGetter, EventHandler<CellEditEvent<K,Double>> handler) {
		TableColumn<K,Double> descCol = new TableColumn<K,Double>(columnName);
		descCol.setCellValueFactory(propertyGetter);
		descCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		descCol.setOnEditCommit( handler);
		table.getColumns().add(descCol);
		descCol.setMinWidth(10);
		return descCol;
	}
	
	public static <K> TableColumn<K,PlantOrUnregistered> addPlantColumn(TableView<K> table, Translation.Key columnName, String property, EventHandler<CellEditEvent<K,PlantOrUnregistered>> handler) {
		TableColumn<K,PlantOrUnregistered> descCol = new TableColumn<K,PlantOrUnregistered>(Translation.getCurrent().translate(columnName));
		descCol.setCellValueFactory(new PropertyValueFactory<K,PlantOrUnregistered>(property));
		descCol.setCellFactory(PlantTableCell.getCallBack());
		descCol.setOnEditCommit( handler);
		table.getColumns().add(descCol);
		descCol.setMinWidth(10);
		if(handler==null) {
			descCol.setEditable(false);
		}
		return descCol;
	}
	
	public static <K> TableColumn<K,Integer> addIntColumn(TableView<K> table, Translation.Key columnName, String property, EventHandler<CellEditEvent<K,Integer>> handler) {
		return addIntColumn(table,columnName,property,handler,null);
	}
	public static <K> TableColumn<K,Integer> addIntColumn(TableView<K> table, Translation.Key columnName, String property, EventHandler<CellEditEvent<K,Integer>> handler, Set<Integer> options) {
		TableColumn<K,Integer> descCol = new TableColumn<K,Integer>(Translation.getCurrent().translate(columnName));
		descCol.setCellValueFactory(new PropertyValueFactory<K,Integer>(property));
		if(options==null||options.size()==0) {
			descCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		}
		else {
			descCol.setCellFactory(ComboBoxTableCell.forTableColumn(new IntegerStringConverter(),options.toArray(new Integer[options.size()])));
			//descCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		}

		/*descCol.setCellFactory(new Callback<TableColumn<K, Integer>, TableCell<K, Integer>>() {

	        @Override
	        public TableCell<K, Integer> call(TableColumn<K, Integer> param) {
	            TableCell<K,Integer> toRet =  new TableCell<K, Integer>() {

	                private final TextFormatter<Integer> formatter;
	                private final TextField textField;

	                {
	                    textField = new TextField();
	                    textField.getStyleClass().add("text-field-table-cell");
	                    //removes the border around the textfield so that it matches
	                    textField.setStyle("-fx-background-color: -fx-control-inner-background;");
	                    formatter = new TextFormatter<>(new IntegerStringConverter(), null, change -> {
	                	    if (change.isAdded()) {
	                	         change = change.getControlNewText().matches("^\\d*$") ? change : null;
	                	    }
	                	    return change;
	                	});
	                    textField.setTextFormatter(formatter);
	                    formatter.valueProperty().addListener((o, oldValue, newValue) -> {
	                        K water = (K) getTableRow().getItem();
	                        ObservableValue<Integer> currVal = descCol.getCellValueFactory().call(new CellDataFeatures<K,Integer>(table,descCol,water));
	                        if (currVal== null && newValue!=null || currVal!=null&&!Objects.equals(currVal.getValue(), newValue)) {
	                        	handler.handle(new CellEditEvent<K,Integer>(table,new TablePosition<K,Integer>(table,getTableRow().getIndex(),descCol),TableColumn.editCommitEvent(),newValue));
	                        }
	                    });
	                }

	                @Override
	                protected void updateItem(Integer value, boolean empty){
	                    super.updateItem(value, empty);
	                    if (empty){
	                        setGraphic(null);
	                    } else {
	                        setGraphic(textField);
	                        formatter.setValue(value);
	                    }
	                }
	            };
	            return toRet;
	        }
	    });*/
		descCol.setOnEditCommit( handler);
		table.getColumns().add(descCol);
		descCol.setMinWidth(10);
		return descCol;
	}
	
	public static <K> TableColumn<K,Set<Plant>> addMultiselectPlantColumn(TableView<K> table, Translation.Key columnName, String property, EventHandler<CellEditEvent<K,Set<Plant>>> handler) {
		TableColumn<K,Set<Plant>> descCol = new TableColumn<K,Set<Plant>>(Translation.getCurrent().translate(columnName));
		descCol.setCellValueFactory(new PropertyValueFactory<K,Set<Plant>>(property));
		descCol.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Set<Plant>>() {
			@Override
			public String toString(Set<Plant> object) {
				if(object==null || object.size()==0) {
					return Translation.getCurrent().all();
				}
				String list = "";
				for(Plant curr : object) {
					if(list.length()>0) {
						list+=", ";
					}
					list += Translation.getCurrent().translate(curr);
				}
				return list;
			}

			@Override
			public Set<Plant> fromString(String string) {
				throw new UnsupportedOperationException();
			}
			
		}));
		descCol.setOnEditCommit( handler);
		table.getColumns().add(descCol);
		descCol.setMinWidth(10);
		return descCol;
	}

}
