package org.sourceforge.kga.gui.gardenplan;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import org.sourceforge.kga.Garden;
import org.sourceforge.kga.GardenObserver;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.Rectangle;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.gui.actions.Export;
import org.sourceforge.kga.gui.actions.ExportableImage;
import org.sourceforge.kga.gui.components.ImageButton;
import org.sourceforge.kga.gui.gardenplan.toolbar.SelectYearDialog;
import org.sourceforge.kga.gui.gardenplan.toolbar.Toolbox;
import org.sourceforge.kga.gui.gardenplan.toolbar.YearSelector;
import org.sourceforge.kga.gui.gardenplan.toolbar.Zoom;
import org.sourceforge.kga.translation.Translation;

import java.util.Calendar;

/**
 * Created by Tiberius on 1/7/2017.
 */
public class GardenTabPane extends BorderPane implements GardenObserver
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(GardenTabPane.class.getName());
    private EditableGarden garden = null;
    private ToggleGroup toggleGroup = new ToggleGroup();
    private GardenView gardenView;
    Zoom zoomControl;
    
    YearSelector yearSelector;
    
    public ExportableImage getExportableView() {
    	return gardenView;
    }
    
    public GardenView getGardenView() {
    	return gardenView;
    }

    public GardenTabPane(Toolbox box, Stage primaryStage)
    {
        zoomControl = new Zoom();
        
        gardenView = new GardenView();
                
       

        

        yearSelector = new YearSelector(gardenView, this);
        setCenter(gardenView);
    }

    Integer getYear(int index)
    {
    	return yearSelector.getItems().get(index);
    }

    private void createToggleButton(Integer year)
    {
        // find position to insert year
        int index;
        for (index = 0; index < yearSelector.getItems().size(); ++index)
            if (getYear(index)!=null && getYear(index) > year)
                break;

        yearSelector.getItems().add(index, year);
    }
    
    public EditableGarden getGarden() {
    	return garden;
    }

    public void setGarden(EditableGarden garden)
    {
    	zoomControl.setGarden(garden);
        if (this.garden != null)
            this.garden.removeObserver(this);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        yearSelector.getItems().clear();
        this.garden = garden;

        
        for (Integer year : garden.getYears())
        {
        	log.info("Adding " + year + " to tabbed pane");
        	createToggleButton(year);
        	if(yearSelector.getItems().contains(currentYear)) {
        		yearSelector.setValue(currentYear);
        	}
        	else if(yearSelector.getItems().size()>1) {
        		yearSelector.setValue(yearSelector.getItems().get(yearSelector.getItems().size()-1));
        	}
        }
        
        garden.addObserver(this);
    }

    public int selectedYear()
    {
        return yearSelector.getValue();
    }

    public void selectYear(int year)
    {
    	yearSelector.setValue(year);
    }

    @Override
    public void yearAdded(Garden sender, int year)
    {
        createToggleButton(year);
    }

    @Override
    public void yearDeleted(Garden sender, int year)
    {
    	if (yearSelector.getItems().contains(year)) {
    		boolean wasSelected = yearSelector.getValue()==year;
    		yearSelector.getItems().remove((Integer)year);
    		if(wasSelected) {
    			Integer toSelect=(yearSelector.getItems().size()>0)?yearSelector.getItems().get(yearSelector.getItems().size()-1):null;
    			yearSelector.setValue(toSelect);
    		}
    	}
    }

    @Override
    public void hintsChanged(int year, Point grid) {}

    @Override
    public void plantsChanged(int year, Point grid) {}

    @Override
    public void boundsChanged(Rectangle bounds) {}
}
