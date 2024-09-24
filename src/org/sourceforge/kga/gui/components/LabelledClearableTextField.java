package org.sourceforge.kga.gui.components;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LabelledClearableTextField extends AbstractLabelledControl<TextField> {

	
	public LabelledClearableTextField(String label) {
		super(label, new TextField());
		
		Button clearBut = new Button("X");
		clearBut.setOnAction(e->myControl.setText(""));
		myControl.textProperty().addListener((observable, oldValue, newValue) -> {
			clearBut.setDisable(newValue == null || newValue.length()==0);
        });
		clearBut.setDisable(true);
		addSideButton(clearBut,1);
	}
	
	
	public StringProperty textProperty() {
		return myControl.textProperty();
	}
	
	public String getText() {
		return myControl.getText();
	}
	
	public void setText(String value) {
		myControl.setText(value);
	}

}
