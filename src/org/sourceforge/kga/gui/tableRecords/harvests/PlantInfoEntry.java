package org.sourceforge.kga.gui.tableRecords.harvests;


import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.flowlist.AbstractFlowList;
import org.sourceforge.kga.flowlist.FlowListRecordRow;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

public class PlantInfoEntry extends AbstractFlowList<PlantInfoEntry> implements FlowListRecordRow<PlantInfoEntry>, Comparable<PlantInfoEntry> {
	
	String harvestUnit;
	Double unitValue;
	TaxonVariety<Plant> plant;
	
	public PlantInfoEntry() {
	}
	public PlantInfoEntry(TaxonVariety<Plant> parent) {
		plant=parent;
	}

	@Override
	public void save(XmlWriter writer) throws XmlException {
		writer.writeStartElement("PlantInfoEntry");
		if(plant!=null) {
			writer.writeIntAttribute("plant", plant.getId());
			if(plant.getVariety()!=null && plant.getVariety().length()!=0) {
				writer.writeAttribute("variety", plant.getVariety());				
			}
		}
		if(unitValue!=null)
			writer.writeDoubleAttribute("unitValue", unitValue);
		if(harvestUnit!=null )
			writer.writeAttribute("harvestUnit", harvestUnit);
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
		harvestUnit=xml.getAttributeValue("", "harvestUnit");
		unitValue=xml.getDoubleAttributeValue("unitValue");
	}

	@Override
	public int compareTo(PlantInfoEntry o) {
		return hashCode()-o.hashCode();
	}

}
