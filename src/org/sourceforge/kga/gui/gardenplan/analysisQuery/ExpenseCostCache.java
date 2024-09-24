package org.sourceforge.kga.gui.gardenplan.analysisQuery;

import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.tableRecords.expenses.ExpenseEntry;

public class ExpenseCostCache {
	ConcurrentHashMap<Integer,Future<costCache>> costCache;
	ProjectFileWithChanges project;
	ExpenseEntry entry;

	public ExpenseCostCache(ExpenseEntry entry, ProjectFileWithChanges project) {
		costCache = new ConcurrentHashMap<>();
		this.project=project;
		this.entry=entry;
	}

	public double getCost(int year, TaxonVariety<Plant> plant) {
		if(entry.getStartYear()>year || entry.getStartYear()+entry.getUsefulLifeYears()<year) {
			return 0;
		}
		FutureTask<costCache> ifAbsent = new FutureTask<costCache>(new Callable<costCache>() {

			@Override
			public costCache call() throws Exception {
				Set<Plant> toFind;
				if(entry.getDirectPlants()==null || entry.getDirectPlants().size()==0) {//weight across all plants in that year.
					toFind=project.getGarden().streamByPlant().unordered().filter(p->p.getKey().getYear()==year && !p.getValue().isItem()).map(p->p.getValue().getTaxon()).distinct().collect(Collectors.toSet());
				}
				else {
					toFind=entry.getDirectPlants();
				}
				return new costCache(toFind,year);
			}

		});
		costCache.putIfAbsent(year, ifAbsent);
		try {
			Future<costCache> fromCache=costCache.get(year);
			if(fromCache==ifAbsent) {
				ifAbsent.run();
			}
			costCache cache = fromCache.get();
			return cache.getCostPercentage(plant)*(entry.getCost()/entry.getUsefulLifeYears());
		}
		catch (Exception e) {
			throw new Error(e);
		}
	}

	class costCache {
		int totalSize;
		int year;
		Set<Plant> weightAgainst;
		public costCache(Set<Plant> weightAgainst, int year) {
			this.year=year;
			totalSize=getSquareFeet(getGardenPlantsFor(weightAgainst,year));
			this.weightAgainst=weightAgainst;
		}

		public Double getCostPercentage(TaxonVariety<Plant> toCheck) {
			if(toCheck.isItem() || (weightAgainst.size()>0 && !weightAgainst.contains(toCheck.getTaxon()))) {
				return 0.0;
			}else {
				return ((double)(toCheck.getSize().x*toCheck.getSize().y))/totalSize;
			}
		}

		private int getSquareFeet(Stream<TaxonVariety<Plant>> stream) {
			return stream.mapToInt(p->p.getSize().x*p.getSize().y).sum();
		}

		private Stream<TaxonVariety<Plant>> getGardenPlantsFor(Set<Plant> weightAgainst, int year){
			return project.getGarden().streamByPlant().unordered().filter(p->p.getKey().getYear()==year && weightAgainst.contains(p.getValue().getTaxon())).map(p->p.getValue());
		}

	}
}
