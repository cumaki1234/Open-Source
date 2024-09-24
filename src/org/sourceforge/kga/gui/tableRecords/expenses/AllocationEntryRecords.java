package org.sourceforge.kga.gui.tableRecords.expenses;


import org.sourceforge.kga.Project;
import org.sourceforge.kga.gui.tableRecords.SingleElementEntryRecords;
import org.sourceforge.kga.translation.Translation.Key;


public class AllocationEntryRecords extends SingleElementEntryRecords<AllocationEntry> {
	
	@Override
	public Key getType() {
		return Key.expense_allocations;
	}
	
	public AllocationEntryRecords(Project proj) {
		super(new AllocationProvider(proj.getAllocationEntries()));
	}

}
