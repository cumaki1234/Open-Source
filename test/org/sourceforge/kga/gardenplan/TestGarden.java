package org.sourceforge.kga.gardenplan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.sourceforge.kga.Garden;
import org.sourceforge.kga.KGATest;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.Garden.FindResult;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.gardenplan.EditableGarden;
import org.sourceforge.kga.gui.gardenplan.EditableGardenObserver;

public class TestGarden extends KGATest {
	
	

	protected void assertPlantEquals(ProjectFileWithChanges proj,Point p, int id, String variety) {
		assertPlantEquals(proj,firstYear(proj),p,id,variety);
	}
	
	protected void assertPlantEquals(ProjectFileWithChanges proj,int year,Point p, int id, String variety) {
		 List<TaxonVariety<Plant>> inSquare = proj.getGarden().getSpeciesNoPreview(year, p);
		 assertEquals(1,inSquare.size());
		 assertEquals(variety.toUpperCase(),inSquare.get(0).getVariety());
		 assertEquals(id,inSquare.get(0).getId());
	}
	
	protected void assertSpaceEmpty(ProjectFileWithChanges proj,int year,Point p) {
		 List<TaxonVariety<Plant>> inSquare = proj.getGarden().getSpeciesNoPreview(year, p);
		 assertTrue(inSquare==null||inSquare.size()==0 || (inSquare.size()==1 && inSquare.get(0).getId()==Plant.ID_PLANT_SPACE_ERASER));
	}

	@Test
	public void test_Add_with_variety() throws IOException {
		 ProjectFileWithChanges proj = getEmptyProject();
		 int startingVarieties = super.getPlantVarieties(ID_FENNEL).size();
		 Point p = addPlant(proj,3,2,ID_FENNEL,"licorice");
		 assertEquals(startingVarieties+1,super.getPlantVarieties(ID_FENNEL).size());
		 assertPlantEquals(proj,p,ID_FENNEL,"licorice");
		 
		 proj=super.saveAndReload(proj);
		 assertEquals(startingVarieties+1,super.getPlantVarieties(ID_FENNEL).size());
		 assertEquals(1,proj.getGarden().getAllSquares().size());
		 assertPlantEquals(proj,p,ID_FENNEL,"licorice");
		 
		 assertEquals(4,proj.getGarden().getBounds().width);
		 assertEquals(3,proj.getGarden().getBounds().height);
	}
	
	

	/**
	 * the IO Subsystem has logic that compresses adjacent blocks if they are identical.
	 * This tests that compression only happens if the variety matches.
	 * This tests that compression happens between horizontally adjacent points.
	 * @throws IOException
	 */
	@Test
	public void test_block_add_compression_variety_not_compressed_horizontal() throws IOException{
		prototype_test_block_add_compression_variety_not_compressed(1,0);
	}
	
	/**
	 * the IO Subsystem has logic that compresses adjacent blocks if they are identical.
	 * This tests that compression only happens if the variety matches.
	 * This tests that compression happens between vertically adjacent points.
	 * @throws IOException
	 */
	@Test
	public void test_block_add_compression_variety_not_compressed_vertical() throws IOException{
		prototype_test_block_add_compression_variety_not_compressed(0,1);
	}
	
	private void prototype_test_block_add_compression_variety_not_compressed(int dx, int dy) throws IOException{
		 ProjectFileWithChanges adjacentProject = getEmptyProject();
		 ProjectFileWithChanges notadjProject = getEmptyProject();
		 Point p1 = addPlant(adjacentProject,3,2,ID_FENNEL,"a");
		 assertPlantEquals(adjacentProject,p1,ID_FENNEL,"a");
		 
		 addPlant(notadjProject,3,2,ID_FENNEL,"a");
		 assertPlantEquals(notadjProject,p1,ID_FENNEL,"a");
		 
		 
		 Point p2 = addPlant(adjacentProject,3+dx,2+dy,ID_FENNEL,"a");
		 assertPlantEquals(adjacentProject,p1,ID_FENNEL,"a");
		 assertPlantEquals(adjacentProject,p2,ID_FENNEL,"a");
		 

		 addPlant(notadjProject,3+dx,2+dy,ID_FENNEL,"b");	
		 assertPlantEquals(notadjProject,p1,ID_FENNEL,"a");
		 assertPlantEquals(notadjProject,p2,ID_FENNEL,"b");	
		 
		 File adjFile = super.saveToTemp(adjacentProject);
		 File notFile = super.saveToTemp(notadjProject);
		 assertTrue(adjFile.length()<notFile.length());
		 adjacentProject = super.openProjectFile(adjFile);
		 notadjProject=super.openProjectFile(notFile);
		 assertPlantEquals(adjacentProject,p1,ID_FENNEL,"a");
		 assertPlantEquals(adjacentProject,p2,ID_FENNEL,"a");
		 assertPlantEquals(notadjProject,p1,ID_FENNEL,"a");
		 assertPlantEquals(notadjProject,p2,ID_FENNEL,"b");
		
	}

