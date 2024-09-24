package org.sourceforge.kga.io;

import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

public interface SaveableRecordRow {
	
	enum recordType {SoilNutritionEntry,expense,expense_allocations,harvest,plant_info};
	
	public void save(XmlWriter writer) throws XmlException;
	
	public void load(XmlReader xml, int version);

}
