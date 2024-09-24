package org.sourceforge.kga.gui.tableRecords;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Project;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.tableRecords.RecordTable;
import org.sourceforge.kga.gui.tableRecords.expenses.AllocationEntry;
import org.sourceforge.kga.gui.tableRecords.expenses.ExpenseEntry;
import org.sourceforge.kga.gui.tableRecords.expenses.ExpenseProvider;
import org.sourceforge.kga.gui.tableRecords.soilNutrition.SoilNutritionEntry;
import org.sourceforge.kga.gui.tableRecords.soilNutrition.SoilNutritionProvider;

public class TestExpenseEntry extends tableRecordTest<ExpenseEntry> {
	//@Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
	
	public enum columnIndexes{description,cost,year,usefulListYears,allocation,comment};
	
	
	@Test
	public void fileAdds() throws IOException{
		ProjectFileWithChanges pf = super.getExampleGarden();
		assertEquals(0,pf.getProject().getExpenseEntries().size());
		ExpenseEntry expected = new ExpenseEntry(pf.getProject());
		expected.setComment("comment");
		expected.setAllocation("someAlloc");
		expected.setCost(1.5);
		expected.setDescription("desc");
		expected.setStartYear(1945);
		expected.setUsefulLifeYears(7);
		HashSet<Plant> plants = new HashSet<Plant>();
		plants.addAll(Arrays.asList(new Plant[] {Resources.plantList().getPlant(super.ID_BASIL)}));
		pf.getProject().getExpenseEntries().add(expected);
		ProjectFileWithChanges reopened = super.saveAndReload(pf);
		
		assertEquals(1,reopened.getProject().getExpenseEntries().size());
		ExpenseEntry actual = reopened.getProject().getExpenseEntries().iterator().next();
		assertEquals(false,expected==actual);
		assertEquals(expected.getComment(),actual.getComment());
		assertEquals(expected.getAllocation(),actual.getAllocation());
		assertEquals(expected.getCost(),actual.getCost());
		assertEquals(expected.getDescription(),actual.getDescription());
		assertEquals(expected.getStartYear(),actual.getStartYear());
		assertEquals(expected.getUsefulLifeYears(),actual.getUsefulLifeYears());
	}
	
	private RecordTable<ExpenseEntry> getTable(Project proj){
		Collection<ExpenseEntry> entries = proj.getExpenseEntries();
		ExpenseProvider prov = new ExpenseProvider(entries,proj);
		RecordTable<ExpenseEntry> table = new RecordTable<ExpenseEntry>(prov);
		return table;
	}
	
	@Test
	public void test_uiAdds() {
		ProjectFileWithChanges pf = super.getEmptyProject();
		List<ExpenseEntry> entries = new ArrayList<>(pf.getProject().getExpenseEntries());
		RecordTable<ExpenseEntry> table = getTable(pf.getProject());
		
		assertEquals(entries.size(),ui_rowCount(table));
		ui_addRow(table);
		entries = new ArrayList<>(pf.getProject().getExpenseEntries());
		assertEquals(entries.size(),ui_rowCount(table));
		ExpenseEntry item = ui_getRow(table,0);
		assertEquals(item,entries.get(0));
	}
	

	private ProjectFileWithChanges getClean1ItemGarden() throws IOException{

		ProjectFileWithChanges pf = super.getExampleGarden();
		pf.getProject().getExpenseEntries().clear();
		pf.getProject().getExpenseEntries().add(new ExpenseEntry(pf.getProject()));
		
		pf=saveAndReload(pf);
		return pf;
	}
	
	@Test
	public void test_project_dirty() throws IOException{
		
		ProjectFileWithChanges pf = getClean1ItemGarden();
		assertEquals(false,pf.hasUnsavedChanges());
		pf.getProject().getExpenseEntries().add(new ExpenseEntry(pf.getProject()));
		assertEquals(true,pf.hasUnsavedChanges());
	}
	
	@Test
	public void test_cost() throws Exception{
		ProjectFileWithChanges pf = getClean1ItemGarden();
		Collection<ExpenseEntry> entries = pf.getProject().getExpenseEntries();
		RecordTable<ExpenseEntry> table = getTable(pf.getProject());

		assertEquals(false,pf.hasUnsavedChanges());
		super.ui_editCol(table, 4.0, 0, columnIndexes.cost);
		ExpenseEntry item = ui_getRow(table,0);
		assertEquals(4.0,entries.iterator().next().getCost());
		assertEquals(true,pf.hasUnsavedChanges());
	}
	
