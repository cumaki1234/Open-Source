package org.sourceforge.kga.gui.tableRecords.expenses;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Project;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.flowlist.AbstractFlowList;
import org.sourceforge.kga.flowlist.FlowListRecordRow;
import org.sourceforge.kga.io.SaveableRecordRow;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

public class ExpenseEntry extends AbstractFlowList<ExpenseEntry> implements FlowListRecordRow<ExpenseEntry> {
	
	private String description;
	private String allocation;
	private Project project;
	public String getAllocation() {
		return allocation;
	}

	public void setAllocation(String allocation) {
		this.allocation = allocation;
	}

	private String comment;
	private Integer startYear;
	private Integer usefulLifeYears;
	private Double cost;
	
	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
		markDirty();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		markDirty();
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
		markDirty();
	}

	public Integer getStartYear() {
		return startYear;
	}

	public void setStartYear(Integer startYear) {
		this.startYear = startYear;
		markDirty();
	}

	public Integer getUsefulLifeYears() {
		return usefulLifeYears;
	}

	public void setUsefulLifeYears(Integer usefulLifeYears) {
		this.usefulLifeYears = usefulLifeYears;
		markDirty();
	}

	public Set<Plant> getDirectPlants() {
		if(allocation.equals(AllocationEntry.RESERVED_STRING_ALL)) {
			return Collections.emptySet();
		}else {
			return project.getAllocationEntries().stream().unordered().filter(ae->ae.getName().equals(allocation)).map(ae->ae.getPlant().plant).distinct().collect(Collectors.toSet());
		}
	}
	
	
	public ExpenseEntry(Project project) {
		description="";
		comment="";
		usefulLifeYears=1;
		cost=0.0;
		allocation=AllocationEntry.RESERVED_STRING_ALL;
		this.project=project;
	}

	@Override
	public void save(XmlWriter writer) throws XmlException {
		writer.writeStartElement("ExpenseEntry");
		if(description!=null && description.length()>0)
			writer.writeAttribute("description", description);
		if(comment!=null && comment.length()>0)
			writer.writeAttribute("comment", comment);
		if(allocation!=null && allocation.length()>0 && !allocation.equals(AllocationEntry.RESERVED_STRING_ALL))
			writer.writeAttribute("allocation", allocation);
		writer.writeIntAttribute("startYear", startYear);
		writer.writeIntAttribute("usefulYears", usefulLifeYears);
		writer.writeDoubleAttribute("cost", cost);
		writer.writeEndElement();
	}

	@Override
	public void load(XmlReader xml, int version) {
		if(version!=1) {
			throw new Error("Unknown version");
		}		
		description = xml.getAttributeValue("", "description");
		if(description==null) {
			description="";
		}
		comment = xml.getAttributeValue("", "comment");
		if(comment==null) {
			comment="";
		}
		allocation = xml.getAttributeValue("", "allocation");
		if(allocation==null) {
			allocation=AllocationEntry.RESERVED_STRING_ALL;
		}
		startYear=xml.getIntAttributeValue("startYear");
		usefulLifeYears=xml.getIntAttributeValue("usefulYears");
		cost=xml.getDoubleAttributeValue("cost");
		String ids = xml.getAttributeValue("", "plants");

	}


}
