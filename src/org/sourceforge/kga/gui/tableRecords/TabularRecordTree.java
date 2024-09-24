package org.sourceforge.kga.gui.tableRecords;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.scene.input.MouseEvent;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.gui.tableRecords.TreeDisplayable.nodeGenerator;
import org.sourceforge.kga.translation.Translation;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TabularRecordTree extends TreeView<TreeDisplayable<?>> {
	Collection<RecordList<?>> recordLists;
	BorderPane mainPane;
	Map <RecordList<?>,TreeItem<TreeDisplayable<?>>> recordRoots;

	public static final String BASE_BUTTON_STYLE = "-fx-border-color: #a0a0a0; -fx-border-width: 1px; -fx-background-color: #ffffff; -fx-font-weight: normal; -fx-padding: 5px; -fx-border-radius: 5px;";

	public static final String HOVER_BUTTON_STYLE = "-fx-border-color: #005f9e; -fx-border-width: 2px; -fx-background-color: #e8f4fc; -fx-font-weight: bold; -fx-padding: 5px; -fx-border-radius: 5px;";

	private final String BASE_STYLE = "-fx-border-color: #005f9e; " +
			"-fx-border-width: 2px; " +
			"-fx-background-color: #e8f4fc; " +
			"-fx-font-weight: bold; " +
			"-fx-padding: 5px; " +
			"-fx-border-radius: 5px; " +
			"-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 5, 0, 2, 2); " +
			"-fx-text-fill: #003366; " +
			"-fx-font-size: 14px;";

	private final String HOVER_STYLE = "-fx-border-color: #0073e6; " +
			"-fx-border-width: 2px; " +
			"-fx-background-color: #d0e6f8; " +
			"-fx-font-weight: bold; " +
			"-fx-padding: 5px; " +
			"-fx-border-radius: 5px; " +
			"-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 5, 0, 2, 2); " +
			"-fx-text-fill: #002a72; " +
			"-fx-font-size: 14px;";

	/*private void applyHoverStyle(MouseEvent e) {
		if (getStyle().equals(BASE_STYLE)) {
			setStyle(HOVER_STYLE);
		}
	}

	private void applyBaseStyle(MouseEvent e) {
		if (getStyle().equals(HOVER_STYLE)) {
			setStyle(BASE_STYLE);
		}
	}*/
	public TabularRecordTree(Stage primaryStage, BorderPane mainPane, Collection<RecordList<?>> recordLists) {
		setEditable(true);
		this.mainPane=mainPane;
		this.recordLists=recordLists;
		setCellFactory(w->new TextFieldTreeCell<TreeDisplayable<?>>() {
			@Override
            public void updateItem(TreeDisplayable<?> val, boolean empty) {
                super.updateItem(val, empty);
            	setConverter(val);
                if(val!=null) {
                	super.setText(val.toString());
                	val.applyStyling(this);
                }
                else {
        			setEditable(false);
					setStyle(BASE_STYLE);

				}
				// Aplicar estilos base y hover
				setOnMouseEntered(this::applyHoverStyle);
				setOnMouseExited(this::applyBaseStyle);




                if (val!=null && val.deleteable) {
                	Button deleter = new Button("[x]");
                	deleter.setStyle(BASE_BUTTON_STYLE);
                	deleter.setOnMouseEntered(e->deleter.setStyle(HOVER_BUTTON_STYLE));
                	deleter.setOnMouseExited(e->deleter.setStyle(BASE_BUTTON_STYLE));
                	setGraphic(deleter);
                	deleter.setOnAction(w->{
                		Alert verify = new Alert(AlertType.CONFIRMATION, Translation.getCurrent().delete()+" "+val.toString()+" ?",ButtonType.YES, ButtonType.NO);
                		verify.initModality(Modality.APPLICATION_MODAL);
                		verify.initOwner(primaryStage);
        				verify.showAndWait();
        				if(verify.getResult()==ButtonType.YES) {
                		val.delete();
        				}
                	});
                }
            }



			private void applyHoverStyle(javafx.scene.input.MouseEvent mouseEvent) {
				if (getStyle().equals(BASE_STYLE)) {
					setStyle(HOVER_STYLE);
				}
			}

			private void applyBaseStyle(MouseEvent mouseEvent) {
				if (getStyle().equals(HOVER_STYLE)) {
					setStyle(BASE_STYLE);
					}
			}
		});


        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
        	if(newValue!=null) {
        		if (newValue.getValue().selectable) {
        			mainPane.setCenter(newValue.getValue().getDisplayNode());
        		}
        		else {
        			if(!newValue.isLeaf())
        				newValue.setExpanded(!newValue.isExpanded());
        			getSelectionModel().select(oldValue);
        		}
        	}
        });
        
        
	}



    public <T> void AllListsChanged() {
    	Object toSelect = (this.getSelectionModel().getSelectedItem()==null)?null:this.getSelectionModel().getSelectedItem().getValue().data;
        recordRoots = new HashMap <RecordList<?>,TreeItem<TreeDisplayable<?>>>();
        TreeDisplayable<Object> dummyRootItem = new TreeDisplayable<Object>(null, null, false,false);
        TreeItem<TreeDisplayable<?>> dummyRoot = new TreeItem<TreeDisplayable<?>>(dummyRootItem);
        super.setRoot(dummyRoot);
        setShowRoot(false);
        
        for(RecordList<?> curr : recordLists) {
            TreeDisplayable<?> seedManagerDisplayable = curr.getRootDisplayable();
            TreeItem<TreeDisplayable<?>> seedManagerItem = new TreeItem<TreeDisplayable<?>>(seedManagerDisplayable);
            dummyRoot.getChildren().add(seedManagerItem);
            recordRoots.put(curr, seedManagerItem);
            seedManagerItem.setExpanded(true);
            listChanged(curr, toSelect);
        }
    }
    
    private <T> TreeItem<TreeDisplayable<?>> listChanged(RecordList<T> rl, Object toSelect)
    {
    	TreeItem<TreeDisplayable<?>> toReturn=null;
    	getSelectionModel().select(null);
    	Collection<TreeItem<TreeDisplayable<?>>> toRemove = new HashSet<TreeItem<TreeDisplayable<?>>>();
    	toRemove.addAll(recordRoots.get(rl).getChildren());
    	if(recordRoots.get(rl).getValue().data.equals(toSelect)) {
    		getSelectionModel().select(recordRoots.get(rl));
    	}
        for (T curr : rl.getCollection()) {
        	TreeDisplayable<T> displayable = rl.getDisplayable(curr);
        	TreeItem<TreeDisplayable<?>> myItem = new TreeItem<TreeDisplayable<?>>(displayable);
        	recordRoots.get(rl).getChildren().add(myItem);
        	if(getSelectionModel().getSelectedItem()==null || curr.equals(toSelect)) {
        		setEditable(true);
        		getSelectionModel().select(myItem);
        		toReturn=myItem;
        	}
        }
        if(rl.canAddChildren()) {
        	TreeDisplayable<TreeDisplayable.actionButtons> seedManagerAdd = new TreeDisplayable<TreeDisplayable.actionButtons>(TreeDisplayable.actionButtons.Add,new TreeDisplayable.unDeleteableNodeGenerator() {

				@Override
				public Node getDisplayNode(Object myData) {
					addNew(rl); return new VBox();
				}
        	}, true,false);//TODO: implement selection handler
        	TreeItem<TreeDisplayable<?>> seedManagerAddItem = new TreeItem<TreeDisplayable<?>>(seedManagerAdd);
        	recordRoots.get(rl).getChildren().add(seedManagerAddItem);
        }
        recordRoots.get(rl).setExpanded(true);

        Platform.runLater(()->{
        	recordRoots.get(rl).getChildren().removeAll(toRemove);
        });
        return toReturn;
    }

    public <T>void addNew(RecordList<T> rl) {
    	T added = rl.addNew();
    	TreeItem<TreeDisplayable<?>> curr = listChanged(rl, added);
    	getSelectionModel().select(curr);
    	Platform.runLater(()->{
    		mainPane.setCenter(curr.getValue().getDisplayNode());
    		getSelectionModel().select(curr);
        	Platform.runLater(()->{
        		edit(curr);
        	});
    	});

    }



}
