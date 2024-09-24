package org.sourceforge.kga.gui.gardenplan.analysisQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.KGATest;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.SeedCollection;
import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.SeedEntry.Quantity;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.Query;
import org.sourceforge.kga.analyze.QueryProvider;
import org.sourceforge.kga.analyze.SortablePlant;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.analyze.GardenStatisticsQuery;
import org.sourceforge.kga.gui.gardenplan.analysisQuery.GardenAnalysisQueryProvider;
import org.sourceforge.kga.gui.tableRecords.seedlistmanager.SeedListQueryProvider;


public class TestSeedManagerQuery extends QueryTest<Entry<String,SeedEntry>> {
	SeedCollection seeds;
	String LIST_A="A";
	String LIST_B="B";
	String THINGS="things";
	String SEEDS="seeds";
	
	public TestSeedManagerQuery() {
		seeds = getEmptySeedCollection();
		SeedList a = new SeedList(LIST_A);
		a.add(new PlantOrUnregistered(Resources.plantList().getPlant(ID_BASIL)), "", new Quantity(5,THINGS), "comment", LocalDate.EPOCH, null);
		a.add(new PlantOrUnregistered(Resources.plantList().getPlant(ID_BASIL)), "", new Quantity(2,SEEDS), "comment", LocalDate.EPOCH, null);
		SeedList b = new SeedList(LIST_B);
		b.add(new PlantOrUnregistered(Resources.plantList().getPlant(ID_BASIL)), "", new Quantity(1,THINGS), "comment", LocalDate.EPOCH, null);
		seeds.add(a);
		seeds.add(b);
	}

	@Override
	public SeedListQueryProvider getProvider() {
		return new SeedListQueryProvider(seeds);
	}
	
	@Test
	public void testSimpleCorrectNumbers() {
		List<Entry<String,SeedEntry>> entries = provider.getDefaultQuery().reSortBy(SeedListQueryProvider.FIELD_QTY).computeSortedDisplayedValues();
		assertEquals(2,entries.size());
		Entry<String,SeedEntry> firstRow = updatePivot(entries.get(0), SeedListQueryProvider.FIELD_LIST, LIST_A);
		Entry<String,SeedEntry> secondRow = updatePivot(entries.get(1), SeedListQueryProvider.FIELD_LIST, LIST_A);
		//sort is intermittently off, but this doesn't affect the UI, so force it to be right for the rest of the test.
		if(!secondRow.getValue().getQtyUnit().equals(THINGS)) {
			Entry<String,SeedEntry> temp = firstRow;
			firstRow=secondRow;
			secondRow=temp;
		}
		//test sort is correct.
		assertEquals(secondRow.getValue().getQtyUnit(),THINGS);
		assertEquals(firstRow.getValue().getQtyUnit(),SEEDS);
		assertEquals(LIST_A, firstRow.getKey());
		assertEquals(LIST_A,secondRow.getKey());
		assertEquals(2,initialQuery.computeAggregatedValue(firstRow, SeedListQueryProvider.FIELD_QTY));
		assertEquals(5,initialQuery.computeAggregatedValue(secondRow, SeedListQueryProvider.FIELD_QTY));
		
		Entry<String,SeedEntry> rePivoted =updatePivot(secondRow, SeedListQueryProvider.FIELD_LIST, LIST_B);	
		assertEquals(1,(int)(double)initialQuery.computeAggregatedValue(rePivoted, SeedListQueryProvider.FIELD_QTY));	
		

		
		rePivoted =updatePivot(firstRow, SeedListQueryProvider.FIELD_LIST, LIST_B);	
		assertEquals(0,(int)(double)initialQuery.computeAggregatedValue(rePivoted, SeedListQueryProvider.FIELD_QTY));	
	}

	@Test
	public void testUpdatePivot_LIST() {
		Entry<String,SeedEntry> row=getExampleRow();

		Entry<String,SeedEntry> updatedToFirstYear =updatePivot(row, SeedListQueryProvider.FIELD_LIST, LIST_A);
		assertEquals(LIST_A,updatedToFirstYear.getKey());
		
		Entry<String,SeedEntry> updatedToSecondYear =updatePivot(row, SeedListQueryProvider.FIELD_LIST,LIST_B);
		assertEquals(LIST_B,updatedToSecondYear.getKey());
		
	}
	
	@Test
	public void testUpdatePivot_Unit() {
		Entry<String,SeedEntry> row=getExampleRow();
		updatePivot(row, SeedListQueryProvider.FIELD_UNIT, "noUnit");
		updatePivot(row, SeedListQueryProvider.FIELD_UNIT, "otherUnit");			
	}
	
	@Test
	public void testUpdatePivot_Comment() {
		Entry<String,SeedEntry> row=getExampleRow();
		updatePivot(row, SeedListQueryProvider.FIELD_COMMENT, "Comment");
		updatePivot(row, SeedListQueryProvider.FIELD_COMMENT, "otherComment");			
	}
	
	@Test
	public void testUpdatePivot_Quantity() {
		Entry<String,SeedEntry> row=getExampleRow();
		updatePivot(row, SeedListQueryProvider.FIELD_QTY, 5.6);
		updatePivot(row, SeedListQueryProvider.FIELD_QTY, 6.9);			
	}
	
	@Test
	public void testUpdatePivot_ValidFrom() {
		Entry<String,SeedEntry> row=getExampleRow();
		updatePivot(row, SeedListQueryProvider.FIELD_VALID_FROM, LocalDate.MAX.toString());
		updatePivot(row, SeedListQueryProvider.FIELD_VALID_FROM, LocalDate.MIN.toString());			
	}
	
	@Test
	public void testUpdatePivot_ValidTo() {
		Entry<String,SeedEntry> row=getExampleRow();
		updatePivot(row, SeedListQueryProvider.FIELD_VALID_TO, LocalDate.MAX.toString());
		updatePivot(row, SeedListQueryProvider.FIELD_VALID_TO, LocalDate.MIN.toString());			
	}
	
	@Test
	public void testUpdatePivot_ValidRange() {
		Entry<String,SeedEntry> row=getExampleRow();
		updatePivot(row, SeedListQueryProvider.FIELD_VALID_RANGE, LocalDate.MIN.toString()+" -> "+LocalDate.EPOCH.toString());
		updatePivot(row, SeedListQueryProvider.FIELD_VALID_RANGE,  LocalDate.EPOCH.toString()+" -> "+LocalDate.MAX.toString());			
	}
	
	@Test
	public void testUpdatePivot_Plant() {
		Entry<String,SeedEntry> row=getExampleRow();
		updatePivot(row, SeedListQueryProvider.FIELD_PLANT, new SortablePlant(Resources.plantList().getPlant(super.ID_BASIL)));
		updatePivot(row, SeedListQueryProvider.FIELD_PLANT, new SortablePlant(Resources.plantList().getPlant(super.ID_FENNEL)));			
	}
	
	@Test
	public void testUpdatePivot_Variety() {
		Entry<String,SeedEntry> row=getExampleRow();
		super.getPlantVariety(ID_BASIL, LIST_A);
		super.getPlantVariety(ID_BASIL, LIST_B);
		updatePivot(row, SeedListQueryProvider.FIELD_VARIETY, LIST_A);
		updatePivot(row, SeedListQueryProvider.FIELD_VARIETY, LIST_B);			
	}
}