	@Test
	public void test_useful_life() {
		ProjectFileWithChanges pf = super.getEmptyProject();
		RecordTable<ExpenseEntry> table = getTable(pf.getProject());
		
		ui_addRow(table);
		super.ui_editCol(table, 5, 0, columnIndexes.usefulListYears);
		ExpenseEntry item = ui_getRow(table,0);
		List<ExpenseEntry> entries = new ArrayList<>(pf.getProject().getExpenseEntries());
		assertEquals(5,entries.get(0).getUsefulLifeYears());
	}
	
	@Test
	public void test_year() {
		ProjectFileWithChanges pf = super.getEmptyProject();
		RecordTable<ExpenseEntry> table = getTable(pf.getProject());
		
		ui_addRow(table);
		super.ui_editCol(table, 1999, 0, columnIndexes.year);
		ExpenseEntry item = ui_getRow(table,0);
		List<ExpenseEntry> entries = new ArrayList<>(pf.getProject().getExpenseEntries());
		assertEquals(1999,entries.get(0).getStartYear());
	}
	
	@Test
	public void test_comment() {
		ProjectFileWithChanges pf = super.getEmptyProject();
		RecordTable<ExpenseEntry> table = getTable(pf.getProject());
		
		ui_addRow(table);
		super.ui_editCol(table, "someComment", 0, columnIndexes.comment);
		ExpenseEntry item = ui_getRow(table,0);
		List<ExpenseEntry> entries = new ArrayList<>(pf.getProject().getExpenseEntries());
		assertEquals("someComment",entries.get(0).getComment());
	}
	
	@Test
	public void test_description() {
		ProjectFileWithChanges pf = super.getEmptyProject();
		RecordTable<ExpenseEntry> table = getTable(pf.getProject());
		
		ui_addRow(table);
		super.ui_editCol(table, "someDesc", 0, columnIndexes.description);
		ExpenseEntry item = ui_getRow(table,0);
		List<ExpenseEntry> entries = new ArrayList<>(pf.getProject().getExpenseEntries());
		assertEquals("someDesc",entries.get(0).getDescription());
	}
	
	@Test
	public void test_allocation_ui() {
		ProjectFileWithChanges pf = super.getEmptyProject();
		AllocationEntry ae = new AllocationEntry();
		ae.setName("someAlloc");
		ae.setPlant(new PlantOrUnregistered(Resources.plantList().getPlant(super.ID_BASIL)));
		RecordTable<ExpenseEntry> table = getTable(pf.getProject());
		
		ui_addRow(table);
		super.ui_editCol(table, "someAlloc", 0, columnIndexes.allocation);
		ExpenseEntry item = ui_getRow(table,0);
		List<ExpenseEntry> entries = new ArrayList<>(pf.getProject().getExpenseEntries());
		assertEquals("someAlloc",entries.get(0).getAllocation());
	}
	
	@Test
	public void test_allocation_model() {
		ProjectFileWithChanges pf = super.getEmptyProject();
		AllocationEntry ae = new AllocationEntry();
		ae.setName("someAlloc");
		ae.setPlant(new PlantOrUnregistered(Resources.plantList().getPlant(ID_BASIL)));
		pf.getProject().getAllocationEntries().add(ae);
		ae = new AllocationEntry();
		ae.setName("someAlloc");
		ae.setPlant(new PlantOrUnregistered(Resources.plantList().getPlant(ID_CARROT)));
		pf.getProject().getAllocationEntries().add(ae);
		
		ExpenseEntry expected = new ExpenseEntry(pf.getProject());
		Set<Plant> plants = expected.getDirectPlants();
		assertEquals(0,plants.size());
		expected.setAllocation("someAlloc");
		expected.setCost(1.5);
		plants = expected.getDirectPlants();
		assertEquals(2,plants.size());
		for(Plant curr : plants) {
			assertEquals(true,curr.getId()==ID_BASIL || curr.getId()==ID_CARROT);
		}
		
	}
	

}
