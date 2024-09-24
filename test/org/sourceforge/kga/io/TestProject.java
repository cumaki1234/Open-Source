package org.sourceforge.kga.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.sourceforge.kga.KGATest;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.SeedCollection;
import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.gui.FileWithChanges;
import org.sourceforge.kga.gui.ProjectFileWithChanges;


public class TestProject extends KGATest{

	
	private class wasChangeCalled implements FileWithChanges.Listener{
		public boolean objectChanged;
		public boolean unsavedChangesChanged;
		
		public wasChangeCalled() {
			objectChanged=unsavedChangesChanged=false;
		}

		@Override
		public void objectChanged() {
			objectChanged=true;
		}

		@Override
		public void unsavedChangesChanged() {
			unsavedChangesChanged=true;
		}
		
	}

	public ProjectFileWithChanges mergeSeedlistIntoProject(ProjectFileWithChanges sourceGarden, SeedCollection seedFile) throws IOException{
		File temp = File.createTempFile("unitTest", ".kga");
		temp.deleteOnExit();
		seedFile.importTo(sourceGarden.getProject().getSeedCollection(), true);
		/*sourceGarden.getProject().getSeedCollection().deleteAllSeedLists();
		for(SeedList curr : seedFile.getSeedCollection())
			sourceGarden.getProject().getSeedCollection().add(curr);*/
		sourceGarden.setFile(temp);
		sourceGarden.save(false);

		ProjectFileWithChanges projectFile = new ProjectFileWithChanges(null);
		projectFile.open(null, new FileInputStream(temp));
		return projectFile;
	}
	

	protected void assertCollectionEquals(SeedCollection source, SeedCollection dest) {
		assertNotNull(source.test_getSeedLists());
		assertNotNull(dest.test_getSeedLists());
		assertEquals(source.test_getSeedLists().size(), dest.test_getSeedLists().size());
		for (int i = 0; i<source.test_getSeedLists().size();i++) {
			SeedList sourceL = source.test_getSeedLists().get(i);
			SeedList destL =dest.test_getSeedLists().get(i);
			assertEquals(sourceL.getName(),destL.getName());
			assertEquals(sourceL.seedsEntries.size(),destL.seedsEntries.size());
			for (int j=0;j<sourceL.seedsEntries.size();j++) {
				SeedEntry sEntry = sourceL.seedsEntries.get(j);
				SeedEntry dEntry = destL.seedsEntries.get(j);
				assertEquals(sEntry.getComment(),dEntry.getComment());
				assertEquals(sEntry.getPlant(),dEntry.getPlant());
				assertEquals(sEntry.getVariety(),dEntry.getVariety());
				assertEquals(sEntry.getQty(),dEntry.getQty());
				assertEquals(sEntry.getQtyUnit(),dEntry.getQtyUnit());
				assertEquals(sEntry.getValidFrom(),dEntry.getValidFrom());
				assertEquals(sEntry.getValidTo(),dEntry.getValidTo());
			}
		}
	}
	
	@Test
	public void check_merged_v1seedlist_matches() throws IOException{
		ProjectFileWithChanges merged = mergeSeedlistIntoProject(getExampleGarden(),loadSeperatev1Seeds());
		SeedCollection source = loadSeperatev1Seeds();
		assertNotNull(source);
		assertNotNull( merged.getProject().getSeedCollection());
		assertCollectionEquals(source,merged.getProject().getSeedCollection());		
	}
	
	@Test
	public void check_merged_v2seedlist_matches() throws IOException{
		ProjectFileWithChanges merged = mergeSeedlistIntoProject(getExampleGarden(),loadSeperatev2Seeds());
		SeedCollection source = loadSeperatev2Seeds();
		assertNotNull(source);
		assertNotNull( merged.getProject().getSeedCollection());
		assertCollectionEquals(source,merged.getProject().getSeedCollection());		
	}
	
	@Test
	public void testListenerCalled() throws IOException{
		ProjectFileWithChanges example = getExampleGarden();
		wasChangeCalled listener = new wasChangeCalled();
		example.addListener(listener);
		ProjectFileWithChanges merged = mergeSeedlistIntoProject(example,loadSeperatev2Seeds());
		assertEquals(true,listener.unsavedChangesChanged);
		assertEquals(true,merged.hasUnsavedChanges());
	}
	
	@Test
	public void testVarietySizeSaves() throws IOException{
		ProjectFileWithChanges example = getExampleGarden();
		getPlantVariety(ID_BASIL, "testVarietySizeSaves").setSize(new Point(10,10));
		File saved = super.saveToTemp(example);
		getPlantVariety(ID_BASIL, "testVarietySizeSaves").setSize(Plant.LEGACY_DEFAULT_SIZE);
		example=super.openProjectFile(saved);
		assertEquals(10,getPlantVariety(ID_BASIL, "testVarietySizeSaves").getSize().x);
	}
}
