package org.sourceforge.kga.gui.analyze;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.translation.Translation;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class FieldSelectionList <T> extends ListView<FXField<T,?>>{
	
	public FieldSelectionList(Callback<FXField<T,?>,ObservableValue<Boolean>> onChange) {
		super.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		Map <String,FXField<T,?>> fieldFinder = new HashMap<>();
	
		Callback<ListView<FXField<T,?>>, ListCell<FXField<T,?>>> factory = CheckBoxListCell.forListView(onChange, new StringConverter<FXField<T,?>>() {

			@Override
			public String toString(FXField<T,?> object) {
				String name = Translation.getCurrent().translate(object.getFieldName());
				fieldFinder.put(name, object);
				return name;
			}

			@Override
			public FXField<T,?> fromString(String string) {
				return fieldFinder.get(string);
			}
			
		});
		setCellFactory(factory);
		super.setMinWidth(Control.USE_PREF_SIZE);
		super.setFixedCellSize(24);
	}
	
	public void updateFields(Collection<FXField<T,?>> fields) {
		super.getItems().clear();
		super.getItems().addAll(fields);
        setPrefHeight(24 * fields.size()+2);	
		
	}

}
