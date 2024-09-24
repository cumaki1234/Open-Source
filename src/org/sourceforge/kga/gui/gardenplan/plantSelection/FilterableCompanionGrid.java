package org.sourceforge.kga.gui.gardenplan.plantSelection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.gui.ClickableTooltip;
import org.sourceforge.kga.gui.actions.ScrollingTilePane;
import org.sourceforge.kga.gui.components.LabelledClearableComboBox;
import org.sourceforge.kga.gui.plants.PlantComponent;
import org.sourceforge.kga.gui.rules.HintListDisplay;
import org.sourceforge.kga.plant.Companion;
import org.sourceforge.kga.rules.Hint;
import org.sourceforge.kga.translation.Translation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FilterableCompanionGrid extends VBox{
	ScrollingTilePane companionDisplay;
	Plant plant;
	Map<Plant,Set<Companion>> companions;
	filterDialog fdialog;
	Predicate<Map.Entry<Plant,Set<Companion>>> filter;
	ToggleButton filterBut;
	
	public int size() {
		return companions.size();
	}
	
	public FilterableCompanionGrid(String headerText, Plant plant) {
        this.plant=plant;
        companionDisplay = new ScrollingTilePane(true);
        BorderPane header = new BorderPane();
        Label l = new Label(headerText);
        l.setAlignment(Pos.BOTTOM_LEFT);
        ImageView filterIcon = new ImageView(Resources.filter());
        filterIcon.setPreserveRatio(true);
        filterIcon.setFitHeight(15);
        filterBut = new ToggleButton("",filterIcon);
        filterBut.setMaxHeight(Control.USE_PREF_SIZE);
        filterBut.setOnAction((e)->{if(fdialog==null) {
        		fdialog=new filterDialog(this);
        	}
        	fdialog.show();});

        BorderPane.setMargin(filterBut, new Insets(0,0,1,0));
		l.setMinWidth(Control.USE_PREF_SIZE);
        header.setLeft(l);
        header.setRight(filterBut);
        l.prefHeightProperty().bind(filterBut.heightProperty());
        
        super.getChildren().addAll(header,companionDisplay);
        companions=new HashMap<>();
        companionDisplay.setFitToHeight(true);
        VBox.setVgrow(companionDisplay, Priority.ALWAYS);
	}
	
	public void addCompanion(Plant companionPlant, Set<Companion> rules) {
		companions.put(companionPlant, rules);
		updateDisplay();
	}
	
	public Stream<Map.Entry<Plant, Set<Companion>>> getFilteredCompanions() {
		if(filter==null)
			return companions.entrySet().stream();
		else
			return companions.entrySet().stream().unordered().filter(filter);
	}
	
	private void updateDisplay() {
		Iterator <Map.Entry<Plant, Set<Companion>>> iter = getFilteredCompanions().iterator();
		companionDisplay.clear();
		while (iter.hasNext()){
			Map.Entry<Plant, Set<Companion>> curr = iter.next();
			Plant companionPlant = curr.getKey();
			Set<Companion> rules = curr.getValue();

			PlantComponent plantLabel = new PlantComponent(companionPlant,false);
			companionDisplay.add(plantLabel);

			Set<Hint> hints = new HashSet<Hint>();
			for(Companion companion : rules) {
				org.sourceforge.kga.rules.Companion cRule = new org.sourceforge.kga.rules.Companion();
				if(hints.size()==0)
					hints.add(new Hint(plant, companion.plant, null, cRule, companion));
				else
					hints.iterator().next().addDetail(new Hint(plant, companion.plant, null, cRule, companion));
				
			}
			Tooltip fancyTip = new ClickableTooltip(new HintListDisplay(hints),plantLabel);
			fancyTip.setShowDelay(new Duration(500));
		}		
	}
	
	class filterDialog extends Stage{
		LabelledClearableComboBox<Companion.Type> typeSelector;
		LabelledClearableComboBox<String> detailSelector;
		
		public filterDialog(FilterableCompanionGrid parent) {
			super.initModality(Modality.APPLICATION_MODAL);
			super.initOwner(parent.getScene().getWindow());
			BorderPane content = new BorderPane();
	        setScene(new Scene(content));
	        this.setOnHiding(v->{
	        	filterBut.setSelected(filter!=null);
	        });
	        this.setOnShowing(v->{
	        	filterBut.setSelected(filter!=null);
	        });
	        
	        VBox filters = new VBox();
	        content.setCenter(filters);
	        typeSelector = new LabelledClearableComboBox<Companion.Type>(Translation.getCurrent().companion_type()+" = ");
	        filters.getChildren().add(typeSelector);
	        for (Companion.Type curr : companions.values().stream().flatMap(m->m.stream()).map(e->e.type).distinct().toArray(i->new Companion.Type[i])) {
	        	typeSelector.getItems().add(curr);
	        }
	        typeSelector.getItems().add(null);
	        typeSelector.setOnAction(e->{
	        	updateDependantSelector();
	        });

	        detailSelector = new LabelledClearableComboBox<String>("");
	        typeSelector.bindSize(detailSelector);
	        filters.getChildren().add(detailSelector);
	        
	        
	        HBox buttons = new HBox();
	        content.setBottom(buttons);
	        Button ok_but = new Button(Translation.getCurrent().ok());
	        Button cancel_but = new Button(Translation.getCurrent().cancel());
	        buttons.getChildren().addAll(ok_but,cancel_but);
	        cancel_but.setOnAction(e->super.hide());
	        ok_but.setOnAction(e->{updateFilter();super.hide();updateDisplay();});
	        BorderPane.setMargin(filters, new Insets(5));
	        BorderPane.setMargin(buttons, new Insets(5));
	        buttons.setAlignment(Pos.CENTER_RIGHT);
	        buttons.setSpacing(5);
	        updateDependantSelector();
	        super.setMinHeight(150);
		}
		
		private void updateDependantSelector(){
			Companion.Type selectedType = typeSelector.getSelectionModel().getSelectedItem();
			boolean empty = false;
			if(selectedType==null) {
				empty=true;
			}
			else {
				String [] detailedVals=companions.values().stream().flatMap(s->s.stream()).filter(c->c.type.equals(selectedType)).flatMap(c->Arrays.asList(c.getDetails()).stream()).distinct().sorted().toArray(i->new String[i]);
				if(detailedVals.length>0) {
					detailSelector.getItems().clear();
					detailSelector.getItems().addAll(Arrays.asList(detailedVals));
					detailSelector.getItems().add(null);
					detailSelector.setDisable(false);	
				}
				else {
					empty=true;
				}
			}
			if(empty) {
				detailSelector.setValue(null);
				detailSelector.setDisable(true);	
				detailSelector.getItems().clear();			
			}			
		}
		
		private void updateFilter() {
			Companion.Type selectedType = typeSelector.getSelectionModel().getSelectedItem();
			String selectedDetail = detailSelector.getSelectionModel().getSelectedItem();
			if(selectedType==null) {
				filter=null;
			}
			else {
				
				Predicate<Companion> cMatchRule;
				//cMatchRule is run for every companion rule for a given plant. if any matches, then that entry is kept.
				if (selectedDetail==null) {
					cMatchRule=(c)->c.type==selectedType;
				}
				else {
					cMatchRule=(c)->c.type==selectedType&&Arrays.asList(c.getDetails()).contains(selectedDetail);					
				}
				
				filter = (e)->{
					for(Companion curr:e.getValue()) {
						if(cMatchRule.test(curr))
							return true;
					}
					return false;
				};
					
			}
		}

	}
}