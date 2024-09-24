package org.sourceforge.kga.gui.tableRecords.expenses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.sourceforge.kga.gui.tableRecords.RecordTableProvider;
import org.sourceforge.kga.gui.tableRecords.TableRecordUtil;
import org.sourceforge.kga.translation.Translation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class AllocationProvider implements RecordTableProvider<AllocationEntry> {

	Collection<AllocationEntry> entries;
	
	public AllocationProvider(Collection<AllocationEntry> items) {
		entries = items;
	}

	@Override
	public Collection<AllocationEntry> getAllRecords() {
		List<AllocationEntry> entries = new ArrayList<AllocationEntry>(this.entries);
		return entries;
	}


	@Override
	public AllocationEntry addNew() {
		AllocationEntry ne = new AllocationEntry();
		entries.add(ne);
		return ne;
	}

	@Override
	public void remove(AllocationEntry toRemove) {
		entries.remove(toRemove);		
	}
	
	@Override
	public void AddColumns(TableView<AllocationEntry> table) {	
		ObservableList<String> allocationMethods = FXCollections.observableList(entries.stream().unordered().map(e->e.getName()).distinct().sorted().collect(Collectors.toList()));
		
		TableRecordUtil.addStringComboColumn(table, Translation.Key.allocation, "name", t -> 
			{
				if(t.getNewValue().equals(AllocationEntry.RESERVED_STRING_ALL)) {
					table.refresh();
				}else {
					t.getRowValue().setName(t.getNewValue());
					if(!allocationMethods.contains(t.getNewValue())){
						allocationMethods.add(t.getNewValue());
					}
				}
			},allocationMethods,true).setMinWidth(180);;
		TableRecordUtil.addPlantColumn(table, Translation.Key.plant, "plant", t -> {t.getRowValue().setPlant(t.getNewValue());}).setMinWidth(300);;
	}

}
