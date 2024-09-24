/**
 * Kitchen garden aid is a planning tool for kitchengardeners.
 * Copyright (C) 2010 Christian Nilsson
 *
 * This file is part of Kitchen garden aid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 * Email contact: tiberius.duluman@gmail.com; christian1195@gmail.com
 */

package org.sourceforge.kga.io;

import org.sourceforge.kga.*;
import org.sourceforge.kga.translation.Translation;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * Created by Tiberius on 3/22/2018.
 */
public class SeedListFormatV2 implements SeedListFormat
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    public static final int SEED_FILE_VERSION = 2;
    public static final String namespace = "org:sourceforge:kga:seedList";

    public void load(SeedCollection seedCollection, XmlReader xml) throws IOException, InvalidFormatException
    {
    	Translation t = Translation.getCurrent();
    	try
    	{

    		boolean parseEntry = false;
    		while (xml.hasNext())
    		{
    			int eventId = xml.next();
    			if (eventId == XmlReader.END_DOCUMENT || (eventId==XmlReader.END_ELEMENT&&xml.getLocalName().equals("seed-list")))
    				break;
    			if (eventId == XmlReader.START_ELEMENT) {
    			if (xml.getLocalName().equals("list") && xml.isStartElement()){
    				SeedList currentList  = new SeedList(xml.getAttributeValue("","name"));
    				while (xml.hasNext()&& !(xml.isEndElement()&&xml.getLocalName().equals("list")))
    				{
    					xml.next();
    					if (xml.isStartElement()&&xml.getLocalName().equals("seed")) {
    						String plant = xml.getAttributeValue("", "plant");
    						SeedEntry.PlantOrUnregistered plantOrUnregistered;
    						if (plant == null)
    						{
    							int plantId = Integer.parseInt(xml.getAttributeValue("", "plantId"));
    							plantOrUnregistered = new SeedEntry.PlantOrUnregistered(Resources.plantList().getPlant(plantId));
    						}
    						else
    						{
    							plantOrUnregistered = new SeedEntry.PlantOrUnregistered(plant);
    						}
    						String variety = xml.getAttributeValue("", "variety");
    						String quantityStr = xml.getAttributeValue("", "quantity");
    						SeedEntry.Quantity quantity = null;
    						if (quantityStr != null)
    						{
    							quantity = new SeedEntry.Quantity();
    							String[] tokens = quantityStr.split(" ");
    							if (tokens.length > 0)
    								quantity.quantity = Double.parseDouble(tokens[0]);
    							if (tokens.length > 1 &&!"null".equals(tokens[1]))
    								quantity.unit = tokens[1];
    							else
    								quantity.unit = "";
    						}
    						String comment = xml.getAttributeValue("", "comment");
    						LocalDate validFrom = LocalDate.parse(xml.getAttributeValue("", "validFrom"), DateTimeFormatter.BASIC_ISO_DATE);
    						LocalDate validTo = null;
    						String validToStr = xml.getAttributeValue("", "validTo");
    						if (validToStr != null)
    							validTo = LocalDate.parse(validToStr, DateTimeFormatter.BASIC_ISO_DATE);
    						currentList.add(plantOrUnregistered, variety, quantity, comment, validFrom, validTo);
    					}              	
    				}   
    				seedCollection.add(currentList);             
    			}
    			}
    		}
    	}
    	catch (XmlException ex)
    	{
    		throw new IOException(ex);
        }

        log.info("Garden loaded");
    }

    public void save(SeedCollection seedCollection, XmlWriter xml) throws IOException
    {
        try
        {
            xml.writeCharacters("\n");
            xml.writeStartElement("seed-list");
            xml.setDefaultNamespace(namespace);
            xml.writeDefaultNamespace();
            xml.writeAttribute("version", Integer.toString(SEED_FILE_VERSION));

            for (SeedList seedList : seedCollection)
            {
                xml.writeCharacters("\n    ");
                xml.writeStartElement("list");
                xml.writeAttribute("name", seedList.getName());

                for (SeedEntry entry : seedList.getAllEntries())
                {
                    xml.writeCharacters("\n        ");
                    xml.writeEmptyElement("seed");
                    if (entry.getPlant().plant == null)
                        xml.writeAttribute("plant", entry.getPlant().unregisteredPlant);
                    else
                        xml.writeAttribute("plantId", Integer.toString(entry.getPlant().plant.getId()));
                    if (entry.getVariety() != null)
                        xml.writeAttribute("variety", entry.getVariety());
                    if (entry.getQuantity() != null) {
                    	if(entry.getQuantity().unit==null || entry.getQuantity().unit.length()==0)
                    		xml.writeAttribute("quantity", Double.toString(entry.getQuantity().quantity));
                    	else
                    		xml.writeAttribute("quantity", Double.toString(entry.getQuantity().quantity) + " " + entry.getQuantity().unit);
                    }
                    if (entry.getComment() != null && !entry.getComment().isEmpty())
                        xml.writeAttribute("comment", entry.getComment());
                    xml.writeAttribute("validFrom", DateTimeFormatter.BASIC_ISO_DATE.format(entry.getValidFrom()));
                    if (entry.getValidTo() != null)
                        xml.writeAttribute("validTo", DateTimeFormatter.BASIC_ISO_DATE.format(entry.getValidTo()));
                }

                xml.writeCharacters("\n    ");
                xml.writeEndElement();
            }
            xml.writeCharacters("\n");
            xml.writeEndElement();
        }
        catch (XmlException ex)
        {
            throw new IOException(ex);
        }
    }
}
