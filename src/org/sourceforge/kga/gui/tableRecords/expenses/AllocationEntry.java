package org.sourceforge.kga.gui.tableRecords.expenses;


import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.flowlist.AbstractFlowList;
import org.sourceforge.kga.flowlist.FlowListRecordRow;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

public class AllocationEntry extends AbstractFlowList<AllocationEntry> implements FlowListRecordRow<AllocationEntry> {
	
	private String name;
	private PlantOrUnregistered plant;
	
	public static final String RESERVED_STRING_ALL="*";
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlantOrUnregistered getPlant() {
		return plant;
	}

	public void setPlant(PlantOrUnregistered plant) {
		this.plant = plant;
	}

	public AllocationEntry() {
		name="";
		plant=new PlantOrUnregistered((Plant)null);
	}

	@Override
	public void save(XmlWriter writer) throws XmlException {
		writer.writeStartElement("AllocationGroupEntry");
		if(name!=null && name.length()>0)
			writer.writeAttribute("name", name);
		if(plant!=null)
			writer.writeIntAttribute("plant", plant.plant.getId());
		writer.writeEndElement();
	}

	@Override
	public void load(XmlReader xml, int version) {
		if(version!=1) {
			throw new Error("Unknown version");
		}		
		name = xml.getAttributeValue("", "name");
		if(name==null) {
			name="";
		}
		String id = xml.getAttributeValue("", "plant");
		if(id!=null && id.length()>0) {
			plant = new PlantOrUnregistered(Resources.plantList().getPlant(Integer.parseInt(id)));
		}
		else {
			plant=new PlantOrUnregistered((Plant)null);			
		}

	}


}
