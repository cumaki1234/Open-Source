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


package org.sourceforge.kga.gui.actions;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import org.sourceforge.kga.PlantList;
import org.sourceforge.kga.PlantListSelection;
import org.sourceforge.kga.gui.PersistWindowBounds;
import org.sourceforge.kga.gui.gardenplan.plantSelection.PlantSelectionPane;
import org.sourceforge.kga.plant.Tag;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Translation;

import java.util.Optional;

public class RenameTag extends Dialog<ButtonType>
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(PlantList.class.getName());
    private static final long serialVersionUID = 1L;

    public RenameTag()
    {
    }

    String name = null;

    public String getName()
    {
        return name;
    }

    public boolean showAndWait(Window parent, Tag tag)
    {
        // initialize
        initOwner(parent);

        Translation t = Translation.getCurrent();

        setTitle(t.action_tags());

        //create interface
        Label labelName = new Label(t.tag());
        labelName.setMaxHeight(Double.MAX_VALUE);
        TextField textName = new TextField();
        textName.setMaxWidth(Double.MAX_VALUE);
        HBox boxName = new HBox();
        boxName.setMaxWidth(Double.MAX_VALUE);
        boxName.setSpacing(3.);
        boxName.setHgrow(textName, Priority.ALWAYS);
        boxName.getChildren().addAll(labelName, textName);

        /* VBox boxAll = new VBox();
        boxAll.getChildren().addAll(boxName, plantSelectionPane, buttonBar);
        boxAll.setVgrow(plantSelectionPane, Priority.ALWAYS);
        boxAll.setMaxWidth(Double.MAX_VALUE);
        boxAll.setMaxHeight(Double.MAX_VALUE);
        boxAll.setPrefHeight(600);
        boxAll.setPrefWidth(450); */

        getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        getDialogPane().setContent(boxName);

        // load controls
        if (tag != null)
        {
            textName.setText(tag.getName());
        }

        // setup action handlers
        final Button btOk = (Button)getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, event ->
        {
            if (textName.getText().trim().isEmpty())
            {
                event.consume();
                Alert alert = new Alert(Alert.AlertType.WARNING, "Debe de agregar un nombre al tag.");
                alert.initOwner(this.getDialogPane().getScene().getWindow());
                alert.showAndWait();
                textName.requestFocus();
            }
        });

        // show dialog
        PersistWindowBounds.persistWindowBounds(getDialogPane(), Preferences.gui.renameTagWindow.windowBounds, false);
        Optional<ButtonType> resultButton = showAndWait();
        boolean result = resultButton.isPresent() && resultButton.get() == ButtonType.OK;
        if (result)
            name = textName.getText().trim();
        return result;
    }

}
