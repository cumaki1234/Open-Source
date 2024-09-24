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

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.sourceforge.kga.*;
import org.sourceforge.kga.translation.Translation;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;


public class SeedListFormatV1 implements SeedListFormat
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    public static final int SEED_FILE_VERSION = 1;
    public static final String namespace = "org:sourceforge:kga:seedList";

    public void load(SeedCollection seedCollection, XmlReader xml) throws IOException, InvalidFormatException
    {
        Translation t = Translation.getCurrent();
        try
        {
            SeedList currentList = null;
            boolean parseEntry = false;
            while (xml.hasNext())
            {
                int eventId = xml.next();
                if (eventId == XmlReader.END_DOCUMENT)
                    break;
                if (eventId == XmlReader.END_ELEMENT)
                {
                    if (!parseEntry)
                        currentList = null;
                    parseEntry = false;
                    continue;
                }
                if (eventId != XmlReader.START_ELEMENT)
                    continue;
                if (currentList == null)
                {
                    if (xml.getLocalName().equals("inventory"))
                        currentList = new SeedList(t.seed_manager_inventory());
                    else if (xml.getLocalName().equals("shopping"))
                        currentList = new SeedList(t.seed_manager_shopping_list());
                    if (currentList != null)
                        seedCollection.add(currentList);
                    continue;
                }

                parseEntry = true;
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
                    if (tokens.length > 1)
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
        catch (XmlException ex)
        {
            throw new IOException(ex);
        }

        log.info("Garden loaded");
    }

    public void save(SeedCollection seedCollection, XmlWriter out) throws IOException
    {
        throw new IOException("Unsupported");
    }
}
