package org.sourceforge.kga.gui.tableRecords.harvests;


import org.sourceforge.kga.Project;
import org.sourceforge.kga.gui.tableRecords.SingleElementEntryRecords;
import org.sourceforge.kga.translation.Translation.Key;


public class HarvestEntryRecords extends SingleElementEntryRecords<UnifiedHarvestEntry> {
	
	@Override
	public Key getType() {
		return Key.harvest;
	}
	
	public HarvestEntryRecords(Project proj) {
		super(new HarvestProvider(proj));
	}

}
