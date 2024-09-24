package org.sourceforge.kga.gui.tableRecords;

import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

public class DatePickerCell<S> extends TableCell<S, LocalDate> {

	private DatePicker datePicker;

	public DatePickerCell() {
		createDatePicker();
		setGraphic(datePicker);
	}

	@Override
	public void updateItem(LocalDate item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setDatepikerDate(item);
			setGraphic(this.datePicker);
		}
	}

	private void setDatepikerDate(LocalDate value) {
		datePicker.setValue(value);
	}

	private void createDatePicker() {
		this.datePicker = new DatePicker();
		datePicker.setPromptText("jj/mm/aaaa");
		datePicker.setEditable(true);

		setOnAction(null);

		setAlignment(Pos.CENTER);
	}
	
	public void setOnAction(EventHandler<ActionEvent> externalHandler) {
		datePicker.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				LocalDate date = datePicker.getValue();
				if(date!=null) {
					startEdit();
					getTableView().edit(getTableRow().getIndex(),getTableColumn());
					
					commitEdit(date);
					if(externalHandler!=null) {
						externalHandler.handle(t);
					}
				}
			}
		});
	}

}