package org.sourceforge.kga.gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class AbstractLabelledControl <T extends Control> extends HBox implements LabelledControl<T> {

	T myControl;
	Label l;

	
	public AbstractLabelledControl(String label, T control) {
		super();
		l = new Label(label);
        l.setMinWidth(Region.USE_PREF_SIZE);
		getChildren().add(l);
		l.setAlignment(Pos.CENTER_LEFT);
		HBox.setMargin(l, new Insets(5));
		myControl = control;
		getChildren().add(myControl);
	}
	
	@Override
	public Label getLabel() {
		return l;
	}

	@Override
	public T getControl() {
		// TODO Auto-generated method stub
		return myControl;
	}
	
	public <K extends Control> void bindSize(LabelledControl<K> other) {
		l.minWidthProperty().bind(other.getLabel().widthProperty());
		other.getLabel().minWidthProperty().bind(l.widthProperty());
		myControl.minWidthProperty().bind(other.getControl().widthProperty());
		other.getControl().minWidthProperty().bind(myControl.widthProperty());
	}
	
	public void addSideButton(Button b, int margin) {
		getChildren().add(b);
		HBox.setMargin(b, new Insets(0,0,0,margin));
		
	}

}
