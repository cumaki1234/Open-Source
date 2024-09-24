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

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import org.sourceforge.kga.*;
import org.sourceforge.kga.translation.Iso639_1;
import org.sourceforge.kga.translation.Iso639_1.Language;
import org.sourceforge.kga.translation.Translation;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

public class EditTranslation
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    Set<String> languages;
    
    private void refreshTableItems(TableView<transationEntry<?>> table, Translation t, boolean onlyMissing) {
    	table.getItems().clear();
    	for (Translation.Key key : Translation.Key.values())
        {
            if (!onlyMissing ||
                t.getCustomTranslations().getProperty(key.toString()) != null ||
                t.getDefaultTranslation(key) == null)
            {
            	table.getItems().add(new transationEntry<Translation.Key>(key,t));
            }
        }

        for (Plant p : Resources.plantList().getPlants())
        {
            if (p.getTranslation("en") == null)
                continue;
            if (!onlyMissing ||
                t.getCustomTranslations().getProperty(p.getName()) != null ||
                t.getDefaultTranslation(p) == null)
            {
            	table.getItems().add(new transationEntry<Plant>(p,t));
            }
        }

        for (Animal a : Resources.plantList().getAnimals())
        {
            if (a.getTranslation("en") == null)
                continue;
            if (!onlyMissing ||
                t.getCustomTranslations().getProperty(a.getName()) != null ||
                t.getDefaultTranslation(a) == null)
            {
            	table.getItems().add(new transationEntry<Animal>(a,t));
            }
        }
    	
    }


    ComboBox<Iso639_1.Language> comboLanguage = null;
    CheckBox checkOnlyMissing = null;
    Button buttonNewLanguage = null;
    
    public EditTranslation() {
    }
    
    public class transationEntry<T>{
    	T key;
    	Translation myTranslation;
    	
    	public String translation;
    	
    	public transationEntry(T key, Translation entryFor) {
    		this.key = key;
    		myTranslation=entryFor;
    		translation=translate(myTranslation);
    	}
    	
    	private String translate(Translation t,Translation.Key key) {
			if(t.getCustomTranslations().getProperty(key.toString()) == null && t.getDefaultTranslation(key) == null)
				return "";
			else
				return t.translate(key);    		
    	}
    	
    	private String translate(Translation t,Plant key) {
			if(t.getCustomTranslations().getProperty(key.getName()) == null && t.getDefaultTranslation(key) == null)
				return "";
			else
				return t.translate(key);    		
    	}
    	
    	private String translate(Translation t,Animal key) {
			if(t.getCustomTranslations().getProperty(key.getName()) == null && t.getDefaultTranslation(key) == null)
				return "";
			else
				return t.translate(key);    		
    	}

    	public String translate(Translation code) {
    		if (key instanceof Translation.Key) {
    			return translate(code,(Translation.Key)key);
    		}
    		else if (key instanceof Plant)
    			return translate(code,(Plant)key);
    		else if (key instanceof Animal)
    			return translate(code,(Animal)key);
    		else throw new Error("Unsupported");
    	}

    	public String getEnglish() {
    		return translate(Resources.translations().get("en"));
    	}

    	public String getTranslation() {
    		return translate(myTranslation);
    	}

    	public void setTranslation(String val) {
    		if(key instanceof Translation.Key) {
    			myTranslation.setTranslation(((Translation.Key)key).name(), val);
    		}
    		else {
    			myTranslation.setTranslation(((Taxon<?>)key), val);    			
    		}
    	}
    	
    	
    	public String getKey() {
    		if(key instanceof Translation.Key) {
    			return ((Translation.Key)key).name();
    		}
    		else if(key instanceof Taxon) {
    			return ((Taxon<?>)key).getName();   			
    		}
    		else throw new Error("Unsupported");
    	}
    	
    }

    public void showAndWait(Stage parentStage)
    {

    	Insets marg5 = new Insets(5);
    	BorderPane panel = new BorderPane();
    	HBox panel2 = new HBox();//(new GridLayout(2, 1));
        checkOnlyMissing = new CheckBox(Translation.getCurrent().only_missing_translations());
        checkOnlyMissing.setSelected(true);

        // create combo language
        comboLanguage = new ComboBox<Iso639_1.Language>();
        comboLanguage.setConverter(new StringConverter<Iso639_1.Language>() {

			@Override
			public String toString(Iso639_1.Language object) {
				// TODO Auto-generated method stub
				return object.name;
			}

			@Override
			public Iso639_1.Language fromString(String string) {
				// TODO Auto-generated method stub
				return Iso639_1.getbyName(string);
			}
        	
        });
        
        comboLanguage.getItems().addAll(Resources.translations().getLanguageItems());
        
        

        panel2.getChildren().add(comboLanguage);
        panel2.getChildren().add(checkOnlyMissing);
        HBox.setMargin(comboLanguage, marg5);
        HBox.setMargin(checkOnlyMissing, marg5);
        panel.setTop(panel2);

        // configure table
        TableView<transationEntry<?>> table = new TableView<transationEntry<?>>();//model);
        
        TableColumn<transationEntry<?>,String> idCol = new TableColumn<transationEntry<?>,String>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<transationEntry<?>,String>("key"));
		idCol.setMinWidth(180);
		table.getColumns().add(idCol);
		
		TableColumn<transationEntry<?>,String> engCol = new TableColumn<transationEntry<?>,String>("English");
        engCol.setCellValueFactory(new PropertyValueFactory<transationEntry<?>,String>("English"));
        engCol.setMinWidth(180);
		table.getColumns().add(engCol);
		
		
		TableColumn<transationEntry<?>,String> translationCol = new TableColumn<transationEntry<?>,String>("English");
		translationCol.setCellValueFactory(new PropertyValueFactory<transationEntry<?>,String>("translation"));
		translationCol.setMinWidth(180);
		translationCol.setEditable(true);
		translationCol.setCellFactory(TextFieldTableCell.forTableColumn());
		translationCol.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent<transationEntry<?>, String>>() {
            @Override public void handle(TableColumn.CellEditEvent<transationEntry<?>, String> t) {
            	t.getRowValue().myTranslation.setTranslation(t.getRowValue().getKey(), t.getNewValue());
            }
        });
		table.getColumns().add(translationCol);

		table.setEditable(true);
		
        /*table.setDefaultRenderer(String.class,
            new TranslationChangedCellRenderer(table.getDefaultRenderer(String.class)));*/
        ScrollPane scrollPane = new ScrollPane(table);
        panel.setCenter(scrollPane);
        BorderPane.setMargin(scrollPane, marg5);

        // openFile dialog
        buttonNewLanguage = new Button();
        Button buttonExportTranslation = new Button(Translation.getCurrent().action_export_translation());
        buttonExportTranslation.setOnAction(w->{new ExportTranslation().showAndWait(parentStage);});
        buttonNewLanguage.setOnAction(w->{
        	 ChoiceDialog<Language> codePrompt = new ChoiceDialog<Iso639_1.Language>(null,Arrays.asList(Iso639_1.getLanguages()));
        	 codePrompt.setTitle("Enter Language Code");
        	 codePrompt.showAndWait();
        	 
             Iso639_1.Language selected =codePrompt.getSelectedItem();
             if (selected != null)
             {
                 if(comboLanguage.getItems().contains(selected)) {
                     comboLanguage.setValue(selected);

                 }
                 else {
                      //TODO: Translation.addTranslation(selected.code);

                     Properties translationMap = new Properties();
                     
                     Translation translation = new Translation(selected.code, translationMap);
                     Resources.translations().add(selected.code,translation);
                     // reload available languages
                     for (Iso639_1.Language language : Resources.translations().getLanguageItems())
                     {
                         comboLanguage.getItems().add(language);
                         if (language.code.equals(selected.code))
                             comboLanguage.setValue(language);
                     }
                 }
             }
        });
        buttonNewLanguage.setText(Translation.getCurrent().new_language());
        
        HBox buttons = new HBox();
        HBox.setMargin(buttonNewLanguage, marg5);
        HBox.setMargin(buttonExportTranslation, marg5);
        buttons.getChildren().addAll(buttonNewLanguage,buttonExportTranslation);
        panel.setBottom(buttons);
        
        
        comboLanguage.setOnAction(w->{
        	Translation current = Resources.translations().get(comboLanguage.getValue().code);
        	refreshTableItems(table, current,checkOnlyMissing.isSelected());
        	translationCol.setText(comboLanguage.getValue().name);
        });
        checkOnlyMissing.selectedProperty().addListener(w->{
        	Translation current = Resources.translations().get(comboLanguage.getValue().code);
        	refreshTableItems(table, current,checkOnlyMissing.isSelected());
        });

        comboLanguage.setValue(Iso639_1.getLanguage("en"));
    	refreshTableItems(table, Resources.translations().get("en"),checkOnlyMissing.isSelected());
        Stage d = new Stage();
        
        d.setScene(new Scene(panel));
        d.initOwner(parentStage);
        d.initModality(Modality.APPLICATION_MODAL);
        d.showAndWait();
    }
}
