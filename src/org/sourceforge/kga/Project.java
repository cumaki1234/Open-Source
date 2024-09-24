package org.sourceforge.kga;

import javafx.collections.ListChangeListener;

import org.sourceforge.kga.flowlist.FlowList;
import org.sourceforge.kga.flowlist.FlowListRecordRow;
import org.sourceforge.kga.gui.gardenplan.EditableGarden;
import org.sourceforge.kga.gui.gardenplan.EditableGardenObserver;
import org.sourceforge.kga.gui.tableRecords.expenses.AllocationEntry;
import org.sourceforge.kga.gui.tableRecords.expenses.ExpenseEntry;
import org.sourceforge.kga.gui.tableRecords.harvests.HarvestEntry;
import org.sourceforge.kga.gui.tableRecords.harvests.PlantInfoEntry;
import org.sourceforge.kga.gui.tableRecords.soilNutrition.SoilNutritionEntry;
import org.sourceforge.kga.io.SaveableRecordRow;
import org.sourceforge.kga.plant.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * This class is responsible for storing all the objects inside a project:
 * garden, tags list, seeds lists, plants list.
 */
public class Project implements EditableGardenObserver, ListChangeListener<Tag>, SeedList.Listener, SeedCollection.Listener
{
    public void addObserver(ProjectObserver observer)
    {
        observers.add(observer);
    }

    public void removeObserver(ProjectObserver observer)
    {
        observers.remove(observer);
    }

    public EditableGarden garden;
    public TagList tagList;
    private SeedCollection seedCollection;
    private Map<SaveableRecordRow.recordType,FlowList<? extends SaveableRecordRow>> saveableRecords;
    
    public SeedCollection getSeedCollection() {
    	return seedCollection;
    }
    
    public boolean hasSaveableRecordRows() {
    	for (FlowList<? extends SaveableRecordRow> curr : saveableRecords.values()) {
    		if(curr.size()>0) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public Collection<SoilNutritionEntry> getSoilNutritionEntries() {
    	return getEntries(SaveableRecordRow.recordType.SoilNutritionEntry);
    }

    public Collection<ExpenseEntry> getExpenseEntries() {
    	return getEntries(SaveableRecordRow.recordType.expense);
    }

    public Collection<AllocationEntry> getAllocationEntries() {
    	return getEntries(SaveableRecordRow.recordType.expense_allocations);
    }

    public Collection<HarvestEntry> getHarvestEntries() {
    	return getEntries(SaveableRecordRow.recordType.harvest, new TreeSet<HarvestEntry>());
    }

    public Collection<PlantInfoEntry> getPlantInfoEntries() {
    	return getEntries(SaveableRecordRow.recordType.plant_info, new TreeSet<PlantInfoEntry>());
    }

    public <K extends FlowListRecordRow<K>> Collection<K> getEntries(SaveableRecordRow.recordType type){
    	return getEntries(type,new LinkedList<K>());
    }
    
    public <K extends FlowListRecordRow<K>> Collection<K> getEntries(SaveableRecordRow.recordType type, Collection<K> base){
    	if(!saveableRecords.containsKey(type)) {
    		FlowList<K> fl = new FlowList<K>(base);
    		fl.subscribe(new Subscriber<K>() {

				@Override
				public void onSubscribe(Subscription subscription) {}

				@Override
				public void onNext(K item) {
			        for (ProjectObserver observer : observers)
			            observer.projectChanged();					
				}

				@Override
				public void onError(Throwable throwable) {
				}

				@Override
				public void onComplete() {
				}
    			
    		});
    		saveableRecords.put(type, fl);
    	}
    	return (FlowList<K>)saveableRecords.get(type);
    }
    

    public Project()
    {
        garden = new EditableGarden();
        garden.addObserver(this);
        tagList = new TagList();
        tagList.getTags().addListener(this);
        seedCollection = new SeedCollection(this);
        seedCollection.addListener(this);
        saveableRecords = new HashMap<SaveableRecordRow.recordType,FlowList<? extends SaveableRecordRow>>();
    }

    private ArrayList<ProjectObserver> observers = new ArrayList();

    @Override
    public void gardenChanged(EditableGarden editableGarden)
    {
        for (ProjectObserver observer : observers)
            observer.projectChanged();
    }

    @Override
    public void zoomFactorChanged(EditableGarden editableGarden) {}

    @Override
    public void previewSpeciesChanged(EditableGarden editableGarden, TaxonVariety<Plant> plant) {}

    @Override
    public void operationChanged(EditableGarden editableGarden) {}

    @Override
    public void onChanged(Change<? extends Tag> change)
    {
        for (ProjectObserver observer : observers)
            observer.projectChanged();
    }

	@Override
	public void SeedListChanged() {
		gardenChanged(null);		
	}

	@Override
	public void viewChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listChanged() {
		gardenChanged(null);		
	}
}
