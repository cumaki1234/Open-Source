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

package org.sourceforge.kga.gui.gardenplan.toolbar;

import javafx.event.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import org.sourceforge.kga.*;
import org.sourceforge.kga.gui.gardenplan.EditableGarden;
import org.sourceforge.kga.gui.gardenplan.EditableGarden.Operation;


public class Toolbox extends VBox
{
    private ToggleGroup buttonGroup = new ToggleGroup();
	private ToggleButton addPlantButton = new ToggleButton("", createImageView(Resources.cursorAdd()));
	private ToggleButton deletePlantButton = new ToggleButton("", createImageView(Resources.cursorDelete()));

	// Etiquetas de texto debajo de cada botón
	private Label addPlantLabel = new Label("Agregar");
	private Label deletePlantLabel = new Label("Eliminar");


	private ImageView createImageView(Image image) {
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(20); // Establece el ancho deseado
		imageView.setFitHeight(20); // Establece la altura deseada
		imageView.setPreserveRatio(true); // Mantiene la proporción de la imagen
		return imageView;
	}


	boolean isPlantSelected;
    
    public void setPlantSelected(boolean status) {
    	isPlantSelected=status;
		addPlantButton.setDisable(!status);
    	if(status){
    		addPlantButton.setSelected(true);
			listener.operationChanged(EditableGarden.Operation.AddPlant);
    	}
    }
    
    public Operation getOperation() {
    	if(addPlantButton.isSelected())
    		return Operation.AddPlant;
    	if(deletePlantButton.isSelected())
    		return Operation.DeletePlant;
    	else
    		return Operation.PickPlant;
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

    public Toolbox()
    {
    	isPlantSelected=false;
    	TilePane buttons = new TilePane();

        buttons.setHgap(5);
        buttons.setPadding(new Insets(5));
        setPadding(new Insets(5));
		VBox addPlantContainer = new VBox(addPlantButton, addPlantLabel);
		VBox deletePlantContainer = new VBox(deletePlantButton, deletePlantLabel);
		// Espaciado entre el botón y la etiqueta
		addPlantContainer.setSpacing(5);
		deletePlantContainer.setSpacing(5);

		// Añadir los VBox al TilePane
		buttons.getChildren().addAll(addPlantContainer, deletePlantContainer);

		// Configuración de los ToggleButtons
		addPlantButton.setToggleGroup(buttonGroup);
        addPlantButton.setDisable(true);
        deletePlantButton.setToggleGroup(buttonGroup);

        addPlantButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(addPlantButton.isSelected()) {
					 if (!isPlantSelected)
			                addPlantButton.setSelected(false);
			            else
			                listener.operationChanged(EditableGarden.Operation.AddPlant);
				}
				else {
					addPlantButton.setSelected(true);
				}
			}
    		
    	});
        

        
        deletePlantButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(deletePlantButton.isSelected()) {
					listener.operationChanged(EditableGarden.Operation.DeletePlant);
				}
				else {
					deletePlantButton.setSelected(true);
				}
			}
    		
    	});
        getChildren().add(buttons);
        
    }
}

