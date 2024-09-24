package org.sourceforge.kga.gui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.gui.gardenplan.GardenView;
import org.sourceforge.kga.translation.Translation;
import org.sourceforge.kga.SeedList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class PlantTableCell <K> extends TableCell<K, PlantOrUnregistered> {
	

    private ComboBox<PlantOrUnregistered> comboBox;
    
    public static List<PlantOrUnregistered> getSortedPlants(){
    	List<PlantOrUnregistered>plantList=new ArrayList<PlantOrUnregistered>(SeedList.getKnownPlants());
    	plantList.sort(null);
    	return plantList;
    }
    
    /*public interface plantCallback {
    	public void selectedPlantChanged(PlantOrUnregistered newVal);
    }
    
    plantCallback callback;*/
    
	
	public PlantTableCell() {//, plantCallback callback) {
		//super(new PlantStringConverter(), seedFile.getKnownPlants());
		//this.callback=callback;
	}
	
	 @Override
     public void updateItem(PlantOrUnregistered entry, boolean empty) {
         super.updateItem(entry, empty);
         if (empty) {
             setGraphic(null);
  			setText(null);
         } else if (entry.plant!=null){
             setGraphic(getImageForPlant(entry));
 			setText(Translation.getCurrent().translate(entry.plant));
 		}
 		else {
 			setText(entry.unregisteredPlant);
 			System.out.println("non-plant found: "+entry.unregisteredPlant);
 		}
     }
	 
	 public static ImageView getImageForPlant(PlantOrUnregistered entry) {
		 ImageView imageView = entry.plant.createImageview(GardenView.PLANT_SIZE);
         return imageView;
	 }
	 
	  @Override public void startEdit() {
	        super.startEdit();
	        if (! isEditable() || ! getTableView().isEditable() || ! getTableColumn().isEditable()) {
	            return;
	        }

	        if (comboBox == null) {
	        	List<PlantOrUnregistered> plantList= getSortedPlants();
	        	comboBox = new ComboBox<PlantOrUnregistered>(FXCollections.observableArrayList(plantList));
	            comboBox.setConverter(new PlantStringConverter());
	            comboBox.setCellFactory(new Callback<ListView<PlantOrUnregistered>,ListCell<PlantOrUnregistered>>() {

	            	@Override
	            	public ListCell<PlantOrUnregistered> call(ListView<PlantOrUnregistered> param) {
	            		return new PlantListCell();
	            	}});
	            comboBox.setButtonCell(comboBox.getCellFactory().call(null));
	            PlantOrUnregistered selected = getItem();
	            comboBox.getSelectionModel().select(selected);
	            int index = -1;
	            for (int count=0;count<plantList.size();count++) {
	            	if(plantList.get(count).equals(selected)) {
	            		index=count;
	            		break;
	            	}
	            }

	            final int targetIndex=index;
	            Platform.runLater(new Runnable() {
	            	@Override
		        	public void run() {
	            		comboBox.getButtonCell().updateIndex( targetIndex);
	        	        comboBox.setOnAction(new EventHandler<ActionEvent>() {

							@Override
							public void handle(ActionEvent event) {
								commitEdit(comboBox.getSelectionModel().getSelectedItem());
								//callback.selectedPlantChanged(comboBox.getSelectionModel().getSelectedItem());
								
							}});
		        	}
		        });
	        }

	        setText(null);
	        setGraphic(comboBox);
	    }
	
	public static <K> Callback<TableColumn<K, PlantOrUnregistered>,TableCell<K, PlantOrUnregistered>> getCallBack(){//, plantCallback callback) {
		
		return new Callback<TableColumn<K, PlantOrUnregistered>,TableCell<K, PlantOrUnregistered>>(){

			@Override
			public TableCell<K, PlantOrUnregistered> call(TableColumn<K, PlantOrUnregistered> param) {
				PlantTableCell<K> toReturn =  new PlantTableCell<K>();//, callback);
				return toReturn;
			}
			
		};
		
	}

}

class PlantListCell extends ListCell<PlantOrUnregistered> {
	@Override
	public void updateItem(PlantOrUnregistered item,boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setGraphic(null);
			setText(null);
		} else if (item.plant!=null){
			setGraphic(PlantTableCell.getImageForPlant(item));
			setText(Translation.getCurrent().translate(item.plant));
		}
		else {
			setText(item.unregisteredPlant);
			System.out.println("non-plant found: "+item.unregisteredPlant);
		}
	}
}

class PlantStringConverter extends StringConverter<PlantOrUnregistered> {

	@Override
	public String toString(PlantOrUnregistered object) {
		if (object.plant!=null)
			return Integer.toString(object.plant.getId());
		else
			return object.unregisteredPlant;
	}

	@Override
	public PlantOrUnregistered fromString(String string) {
		Plant asPlant=null;
		try {
			int asInt = Integer.parseInt(string);
			asPlant=Resources.plantList().getPlant(asInt);
		}
		catch (NumberFormatException e) {;}
		return (asPlant==null)?new PlantOrUnregistered(string):new PlantOrUnregistered(asPlant);
	}
}
