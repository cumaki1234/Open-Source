package org.sourceforge.kga.gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.sourceforge.kga.Resources;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Translation;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.Pair;

public abstract class CentralWindow {
	
	protected Map<Translation.Key,Pair<CentralWindowProvider,ToggleButton>> providerMap;
	protected Stage primaryStage;
	SplitPane paneCenter;
	private CentralWindowProvider current;
    private VBox mainBox;
    protected ProjectFileWithChanges projectFile;
    HBox buttonBox;
    private BorderPane rightSide;
    volatile boolean needsCleanup;
    volatile boolean runCleanup=true;

    Thread cleanup = new Thread((Runnable)()-> {
    	while(runCleanup) {
    		if(needsCleanup) {
    			System.gc();
    			needsCleanup=false;
    		}
    		try {
    			Thread.sleep(50000);
    		}
    		catch (Exception e) {
    		}
    	}
    	});
	
	protected CentralWindowProvider getCurrentProvider() {
		needsCleanup=false;
		return current;
	}
    
	public CentralWindow(Stage primaryStage, Collection<CentralWindowProvider> providers, ProjectFileWithChanges projectFile) {
		this.projectFile=projectFile;
		new ProjectFileWithChanges(primaryStage);
		providerMap = new HashMap<Translation.Key,Pair<CentralWindowProvider,ToggleButton>>();
		ToggleGroup group = new ToggleGroup();
		buttonBox = new HBox();
		buttonBox.setMinWidth(Control.USE_PREF_SIZE);
		Polygon chevron = new Polygon();
		chevron.getPoints().addAll(new Double[] {
				-5.0, 7.0,
				-8.0, 0.0,
			    10.0, 1.0,
			    15.0, 3.0,
			    16.0, 4.0,
			    20.0, 10.0,
			    16.0, 16.0,
			    15.0, 17.0,
			    10.0, 19.0,
			    -8.0, 20.0 ,
			    -5.0, 13.0 ,
			    -4.0, 10.0 });
		for(final CentralWindowProvider curr : providers) {
			ToggleButton t = new ToggleButton();
			t.setMnemonicParsing(false);
			//t.setShape(chevron);
			t.setToggleGroup(group);
			t.setWrapText(true);
			t.setMinWidth(50);//Changed from use pref width since it was not working with javafx11 on linux.
			t.setPrefWidth(Control.USE_COMPUTED_SIZE);
			
			//HBox.setMargin(t, new Insets(5,-5,5,-5));
			buttonBox.getChildren().add(t);
			t.setOnAction(e->{changeDisplayToProvider(curr);});
			providerMap.put(curr.getName(), new Pair<CentralWindowProvider,ToggleButton>(curr,t));
		}
		
		this.primaryStage=primaryStage;
		
		buildLayout();
        primaryStage.getIcons().add(Resources.applicationIcon());

        changeDisplayToProvider(providers.iterator().next());
        PersistWindowBounds.moveToCenter(mainBox, 1);
        PersistWindowBounds.persistWindowBounds(mainBox, Preferences.gui.mainWindow.windowBounds, true);
        
	}
	
	public void changeDisplayToProvider(CentralWindowProvider toShow) {
		if (toShow != providerMap.get(toShow.getName()).getKey()) {
			throw new Error("Only window providers passed at construction are supported");
		}
		if(current!=null) {
			current.onHide();	
		}
		providerMap.get(toShow.getName()).getValue().setSelected(true);
		
		current=toShow;
		toShow.onShow();
		paneCenter.getItems().clear();
		paneCenter.getItems().add(toShow.getLeftPane());
		paneCenter.getItems().add(rightSide);
		rightSide.setCenter(toShow.getMainPane());
		rightSide.setTop(toShow.getToolbar());
        PersistWindowBounds.persistDividerPosition(paneCenter, Preferences.gui.mainWindow.dividerPosition);
        //updateProviderMenu();
        needsCleanup=true;
        cleanup.interrupt();
	}
	
	private void buildLayout() {
		mainBox = new VBox();
        paneCenter = new SplitPane();
        rightSide = new BorderPane();
        VBox.setVgrow(paneCenter, Priority.ALWAYS);
        updateLanguage();
        primaryStage.setScene(new Scene(mainBox));

	}
	
	protected void updateLanguage() {
		mainBox.getChildren().clear();
		mainBox.getChildren().addAll(createMenu(),buttonBox,paneCenter);
		if(current!=null) {
			changeDisplayToProvider(current);
		}
		for(Pair<CentralWindowProvider,ToggleButton> curr : providerMap.values()) {
        	curr.getValue().setText(Translation.getCurrent().translate(curr.getKey().getName()));
        	curr.getValue().setPrefWidth(Translation.getCurrent().translate(curr.getKey().getName()).length()*10);
        }
	}
	
	protected abstract MenuBar createMenu();

}
