package org.sourceforge.kga.gui.gardenplan.analysisQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.tableRecords.expenses.AllocationEntry;
import org.sourceforge.kga.gui.tableRecords.expenses.ExpenseEntry;


public class TestCostAnalysis extends QueryTest<Entry<DatedPoint,TaxonVariety<Plant>>> {
	ProjectFileWithChanges simpleProject;
	int firstYear;
	
	
	public static final int SQ_PEP=4;
	public static final int SQ_FEN=9;
	public static final int SQ_CAR=1;
	public static final int SQ_BAS=1;
	
	public TestCostAnalysis() {
		simpleProject = super.getEmptyProject();
		firstYear = super.firstYear(simpleProject);
		simpleProject.getGarden().addYear(firstYear);
		Resources.plantList().getVariety(Resources.plantList().getPlant(ID_BELL_PEPPER), "bigBell").setSize(new Point(2,2));
		Resources.plantList().getVariety(Resources.plantList().getPlant(ID_FENNEL), "bigFen").setSize(new Point(3,3));
		super.addPlant(simpleProject, firstYear, 10, 10, ID_CARROT, "");
		super.addPlant(simpleProject, firstYear, 10, 11, ID_BASIL, "");
		super.addPlant(simpleProject, firstYear, 10, 13, ID_BASIL, "");
		super.addPlant(simpleProject, firstYear, 1, 1, ID_BELL_PEPPER, "bigBell");
		super.addPlant(simpleProject, firstYear, 4, 4, ID_FENNEL, "bigFen");
		super.addPlant(simpleProject, firstYear, 10, 12, ID_PATH, "");
	}
	
	private Entry<DatedPoint, TaxonVariety<Plant>> getPepPoint(){
		return getPlantPoint(ID_BELL_PEPPER, "bigBell");
	}
	
	private Entry<DatedPoint, TaxonVariety<Plant>> getFenPoint(){
		return getPlantPoint(ID_FENNEL, "bigFen");
	}
	
	private Entry<DatedPoint, TaxonVariety<Plant>> getCarPoint(){
		return getPlantPoint(ID_CARROT, "");
	}
	
	private Entry<DatedPoint, TaxonVariety<Plant>> getBasPoint(){
		return getPlantPoint(ID_BASIL, "");
	}
	
	private Entry<DatedPoint, TaxonVariety<Plant>> getPathPoint(){
		return getPlantPoint(ID_PATH, "");
	}
	
	private Entry<DatedPoint, TaxonVariety<Plant>> getPlantPoint(int id, String name){
		return new SimpleEntry<>(new DatedPoint(new Point(0,0),firstYear),Resources.plantList().getVariety(Resources.plantList().getPlant(id), name));
	}

	@Override
	public GardenAnalysisQueryProvider getProvider() {
		return new GardenAnalysisQueryProvider(simpleProject);
	}
	
	private static double computeCost(double total,int squaresThis,int squaresTotal) {
		return total*((double)squaresThis)/((double)squaresTotal);
	}
	
	private void test_even_expense(int yearsToDepreciate) {
		ExpenseEntry simple = new ExpenseEntry(simpleProject.getProject());
		simple.setCost(100.0);
		simple.setStartYear(firstYear);
		simple.setUsefulLifeYears(yearsToDepreciate);
		simpleProject.getProject().getExpenseEntries().clear();
		simpleProject.getProject().getExpenseEntries().add(simple);
		PlantCostField costField = new PlantCostField(simpleProject);
		int squaresTotal = SQ_PEP+SQ_FEN+SQ_CAR+2*SQ_BAS;
		assertEquals(computeCost(100.0/yearsToDepreciate,SQ_PEP,squaresTotal),costField.getNumericValue(getPepPoint()));
		assertEquals(computeCost(100.0/yearsToDepreciate,SQ_FEN,squaresTotal),costField.getNumericValue(getFenPoint()));
		assertEquals(computeCost(100.0/yearsToDepreciate,SQ_CAR,squaresTotal),costField.getNumericValue(getCarPoint()));
		assertEquals(computeCost(100.0/yearsToDepreciate,SQ_BAS,squaresTotal),costField.getNumericValue(getCarPoint()));
		assertEquals(0,costField.getNumericValue(getPathPoint()));
	}
	
