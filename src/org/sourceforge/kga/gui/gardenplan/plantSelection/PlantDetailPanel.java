/**
 * Kitchen garden aid is a planning tool for kitchengardeners.
 * Copyright (C) 2010 Christian Nilsson
 *
 * This file is part of Kitchen garden aid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 * Email contact: tiberius.duluman@gmail.com; christian1195@gmail.com
 */

package org.sourceforge.kga.gui.gardenplan.plantSelection;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.HashSet;
import java.util.Set;

import org.sourceforge.kga.*;
import org.sourceforge.kga.gui.gardenplan.EditableGarden;
import org.sourceforge.kga.gui.gardenplan.EditableGarden.Operation;
import org.sourceforge.kga.translation.Translation;


public class PlantDetailPanel extends VBox
{
    private ToggleGroup buttonGroup = new ToggleGroup();
    private ComboBox <TaxonVariety<Plant>>varietyPicker;
    private ComboBox <Point>sizePicker;
    private TaxonVariety<Plant> selectedPlant = null;
    ImageView plantImage;

    private Label familyNameLabel;
    private Label plantNameLabel;
    private Label SpeciesTypeLabel;
    private Label SpeciesNameLabel;
    PlantRelationshipPane relationship;
    Callback<TaxonVariety<Plant>,?> plantSizeCallback;
    

    public void update(final TaxonVariety<Plant> plant) {
    	Platform.runLater(new Runnable() {

    		public void run() {

    			ObservableList<TaxonVariety<Plant>> varieties = varietyPicker.getItems();
				varieties.clear();

    			if (plant != null) {
    				for (TaxonVariety<Plant> curr: plant.getTaxon().getVarieties()) {
    					if(!varieties.contains(curr)) {
    						varieties.add(curr);
    					}
    				}
    				//varieties.addAll(plant.getTaxon().getVarieties());
    				
    				sizePicker.getItems().clear();
					sizePicker.getItems().addAll(plant.getTaxon().getCommonSizes());
					sizePicker.getSelectionModel().select(plant.getTaxon().getCommonSizes().get(0));
					if(plant.getTaxon().getCommonSizes().size()<2)
						sizePicker.setDisable(true);
					else
						sizePicker.setDisable(false);						

    				Set<TaxonVariety<Plant>> setTo= new HashSet<TaxonVariety<Plant>>();
    				if(plant.getVariety().length()==0) {
    					if(varieties.size()<=2) {
    						for(TaxonVariety<Plant> curr : varieties) {
    							if(curr.getVariety().trim().length()!=0) {
    								setTo.add(curr);
    								break;    								
    							}
    						}
    					}
    				}
    				
    				if(setTo.size()==1)
        				selectedPlant=setTo.iterator().next();
    				else
        				selectedPlant=plant;
    				varietyPicker.setValue(selectedPlant);
    				if(selectedPlant!=null)
    					sizePicker.setValue(selectedPlant.getSize());
    				
					listener.selectedPlantVarietyChanged(selectedPlant);	
    			}
    			else {
    				varietyPicker.setValue(null);    	
    				sizePicker.getItems().clear();
					sizePicker.getItems().add(Plant.LEGACY_DEFAULT_SIZE);
					sizePicker.getSelectionModel().select(Plant.LEGACY_DEFAULT_SIZE);
					sizePicker.setDisable(true);
    			}

    			/*if(varieties.size()>1)
    				varietyPicker.setDisable(false);
    			else
    				varietyPicker.setDisable(true);*/
    		}
    	});
    	Translation t = Translation.getCurrent();
    	if(plant!=null) {
    		plantImage.setImage(plant.getTaxon().getImage());
    		plantNameLabel.setText(t.translate(plant.getTaxon()));
    		familyNameLabel.setText(t.translate(plant.getFamily()));
    		SpeciesNameLabel.setText(plant.getTaxon().getName());
    		SpeciesTypeLabel.setText((plant.getTaxon().getType() == Taxon.Type.GENUS ? t.genus() : t.species())+":");
        	//properties.setupNodes(plant.getTaxon(),false);
    		if(relationship!=null) {
    			getChildren().remove(relationship);
    		}
    		relationship=new PlantRelationshipPane(plant.getTaxon());

            getChildren().add(relationship);
            VBox.setVgrow(relationship, Priority.ALWAYS);
    	}
    	else {
    		plantImage.setImage(null);
    		plantNameLabel.setText(t.pleaseSelectPlant());
    		familyNameLabel.setText(t.pleaseSelectPlant());
    		SpeciesNameLabel.setText(t.pleaseSelectPlant());
    		SpeciesTypeLabel.setText(t.species());
    		if(relationship!=null) {
    			getChildren().remove(relationship);
    			relationship=null;
    		}
    	}

    	
    }

    public interface Listener
    {
        public void operationChanged(EditableGarden.Operation operation);
        
        public void selectedPlantVarietyChanged(TaxonVariety<Plant> plant);
    }

    Listener listener;

    public void setListener(Listener listener)
    {
        this.listener = listener;
    }
    
