package org.sourceforge.kga.gui.gardenplan.plantSelection;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.sourceforge.kga.*;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.plants.PlantComponent;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Tiberius on 1/14/2017.
 */
public class PlantSelectionPane extends BorderPane implements PlantListSelection.Listener, PlantListFilter.Listener
{
    PlantListFilterPane paneFilter;
    PlantListSelection selection = new PlantListSelection();

    TreeMap<Plant, PlantComponent> labels = new TreeMap<>(new TaxonComparatorByName());
    

    
    public void setTaglist(TagList instance) {
    	paneFilter.setTaglist(instance);
    }

    public PlantSelectionPane(Stage primaryStage, ProjectFileWithChanges project)
    {
        // create label for each plant in resource file
        ArrayList<Plant> plants = new ArrayList<>();
        for (Plant plant : Resources.plantList().getPlants())
            if (plant.getImage() != null)
            {
                plants.add(plant);

                PlantComponent label = new PlantComponent(plant);
                label.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
                    Plant clickedPlant = label.getPlant();
                    selection.selectPlant(clickedPlant, !selection.isSelected(clickedPlant));
                });
                labels.put(plant, label);
            }

        // create gui
        TilePane boxPlants = new TilePane();
        boxPlants.getChildren().addAll(labels.values());
        //this.getStylesheets().add("/resources/css/PlantSelectionPane.css");
        //this.getStyleClass().add("PlantSelectionPane");
        // this.getCssMetaData().stream().filter(p->p.getProperty().equals("-fx-region-border")).findFirst().get().getSubProperties().stream().filter(p->p.getProperty().equals("-fx-border-color")).findFirst().get().getInitialValue(new Color());
        //this.getStyleClass()
        this.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        //this.setBorder(new Border(new BorderStroke(Color.gray(0.8), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        paneFilter = new PlantListFilterPane(plants, primaryStage, project);
        paneFilter.getFilter().addListener(this);
        selection.addListener(this);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(boxPlants);
        scrollPane.setStyle("-fx-background-color:transparent;");

        /* plantListScrollPane = new PlantListScrollPane(plants);
        plantListScrollPane.addListener(plant -> {
            selection.selectPlant(plant, true);
        }); */
        setTop(paneFilter);
        BorderPane.setMargin(scrollPane, new Insets(3, 5, 5, 5));
        BorderPane.setMargin(paneFilter, new Insets(5, 5, 0, 5));
        setCenter(scrollPane);
    }

    /* public PlantSelectionPane(Collection<Plant> plants)
    {

    } */

    public PlantListSelection getSelection()
    {
        return selection;
    }

    public PlantListFilter getFilter()
    {
        return paneFilter.getFilter();
    }

    void refreshPlantLabels()
    {
        for (PlantComponent label : labels.values())
            label.setVisible(false);
        for (Plant plant : paneFilter.getFilter().getFilteredPlants())
        {
            PlantComponent foundLabel = labels.get(plant);
            if (foundLabel != null)
                foundLabel.setVisible(true);
        }
        for (PlantComponent label : labels.values())
            label.setManaged(label.isVisible());
    }

    ArrayList<PlantComponent> lastSelectedLabels = new ArrayList<>();
    @Override
    public void selectedPlantChanged(Plant plant)
    {
        for (PlantComponent label : lastSelectedLabels)
            label.setSelected(false);
        lastSelectedLabels.clear();
        for (Plant selectedPlant : selection.getSelectedPlants())
        {
            PlantComponent label = labels.get(selectedPlant);
            if (label != null)
            {
                label.setSelected(true);
                lastSelectedLabels.add(label);
            }
        }
    }

    @Override
    public void filteredPlantsChanged()
    {
        refreshPlantLabels();
        if (paneFilter.getFilter().getFilteredPlants().size() == 1)
             selection.selectPlant(paneFilter.getFilter().getFilteredPlants().get(0), true);
    }
}
