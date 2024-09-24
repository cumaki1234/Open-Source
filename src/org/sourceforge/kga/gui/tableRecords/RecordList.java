package org.sourceforge.kga.gui.tableRecords;

import java.util.Collection;

import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.translation.Translation;

public interface RecordList <T>{

	public Translation.Key getType();
	
	public Iterable<T> getCollection();
	
	public TreeDisplayable<T> getDisplayable(T of);
	
	public T addNew();
	
	public TreeDisplayable<Translation.Key> getRootDisplayable();
	
	public boolean canAddChildren();
}
