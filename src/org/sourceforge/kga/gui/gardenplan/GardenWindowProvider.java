package org.sourceforge.kga.gui.gardenplan;

import java.util.Arrays;
import java.util.List;

import org.sourceforge.kga.Garden;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.TagList;
import org.sourceforge.kga.Taxon;
import org.sourceforge.kga.gui.CentralWindowProvider;
import org.sourceforge.kga.gui.FileWithChanges;
import org.sourceforge.kga.gui.Printable;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.actions.Export;
import org.sourceforge.kga.gui.actions.Print;
import org.sourceforge.kga.gui.actions.PrintSetup;
import org.sourceforge.kga.gui.components.ImageButton;
import org.sourceforge.kga.gui.gardenplan.plantSelection.PlantDetailPanel;
import org.sourceforge.kga.gui.gardenplan.plantSelection.PlantSelectionPane;
import org.sourceforge.kga.gui.gardenplan.toolbar.Toolbox;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Translation;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class GardenWindowProvider implements CentralWindowProvider, FileWithChanges.Listener{
    private Toolbox toolbox;
    private GardenTabPane gardenTabPane;
    private PlantSelectionPane selectionPane;
    private OperationMediator operationMediator;
    private PlantDetailPanel plantDetail;
	SplitPane panelLeft;
	Stage primaryStage;
	ProjectFileWithChanges project;
	BorderPane topBar;
	
	 
	
    private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(Garden.class.getName());

	@Override
	public Translation.Key getName() {
		return Translation.Key.plan;
	}

	@Override
	public Node getLeftPane() {
		return panelLeft;
	}
	
	@Override
	public Node getMainPane() {
		return gardenTabPane;
	}
	
	@Override
	public Node getToolbar() {
		return topBar;
	}
	
	public GardenWindowProvider(Stage primaryStage, ProjectFileWithChanges project) {
		this.primaryStage = primaryStage;
		this.project=project;
		toolbox = new Toolbox();
		gardenTabPane = new GardenTabPane(toolbox, primaryStage);
        selectionPane = new PlantSelectionPane(primaryStage, project);
        plantDetail = new PlantDetailPanel(v->{
        	project.getGarden().plantVarietyChanged(v); return null;}
        );
        topBar = new BorderPane();

        topBar.setMaxWidth(Double.MAX_VALUE);
        topBar.setMinWidth(Control.USE_PREF_SIZE);
        
        topBar.setLeft(toolbox);
        HBox center = new HBox();
        center.getChildren().add(gardenTabPane.zoomControl);
        ImageButton snapshot = new ImageButton(Resources.snapshot(),null,e->new Export().export(primaryStage,gardenTabPane.getExportableView()));
        center.setMaxWidth(Control.USE_PREF_SIZE);
        HBox.setMargin(snapshot, new Insets(15));
        center.getChildren().add(snapshot);
        topBar.setCenter(center);
        topBar.setRight(gardenTabPane.yearSelector);
        operationMediator = new OperationMediator(toolbox, plantDetail, selectionPane.getSelection());
        panelLeft = new SplitPane();
        panelLeft.setOrientation(Orientation.VERTICAL);
        panelLeft.getItems().addAll(selectionPane,plantDetail);
		project.addListener(this);

        // restore filter selected values
        int selectedFamily = Preferences.gui.mainWindow.selectedFamily.get();
        if (selectedFamily < 0)
        {
            try
            {
                log.info("Selected family " + selectedFamily);
                selectionPane.getFilter().filterByFamily(
                        Resources.plantList().getPlant(selectedFamily));
            }
            catch (Exception ex)
            {
                log.warning(ex.toString());
            }
        }

		if(project.getProject()!=null) {
			objectChanged();
		}
	}

	@Override
	public void onHide() {
		saveSelectedFilters();
		//project.removeListener(this);
		//project.getGarden().removeAllIfObserving(Arrays.asList(new Object[] {gardenTabPane,operationMediator,gardenTabPane.getGardenView()}));
		//toolbox=null;
		//gardenTabPane=null;
		//selectionPane=null;
		//operationMediator=null;
		//topBar=null;
	}

	@Override
	public void onShow() {
		/* TODO: note, we are not disposing of the UI when hidden, and regenrating it when shown. This is for two reasons.
		 * 1) right now this causes the canvas grid to grow each time the UI is closed and reopened for some reason. this will cause a nullPointerException deep within javaFX. see https://stackoverflow.com/questions/53908235/how-to-catch-this-javafx-nullpointerexception-that-occurs-once-a-canvas-gets-too
		 * 2) I have not done the due-diligence to ensure that all listeners in the UI stop listening for project/garden/etc changes. As such, attempting to dispose of the UI will likely leak memory.
		 */
        

	}

	@Override
	public Printable getPrintTask() {
		return gardenTabPane.getGardenView();
	}
	


    private void saveSelectedFilters()
    {
        Taxon selectedFamily = selectionPane.getFilter().getFilterByFamily();
        if (selectedFamily == null)
        {
            log.info("No family selected");
            Preferences.gui.mainWindow.selectedFamily.remove();
        }
        else
        {
            log.info("Selected family " + Integer.toString(selectedFamily.getId()) + " " + selectedFamily.getName());
            Preferences.gui.mainWindow.selectedFamily.set(selectedFamily.getId());
        }

        /* Tag selectedTag = speciesListPanel.getSelectedTag();
        if (selectedTag == null)
            log.info("No tag selected");
        else
            log.info("Selected tag " + selectedTag.getName());
        prefs.put("selectedTag", selectedTag == null ? "" : selectedTag.getName()); */
    }

	@Override
	public void objectChanged() {
        EditableGarden garden = project.getGarden();
        gardenTabPane.setGarden(garden);	
        operationMediator.setGarden(garden);
        TagList instance = project.getProject().tagList;
        selectionPane.setTaglist(instance);	
	}

	@Override
	public void unsavedChangesChanged() {
		// TODO Auto-generated method stub
		
	}

}
