package org.sourceforge.kga.gui.tableRecords;

import java.util.ArrayList;
import java.util.List;

import org.sourceforge.kga.translation.Translation;

import javafx.scene.Node;

public abstract class SingleElementEntryRecords <T>  implements RecordList<List<T>> {

	protected RecordTableProvider<T> p;
	
	public SingleElementEntryRecords(RecordTableProvider<T> provider) {
		p=provider;
	}

	@Override
	public Iterable<List<T>> getCollection() {
		return new ArrayList<List<T>>();
	}

	@Override
	public TreeDisplayable<List<T>> getDisplayable(List<T> of) {
		return null;
	}

	@Override
	public List<T> addNew() {
		return new ArrayList<T>();
	}

	@Override
	public TreeDisplayable<Translation.Key> getRootDisplayable() {
		return new TreeDisplayable<Translation.Key>(getType(),new TreeDisplayable.unDeleteableNodeGenerator<Translation.Key>() {

			public Node getDisplayNode(Translation.Key myData) {
				RecordTable toShow =  new RecordTable<T>(p);
				return toShow;
			}
		}, true, false);
	}

	@Override
	public boolean canAddChildren() {
		return false;
	}

}
