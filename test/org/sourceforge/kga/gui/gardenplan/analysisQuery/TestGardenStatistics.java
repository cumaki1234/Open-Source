package org.sourceforge.kga.gui.gardenplan.analysisQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.KGATest;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.Query;
import org.sourceforge.kga.analyze.QueryProvider;
import org.sourceforge.kga.analyze.SortablePlant;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.analyze.GardenStatisticsQuery;
import org.sourceforge.kga.gui.gardenplan.analysisQuery.GardenAnalysisQueryProvider;


public class TestGardenStatistics extends QueryTest<Entry<DatedPoint,TaxonVariety<Plant>>> {
	ProjectFileWithChanges simpleProject;
	int firstYear;
	
	public TestGardenStatistics() {
		simpleProject = super.getEmptyProject();
		firstYear = super.firstYear(simpleProject);
		simpleProject.getGarden().addYear(firstYear+1);
		super.addPlant(simpleProject, firstYear, 10, 10, ID_CARROT, "");
		super.addPlant(simpleProject, firstYear, 10, 11, ID_CARROT, "");
		super.addPlant(simpleProject, firstYear+1, 10, 10, ID_CARROT, "");	
	}

	@Override
	public GardenAnalysisQueryProvider getProvider() {
		return new GardenAnalysisQueryProvider(simpleProject);
	}
	
	@Test
	public void testSimpleNoDupes() {
		Set<Plant> found = new HashSet<>();
		for (Entry<DatedPoint,TaxonVariety<Plant>> curr : initialQuery.computeSortedDisplayedValues()) {
			assertTrue(found.add(curr.getValue().getTaxon()));
		}
		assertEquals(1,found.size());
	}
	
	@Test
	public void testSimpleCorrectNumbers() {
		Entry<DatedPoint,TaxonVariety<Plant>> row=getExampleRow();
		
		Entry<DatedPoint,TaxonVariety<Plant>> updatedToFirstYear =updatePivot(row, GardenAnalysisQueryProvider.FIELD_YEAR, firstYear);	
		assertEquals(2,initialQuery.computeAggregatedValue(updatedToFirstYear, GardenAnalysisQueryProvider.FIELD_PLANT_COUNT));	
		
		Entry<DatedPoint,TaxonVariety<Plant>> updatedToSecondYear =GardenAnalysisQueryProvider.FIELD_YEAR.updateValueforPivot(row, firstYear+1);		
		assertEquals(1,initialQuery.computeAggregatedValue(updatedToSecondYear, GardenAnalysisQueryProvider.FIELD_PLANT_COUNT));
	}

	@Test
	public void testUpdatePivot_Year() {
		Entry<DatedPoint,TaxonVariety<Plant>> row=getExampleRow();

		Entry<DatedPoint,TaxonVariety<Plant>> updatedToFirstYear =updatePivot(row, GardenAnalysisQueryProvider.FIELD_YEAR, firstYear);
		assertEquals(firstYear,updatedToFirstYear.getKey().getYear());
		
		Entry<DatedPoint,TaxonVariety<Plant>> updatedToSecondYear =updatePivot(row, GardenAnalysisQueryProvider.FIELD_YEAR, firstYear+1);
		assertEquals(firstYear+1,updatedToSecondYear.getKey().getYear());
		
	}

	@Test
	public void testUpdatePivot_Plant() {
		Entry<DatedPoint,TaxonVariety<Plant>> row=getExampleRow();

		updatePivot(row, GardenAnalysisQueryProvider.FIELD_PLANT, new SortablePlant(Resources.plantList().getPlant(super.ID_BASIL)));
		updatePivot(row, GardenAnalysisQueryProvider.FIELD_PLANT, new SortablePlant(Resources.plantList().getPlant(super.ID_FENNEL)));			
	}

	@Test
	public void testUpdatePivot_Variety() {
		Entry<DatedPoint,TaxonVariety<Plant>> row=getExampleRow();
		getPlantVariety(ID_CARROT,"test1");
		getPlantVariety(ID_CARROT,"test2");
		updatePivot(row, GardenAnalysisQueryProvider.FIELD_VARIETY, "TEST1");
		updatePivot(row, GardenAnalysisQueryProvider.FIELD_VARIETY, "TEST2");			
	}
}
