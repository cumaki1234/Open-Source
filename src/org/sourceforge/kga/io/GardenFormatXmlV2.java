package org.sourceforge.kga.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;

import org.sourceforge.kga.*;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

public class GardenFormatXmlV2 implements GardenFormat
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    private static final int KGA_FILE_VERSION = 2;
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
            load(garden, xml);
        }
        catch (XmlException ex)
        {
            throw new IOException(ex);
        }

        log.info("Garden loaded");        
    }

    public static void load(Garden garden, XmlReader xml) throws XmlException
    {
        if (xml.getLocalName().compareTo("garden") != 0)
            throw new XmlException(new Exception("Expected <garden> element"));
        int year = 0;
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

            if (eventId == XmlReader.END_ELEMENT && xml.getLocalName().compareTo("year") == 0 && level == 1)
            {
                year = 0;
                continue;
            }
            if (eventId != XmlReader.START_ELEMENT)
                continue;

            if (level == 1 && xml.getLocalName().compareTo("year") == 0)
            {
                year = Integer.parseInt(xml.getAttributeValue("", "value"));
                garden.addYear(year);
                continue;
            }
            if (year == 0 || level != 2)
                continue;

            int x = 0, y = 0, w = 0, h = 0;
            if (xml.getLocalName().compareTo("rectangle") == 0)
            {
                x = Integer.parseInt(xml.getAttributeValue("", "x"));
                y = Integer.parseInt(xml.getAttributeValue("", "y"));
                w = Integer.parseInt(xml.getAttributeValue("", "w"));
                h = Integer.parseInt(xml.getAttributeValue("", "h"));
            } else if (xml.getLocalName().compareTo("cell") == 0)
            {
                x = Integer.parseInt(xml.getAttributeValue("", "x"));
                y = Integer.parseInt(xml.getAttributeValue("", "y"));
                w = 1;
                h = 1;
            } else
            {
                continue;
            }

            String speciesList = xml.getAttributeValue("", "species");
            if (speciesList.length() != 0)
                for (String species : speciesList.split(";"))
                {
                    for (int i = 0; i < w; ++i)
                        for (int j = 0; j < h; ++j)
                        {

                        	String [] split =species.split("-");
                        	int id = Integer.parseInt(split[0]);
                        	String variety = (split.length>1)?split[1]:"";
                        	TaxonVariety<Plant> toAdd = Resources.plantList().getVariety(Resources.plantList().getPlant(id),variety);
                            
                            Point grid = new Point(x + i, y + j);
                            if (toAdd==null)
                            	log.log(Level.SEVERE, "Unable to find plant ID: "+id +" when loading garden.");
                            else
                            	garden.addPlant(year, grid,toAdd);
                        }
                }
        }
    }

    static boolean equalPlantList(List<TaxonVariety<Plant>> p1, List<TaxonVariety<Plant>> p2)
    {
        if (p1 == null || p2 == null)
            return false;
        if (p1.size() != p2.size())
            return false;
        for (TaxonVariety<Plant> p : p1)
            if (!p2.contains(p))
                return false;
        return true;
    }
    
    public void save(Garden garden, DataOutputStream out) throws IOException
    {
    }

    public static void save(Garden garden, XmlWriter xml) throws XmlException
    {
        xml.writeStartElement("garden");
        for (Integer year : garden.getAllSquares().keySet())
        {
            xml.writeCharacters("\n    ");
            xml.writeStartElement("year");
            xml.writeAttribute("value", year.toString());
            TreeSet<Point> occupiedCells = new TreeSet<Point>(new Comparator<Point>() {
                @Override
                public int compare(Point p1, Point p2) {
                    if (p1.y < p2.y)
                        return -1;
                    if (p1.y > p2.y)
                        return 1;
                    return p1.x < p2.x ? -1 : p1.x > p2.x ? 1 : 0;
                } });
            occupiedCells.addAll(garden.getAllSquares().get(year).keySet());
            while (!occupiedCells.isEmpty())
            {
                Point topLeft = occupiedCells.pollFirst();
                List<TaxonVariety<Plant>> topLeftPlants = garden.getPlants(year, topLeft);
                occupiedCells.remove(topLeft);

                // try to expand as much on the right
                int w = 1, h = 1;
                while (true)
                {
                    Point neighbor = new Point(topLeft.x + w, topLeft.y);
                    if (!occupiedCells.contains(neighbor))
                        break;
                    List<TaxonVariety<Plant>> plants = garden.getPlants(year, neighbor);
                    if (!equalPlantList(plants, topLeftPlants))
                        break;

                    occupiedCells.remove(neighbor);
                    ++w;
                }

                // try to expand as much as possible down
                while (true)
                {
                    boolean canExpand = true;
                    for (int i = 0; i < w; ++i)
                    {
                        Point neighbor = new Point(topLeft.x + i, topLeft.y + h);
                        if (!occupiedCells.contains(neighbor))
                        {
                            canExpand = false;
                            break;
                        }
                        List<TaxonVariety<Plant>> plants = garden.getPlants(year, neighbor);
                        if (!equalPlantList(plants, topLeftPlants))
                        {
                            canExpand = false;
                            break;
                        }
                    }
                    if (!canExpand)
                        break;
                    for (int i = 0; i < w; ++i)
                        occupiedCells.remove(new Point(topLeft.x + i, topLeft.y + h));
                    ++h;
                }

                // save
                StringBuilder tmp = new StringBuilder();
                for (TaxonVariety<Plant> plant : topLeftPlants)
                {
                    if (tmp.length() != 0)
                        tmp.append(";");
                    if (plant.getVariety().length()==0) {
                    	tmp.append(plant.getId());
                    }
                    else {
                    	tmp.append(plant.getId()+"-"+plant.getVariety().replace(';', ' ').replace('-',' '));
                    }
                }
                if (tmp.length() != 0)
                {
                    xml.writeCharacters("\n        ");
                    if (w == 1 && h == 1)
                        xml.writeEmptyElement("cell");
                    else
                        xml.writeEmptyElement("rectangle");
                    xml.writeAttribute("x", Integer.toString(topLeft.x));
                    xml.writeAttribute("y", Integer.toString(topLeft.y));
                    if (w != 1 || h != 1)
                    {
                        xml.writeAttribute("w", Integer.toString(w));
                        xml.writeAttribute("h", Integer.toString(h));
                    }
                    xml.writeAttribute("species", tmp.toString());
                }
            }
            xml.writeCharacters("\n    ");
            xml.writeEndElement();
        }
        xml.writeCharacters("\n");
        xml.writeEndElement();
    }
}
