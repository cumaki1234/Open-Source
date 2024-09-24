package org.sourceforge.kga.io;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.PlantList;
import org.sourceforge.kga.TagList;
import org.sourceforge.kga.plant.Tag;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

import java.util.ArrayList;

public class TagListFormatV1 implements TagListFormat
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(TagListFormatV1.class.getName());

    @Override
    public void load(TagList tags, PlantList plants, XmlReader xml) throws XmlException
    {
        if (xml.getLocalName().compareTo("tag-list") != 0)
            throw new XmlException(new Exception("Expected <tag-list> element"));
        int level = 0;
        while (xml.hasNext())
        {
            int eventId = xml.next();

            if (eventId == XmlReader.START_ELEMENT)
                ++level;
            else if (eventId == XmlReader.END_ELEMENT)
            {
                --level;
                if (level < 0)
                    break;
            }

            if (eventId == XmlReader.START_ELEMENT && xml.getLocalName().compareTo("tag") == 0 && level == 1)
            {
                String name = xml.getAttributeValue("", "name");
                String plantListStr = xml.getAttributeValue("", "plants");
                log.info("Loading tag " + name + " with species " + plantListStr);
                ArrayList <Plant> tagPlants = new ArrayList<Plant>();
                for (String plantId : plantListStr.split(" "))
                {
                    Plant plant = null;
                    try
                    {
                        plant = plants.getPlant(Integer.parseInt(plantId));
                    }
                    catch (NumberFormatException ex)
                    {
                        log.warning(ex.toString());
                    }
                    if (plant == null)
                        log.warning("Invalid species id " + plantId);
                    else
                        tagPlants.add(plant);
                }
                Tag t = tags.addTag(name);//, tagPlants);
                t.setSpecies(tagPlants);
                
            }
        }
    }

    @Override
    public void save(TagList tags, XmlWriter xml) throws XmlException
    {
        xml.writeStartElement("tag-list");
        xml.writeCharacters("\n");
        for (Tag tag : tags.getTags())
        {
            tag.getName();
            StringBuilder plantList =  new StringBuilder();
            for (Plant plant : tag.getSpecies())
            {
                if (plantList.length() != 0)
                    plantList.append(" ");
                plantList.append(Integer.toString(plant.getId()));
            }
            xml.writeCharacters("    ");
            xml.writeEmptyElement("tag");
            xml.writeAttribute("name", tag.getName());
            xml.writeAttribute("plants", plantList.toString());
            xml.writeCharacters("\n");
        }
        xml.writeEndElement();
    }
}
