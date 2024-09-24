package org.sourceforge.kga.gui.tableRecords;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.tableRecords.harvests.UnifiedHarvestEntry;

public class TestUnifiedHarvestEntry extends tableRecordTest<UnifiedHarvestEntry> {
	//@Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
	
	public enum columnIndexes{plant,variety,unit,unit_value,first_harvest,second_harvest};
	
	
	@Test
	public void fileAdds() throws IOException{
		ProjectFileWithChanges pf = super.getExampleGarden();
		assertEquals(0,pf.getProject().getExpenseEntries().size());
		UnifiedHarvestEntry expected = new UnifiedHarvestEntry(Resources.plantList().getVariety(Resources.plantList().getPlant(ID_BASIL),""),pf.getProject());
		expected.setHarvest(2020, 2.0);
		expected.setUnit("testUnit");
		expected.setUnitValue(1.5);
		UnifiedHarvestEntry expected2 = new UnifiedHarvestEntry(Resources.plantList().getVariety(Resources.plantList().getPlant(ID_BASIL),"testVar"),pf.getProject());
		expected2.setHarvest(2020, 2.1);
		expected2.setHarvest(2021, 2.3);
		expected2.setUnit("testUnit2");
		expected2.setUnitValue(1.2);
		ProjectFileWithChanges reopened = super.saveAndReload(pf);

		assertEquals(3,reopened.getProject().getHarvestEntries().size());
		assertEquals(2,reopened.getProject().getPlantInfoEntries().size());
		UnifiedHarvestEntry actual = new UnifiedHarvestEntry(Resources.plantList().getVariety(Resources.plantList().getPlant(ID_BASIL),""),reopened.getProject());
		UnifiedHarvestEntry actual2 = new UnifiedHarvestEntry(Resources.plantList().getVariety(Resources.plantList().getPlant(ID_BASIL),"testVar"),reopened.getProject());
		
		assertEquals(expected.getUnit(),actual.getUnit());
		assertEquals(expected2.getUnit(),actual2.getUnit());
		
		assertEquals(expected.getUnitValue(),actual.getUnitValue());
		assertEquals(expected2.getUnitValue(),actual2.getUnitValue());
		
		assertEquals(expected.getHarvest(2020),actual.getHarvest(2020));
		assertEquals(expected.getHarvest(2021),actual.getHarvest(2021));
		assertEquals(expected2.getHarvest(2020),actual2.getHarvest(2020));
		assertEquals(expected2.getHarvest(2021),actual2.getHarvest(2021));
	}

}
