package org.sourceforge.kga.io;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.Project;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

public class VarietyInfo implements ProjectChild {

	@Override
	public String getFileTag() {
		return "VarietyInfo";
	}

	@Override
	public void load(Project project, XmlReader xml, int version) {
		int plantID = Integer.parseInt(xml.getAttributeValue("", "plantID"));
		int size = Integer.parseInt(xml.getAttributeValue("", "size"));
		String variety = xml.getAttributeValue("", "variety");
		Plant p = Resources.plantList().getPlant(plantID);
		Resources.plantList().getVariety(p, variety).setSize(new Point(size,size));
	}

	@Override
	public void save(Project project, XmlWriter xml) throws XmlException{
		for (Plant plant : Resources.plantList().getPlants()) {
			for (TaxonVariety<Plant> variety : Resources.plantList().getVarieties(plant)) {
				if(variety.getSize()!=Plant.LEGACY_DEFAULT_SIZE) {
					xml.writeStartElement(getFileTag());
					xml.writeAttribute("plantID", Integer.toString(plant.getId()));
					xml.writeAttribute("variety", variety.getVariety());
					xml.writeAttribute("size", Integer.toString(variety.getSize().x));
					xml.writeEndElement();
	            	xml.writeCharacters("\n");
				}
			}
		}
		
	}

}
