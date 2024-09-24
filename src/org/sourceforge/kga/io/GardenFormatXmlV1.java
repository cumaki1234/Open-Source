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
import java.util.*;

import org.sourceforge.kga.*;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;


class GardenFormatXmlV1 implements GardenFormat
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    private static final int KGA_FILE_VERSION = 1;
    public static final String namespace = "org:sourceforge:kga:garden";

    public void load(Garden garden, InputStream in) throws IOException, InvalidFormatException
    {
        try
        {
            XmlReader xml = new XmlReader(in);

            while (xml.hasNext())
                if (xml.next() == XmlReader.START_ELEMENT)
                    break;
            if (xml.getLocalName().compareTo("garden") != 0)
                throw new InvalidFormatException();
            String version = xml.getAttributeValue("", "version");
            if (Integer.parseInt(version) != KGA_FILE_VERSION)
                throw new InvalidFormatException();
            while (xml.hasNext())
            {
                int eventId = xml.next();
                if (eventId == XmlReader.END_DOCUMENT)
                    break;
                if (eventId != XmlReader.START_ELEMENT)
                    continue;
                int year = Integer.parseInt(xml.getAttributeValue("", "year"));
                int x = Integer.parseInt(xml.getAttributeValue("", "x"));
                int y = Integer.parseInt(xml.getAttributeValue("", "y"));
                Point grid = new Point(x, y);
                String speciesList = xml.getAttributeValue("", "species");
                if (speciesList.length() != 0)
                    for (String species : speciesList.split(";"))
                    {
                        int id = Integer.parseInt(species);
                        if (id == 117 || id == 119)
                            id = 116;
                        garden.addPlant(year, grid, Resources.plantList().getVariety(Resources.plantList().getPlant(id),""));
                    }
            }
        }
        catch (XmlException ex)
        {
            throw new IOException(ex);
        }

        log.info("Garden loaded");
    }

    public void save(Garden garden, DataOutputStream out) throws IOException
    {
        try
        {
            XmlWriter xml = new XmlWriter(out, "UTF-8", "1.0");
            xml.writeCharacters("\n");
            xml.writeStartElement("garden");
            xml.setDefaultNamespace(namespace);
            xml.writeDefaultNamespace();
            xml.writeAttribute("version", Integer.toString(KGA_FILE_VERSION));
            for (Map.Entry<Integer, HashMap<Point, java.util.List<TaxonVariety<Plant>>>> yearMap : garden.getAllSquares().entrySet())
                for (Map.Entry<Point, java.util.List<TaxonVariety<Plant>>> s : yearMap.getValue().entrySet())
                {
                    StringBuilder tmp = new StringBuilder();
                    for (TaxonVariety<Plant> plant : s.getValue())
                    {
                        if (tmp.length() != 0)
                            tmp.append(";");
                        tmp.append(plant.getId());
                    }
                    if (tmp.length() != 0)
                    {
                        xml.writeCharacters("\n    ");
                        xml.writeEmptyElement("square");
                        xml.writeAttribute("year", Integer.toString(yearMap.getKey()));
                        xml.writeAttribute("x", Integer.toString(s.getKey().x));
                        xml.writeAttribute("y", Integer.toString(s.getKey().y));
                        xml.writeAttribute("species", tmp.toString());
                    }
                }
            xml.writeCharacters("\n");
            xml.writeEndElement();
            xml.flush();
            xml.close();
        }
        catch (XmlException ex)
        {
            throw new IOException(ex);
        }
    }
}
