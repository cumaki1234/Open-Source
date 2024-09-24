package org.sourceforge.kga.gui.tableRecords;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import org.sourceforge.kga.translation.Translation;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;

public class ComboBoxCell<S> extends TableCell<S, String> {

	private ComboBox<String> picker;

	public ComboBoxCell(Collection<String> options) {
		createDatePicker();
		setGraphic(picker);
	}

	public ComboBoxCell(Enum[] options) {
		createDatePicker();
		setGraphic(picker);
	}
	
	static Collection<String> enumToStringArray(Enum [] asEnum) {
		String [] asString = new String[asEnum.length];
		for(int i=0;i<asEnum.length;i++)
			asString[i]=Translation.getCurrent().translate(asEnum[i]);
		Arrays.sort(asString);
		return Arrays.asList(asString);
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setPickerValue(item);
			setGraphic(this.picker);
		}
	}

	private void setPickerValue(String value) {
		picker.setValue(value);
	}

	private void createDatePicker() {
		this.picker = new ComboBox<String>();
		picker.setPromptText("jj/mm/aaaa");
		picker.setEditable(true);

		picker.setOnAction(new EventHandler() {
			public void handle(Event t) {
				String val = picker.getValue();
				commitEdit(val);
			}
		});

		setAlignment(Pos.CENTER);
	}

}