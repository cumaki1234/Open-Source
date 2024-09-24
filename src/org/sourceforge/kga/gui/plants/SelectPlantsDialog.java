/**
 * Kitchen garden aid is a planning tool for kitchengardeners.
 * Copyright (C) 2018 Tiberius Duluman
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
 * Email contact: tiberius.duluman@gmail.com
 */


package org.sourceforge.kga.gui.plants;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.PlantList;
import org.sourceforge.kga.PlantListSelection;
import org.sourceforge.kga.gui.PersistWindowBounds;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.gardenplan.plantSelection.PlantSelectionPane;
import org.sourceforge.kga.plant.Tag;
import org.sourceforge.kga.prefs.EntryWindowBounds;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Translation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SelectPlantsDialog extends Dialog<ButtonType>
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(PlantList.class.getName());
    private static final long serialVersionUID = 1L;

    public SelectPlantsDialog()
    {
    }

    PlantListSelection selection = null;

    public List<Plant> getSelection()
    {
        return selection.getSelectedPlants();
    }

    public boolean showAndWait(Stage parent, ProjectFileWithChanges project, Collection<Plant> selection, EntryWindowBounds entryBounds)
    {
        // initialize
        setResizable(true);
        initOwner(parent);

        Translation t = Translation.getCurrent();

        setTitle(t.action_tags());

        //create interface
        PlantSelectionPane plantSelectionPane;
        plantSelectionPane = new PlantSelectionPane(parent,project);
        plantSelectionPane.getSelection().setMultipleSelection(true);
        plantSelectionPane.setMaxHeight(Double.MAX_VALUE);

        Button buttonAll = new Button(t.all());
        Button buttonNone = new Button(t.none());
        Button buttonInvert = new Button(t.invert());
        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(buttonAll, buttonNone, buttonInvert);
        buttonBar.setPadding(new Insets(5, 0, 0, 0));

        VBox boxAll = new VBox();
        boxAll.getChildren().addAll(plantSelectionPane, buttonBar);
        boxAll.setVgrow(plantSelectionPane, Priority.ALWAYS);
        boxAll.setMaxWidth(Double.MAX_VALUE);
        boxAll.setMaxHeight(Double.MAX_VALUE);
        boxAll.setPrefHeight(600);
        boxAll.setPrefWidth(450);

        getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        getDialogPane().setContent(boxAll);

        // load controls
        if (selection != null)
        {
            plantSelectionPane.getSelection().selectAll(selection);
        }

        // setup action handlers
        buttonAll.setOnAction(event ->
        {
            plantSelectionPane.getSelection().selectAll(plantSelectionPane.getFilter().getFilteredPlants());
        });
        buttonNone.setOnAction(event ->
        {
            plantSelectionPane.getSelection().unselectAll(plantSelectionPane.getFilter().getFilteredPlants());
        });
        buttonInvert.setOnAction(event ->
        {
            plantSelectionPane.getSelection().invertSelection(plantSelectionPane.getFilter().getFilteredPlants());
        });
        final Button btOk = (Button)getDialogPane().lookupButton(ButtonType.OK);

        // show dialog
        PersistWindowBounds.persistWindowBounds(getDialogPane(), entryBounds, true);
        Optional<ButtonType> resultButton = showAndWait();
        boolean result = resultButton.isPresent() && resultButton.get() == ButtonType.OK;
        if (result)
            this.selection = plantSelectionPane.getSelection();
        return result;
    }

}
