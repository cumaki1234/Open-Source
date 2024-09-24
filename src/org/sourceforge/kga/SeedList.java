package org.sourceforge.kga;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.SeedEntry.Quantity;
import org.sourceforge.kga.translation.Translation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SeedList
{
    public interface Listener
    {
        void viewChanged();
        
        void listChanged();
    }
    ArrayList<Listener> listeners = new ArrayList<>();
    String name = new String("");

    public SeedList(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    
    public String toString() {
    	return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public synchronized void addListener(Listener listener)
    {
        listeners.add(listener);
    }

    public synchronized void removeListener(Listener listener)
    {
        listeners.remove(listener);
    }


    static private Set<String> units = new TreeSet<>();

    static
    {
        Translation t = Translation.getCurrent();
        units.add(t.measurement_unit_grams());
        units.add(t.measurement_unit_pieces());
    }
    
    static public Set<String> getUnits()
    {
        return units;
    }


    private LocalDate date = LocalDate.now();
    private ArrayList<SeedEntry> seedView = new ArrayList<>();
    private Set<LocalDate> dates = new TreeSet<>();

    public void sortSeedView()
    {
        Collections.sort(seedView, new java.util.Comparator<SeedEntry>() {
            @Override
            public int compare(SeedEntry o1, SeedEntry o2)
            {
                Translation t = Translation.getCurrent();
                return t.getCollator().compare(o1.getPlantName(true), o2.getPlantName(true));
            }
        });
    }

    public void setDate(LocalDate date)
    {
    	this.date = date;
    	seedView.clear();
    	if(date!=null) {
    		for (SeedEntry entry : seedsEntries)
    			if (entry.isValid(date))
    				seedView.add(entry);
    		sortSeedView();
    	}
        for (Listener listener : listeners)
            listener.viewChanged();
    }
    
    public LocalDate getDate()
    {
        return date;
    }
    
    public Set<LocalDate> getValidFromDates()
    {
        return dates;
    }

    public int size()
    {
        return seedView.size();
    }

    public SeedEntry get(int index)
    {
        return seedView.get(index);
    }
    
    public ArrayList<SeedEntry> getSeedView()
    {
        return seedView;
    }
    
    public ArrayList<SeedEntry> getAllEntries()
    {
        return seedsEntries;
    }
    

    public ArrayList<SeedEntry> seedsEntries = new ArrayList<>();

    synchronized void fireListChanged()
    {
    	try {
        for (Listener listener : listeners)
            listener.listChanged();
    	}
    	catch (ConcurrentModificationException e) {
    		fireListChanged();
    	}
    }

    private void add(SeedEntry entry)
    {
        if (entry.getQuantity() != null && entry.getQuantity().unit != null)
            units.add(entry.getQuantity().unit);
        seedsEntries.add(entry);
        dates.add(entry.getValidFrom());
        if(date!=null) {
        	if (entry.isValid(date))
        		seedView.add(entry);
        }
        fireListChanged();
    }

    public SeedEntry add(PlantOrUnregistered plantOrUnregistered, String variety,
            Quantity quantity, String comment, LocalDate validFrom,
            LocalDate validTo)
    {
    	SeedEntry toAdd =new SeedEntry(plantOrUnregistered, variety, quantity, comment, validFrom, validTo, this); 
        add(toAdd);
        return toAdd;
    }

    public SeedEntry add(Plant plant, LocalDate date)
    {
        return add(new PlantOrUnregistered(plant), null, null, null, date, null);
    }
    
    public SeedEntry add(String plant, LocalDate date)
    {
        return add(new PlantOrUnregistered(plant), null, null, null, date, null);
    }
 /*
    private void modifyEntry(SeedEntry entry, LocalDate date)
    {
        if (!entry.validFrom.equals(date))
        {
            SeedEntry oldEntry = (SeedEntry) entry.clone();
            oldEntry.validTo = date;
            seedsEntries.add(oldEntry);
            dates.add(date);
            entry.validFrom = date;
        }
    }
*/
    public void remove(SeedEntry entry, LocalDate date) throws  IllegalArgumentException
    {
        boolean inView = entry.isValid(date);
        entry.remove(date);
        if (entry.getValidFrom().equals(date))
        {
            seedsEntries.remove(entry);
        }
        if (inView)
            seedView.remove(entry);
        fireListChanged();
    }

    /*
    public void setVariety(Entry entry, String variety, LocalDate date)
    {
        if (!entry.validFrom.equals(date))
            throw new IllegalArgumentException("Can not modify variety on a different date");
        if (compareNullStrings(entry.variety, variety) == 0)
            return;
        entry.variety = variety;
        fireListChanged();
    }
    
    public void setComment(Entry entry, String comment, LocalDate date)
    {
        if (!entry.validFrom.equals(date))
            throw new IllegalArgumentException("Can not modify comment on a different date");
        if (compareNullStrings(entry.comment, comment) == 0)
            return;
        entry.comment = comment;
        fireListChanged();
    }
    
    public void setQuantity(Entry entry, Quantity quantity, LocalDate date)
    {
        modifyEntry(entry, date);
        if (entry.quantity == null && quantity == null)
            return;
        entry.quantity = quantity;
        if (quantity != null && quantity.unit != null)
            units.add(quantity.unit);
        fireListChanged();
    }
    
    static public class PlantOrUnregisteredComparatorByName implements java.util.Comparator<PlantOrUnregistered>
    {
        @Override
        public int compare(PlantOrUnregistered o1, PlantOrUnregistered o2)
        {
            Translation t = Translation.getCurrent();
            boolean p1IsItem = o1.plant != null && o1.plant.isItem();
            boolean p2IsItem = o2.plant != null && o2.plant.isItem();
            String p1Name = o1.unregisteredPlant != null ? o1.unregisteredPlant : t.translate(o1.plant);
            String p2Name = o2.unregisteredPlant != null ? o2.unregisteredPlant : t.translate(o2.plant);
            if (p1IsItem == p2IsItem)
                return t.getCollator().compare(p1Name, p2Name);
            return p1IsItem ? 1 : -1;
        }
    } */
    
    public static List<PlantOrUnregistered> getKnownPlants(){
    	ArrayList<PlantOrUnregistered> toReturn = new ArrayList<PlantOrUnregistered>(Resources.plantList().getPlants().size());
    	/*toReturn.sort(new Comparator<PlantOrUnregistered>() {

			@Override
			public int compare(PlantOrUnregistered o1, PlantOrUnregistered o2) {
				if (o1.plant!=null && o2.plant!=null) {
					return o1.plant.id-o2.plant
				}
				return 0;
			}
    		
    	};*/
    	for(Plant p : Resources.plantList().getPlants() ) {
    		toReturn.add(new PlantOrUnregistered(p));
    	}
    	return toReturn;  	
    }
}
