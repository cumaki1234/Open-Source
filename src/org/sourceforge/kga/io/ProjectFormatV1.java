/**
 * Kitchen garden aid is a planning tool for kitchengardeners.
 * Copyright (C) 2018 Tiberius Duluman
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
 * Email contact: tiberius.duluman@gmail.com
 */

package org.sourceforge.kga.io;

import java.io.*;

import org.sourceforge.kga.*;
import org.sourceforge.kga.flowlist.FlowListRecordRow;
import org.sourceforge.kga.gui.tableRecords.expenses.AllocationEntry;
import org.sourceforge.kga.gui.tableRecords.expenses.ExpenseEntry;
import org.sourceforge.kga.gui.tableRecords.harvests.HarvestEntry;
import org.sourceforge.kga.gui.tableRecords.harvests.PlantInfoEntry;
import org.sourceforge.kga.gui.tableRecords.soilNutrition.SoilNutritionEntry;
import org.sourceforge.kga.io.SaveableRecordRow.recordType;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;


class ProjectFormatV1 implements ProjectFormat
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    private static final int KGA_FILE_VERSION = 1;
    public static final String namespace = "org:sourceforge:kga:garden";
    
    public static final ProjectChild []children =new ProjectChild[] {new VarietyInfo()};

    public void load(Project project, InputStream in) throws IOException, InvalidFormatException
    {
        try
        {
            XmlReader xml = new XmlReader(in);

            while (xml.hasNext())
                if (xml.next() == XmlReader.START_ELEMENT)
                    break;
            if (xml.getLocalName().compareTo("kga") != 0)
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
                if (xml.getLocalName().compareTo("garden") == 0)
                    GardenFormatXmlV2.load(project.garden, xml);
                else if (xml.getLocalName().compareTo("tag-list") == 0)
                    (new TagListFormatV1()).load(project.tagList, Resources.plantList(), xml);
                else if (xml.getLocalName().compareTo("seed-list") == 0) {
                	project.getSeedCollection().deleteAllSeedLists();
                    (new SeedListFormatV2()).load(project.getSeedCollection(), xml);
                }
                else if (xml.getLocalName().compareTo("SaveableRecords")==0) {
                	int srVersion = Integer.parseInt(xml.getAttributeValue("", "version"));
                	if(srVersion!=1) {
                        throw new InvalidFormatException();
                	}
                	while (xml.hasNext()) {
                        int srEventId = xml.next();
                		if(srEventId==XmlReader.END_ELEMENT && xml.getLocalName().compareTo("SaveableRecords")==0) {
                			break;
                		}
                		if(srEventId==XmlReader.START_ELEMENT) {
                			if(xml.getLocalName().equals("SoilNutritionEntry")) {
                				SoilNutritionEntry read=new SoilNutritionEntry();
                				read.load(xml, srVersion);
                				project.getSoilNutritionEntries().add(read);
                			}
                			else if(xml.getLocalName().equals("ExpenseEntry")) {
                				ExpenseEntry read=new ExpenseEntry(project);
                				read.load(xml, srVersion);
                				project.getExpenseEntries().add(read);
                			}
                			else if(xml.getLocalName().equals("AllocationGroupEntry")) {
                				AllocationEntry read=new AllocationEntry();
                				read.load(xml, srVersion);
                				project.getAllocationEntries().add(read);
                			}
                			else if(xml.getLocalName().equals("HarvestEntry")) {
                				HarvestEntry read=new HarvestEntry();
                				read.load(xml, srVersion);
                				project.getHarvestEntries().add(read);
                			}
                			else if(xml.getLocalName().equals("PlantInfoEntry")) {
                				PlantInfoEntry read=new PlantInfoEntry();
                				read.load(xml, srVersion);
                				project.getPlantInfoEntries().add(read);
                			}else {
                                throw new InvalidFormatException();
                			}
                		}
                	}
                }
                else {
                	for (ProjectChild curr : children) {
                		if(xml.getLocalName().compareTo(curr.getFileTag())==0) {
                			curr.load(project, xml, Integer.parseInt(version));
                		}
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

    public void save(Project project, DataOutputStream out) throws IOException
    {
        try
        {
            XmlWriter xml = new XmlWriter(out, "UTF-8", "1.0");
            xml.writeCharacters("\n");
            xml.writeStartElement("kga");
            xml.setDefaultNamespace(namespace);
            xml.writeDefaultNamespace();
            xml.writeAttribute("version", Integer.toString(KGA_FILE_VERSION));
            xml.writeCharacters("\n");
            GardenFormatXmlV2.save(project.garden, xml);
            xml.writeCharacters("\n");
            (new TagListFormatV1()).save(project.tagList, xml);
            xml.writeCharacters("\n");
            (new SeedListFormatV2()).save(project.getSeedCollection(), xml);
            xml.writeCharacters("\n");
            if(project.hasSaveableRecordRows()) {
            	xml.writeStartElement("SaveableRecords");
            	xml.writeAttribute("version", "1");
            	for (recordType type : recordType.values()) {
            		for (FlowListRecordRow<?> curr : project.getEntries(type)){//project.getSoilNutritionEntries()){
            			curr.save(xml);
            		}
            		xml.writeCharacters("\n");
            	}
            	xml.writeEndElement();
            }
            for(ProjectChild curr:children) {
            	curr.save(project, xml);
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
