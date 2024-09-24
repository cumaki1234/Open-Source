package org.sourceforge.kga.gui.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageButton extends Button{

	public ImageButton(Image toShow, Tooltip tooltip, EventHandler<ActionEvent> handler) {
		this(new ImageView(toShow),tooltip,handler, false);
	}
	public ImageButton(Image toShow, Tooltip tooltip, EventHandler<ActionEvent> handler, boolean mini) {
		this(new ImageView(toShow),tooltip,handler, mini);
	}
	
	private ImageButton(ImageView iv, Tooltip tooltip, EventHandler<ActionEvent> handler, boolean mini) {
		super("",iv);

		iv.setPreserveRatio(true);
		setTooltip(tooltip);
		if(mini) {

			double imageHeight = (getFont().getSize()+getLabelPadding().getTop()+getLabelPadding().getBottom())+1;
			iv.setFitHeight(imageHeight);
		}
		else {
			double padding = super.getPadding().getTop()+super.getPadding().getBottom();
			super.setPadding(new Insets(2));

			double imageHeight = 2*(getFont().getSize()+getLabelPadding().getTop()+getLabelPadding().getBottom())+1-4;
			iv.setFitHeight(imageHeight);

		}
		setOnAction(handler);
	}
}
