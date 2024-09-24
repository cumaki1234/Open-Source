package org.sourceforge.kga.gui.tableRecords.SeedManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.tableRecords.RecordTable;
import org.sourceforge.kga.gui.tableRecords.tableRecordTest;
import org.sourceforge.kga.gui.tableRecords.soilNutrition.SoilNutritionEntry;
import org.sourceforge.kga.gui.tableRecords.soilNutrition.SoilNutritionProvider;

public class TestSeedNutritionEntry extends tableRecordTest<SoilNutritionEntry> {
	//@Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
	
	public enum columnIndexes{date,nitrogen,phosphorus,potassium,magnesium,calcium,zinc,ph,comment};
	
	
	@Test
	public void fileAdds() throws IOException{
		ProjectFileWithChanges pf = super.getExampleGarden();
		assertEquals(0,pf.getProject().getSoilNutritionEntries().size());
		SoilNutritionEntry expected = new SoilNutritionEntry();
		expected.setNitrogen(1.0);
		expected.setPhosphorus(2.0);
		expected.setPotassium(3.0);
		expected.setComment("test");
		expected.setDate(LocalDate.now());
		expected.setMagnesium(4.0);
		expected.setZinc(5.0);
		expected.setCalcium(6.0);
		expected.setPH(7.5);
		pf.getProject().getSoilNutritionEntries().add(expected);
		ProjectFileWithChanges reopened = super.saveAndReload(pf);
		
		assertEquals(1,reopened.getProject().getSoilNutritionEntries().size());
		SoilNutritionEntry actual = reopened.getProject().getSoilNutritionEntries().iterator().next();
		assertEquals(false,expected==actual);
		assertEquals(expected.getNitrogen(),actual.getNitrogen());
		assertEquals(expected.getPhosphorus(),actual.getPhosphorus());
		assertEquals(expected.getPotassium(),actual.getPotassium());
		assertEquals(expected.getComment(),actual.getComment());
		assertEquals(expected.getDate(),actual.getDate());
		assertEquals(expected.getMagnesium(),actual.getMagnesium());
		assertEquals(expected.getZinc(),actual.getZinc());
		assertEquals(expected.getCalcium(),actual.getCalcium());
		assertEquals(expected.getPH(),actual.getPH());
	}
	
	private RecordTable<SoilNutritionEntry> getTable(Collection<SoilNutritionEntry> entries){
		SoilNutritionProvider prov = new SoilNutritionProvider(entries);
		RecordTable<SoilNutritionEntry> table = new RecordTable<SoilNutritionEntry>(prov);
		return table;
	}
	
	@Test
	public void test_uiAdds() {
		List<SoilNutritionEntry> entries = new LinkedList<SoilNutritionEntry>();
		RecordTable<SoilNutritionEntry> table = getTable(entries);
		
		assertEquals(entries.size(),ui_rowCount(table));
		ui_addRow(table);
		assertEquals(entries.size(),ui_rowCount(table));
		SoilNutritionEntry item = ui_getRow(table,0);
		assertEquals(item,entries.get(0));
	}
	

	private ProjectFileWithChanges getClean1ItemGarden() throws IOException{

		ProjectFileWithChanges pf = super.getExampleGarden();
		pf.getProject().getSoilNutritionEntries().clear();
		pf.getProject().getSoilNutritionEntries().add(new SoilNutritionEntry());
		
		pf=saveAndReload(pf);
		return pf;
	}
	
	@Test
	public void test_project_dirty() throws IOException{
		
		ProjectFileWithChanges pf = getClean1ItemGarden();
		assertEquals(false,pf.hasUnsavedChanges());
		pf.getProject().getSoilNutritionEntries().add(new SoilNutritionEntry());
		assertEquals(true,pf.hasUnsavedChanges());
	}
	
