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


package org.sourceforge.kga.gui.actions;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.PlantListSelection;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.Gui;
import org.sourceforge.kga.gui.PersistWindowBounds;
import org.sourceforge.kga.gui.gardenplan.plantSelection.PlantSelectionPane;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Translation;


/**
 * Window that displays properties and rules for a plant
 */
public class PlantEditor extends Stage
{
    private static final long serialVersionUID = 1L;

    public void showAndWait(Window owner, Plant selection)
    {
        // initialize
        Translation t = Translation.getCurrent();
        setTitle(t.action_species_properties());
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
        // TODO: properties.setIconImage(SwingFXUtils.fromFXImage(plant.getImage(), null));

        //create interface
        /*Button buttonAdd = new Button(t.add());
        Button buttonRemove = new Button(t.remove());
        Button buttonModify = new Button(t.modify());*/
        Button buttonClose = new Button(t.close());
        ButtonBar.setButtonData(buttonClose, ButtonBar.ButtonData.CANCEL_CLOSE);
        buttonClose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
		        close();
			}
        });

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(buttonClose);
        buttonBar.setPadding(new Insets(10., 10., 10., 10.));

        PlantSelectionPane plantSelectionPane = new PlantSelectionPane(this,null);

        PlantPropertiesPane plantPropertiesPane = new PlantPropertiesPane();

        SplitPane paneTop = new SplitPane();
        paneTop.getItems().addAll(plantSelectionPane, plantPropertiesPane);

        BorderPane pane = new BorderPane();
        pane.setCenter(paneTop);
        pane.setBottom(buttonBar);

        // load controls
        plantPropertiesPane.setVisible(selection != null);
        if (selection != null)
        {
            plantSelectionPane.getSelection().selectPlant(selection, true);
            plantPropertiesPane.loadPlant(selection);
        }
        PersistWindowBounds.persistDividerPosition(paneTop, Preferences.gui.tagWindow.dividerPosition);

        // setup action handlers
        plantSelectionPane.getSelection().addListener(plant ->
        {
            plantPropertiesPane.setVisible(plant != null);
            if (plant != null)
                plantPropertiesPane.loadPlant(plant);
        });

        // show window
        setScene(new Scene(pane));
        PersistWindowBounds.persistWindowBounds(pane, Preferences.gui.plantEditorWindow.windowBounds, true);
        showAndWait();
   }
}
