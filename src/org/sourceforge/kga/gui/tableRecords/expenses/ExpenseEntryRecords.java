package org.sourceforge.kga.gui.tableRecords.expenses;


import org.sourceforge.kga.Project;
import org.sourceforge.kga.gui.tableRecords.SingleElementEntryRecords;
import org.sourceforge.kga.translation.Translation.Key;


public class ExpenseEntryRecords extends SingleElementEntryRecords<ExpenseEntry> {
	
	@Override
	public Key getType() {
		return Key.expenses;
	}
	
	public ExpenseEntryRecords(Project proj) {
		super(new ExpenseProvider(proj.getExpenseEntries(),proj));
	}

}