	@Test
	public void test_Nitrogen() throws Exception{
		ProjectFileWithChanges pf = getClean1ItemGarden();
		Collection<SoilNutritionEntry> entries = pf.getProject().getSoilNutritionEntries();
		RecordTable<SoilNutritionEntry> table = getTable(entries);

		assertEquals(false,pf.hasUnsavedChanges());
		super.ui_editCol(table, "4.0", 0, columnIndexes.nitrogen);
		SoilNutritionEntry item = ui_getRow(table,0);
		assertEquals(4.0,entries.iterator().next().getNitrogen());
		assertEquals(true,pf.hasUnsavedChanges());
	}
	
	@Test
	public void test_Phosphorus() {
		List<SoilNutritionEntry> entries = new LinkedList<SoilNutritionEntry>();
		RecordTable<SoilNutritionEntry> table = getTable(entries);
		
		ui_addRow(table);
		super.ui_editCol(table, "5.0", 0, columnIndexes.phosphorus);
		SoilNutritionEntry item = ui_getRow(table,0);
		assertEquals(5.0,entries.get(0).getPhosphorus());
	}
	
	@Test
	public void test_potassium() {
		List<SoilNutritionEntry> entries = new LinkedList<SoilNutritionEntry>();
		RecordTable<SoilNutritionEntry> table = getTable(entries);
		
		ui_addRow(table);
		super.ui_editCol(table, "5.0", 0, columnIndexes.potassium);
		SoilNutritionEntry item = ui_getRow(table,0);
		assertEquals(5.0,entries.get(0).getPotassium());
	}
	
	@Test
	public void test_magnesium() {
		List<SoilNutritionEntry> entries = new LinkedList<SoilNutritionEntry>();
		RecordTable<SoilNutritionEntry> table = getTable(entries);
		
		ui_addRow(table);
		super.ui_editCol(table, "5.0", 0, columnIndexes.magnesium);
		SoilNutritionEntry item = ui_getRow(table,0);
		assertEquals(5.0,entries.get(0).getMagnesium());
	}
	
	@Test
	public void test_calciumm() {
		List<SoilNutritionEntry> entries = new LinkedList<SoilNutritionEntry>();
		RecordTable<SoilNutritionEntry> table = getTable(entries);
		
		ui_addRow(table);
		super.ui_editCol(table, "5.0", 0, columnIndexes.calcium);
		SoilNutritionEntry item = ui_getRow(table,0);
		assertEquals(5.0,entries.get(0).getCalcium());
	}
	
	@Test
	public void test_zinc() {
		List<SoilNutritionEntry> entries = new LinkedList<SoilNutritionEntry>();
		RecordTable<SoilNutritionEntry> table = getTable(entries);
		
		ui_addRow(table);
		super.ui_editCol(table, "5", 0, columnIndexes.zinc);
		SoilNutritionEntry item = ui_getRow(table,0);
		assertEquals(5.0,entries.get(0).getZinc());
	}
	
	@Test
	public void test_ph() {
		List<SoilNutritionEntry> entries = new LinkedList<SoilNutritionEntry>();
		RecordTable<SoilNutritionEntry> table = getTable(entries);
		
		ui_addRow(table);
		super.ui_editCol(table, "5", 0, columnIndexes.ph);
		SoilNutritionEntry item = ui_getRow(table,0);
		assertEquals(5.0,entries.get(0).getPH());
	}
	
	@Test
	public void test_comment() {
		List<SoilNutritionEntry> entries = new LinkedList<SoilNutritionEntry>();
		RecordTable<SoilNutritionEntry> table = getTable(entries);
		
		ui_addRow(table);
		super.ui_editCol(table, "someComment", 0, columnIndexes.comment);
		SoilNutritionEntry item = ui_getRow(table,0);
		assertEquals("someComment",entries.get(0).getComment());
	}
	
	@Test
	public void test_date() {
		List<SoilNutritionEntry> entries = new LinkedList<SoilNutritionEntry>();
		RecordTable<SoilNutritionEntry> table = getTable(entries);
		
		ui_addRow(table);
		LocalDate expected = LocalDate.now();
		super.ui_editCol(table, expected, 0, columnIndexes.date);
		SoilNutritionEntry item = ui_getRow(table,0);
		assertEquals(expected,entries.get(0).getDate());
	}

}
