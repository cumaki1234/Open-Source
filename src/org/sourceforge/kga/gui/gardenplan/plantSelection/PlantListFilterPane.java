package org.sourceforge.kga.gui.gardenplan.plantSelection;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.sourceforge.kga.*;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.actions.Tags;
import org.sourceforge.kga.gui.components.ImageButton;
import org.sourceforge.kga.gui.components.LabelledClearableComboBox;
import org.sourceforge.kga.gui.components.LabelledClearableTextField;
import org.sourceforge.kga.translation.Translation;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import org.sourceforge.kga.plant.Tag;
import org.sourceforge.kga.plant.TagInGarden;
import org.sourceforge.kga.plant.TagInInventory;

/**
 * Created by Tiberius on 1/14/2017.
 */
public class PlantListFilterPane extends GridPane implements PlantListFilter.Listener
{
    LabelledClearableComboBox<Taxon> comboFamily;
    LabelledClearableTextField  textName;
    LabelledClearableComboBox<Tag> comboTag;

    PlantListFilter plantListFilter;
    TagList myTagList;
    

    public void setTaglist(TagList instance) {
    	this.myTagList=instance;
    	loadTags();
    }


    /* TODO:
    private class FamilyComboItem
    {
        Plant family;

        public FamilyComboItem(Plant f)
        {
            family = f;
        }

        public String toString()
        {
            if (family == null)
                return "";
            return Translation.getCurrent().translate(family);
        }
    }
     */

    public PlantListFilterPane(Collection<Plant> plants, Stage primaryStage, ProjectFileWithChanges project)
    {
    	comboFamily = new LabelledClearableComboBox<>(Translation.getCurrent().family());
    	comboTag = new LabelledClearableComboBox<>(Translation.getCurrent().tag());
    	textName      = new LabelledClearableTextField(Translation.getCurrent().name());
        setPadding(new Insets(5));
        setHgap(5);
        setVgap(5);

        GridPane.setHgrow(comboFamily, Priority.ALWAYS);
        GridPane.setHgrow(textName, Priority.ALWAYS);
        GridPane.setHgrow(comboTag, Priority.ALWAYS);

        comboFamily.setMaxWidth(Double.MAX_VALUE);
        textName.setMaxWidth(Double.MAX_VALUE);
        comboTag.setMaxWidth(Double.MAX_VALUE);
        
        comboTag.setConverter(new StringConverter<Tag>() {

			@Override
			public String toString(Tag object) {
				if(object==null) {
					return null;
				}else {
					return object.getName();
				}
			}

			@Override
			public Tag fromString(String string) {
				if(myTagList==null) {
					return null;
				}else {
					return myTagList.getTag(string);
				}
			}
        	
        });


        Label helper =new Label(Translation.getCurrent().selectPlantToAdd());
        //helper.setStyle("-fx-font-weight: bold;");
        add(helper,0,0,2,1);
        add(textName, 2, 0);

        add(comboFamily, 1, 1);
        comboTag.bindSize(textName);
        comboFamily.setMinWidth(Region.USE_PREF_SIZE);
        comboTag.setMinWidth(Region.USE_PREF_SIZE);
        comboTag.setMaxWidth(Region.USE_PREF_SIZE);
        comboFamily.setMaxWidth(Region.USE_PREF_SIZE);
        
        add(comboTag, 2, 1);

        plantListFilter = new PlantListFilter(plants);
        plantListFilter.addListener(this);
        textName.textProperty().addListener((observable, oldValue, newValue) -> {
            plantListFilter.filterByName(newValue);
        });

        TreeSet<Taxon> families = new TreeSet<>(new TaxonComparatorByName());
        for (Plant plant : plants)
            families.add(plant.getFamily());
        Callback<ListView<Taxon>, ListCell<Taxon>> cellFactory = new Callback<ListView<Taxon>, ListCell<Taxon>>()
        {
            @Override
            public ListCell<Taxon> call(ListView<Taxon> param)
            {
                return new ListCell<Taxon>()
                {
                    @Override
                    protected void updateItem(Taxon item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : Translation.getCurrent().translate(item));
                    }
                };
            }
        };
        comboFamily.setCellFactory(cellFactory);
        comboFamily.setButtonCell(cellFactory.call(null));
        comboFamily.getItems().addAll(families);
        comboFamily.valueProperty().addListener((observable, oldValue, newValue) -> {
            plantListFilter.filterByFamily(newValue);
        });
        comboTag.valueProperty().addListener((observable, oldValue, newValue) -> {
            plantListFilter.filterByTag(newValue);
        });
        ImageButton editTags = new ImageButton(Resources.edit(),null,e->{new Tags().showAndWait(primaryStage, project);});
        comboTag.addSideButton(editTags, 1);

    }

    PlantListFilter getFilter()
    {
        return plantListFilter;
    }

    private void loadTags()
    {
    	// recreate array of tags
    	ObservableList<Tag> tags = comboTag.getItems();
    	Tag selected = (Tag) comboTag.getValue();
    	tags.clear();
    	tags.addAll(myTagList.getTags());
    	myTagList.getTags().addListener(new ListChangeListener<Tag>() {

    		@Override
    		public void onChanged(Change<? extends Tag> c) {
    			while(c.next()) {
    				if(c.getRemoved().contains(comboTag.getValue())) {
    					comboTag.setValue(null);
    				}
    				tags.removeAll(c.getRemoved());
    				tags.addAll(c.getAddedSubList());
    			}
				tags.sort(myTagList.getSorter());
    		}

    	}

        );
        Collections.sort(tags, myTagList.getSorter());
        tags.add(0, myTagList.notTagged);
        tags.add(0, TagInGarden.getInstance());
        tags.add(0, TagInInventory.getInstance());
        
        comboTag.setValue(selected);
    }

    @Override
    public void filteredPlantsChanged()
    {
        if (comboFamily.getSelectionModel().getSelectedItem() != plantListFilter.getFilterByFamily())
            comboFamily.getSelectionModel().select(plantListFilter.getFilterByFamily());
    }
}
