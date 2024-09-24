package org.sourceforge.kga.gui.gardenplan.toolbar;


import org.sourceforge.kga.Resources;
import org.sourceforge.kga.gui.components.ImageButton;
import org.sourceforge.kga.gui.gardenplan.GardenTabPane;
import org.sourceforge.kga.gui.gardenplan.GardenView;
import org.sourceforge.kga.translation.Translation;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.converter.IntegerStringConverter;

public class YearSelector extends GridPane{
	Spinner<Integer> spinner;
    private ComboBox<Integer> buttonCombo;
    arrowButton decrementArrowButton;
    arrowButton incrementArrowButton;
    
    
	
	public YearSelector(GardenView gardenView,  GardenTabPane pane) {
        setMaxWidth(Control.USE_PREF_SIZE);
        setMinWidth(Control.USE_PREF_SIZE);
        BorderPane.setMargin(this,new Insets(5));
		
		 Label year =new Label(Translation.getCurrent().year());
	     year.setMinWidth(Control.USE_PREF_SIZE);
	     add(year, 0, 0);

		

        buttonCombo=new ComboBox<Integer>();
        buttonCombo.setConverter(new IntegerStringConverter());

        buttonCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
        	if(gardenView!=null&&pane!=null && pane.getGarden()!=null && newValue!=null)
        		gardenView.setGardenAndYear(pane.getGarden(), newValue);
        });
        add(buttonCombo, 2, 0);
        GridPane.setMargin(buttonCombo, new Insets(5,0,5,0));
        
        ImageButton addBut = new ImageButton(Resources.cursorAdd(),new Tooltip(Translation.getCurrent().action_year_add()), 
        		event -> { SelectYearDialog.newYear(pane.getGarden(), pane); },true);

        GridPane.setMargin(addBut,new Insets(5,0,5,5));	
        add(addBut, 4, 0);
        final ImageButton delYear= new ImageButton(Resources.cursorDelete(),new Tooltip(Translation.getCurrent().action_year_delete()), 
        		event -> { SelectYearDialog.deleteYear(pane.getGarden(), pane); },true);
        
        add(delYear, 5, 0);

        buttonCombo.getItems().addListener(new ListChangeListener<Integer>() {

			@Override
			public void onChanged(Change<? extends Integer> c) {
				delYear.setDisable(buttonCombo.getItems().size()<2);
				if (buttonCombo.getItems().indexOf(buttonCombo.getValue())<=0) {
					decrementArrowButton.setDisable(true);
				}
				else {
					decrementArrowButton.setDisable(false);
				}
				if (buttonCombo.getItems().indexOf(buttonCombo.getValue())>=buttonCombo.getItems().size()-1) {
					incrementArrowButton.setDisable(true);
				}
				else {
					incrementArrowButton.setDisable(false);
				}
				
			}
        });

        decrementArrowButton = new decrementButton(this);
        GridPane.setMargin(decrementArrowButton,new Insets(5,0,5,5));
        incrementArrowButton = new incrementButton(this);
        
        add(decrementArrowButton,1,0);
        add(incrementArrowButton,3,0);
	}
	
	
	public ObservableList<Integer> getItems() {
		return buttonCombo.getItems();
	}
	
	public void increment() {
		int index = buttonCombo.getItems().indexOf(buttonCombo.getValue())+1;
		if(index<buttonCombo.getItems().size()) {
			setValue(buttonCombo.getItems().get(index));
		}
	}
	
	public void decrement() {
		int index = buttonCombo.getItems().indexOf(buttonCombo.getValue())-1;
		if(index >=0 && index<buttonCombo.getItems().size()) {
			setValue(buttonCombo.getItems().get(index));
		}
	}
	
	public void setValue(Integer value) {
		buttonCombo.setValue(value);
		if (buttonCombo.getItems().indexOf(value)<=0) {
			decrementArrowButton.setDisable(true);
		}
		else {
			decrementArrowButton.setDisable(false);
		}
		if (buttonCombo.getItems().indexOf(value)>=buttonCombo.getItems().size()-1) {
			incrementArrowButton.setDisable(true);
		}
		else {
			incrementArrowButton.setDisable(false);
		}
	}
	
	public int getValue() {
		return buttonCombo.getValue();
	}
	
	
	private class arrowButton extends Button{
		Runnable onFire;
		public arrowButton(final Runnable fireAction, String label) {
			super(label);
	        //this.setStyle("-fx-font-size: " + 14);
	        this.setStyle("-fx-font-weight: bold");
			onFire=fireAction;
	        setFocusTraversable(false);
	        setMinWidth(Control.USE_PREF_SIZE);
	        setOnMousePressed(e -> {
	        	fireAction.run();
	        });
		}
		
		public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
            switch (action) {
                case FIRE: onFire.run(); break;
                default: super.executeAccessibleAction(action, parameters);
            }
        }
		
	}
	
	private class incrementButton extends arrowButton{
		public incrementButton(YearSelector selector) {
			super(()->selector.increment(),""+(char)0x25B6);
	        setAccessibleRole(AccessibleRole.INCREMENT_BUTTON);
	        //getStyleClass().setAll("increment-arrow-button");
	        
		}
	}
	
	private class decrementButton extends arrowButton{
		public decrementButton(YearSelector selector) {
			super(()->selector.decrement(), ""+(char)0x25C0);
	        setAccessibleRole(AccessibleRole.DECREMENT_BUTTON);
	        //getStyleClass().setAll("decrement-arrow-button");
		}
	}

}
