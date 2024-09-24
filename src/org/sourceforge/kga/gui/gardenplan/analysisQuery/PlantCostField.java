package org.sourceforge.kga.gui.gardenplan.analysisQuery;

import java.util.Map.Entry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.QueryField;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.tableRecords.expenses.ExpenseEntry;
import org.sourceforge.kga.translation.Translation;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

class PlantCostField extends FXField<Entry<DatedPoint, TaxonVariety<Plant>>, Double>{
	ProjectFileWithChanges project;	
	Set<ExpenseCostCache> expenses;
	
	public PlantCostField(ProjectFileWithChanges project) {
		super(Translation.Key.cost, QueryField.ALLOWED_AGGREGATIONS.SUM,false);
		this.project=project;
		expenses = new HashSet<ExpenseCostCache>(project.getProject().getExpenseEntries().size());
		for(ExpenseEntry curr : project.getProject().getExpenseEntries()) {
			expenses.add(new ExpenseCostCache(curr,project));
		}
	}

	@Override
	public Callback<TableColumn<Entry<DatedPoint, TaxonVariety<Plant>>, Double>, TableCell<Entry<DatedPoint, TaxonVariety<Plant>>, Double>> getCellFactory() {
		return TextFieldTableCell.forTableColumn(new DoubleStringConverter());
	}

	@Override
	public Double getValue(Entry<DatedPoint, TaxonVariety<Plant>> point) {
		double value=0;
		for (ExpenseCostCache curr : expenses) {
			value +=curr.getCost(point.getKey().getYear(), point.getValue());
		}
		return value;
	}

	@Override
	public Entry<DatedPoint, TaxonVariety<Plant>> updateValueforPivot(
			Entry<DatedPoint, TaxonVariety<Plant>> initialPoint, Double pivotedBy) {
		return initialPoint;
	}
	
	@Override
	public double getNumericValue(Entry<DatedPoint, TaxonVariety<Plant>> data) {
		return getValue(data);
	}
	
	@Override
	public Callback<TableColumn<Entry<DatedPoint, TaxonVariety<Plant>>,Entry<Entry<DatedPoint, TaxonVariety<Plant>>,Double>>, TableCell<Entry<DatedPoint, TaxonVariety<Plant>>,Entry<Entry<DatedPoint, TaxonVariety<Plant>>,Double>>> getAggregatedCellFactory(){
		return FXField.DoubleAsCurrencyCellFactory();
	}
	

	
	
	
}