	@Test
	public void test_add_year() throws IOException{
		ProjectFileWithChanges proj = getEmptyProject();
		Garden g = proj.getGarden();
		int currYear = firstYear(proj);
		testGardenObserver obs = new testGardenObserver();
		g.addObserver(obs);
		boolean added=g.addYear(currYear);
		assertEquals(false,added);
		assertEquals(0,obs.yearsAdded.size());
		added=g.addYear(currYear+1);
		assertEquals(true,added);
		assertEquals(1,obs.yearsAdded.size());
		assertEquals(0,obs.yearsRemoved.size());
		Point p = addPlant(proj,currYear,3,2,ID_FENNEL,"");
		g.deleteYear(currYear);
		assertEquals(1,obs.yearsRemoved.size());
		assertSpaceEmpty(proj,currYear,p);
	}

	@Test
	public void test_add_Year_Copy_forward() throws IOException {
		 ProjectFileWithChanges proj = getEmptyProject();
		 Point perennialPoint = addPlant(proj,3,2,ID_FENNEL,"");
		 Point itemPoint = addPlant(proj,3,3,ID_PATH,"");
		 Point annualPoint = addPlant(proj,3,4,ID_CARROT,"");
		 assertPlantEquals(proj,perennialPoint,ID_FENNEL,"");
		 assertPlantEquals(proj,itemPoint,ID_PATH,"");
		 assertPlantEquals(proj,annualPoint,ID_CARROT,""); 
		 
		 int newYear = firstYear(proj)+1;
		 assertTrue(proj.getGarden().addYear(newYear));
		 assertPlantEquals(proj,newYear,perennialPoint,ID_FENNEL,"");
		 assertPlantEquals(proj,newYear,itemPoint,ID_PATH,"");
		 assertSpaceEmpty(proj,newYear,annualPoint); 
		 
		 proj=super.saveAndReload(proj);
		 assertEquals(2,proj.getGarden().getAllSquares().size());
		 assertEquals(2,proj.getGarden().getAllSquares().get(newYear).size());
		 assertPlantEquals(proj,newYear,perennialPoint,ID_FENNEL,"");
		 assertPlantEquals(proj,newYear,itemPoint,ID_PATH,"");
		 assertSpaceEmpty(proj,newYear,annualPoint);
		 
		 proj.getGarden().removePlant(newYear, itemPoint, null);//remove the item
		 proj.getGarden().removePlant(newYear, perennialPoint, proj.getGarden().getPlants(newYear, itemPoint).iterator().next());//remove the fennel, ensuring we hit the other side of the remove method for coverage.
		 assertSpaceEmpty(proj,newYear,perennialPoint);
		 assertSpaceEmpty(proj,newYear,itemPoint);
		 assertSpaceEmpty(proj,newYear,annualPoint);
		 proj=super.saveAndReload(proj);//ensure this remove survives saving and reopening, because there was a bug where it wasn't.
		 assertSpaceEmpty(proj,newYear,perennialPoint);
		 assertSpaceEmpty(proj,newYear,itemPoint);
		 assertSpaceEmpty(proj,newYear,annualPoint);
	}

	@Test
	public void test_find_square() throws IOException {
		 ProjectFileWithChanges proj = getEmptyProject();
		 Point perennialPoint = addPlant(proj,3,2,ID_CARROT,"");
		 ArrayList<FindResult> found = proj.getGarden().findSquare(firstYear(proj), new Point(1,1), 2, 0, 0, Resources.plantList().getPlant(ID_CARROT), true, true);
		 assertEquals(1,found.size());
		 assertEquals(ID_CARROT,found.get(0).plant.getId());
		 assertEquals(firstYear(proj),found.get(0).coordinate.year);
		 assertEquals(perennialPoint,found.get(0).coordinate.grid);
		 
		 found = proj.getGarden().findSquare(firstYear(proj), new Point(1,1), 1, 0, 0, Resources.plantList().getPlant(ID_CARROT), true, true);
		 assertEquals(0,found.size());
		 int newYear=firstYear(proj)+1;
		 proj.getGarden().addYear(newYear);
		 
		 found = proj.getGarden().findSquare(newYear, new Point(1,1), 2, 0, 0, Resources.plantList().getPlant(ID_CARROT), true, true);
		 assertEquals(0,found.size());
		 
		 found = proj.getGarden().findSquare(newYear, new Point(1,1), 2, 0, 1, Resources.plantList().getPlant(ID_CARROT), true, true);
		 assertEquals(1,found.size());
		 assertEquals(ID_CARROT,found.get(0).plant.getId());
		 assertEquals(firstYear(proj),found.get(0).coordinate.year);
		 assertEquals(perennialPoint,found.get(0).coordinate.grid);
	}
	
}
