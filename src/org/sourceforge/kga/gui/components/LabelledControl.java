package org.sourceforge.kga.gui.components;

import javafx.scene.Node;
import javafx.scene.control.Label;

public interface LabelledControl <T extends Node> {

	public Label getLabel();
	
	public T getControl();
}
