package org.sourceforge.kga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.sourceforge.kga.gardenplan.testGardenObserver;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.SeedFileWithChanges;
import org.sourceforge.kga.gui.actions.Language;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Iso639_1;
import org.sourceforge.kga.translation.Translation;

public abstract class KGATest {	 

	public static final int ID_FENNEL=7;
	public static final int ID_CARROT=28;
	public static final int ID_BELL_PEPPER=30;
	public static final int ID_BASIL=48;
	public static final int ID_PATH=Plant.ID_PATH;
	
	@BeforeAll
	public static void setup() {
        Preferences.initialize();
		Resources.load();	
		Translation translation = null;
        String currentTranslation = Translation.getCurrentFromPreferences();
        if (currentTranslation != null)
        {
            translation = Resources.translations().get(currentTranslation);
        }
        if (translation == null)
        {
            translation = Resources.translations().get("en");
            Translation.setCurrent(translation);
            Iso639_1.Language selected = new Language().showAndWait(null);
            translation = Resources.translations().get(selected.code);
        }
        Translation.setCurrent(translation);	
	}
	
	protected int firstYear(ProjectFileWithChanges proj) {
		 Set<Integer> years = proj.getGarden().getYears();
		 return years.iterator().next();
	}
	
	protected ProjectFileWithChanges getExampleGarden() {
		ProjectFileWithChanges projectFile = new ProjectFileWithChanges(null);
		projectFile.open(null, Resources.openFile("example.kga"));
		return projectFile;
	}
	
	protected ProjectFileWithChanges getEmptyProject() {
		ProjectFileWithChanges projectFile = new ProjectFileWithChanges(null);
		projectFile.createNew();
		return projectFile;
	}
	


	protected List<TaxonVariety<Plant>> getListOf(int width, int ... plantIDs){
		List<TaxonVariety<Plant>> toRet = new LinkedList<TaxonVariety<Plant>>();
		for (int curr:plantIDs) {
			TaxonVariety<Plant> retrieved = getPlantVariety(curr, "");
			retrieved.setSize(new Point(width,width));
			toRet.add(getPlantVariety(curr, ""));
		}
		return toRet;
	}
	
	protected TaxonVariety<Plant> getPlantVariety(int ID, String variety) {
		return Resources.plantList().getVariety(Resources.plantList().getPlant(ID),variety);
	}
	
	protected Set<TaxonVariety<Plant>> getPlantVarieties(int ID) {
		return Resources.plantList().getPlant(ID).getVarieties();
	}
	
	protected ProjectFileWithChanges openProjectFile(File source) throws java.io.IOException{
		ProjectFileWithChanges projectFile = new ProjectFileWithChanges(null);
		projectFile.open(source,null);//, new FileInputStream(source));
		return projectFile;
	}
	


	protected SeedCollection loadSeperateSeedFile(String path) {
		SeedFileWithChanges seedFile = new SeedFileWithChanges(null);
		InputStream stream = Resources.class.getResourceAsStream("/" + path);
		assertNotNull(stream);
		seedFile.open(null,stream);
		return seedFile.getSeedCollection();
	}
	
	protected SeedCollection loadSeperatev2Seeds() {
		return loadSeperateSeedFile("org/sourceforge/kga/io/seedsV2.seed");
	}
	
	protected SeedCollection loadSeperatev1Seeds() {
		return loadSeperateSeedFile("org/sourceforge/kga/io/seedsV1.seed");
	}
	
	protected SeedList getNonEmptySeedList() {
		SeedCollection col = loadSeperatev2Seeds();
		SeedList file = col.iterator().next();
		return file;
	}
	
	protected SeedCollection getEmptySeedCollection() {
		SeedCollection col = new SeedCollection(new SeedList.Listener() {

			@Override
			public void viewChanged() {				
			}

			@Override
			public void listChanged() {				
			}
			
		});
		return col;
	}
	
	protected ProjectFileWithChanges saveAndReload(ProjectFileWithChanges pf) throws IOException{
		File temp = saveToTemp(pf);
		return openProjectFile(temp);
	}
	
	protected File saveToTemp(ProjectFileWithChanges pf) throws IOException{
		assertEquals(true,pf.hasUnsavedChanges());
		File temp = File.createTempFile("unittest", ".kga");
		File old=pf.getFile();
		pf.setFile(temp);
		pf.save(false);	
		pf.setFile(old);
		return temp;
	}
	
	protected Point addPlant( ProjectFileWithChanges proj, int year,int x, int y, int id, String variety) {
		 Garden g = proj.getGarden();
		 testGardenObserver obs = new testGardenObserver();
		 g.addObserver(obs);
		 Point p = new Point(x,y);
		 g.addPlant(year, p, getPlantVariety(id, variety));
		 g.removeObserver(obs);
		 assertTrue(obs.boundsChanged.size()==1 || obs.plantsChanged.size()==1);
		 return p;		
	}
	
	protected Point addPlant( ProjectFileWithChanges proj, int x, int y, int id, String variety) {
		 Set<Integer> years = proj.getGarden().getYears();
		 assertEquals(1,years.size());
		 int year = years.iterator().next();
		 return addPlant(proj,year,x,y,id,variety);
	}
	
}
