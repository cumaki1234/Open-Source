package org.sourceforge.kga.gui.tableRecords;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.sourceforge.kga.SeedCollection;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.gui.CentralWindowProvider;
import org.sourceforge.kga.gui.FileWithChanges;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.SeedFileWithChanges;
import org.sourceforge.kga.gui.actions.SeedManagerAutogenerate;
import org.sourceforge.kga.gui.FileWithChanges.Listener;
import org.sourceforge.kga.gui.Printable;
import org.sourceforge.kga.gui.tableRecords.expenses.AllocationEntryRecords;
import org.sourceforge.kga.gui.tableRecords.expenses.ExpenseEntryRecords;
import org.sourceforge.kga.gui.tableRecords.harvests.HarvestEntryRecords;
import org.sourceforge.kga.gui.tableRecords.harvests.HarvestProvider;
import org.sourceforge.kga.gui.tableRecords.seedlistmanager.SeedListRecords;
import org.sourceforge.kga.gui.tableRecords.soilNutrition.SoilNutritionRecords;
import org.sourceforge.kga.translation.Translation;
import org.sourceforge.kga.translation.Translation.Key;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Created by tidu8815 on 21/03/2018.
 */
public class RecordTrackerPane implements FileWithChanges.Listener, CentralWindowProvider, SeedCollection.Listener
{
   //private SeedFileWithChanges seedFile;
	
    //TabPane tabs;
    BorderPane toolbox;
    TabularRecordTree buttonPane;
    
    public SeedCollection collection;
    ProjectFileWithChanges project;
    Stage primaryStage;
    
    BorderPane leftPane;
    BorderPane mainPane;

    public RecordTrackerPane(Stage primaryStage, ProjectFileWithChanges project)
    {
		this.primaryStage=primaryStage;
		this.project=project;
    }


	@Override
	public void onShow() {
		project.addListener(this);
		collection = project.getProject().getSeedCollection();
		collection.addListener(this);

		// Crear los elementos de la interfaz
		leftPane = new BorderPane();
		mainPane = new BorderPane();

		buttonPane = new TabularRecordTree(primaryStage, mainPane, Arrays.asList(new RecordList<?>[] {
				new SeedListRecords(collection),
				new SoilNutritionRecords(project.getProject()),
				new ExpenseEntryRecords(project.getProject()),
				new AllocationEntryRecords(project.getProject()),
				new HarvestEntryRecords(project.getProject())
		}));

		ScrollPane leftScrollPane = new ScrollPane();
		leftScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		leftScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		leftScrollPane.setContent(buttonPane);
		leftScrollPane.setFitToHeight(true);
		leftScrollPane.setFitToWidth(true);

		// Estilo para el ScrollPane
		leftScrollPane.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #b0b0b0; -fx-border-width: 2px;");

		// Estilo para el BorderPane que contiene el ScrollPane
		leftPane.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 15px;");

		// Estilo para el TabularRecordTree
		//buttonPane.setStyle("-fx-background-color: #ffffff; -fx-border-color: #a0a0a0; -fx-border-radius: 10px; -fx-padding: 15px;");
		buttonPane.setStyle("-fx-border-width: 2px; -fx-background-color: #e8f4fc; -fx-font-weight: bold; -fx-padding: 5px; -fx-border-radius: 5px;");

		// Estilo para el BorderPane superior (toolbox)
		toolbox = new BorderPane();
		toolbox.setStyle("-fx-background-color: #d0d0d0; -fx-padding: 10px;");

		// Añadir el ScrollPane al leftPane
		leftPane.setCenter(leftScrollPane);

		// Establecer el mainPane en algún lugar del escenario
		mainPane.setCenter(new VBox()); // Ejemplo de contenido vacío

		// Refrescar la lista
		listChanged();
	}



	public void listChanged()
    {
    	//dateList.clear();
    	//dateList.addAll(collection.getValidFromDates());


        buttonPane.AllListsChanged();
    }

    @Override
    public void objectChanged() {
		listChanged();
		
	}

	@Override
	public void unsavedChangesChanged() {
		listChanged();		
	}

	@Override
	public Key getName() {
		return Translation.Key.action_track;
	}

	@Override
	public Node getLeftPane() {
		return leftPane;
	}

	@Override
	public Node getMainPane() {
		return mainPane;
	}

	@Override
	public Node getToolbar() {
		return toolbox;
	}

	@Override
	public void onHide() {
		project.removeListener(this);
		collection.removeListener(this);
		collection=null;
        toolbox = null;
        buttonPane=null;
	}

	@Override
	public Printable getPrintTask() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void SeedListChanged() {
		listChanged();
	}


	@Override
	public void viewChanged() {
		// TODO Auto-generated method stub
		
	}
	
}
