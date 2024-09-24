package org.sourceforge.kga;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.Map.Entry;

import org.sourceforge.kga.gui.FileWithChanges;
import org.sourceforge.kga.translation.Translation;


/**
 * Created by tidu8815 on 21/03/2018.
 */
public class SeedCollection implements Iterable<SeedList>
{
    private ArrayList<SeedList> seedLists;
    private Set<Listener> listeners;
    SeedList.Listener addToAll;
    
    public SeedCollection(SeedList.Listener addToAll) {
    	seedLists=new ArrayList<SeedList>();
    	listeners = new HashSet<Listener>();
    	addDefaultLists(); 
    	this.addToAll=addToAll;
    }
    
    private void addDefaultLists() {
    	seedLists.add(new SeedList(Translation.getCurrent().translate(Translation.Key.seed_manager_inventory)));
    	seedLists.add(new SeedList(Translation.getCurrent().translate(Translation.Key.seed_manager_shopping_list)));
    	
    }
    
    public boolean isEmptyDefault() {
    	if (seedLists.size()!=2) {
    		return false;
    	}
    	for(SeedList curr:seedLists) {
    		if(!curr.getName().equals(Translation.getCurrent().translate(Translation.Key.seed_manager_inventory)) ||
    				!curr.getName().equals(Translation.getCurrent().translate(Translation.Key.seed_manager_shopping_list))) {
    			return false;
    		}
    		if(curr.seedsEntries.size()!=0) {
    			return false;
    		}
    	}
    	return true;
    }

    public Stream<Entry<String, SeedEntry>> stream(){
    	Collection<Stream<Entry<String, SeedEntry>>> allStreams = new LinkedList<>();
    	for(SeedList curr : seedLists) {
    		String name = curr.getName();
    		allStreams.add(curr.seedsEntries.stream().map(se->new AbstractMap.SimpleEntry<String, SeedEntry>(name,se)));
    	}
    	Stream<Entry<String, SeedEntry>> result =  Stream.of(allStreams.toArray(new Stream[allStreams.size()])).flatMap(i->i);
    	return result;
    }
    
    public void addListener(Listener l) {
    	listeners.add(l);
    }
    
    public void removeListener(Listener l) {
    	listeners.remove(l);
    }
    
    public ArrayList<SeedList> test_getSeedLists(){
    	return seedLists;
    }

    public void deleteAllSeedLists() {
    	deleteAllSeedLists(false);
    }
    public void deleteAllSeedLists(boolean addDefaultLists) {
    	for(SeedList curr : seedLists)
        	curr.removeListener(addToAll);
    		
    	seedLists.clear();
    	notifyListenersOfChange();
    	if(addDefaultLists) {
    		addDefaultLists();
    	}
    }
    
    public void add(SeedList toAdd) {
    	seedLists.add(toAdd);
    	toAdd.setDate(date);
    	toAdd.addListener(addToAll);
    	notifyListenersOfChange();
    }
    
    public void remove(SeedList toAdd) {
    	toAdd.removeListener(addToAll);
    	seedLists.remove(toAdd);
    	notifyListenersOfChange();
    }

    public interface Listener
    {
        public void SeedListChanged();
        public void viewChanged();
    }
    
    public void notifyListenersOfChange() {
    	for(Listener curr:listeners) {
    		curr.SeedListChanged();
    	}
    }

	@Override
	public Iterator<SeedList> iterator() {
		return Collections.unmodifiableCollection(seedLists).iterator();
	}
	
    public Set<LocalDate> getValidFromDates()
    {
    	Set<LocalDate>  toReturn = new HashSet<LocalDate>();
    	for (SeedList seedList : seedLists)
            toReturn.addAll(seedList.getValidFromDates());
        return toReturn;
    }
    

    LocalDate date;
    public void setDate(LocalDate date)
    {
        this.date = date;
        for (SeedList seedList : seedLists)
            seedList.setDate(date);
    }
    

    public void importTo(SeedCollection c, boolean eraseExisting) {
    	if (eraseExisting) {
    		c.deleteAllSeedLists();
    	}
    	for (SeedList l: this) {
    		c.add(l);
    	}
    }
}
