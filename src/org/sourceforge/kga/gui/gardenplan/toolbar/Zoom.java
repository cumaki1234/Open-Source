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

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

import org.sourceforge.kga.gui.gardenplan.EditableGarden;
import org.sourceforge.kga.translation.Translation;

import java.util.Optional;

public class Zoom extends HBox
{
	
	EditableGarden garden;
	Spinner<Integer> spinner;
	
    public Zoom()
    {
        Translation t = Translation.getCurrent();

        spinner=new Spinner<>(
                EditableGarden.minZoomFactor, EditableGarden.maxZoomFactor, 100, 10);
        spinner.getEditor().setPrefColumnCount(5);
        spinner.getEditor().setAlignment(Pos.CENTER_RIGHT);
        spinner.setEditable(true);
        
        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
        	if(garden!=null) {
        		garden.setZoomFactor(newValue);
        }});

        getChildren().addAll(new Label(t.action_zoom()), spinner, new Label("%"));
        setSpacing(5);
        setAlignment(Pos.CENTER);
    }
    
    public void setGarden(EditableGarden garden) {
    	this.garden=garden;
    	if(garden!=null)
    		garden.setZoomFactor(spinner.getValue());
    }
}