	@Test
	public void testEvenExpense() {
		test_even_expense(1);
	}
	
	@Test
	public void testEvenExpense_depreciation() {
		test_even_expense(2);
	}
	

	
	private void test_allocated_expense(int yearsToDepreciate, Set<Integer> PlantIDStoAlloc) {
		String aeName="AE";
		simpleProject.getProject().getAllocationEntries().clear();
		for(int curr:PlantIDStoAlloc) {
			AllocationEntry ae = new AllocationEntry();
			ae.setName(aeName);
			ae.setPlant(new PlantOrUnregistered(Resources.plantList().getPlant(curr)));
			simpleProject.getProject().getAllocationEntries().add(ae);
		}
		ExpenseEntry simple = new ExpenseEntry(simpleProject.getProject());
		simple.setCost(100.0);
		simple.setStartYear(firstYear);
		simple.setUsefulLifeYears(yearsToDepreciate);
		simple.setAllocation("AE");
		simpleProject.getProject().getExpenseEntries().clear();
		simpleProject.getProject().getExpenseEntries().add(simple);
		PlantCostField costField = new PlantCostField(simpleProject);
		int squaresTotal = getSquaresTotal(PlantIDStoAlloc);
		assertEquals(computeCostIfInSet(100.0/yearsToDepreciate,SQ_PEP,squaresTotal,PlantIDStoAlloc,ID_BELL_PEPPER),costField.getNumericValue(getPepPoint()));
		assertEquals(computeCostIfInSet(100.0/yearsToDepreciate,SQ_FEN,squaresTotal,PlantIDStoAlloc,ID_FENNEL),costField.getNumericValue(getFenPoint()));
		assertEquals(computeCostIfInSet(100.0/yearsToDepreciate,SQ_CAR,squaresTotal,PlantIDStoAlloc,ID_CARROT),costField.getNumericValue(getCarPoint()));
		assertEquals(computeCostIfInSet(100.0/yearsToDepreciate,SQ_BAS,squaresTotal,PlantIDStoAlloc,ID_BASIL),costField.getNumericValue(getBasPoint()));
		assertEquals(0,costField.getNumericValue(getPathPoint()));
	}

	private static double computeCostIfInSet(double total,int squaresThis,int squaresTotal, Set<Integer> costed, int toCheck) {
		if(costed.contains(toCheck))
			return computeCost(total,squaresThis,squaresTotal);
		else {
			return 0;
		}
	}
	
	
	private int getSquaresTotal(Set<Integer> PlantIDStoAlloc) {
		int squaresTotal =0;
		if(PlantIDStoAlloc.contains(ID_BELL_PEPPER))
			squaresTotal += SQ_PEP;
		if(PlantIDStoAlloc.contains(ID_FENNEL))
			squaresTotal += SQ_FEN;
		if(PlantIDStoAlloc.contains(ID_CARROT))
			squaresTotal += SQ_CAR;
		if(PlantIDStoAlloc.contains(ID_BASIL))
			squaresTotal += 2*SQ_BAS;
		return squaresTotal;
	}
	
	@Test
	public void testDirectToOne() {
		test_allocated_expense(1,new HashSet<Integer>(Arrays.asList(ID_BELL_PEPPER)));
	}
	
	@Test
	public void testDirectToTwo() {
		test_allocated_expense(1,new HashSet<Integer>(Arrays.asList(ID_BELL_PEPPER,ID_FENNEL)));
	}
	
	@Test
	public void testDirectToTwoWDepreciation() {
		test_allocated_expense(2,new HashSet<Integer>(Arrays.asList(ID_BELL_PEPPER,ID_CARROT)));
	}
}
