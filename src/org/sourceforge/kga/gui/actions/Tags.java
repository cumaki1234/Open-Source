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

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.TagList;
import org.sourceforge.kga.gui.PersistWindowBounds;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.plants.PlantComponent;
import org.sourceforge.kga.gui.plants.SelectPlantsDialog;
import org.sourceforge.kga.plant.Tag;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Translation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Tags extends Stage
{
    TilePane panePlants = new TilePane();
    ListView<Tag> listViewTags = new ListView<>();

    ProjectFileWithChanges project;
    
    public void showAndWait(Stage owner, ProjectFileWithChanges project )
    {
    	TagList tags = project.getProject().tagList;
    	this.project = project;
        // initialize
        Translation t = Translation.getCurrent();
        setTitle(t.action_tags());
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);

        //create interface
        Button buttonAdd = new Button(t.add());
        Button buttonRemove = new Button(t.remove());
        Button buttonRename = new Button(t.rename());
        Button buttonSelectPlants = new Button(t.select_plants());
        Button buttonClose = new Button(t.close());
        ButtonBar.setButtonData(buttonClose, ButtonBar.ButtonData.CANCEL_CLOSE);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(buttonAdd, buttonRename, buttonSelectPlants, buttonRemove, buttonClose);
        buttonBar.setPadding(new Insets(10., 10., 10., 10.));

        listViewTags.setMaxHeight(Double.MAX_VALUE);

        ScrollPane scrollPlants = new ScrollPane(panePlants);
        scrollPlants.setFitToWidth(true);

        SplitPane paneTop = new SplitPane();
        paneTop.getItems().addAll(listViewTags, scrollPlants);

        BorderPane pane = new BorderPane();
        pane.setCenter(paneTop);
        pane.setBottom(buttonBar);

        // load controls
        ObservableList<Tag> listTags = tags.getTags();
        listViewTags.setItems(listTags);
        PersistWindowBounds.persistDividerPosition(paneTop, Preferences.gui.tagWindow.dividerPosition);

        // setup action handlers
        listViewTags.setCellFactory(param -> new ListCell<Tag>() {
            @Override
            protected void updateItem(Tag tag, boolean empty) {
                super.updateItem(tag, empty);
                setText(empty || tag == null ? null : tag.getName());
                if (tag == listViewTags.getSelectionModel().getSelectedItem())
                    loadPlantsInTag(tag);
            }
        });
        listViewTags.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> loadPlantsInTag(newValue));

        buttonAdd.setOnAction(event -> {
            RenameTag renameTag = new RenameTag();
            if (renameTag.showAndWait(this, null)) {
                String newName = renameTag.getName();
                if (newName != null && !newName.isEmpty()) {
                    // Verificar si el nombre del nuevo tag ya existe
                    boolean nameExists = tags.getTags().stream()
                            .anyMatch(tag -> tag.getName().equalsIgnoreCase(newName));

                    if (nameExists) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Ya existe un tag con ese nombre.");
                        alert.initOwner(this);
                        alert.showAndWait();
                    } else {
                        // Agregar el nuevo tag si el nombre no existe
                        Tag tag = tags.addTag(newName);
                        showSelectPlantsDialog(tags, tag);
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Debe de agregar un nombre al tag.");
                    alert.initOwner(this);
                    alert.showAndWait();
                }
            }
        });

        buttonRename.setOnAction(event -> {
            Tag selectedTag = listViewTags.getSelectionModel().getSelectedItem();
            if (selectedTag != null) {
                RenameTag renameTagDialog = new RenameTag();
                boolean result = renameTagDialog.showAndWait(this, selectedTag);
                if (result) {
                    String newName = renameTagDialog.getName();
                    // Asegúrate de que el nuevo nombre no esté vacío y sea diferente del nombre actual
                    if (newName != null && !newName.isEmpty() && !newName.equals(selectedTag.getName())) {
                        // Verificar si el nuevo nombre ya existe
                        boolean nameExists = tags.getTags().stream()
                                .anyMatch(tag -> tag.getName().equalsIgnoreCase(newName));

                        if (nameExists) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Ya existe un Tag con ese nombre.");
                            alert.initOwner(this);
                            alert.showAndWait();
                        } else {
                            // Cambiar el nombre del tag si el nuevo nombre no existe
                            selectedTag.setName(newName);
                            listViewTags.setItems(tags.getTags()); // Actualiza la lista de tags en la vista
                        }
                    } else if (newName.equals(selectedTag.getName())) {
                        // Si el nuevo nombre es el mismo que el antiguo, no hacer nada
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "No se hicieron cambios.");
                        alert.initOwner(this);
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "El nombre del Tag no puede estar vacio.");
                        alert.initOwner(this);
                        alert.showAndWait();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "No ha seleccionado un Tag para editar.");
                alert.initOwner(this);
                alert.showAndWait();
            }
        });

        buttonSelectPlants.setOnAction(event -> {
            for (Tag tag : listViewTags.getSelectionModel().getSelectedItems())
            {
                showSelectPlantsDialog(tags, tag);
            }
        });
        buttonRemove.setOnAction(event -> {
        	Set<Tag> toRemove = new HashSet<Tag>();
            for (Tag tag : listViewTags.getSelectionModel().getSelectedItems())
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, t.tag_delete_confirmation(), ButtonType.YES, ButtonType.NO);
                alert.initOwner(this);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES)
                	toRemove.add(tag);
            }
            for(Tag curr : toRemove)
            	tags.removeTag(curr);
        });

        buttonClose.setCancelButton(true);
        buttonClose.setOnAction(event -> { this.close(); });

        listViewTags.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE)
            {
                listViewTags.getSelectionModel().clearSelection();
                listViewTags.getParent().requestFocus();
            }
        });

        // show window
        setScene(new Scene(pane));
        PersistWindowBounds.persistWindowBounds(pane, Preferences.gui.tagWindow.windowBounds, true);
        showAndWait();

        panePlants = new TilePane();
        listViewTags = new ListView<>();
    }

    private void loadPlantsInTag(Tag tag)
    {
        panePlants.getChildren().clear();
        if (tag != null)
            for (Plant plant : tag.getSpecies())
            {
                PlantComponent label = new PlantComponent(plant);
                panePlants.getChildren().add(label);
            }
    }

    private void showSelectPlantsDialog(TagList tags, Tag tag)
    {
        SelectPlantsDialog plantsDialog = new SelectPlantsDialog();
        if (plantsDialog.showAndWait(this,project, tag.getSpecies(), Preferences.gui.selectPlantsTagWindow.windowBounds))
            tags.modifyPlants(tag, plantsDialog.getSelection());
        listViewTags.getSelectionModel().select(tag);
    }
}
