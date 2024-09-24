package org.sourceforge.kga.gui.analyze;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.Query;
import org.sourceforge.kga.gui.gardenplan.analysisQuery.GardenAnalysisQueryProvider;
import org.sourceforge.kga.translation.Translation;

public class GardenStatisticsQuery extends Query<Entry<DatedPoint,TaxonVariety<Plant>>,FXField<Entry<DatedPoint,TaxonVariety<Plant>>,?>> {
	
	
	public GardenStatisticsQuery(GardenAnalysisQueryProvider provider) {
		super(getMeasures(provider),getGroupby(),provider,GardenAnalysisQueryProvider.FIELD_PLANT,GardenAnalysisQueryProvider.FIELD_YEAR);
	}
	
	private static List<FXField<Entry<DatedPoint, TaxonVariety<Plant>>, ?>> getGroupby(){
		List<FXField<Entry<DatedPoint, TaxonVariety<Plant>>, ?>> groupby = new ArrayList<>(3);
		groupby.add(GardenAnalysisQueryProvider.FIELD_PLANT);
		groupby.add(GardenAnalysisQueryProvider.FIELD_YEAR);
		groupby.add(GardenAnalysisQueryProvider.FIELD_VARIETY);
		return groupby;
		
	}
	
	private static Set<FXField<Entry<DatedPoint, TaxonVariety<Plant>>, ?>> getMeasures(GardenAnalysisQueryProvider provider){
		Set<FXField<Entry<DatedPoint, TaxonVariety<Plant>>, ?>> measures = new HashSet<>();
		measures.add(GardenAnalysisQueryProvider.FIELD_PLANT_COUNT);
		measures.add(GardenAnalysisQueryProvider.FIELD_PLANT_SIZE);
		for (FXField<Entry<DatedPoint,TaxonVariety<Plant>>,?> curr : provider.getAvailableFields()) {
			if(curr.getFieldName().equals(Translation.Key.cost)) {
				measures.add(curr);
			}
		}
		return measures;
	}

}
