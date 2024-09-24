package org.sourceforge.kga.gui.tableRecords.harvests;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Project;
import org.sourceforge.kga.TaxonComparatorByName;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.tableRecords.RecordTableProvider;
import org.sourceforge.kga.gui.tableRecords.TableRecordUtil;
import org.sourceforge.kga.translation.Translation;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableView;

public class HarvestProvider implements RecordTableProvider<UnifiedHarvestEntry> {

	Map<TaxonVariety<Plant>,UnifiedHarvestEntry> unified;
	Project project;
	
	public HarvestProvider(Project proj) {
		Collection<HarvestEntry> entries = proj.getHarvestEntries();
		Collection<PlantInfoEntry> plantInfos = proj.getPlantInfoEntries();
		unified = new HashMap<TaxonVariety<Plant>,UnifiedHarvestEntry>();
		project=proj;
		Set<TaxonVariety<Plant>> inGarden = project.garden.streamByPlant().map(me->me.getValue()).distinct().collect(Collectors.toSet());
		for(TaxonVariety<Plant> curr : inGarden) {
			unified.put(curr, new UnifiedHarvestEntry(curr,project));
		}
		for(HarvestEntry curr : entries) {
			if(!unified.containsKey(curr.plant)) {
				unified.put(curr.plant, new UnifiedHarvestEntry(curr.plant,project));
			}
		}
		for(PlantInfoEntry curr : plantInfos) {
			if(!unified.containsKey(curr.plant)) {
				unified.put(curr.plant, new UnifiedHarvestEntry(curr.plant,project));
			}
		}
	}

	@Override
	public Collection<UnifiedHarvestEntry> getAllRecords() {
		TaxonComparatorByName tc = new TaxonComparatorByName();
		return unified.values().stream().filter(u->!u.myPlant.isItem()).sorted((a,b)->{
			if(a.myPlant.getTaxon().equals(b.myPlant.getTaxon())) {
				return a.getMyVariety().toUpperCase().compareTo(b.getMyVariety().toUpperCase());
			}
			else {
				return tc.compare(a.myPlant.getTaxon(), b.myPlant.getTaxon());
			}
		}).collect(Collectors.toList());
		
	}


	@Override
	public UnifiedHarvestEntry addNew() {
		throw new Error("Not Supported");
	}

	@Override
	public void remove(UnifiedHarvestEntry toRemove) {
		throw new Error("Not Supported");
	}
	
	@Override
	public void AddColumns(TableView<UnifiedHarvestEntry> table) {	
		
		TableRecordUtil.addPlantColumn(table, Translation.Key.plant, "myPlant", null);
		TableRecordUtil.addStringColumn(table, Translation.Key.variety, "myVariety", null);
		TableRecordUtil.addStringColumn(table, Translation.Key.unit, "unit", t -> {t.getRowValue().setUnit(t.getNewValue());}).setMinWidth(50);
		TableRecordUtil.addDoubleColumn(table, Translation.Key.unit_value, "unitValue", t -> {t.getRowValue().setUnitValue(t.getNewValue());});
		for (int curr : project.garden.getYears()) {
			TableRecordUtil.addDoubleColumn(table, curr+"\n"+Translation.getCurrent().harvest(), t->new ReadOnlyObjectWrapper<Double>(t.getValue().getHarvest(curr)), t -> {t.getRowValue().setHarvest(curr,t.getNewValue());});
		}
	}

}
