package org.sourceforge.kga.gui.actions;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;

public class ScrollingTilePane extends ScrollPane {
	
	TilePane flower;
	
	public ScrollingTilePane(boolean border) {
		flower = new TilePane();
		setContent(flower);

        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setMaxHeight(Control.USE_PREF_SIZE);
        setFitToWidth(true);
        /*setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        //setMaxHeight(Control.USE_PREF_SIZE);
        setMinHeight(Control.USE_PREF_SIZE);
        Bindings.add(this.prefViewportHeightProperty(),flower.prefHeightProperty());
        setFitToHeight(true);
        setFitToWidth(true);*/
        this.prefViewportHeightProperty().bind(flower.heightProperty());
        
        if(!border)
        	setStyle("-fx-background-color:transparent;");
	}
	
	public void add(Node n) {
		flower.getChildren().add(n);
	}
	
	public void clear() {
		flower.getChildren().clear();
	}

}
