package org.sourceforge.kga.gui.tableRecords.expenses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.sourceforge.kga.Project;
import org.sourceforge.kga.gui.tableRecords.RecordTableProvider;
import org.sourceforge.kga.gui.tableRecords.TableRecordUtil;
import org.sourceforge.kga.translation.Translation;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

public class ExpenseProvider implements RecordTableProvider<ExpenseEntry> {

	Collection<ExpenseEntry> entries;
	Project project;
	
	public ExpenseProvider(Collection<ExpenseEntry> items, Project proj) {
		entries = items;
		project=proj;
	}

	@Override
	public Collection<ExpenseEntry> getAllRecords() {
		List<ExpenseEntry> entries = new ArrayList<ExpenseEntry>(this.entries);
		return entries;
	}


	@Override
	public ExpenseEntry addNew() {
		ExpenseEntry ne = new ExpenseEntry(project);
		entries.add(ne);
		return ne;
	}

	@Override
	public void remove(ExpenseEntry toRemove) {
		entries.remove(toRemove);		
	}
	
	@Override
	public void AddColumns(TableView<ExpenseEntry> table) {	
		TableRecordUtil.addStringColumn(table, Translation.Key.description, "description", t -> {t.getRowValue().setDescription(t.getNewValue());}).setMinWidth(300);
		TableRecordUtil.addDoubleColumn(table, Translation.Key.cost, "cost", t -> {t.getRowValue().setCost(t.getNewValue());}).setMinWidth(25);
		TableRecordUtil.addIntColumn(table, Translation.Key.year, "startYear", t -> {t.getRowValue().setStartYear(t.getNewValue());}, project.garden.getYears()).setMinWidth(25);
		TableRecordUtil.addIntColumn(table, Translation.Key.usefulLifeYears, "usefulLifeYears", t -> {t.getRowValue().setUsefulLifeYears(t.getNewValue());});
		List<String> allocationEntires = project.getAllocationEntries().stream().unordered().map(a->a.getName()).distinct().sorted().collect(Collectors.toList());
		String allString = Translation.getCurrent().all();
		allocationEntires.add(0,allString);		
		TableRecordUtil.addStringComboColumn(table, Translation.Key.allocation, t->{
			return new SimpleStringProperty(t.getValue().getAllocation().equals(AllocationEntry.RESERVED_STRING_ALL)?allString:t.getValue().getAllocation());
		}, t -> {((ExpenseEntry)t.getRowValue()).setAllocation(t.getNewValue());},FXCollections.observableList(allocationEntires), false).setMinWidth(180);	
		TableRecordUtil.addStringColumn(table, Translation.Key.comment, "comment", t -> {t.getRowValue().setComment(t.getNewValue());}).setMinWidth(500);	
	}

}
