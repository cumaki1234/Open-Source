package org.sourceforge.kga.gui.tableRecords.seedlistmanager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.SeedEntry.Quantity;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.SeedList.Listener;
import org.sourceforge.kga.gui.ListenerDisposer;
import org.sourceforge.kga.gui.tableRecords.RecordTable;
import org.sourceforge.kga.translation.Translation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;

public class SeedTable extends RecordTable<SeedEntry> {
	
	public ComboBox<LocalDate> dateBox;
	
	
	public SeedTable(SeedList seedFile) {
		super(new SeedListProvider(seedFile));

	    ObservableList<LocalDate> dateList = FXCollections.observableArrayList(new ArrayList<LocalDate>(seedFile.getValidFromDates().size()+1));
	    dateList.addAll(seedFile.getValidFromDates());
    	Collections.sort(dateList);
        if (!dateList.contains(LocalDate.now()))
    		dateList.add(LocalDate.now());

		dateBox = new ComboBox<LocalDate>(dateList);
        dateBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(dateBox.getValue()!=null)
					seedFile.setDate(dateBox.getValue());				
			}
        	
        });
        int toShow=dateList.size()>1?dateList.size()-2:0;
        dateBox.setValue(dateList.get(toShow));
        seedFile.setDate(dateBox.getValue());

        HBox.setMargin(dateBox, new Insets(5));
        
       
        Label dateLabel = new Label(Translation.getCurrent().working_date());
        super.addToLeftBottomScetion(dateLabel);
        super.addToLeftBottomScetion(dateBox);
        
        ListenerDisposer disposer = new ListenerDisposer(this);
        disposer.addListener(seedFile, new Listener() {

			@Override
			public void viewChanged() {
				updateView();
			}

			@Override
			public void listChanged() {		
				updateView();
			}
			
		});	
		updateView();
		
		
		
			

        

        

	}


}
