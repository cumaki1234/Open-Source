package org.sourceforge.kga.gui.gardenplan.analysisQuery;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.Garden;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.Query;
import org.sourceforge.kga.analyze.QueryProvider;
import org.sourceforge.kga.analyze.SortablePlant;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.analyze.GardenStatisticsQuery;


public class GardenAnalysisQueryProvider implements QueryProvider<Entry<DatedPoint,TaxonVariety<Plant>>,FXField<Entry<DatedPoint,TaxonVariety<Plant>>,?>> {
	
	Garden source;
	
	private Collection<FXField<Entry<DatedPoint, TaxonVariety<Plant>>, ?>> fields;

	public static final FXField<Entry<DatedPoint, TaxonVariety<Plant>>, Integer> FIELD_YEAR = new YearField();
	public static final FXField<Entry<DatedPoint, TaxonVariety<Plant>>, Integer> FIELD_PLANT_SIZE = new PlantSizeField();
	public static final FXField<Entry<DatedPoint, TaxonVariety<Plant>>, SortablePlant> FIELD_PLANT = new PlantField();
	public static final FXField<Entry<DatedPoint, TaxonVariety<Plant>>, Integer> FIELD_PLANT_COUNT = new PlantCountField();
	public static final FXField<Entry<DatedPoint, TaxonVariety<Plant>>, String> FIELD_VARIETY = new VarietyField();
	
	
	public GardenAnalysisQueryProvider(ProjectFileWithChanges project) {
		Garden g = project.getGarden();
		source=g;
		fields = new HashSet<FXField<Entry<DatedPoint, TaxonVariety<Plant>>, ?>>();
		fields.add(FIELD_YEAR);
		fields.add(FIELD_PLANT_SIZE);
		fields.add(FIELD_PLANT);
		fields.add(FIELD_PLANT_COUNT);
		fields.add(FIELD_VARIETY);
		fields.add(new PlantCostField(project));
	}

	@Override
	public Stream<Entry<DatedPoint, TaxonVariety<Plant>>> stream() {
		return source.streamByPlant().filter(t->!t.getValue().isItem());
	}

	@Override
	public Collection<FXField<Entry<DatedPoint, TaxonVariety<Plant>>, ?>> getAvailableFields() {
		return fields;
	}

	@Override
	public Query<Entry<DatedPoint, TaxonVariety<Plant>>, FXField<Entry<DatedPoint, TaxonVariety<Plant>>, ?>> getDefaultQuery() {
		return new GardenStatisticsQuery(this);
	}
}