package org.sourceforge.kga.gui.tableRecords.harvests;


import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.flowlist.AbstractFlowList;
import org.sourceforge.kga.flowlist.FlowListRecordRow;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

public class HarvestEntry extends AbstractFlowList<HarvestEntry> implements FlowListRecordRow<HarvestEntry>, Comparable<HarvestEntry> {
	
	Integer year;
	Double qty;
	TaxonVariety<Plant> plant;
	
	public HarvestEntry() {
	}
	public HarvestEntry(TaxonVariety<Plant> parent, int year) {
		plant=parent;
		this.year=year;
	}

	@Override
	public void save(XmlWriter writer) throws XmlException {
		writer.writeStartElement("HarvestEntry");
		if(plant!=null) {
			writer.writeIntAttribute("plant", plant.getId());
			if(plant.getVariety()!=null && plant.getVariety().length()!=0) {
				writer.writeAttribute("variety", plant.getVariety());				
			}
		}
		if(qty!=null)
			writer.writeDoubleAttribute("qty", qty);
		if(year!=null )
			writer.writeIntAttribute("year", year);
		writer.writeEndElement();
	}

	@Override
	public void load(XmlReader xml, int version) {
		if(version!=1) {
			throw new Error("Unknown version");
		}		
		String variety = xml.getAttributeValue("", "variety");
		if(variety==null) {
			variety="";
		}
		Integer plant = xml.getIntAttributeValue("plant");
		if(plant!=null) {
			this.plant=Resources.plantList().getVariety(Resources.plantList().getPlant(plant), variety);
		}
		year=xml.getIntAttributeValue("year");
		qty=xml.getDoubleAttributeValue("qty");
	}
	@Override
	public int compareTo(HarvestEntry o) {
		return hashCode()-o.hashCode();
	}


}
