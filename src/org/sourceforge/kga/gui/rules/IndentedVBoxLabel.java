package org.sourceforge.kga.gui.rules;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class IndentedVBoxLabel extends Label{
	private static final int indentLimit=16;
	
	public IndentedVBoxLabel(String text, int indents, VBox parent) {
		super(text);
		VBox.setMargin(this, getIndentingInsets(indents));
	}
	
	public static Insets getIndentingInsets(int indents) {
		return new Insets(0,0,0,indents*indentLimit);
	}
	

}
