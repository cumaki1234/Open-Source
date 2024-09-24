package org.sourceforge.kga.gui.components;


import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class LabelledClearableComboBox <T> extends AbstractLabelledControl<ComboBox<T>>{
	
	public LabelledClearableComboBox(String label) {
		super(label, new ComboBox<T>());
		
		Button clearBut = new Button("X");
		clearBut.setOnAction(e->myControl.getSelectionModel().clearSelection());
		myControl.valueProperty().addListener((observable, oldValue, newValue) -> {
			clearBut.setDisable(newValue == null);
        });
		clearBut.setDisable(true);
		addSideButton(clearBut,1);
	}
	
	public void setCellFactory(Callback<ListView<T>, ListCell<T>> value) {
		myControl.setCellFactory(value);
	}
	
	public ObservableList<T> getItems() {
		return myControl.getItems();
	}
	
	public ObjectProperty<T> valueProperty() {
		return myControl.valueProperty();
	}
	
	public void setOnAction(EventHandler<ActionEvent> value) {
		myControl.setOnAction(value);
	}
	
	public SingleSelectionModel<T> getSelectionModel() {
		return myControl.getSelectionModel();
	}
	
	public void setConverter(StringConverter<T> t) {
		myControl.setConverter(t);
	}
	
	public T getValue() {
		return myControl.getValue();
	}
	
	public void setValue(T value) {
		myControl.setValue(value);
	}
	
	public void setButtonCell(ListCell<T> value) {
		myControl.setButtonCell(value);;
	}

}