    private static Label getMinWidthLabel(GridPane varietyBox, String text) {
    	 Label l = new Label(text);
         l.setMinWidth(Region.USE_PREF_SIZE);
         GridPane.setMargin(l,new Insets(0,2,0,2));
         return l;
    }
    
    public PlantDetailPanel(Callback<TaxonVariety<Plant>, ?> plantSizeCallback) 
    {
    	this.plantSizeCallback=plantSizeCallback;
		this.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		GridPane varietyBox = new GridPane();
        VBox.setMargin(varietyBox,new Insets(5));


        

        Translation t = Translation.getCurrent();
        
        getChildren().add(varietyBox);

        varietyBox.add(getMinWidthLabel(varietyBox, t.plantDetails()), 0, 0);
        

        varietyBox.add(getMinWidthLabel(varietyBox, t.name()), 0, 1);
        plantNameLabel=getMinWidthLabel(varietyBox, t.pleaseSelectPlant());
        varietyBox.add(plantNameLabel,1,1);
        
        varietyBox.add(getMinWidthLabel(varietyBox, t.family()), 0, 2);
        familyNameLabel=getMinWidthLabel(varietyBox, t.pleaseSelectPlant());
        varietyBox.add(familyNameLabel,1,2);
        

        SpeciesTypeLabel=getMinWidthLabel(varietyBox, t.species());
        varietyBox.add(SpeciesTypeLabel, 0, 3);
        SpeciesNameLabel=getMinWidthLabel(varietyBox, t.pleaseSelectPlant());
        varietyBox.add(SpeciesNameLabel,1,3);
        
        Label varietyLabel = getMinWidthLabel(varietyBox, Translation.getCurrent().variety());
        varietyBox.add(varietyLabel,0,4);
        varietyPicker = new ComboBox <TaxonVariety<Plant>>();
        sizePicker = new ComboBox <Point>();

        GridPane.setMargin(varietyPicker,new Insets(0,2,0,2));
        GridPane.setMargin(sizePicker,new Insets(0,2,0,2));
		varietyBox.add(varietyPicker,1,4);

        Label sizeLabel = getMinWidthLabel(varietyBox, Translation.getCurrent().size());
        varietyBox.add(sizeLabel,0,5);
		varietyBox.add(sizePicker,1,5);
		varietyPicker.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(varietyPicker, Priority.ALWAYS);
		varietyPicker.setEditable(true);


		varietyPicker.setMinWidth(Region.USE_PREF_SIZE);
		sizePicker.setMinWidth(Region.USE_PREF_SIZE);
        
		//varietyPicker.setEditable(true);
		varietyPicker.setConverter(new StringConverter<TaxonVariety<Plant>>() {

			@Override
			public String toString(TaxonVariety<Plant> object) {
				return (object==null)? "":object.getVariety();
			}

			@Override
			public TaxonVariety<Plant> fromString(String variety) {
				Plant p =(variety==null || variety.length()==0)?null:selectedPlant.getTaxon();
				return Resources.plantList().getVariety(p, variety);
			}

		});
		sizePicker.setConverter(new StringConverter<Point>() {

			@Override
			public String toString(Point object) {
				return (object==null)? "":object.x+"x"+object.y;
			}

			@Override
			public Point fromString(String variety) {
				if(variety==null || variety.length()==0)
						return null;
				String [] split = variety.split("x");
				return new Point(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
			}

		});

		varietyPicker.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				selectedPlant=getSelectedVariety();
				listener.selectedPlantVarietyChanged(selectedPlant);
				if(selectedPlant!=null && !varietyPicker.getItems().contains(selectedPlant)) {
					varietyPicker.getItems().add(selectedPlant);					
				}
				else {
					if(selectedPlant==null)
						sizePicker.getSelectionModel().select(Plant.LEGACY_DEFAULT_SIZE);
					else
						sizePicker.getSelectionModel().select(selectedPlant.getSize());
				}
			}

		});

		sizePicker.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(selectedPlant!=null) {
					selectedPlant.setSize(sizePicker.getSelectionModel().getSelectedItem());
					plantSizeCallback.call(selectedPlant);
				}
			}

		});

        plantImage = new ImageView();
        plantImage.setFitHeight((int)(8*SpeciesNameLabel.getFont().getSize()));
        plantImage.setPreserveRatio(true);
        plantImage.setSmooth(true);

        GridPane.setMargin(plantImage,new Insets(5));
        varietyBox.add(plantImage, 2, 1, 1, 4);
        

        
        update(null);
    }

	TaxonVariety<Plant> getSelectedVariety() {
    	TaxonVariety<Plant> current =varietyPicker.getValue(); 
    	if(current==null) {
    		if(selectedPlant==null) {
    			return null;
    		}
    		return Resources.plantList().getVariety(selectedPlant.getTaxon(),"");
    	}
    	else {
    		return current;
    	}
    }

    
    public void setSelectedPlant(final TaxonVariety<Plant> plant)
    {
    	if (selectedPlant == null) {
    		if(plant==null) {
    			return;
    		}
    		if(plant.equals(selectedPlant)) {
    			return;
    		}
    	}

    	update(plant);

    	selectedPlant = plant;
    }
}

