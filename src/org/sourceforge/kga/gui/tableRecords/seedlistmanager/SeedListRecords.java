package org.sourceforge.kga.gui.tableRecords.seedlistmanager;

import java.util.Collection;

import org.sourceforge.kga.SeedCollection;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.gui.tableRecords.RecordList;
import org.sourceforge.kga.gui.tableRecords.TreeDisplayable;
import org.sourceforge.kga.gui.tableRecords.TreeDisplayable.nodeGenerator;
import org.sourceforge.kga.translation.Translation;
import org.sourceforge.kga.translation.Translation.Key;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class SeedListRecords implements RecordList<SeedList> {
	SeedCollection collection;
	
	public SeedListRecords(SeedCollection collection) {
		this.collection=collection;
	}

	@Override
	public Key getType() {
		// TODO Auto-generated method stub
		return Translation.Key.action_seed_manager;
	}

	@Override
	public Iterable<SeedList> getCollection() {
		// TODO Auto-generated method stub
		return collection;
	}

	@Override
	public TreeDisplayable<SeedList> getDisplayable(final SeedList curr) {
		return new TreeDisplayable<SeedList>(curr,new nodeGenerator<SeedList>() {

			@Override
			public Node getDisplayNode(SeedList myData) {
				return new SeedTable(curr);
			}

			@Override
			public void delete(SeedList myData) {
				collection.remove(myData);
			}
		}, true, true);
	}

	@Override
	public SeedList addNew() {
    	SeedList l = new SeedList(Translation.getCurrent().unknown());
    	collection.add(l);
    	return l;
	}

	@Override
	public TreeDisplayable<Translation.Key> getRootDisplayable() {
		return new TreeDisplayable<Translation.Key>(getType(),null, false, false);
	}

	@Override
	public boolean canAddChildren() {
		// TODO Auto-generated method stub
		return true;
	}

}
