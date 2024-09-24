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
import org.sourceforge.kga.plant.*;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;


import org.sourceforge.kga.wrappers.XmlException;


class SpeciesListFormatV1 implements SpeciesListFormat
{
    static private String serializeIds(Set<Plant> species)
    {
        SortedSet<Integer> sortedSet = new TreeSet<Integer>();
        for (Plant s : species)
            sortedSet.add(s.getId());

        StringBuilder t = null;
        for (Integer i : sortedSet)
        {
            if (t == null)
                t = new StringBuilder();
            if (t.length() > 0)
                t.append(" ");
            t.append(i.toString());
        }
        if (t == null)
            return null;
        return t.toString();
    }

    static private <E extends Enum<E>> String serializeEnum(E e)
    {
        return e.name().toLowerCase().replace('_', '-');
    }

    static private <E extends Enum<E>> E deserializeEnum(Class<E> e, String v)
    {
        return E.valueOf(e, v.toUpperCase().replace('-', '_'));
    }

    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    private static final int SPECIES_FILE_VERSION = 3;
    public static final String namespace = "org:sourceforge:kga:species";


    public PlantList load(InputStream in) throws IOException, InvalidFormatException
    {
        PlantList speciesList = new PlantList();
        try
        {
            XmlReader xml = new XmlReader(in);

            while (xml.hasNext())
                if (xml.next() == XmlReader.START_ELEMENT)
                    break;
            String version = xml.getAttributeValue("", "version");
            if (Integer.parseInt(version) != SPECIES_FILE_VERSION)
                throw new InvalidFormatException();

            Stack<Taxon> parsedTaxons = new Stack<>();
            parsedTaxons.push(Plant.getKingdom());

            Plant items = new Plant(Taxon.Type.FAMILY, 7200, "Items", Plant.getKingdom());
            speciesList.addTaxon(items);

            int level = 0;
            int currentSource = 0;
            String translation = null;
            while (xml.hasNext())
            {
                int eventId = xml.next();
                if (eventId == XmlReader.END_DOCUMENT)
                    break;
                else if (eventId == XmlReader.END_ELEMENT)
                {
                    --level;
                    if (parsedTaxons.size() > 1)
                    {
                        if (xml.getLocalName().compareTo(serializeEnum(parsedTaxons.lastElement().getType())) == 0)
                        {
                            Taxon taxon = parsedTaxons.pop();
                            // log.fine("loaded " + serializeEnum(taxon.getType()) + " " + taxon.getTranslation("en"));
                            speciesList.addTaxon(taxon);
                            continue;
                        }
                    }
                    if (level == 0 && translation != null)
                    {
                        translation = null;
                    }
                    continue;
                }
                else if (eventId != XmlReader.START_ELEMENT)
                {
                    continue;
                }
                ++level;

                Taxon.Type taxonType =
                    xml.getLocalName().equals("family") ? Taxon.Type.FAMILY :
                    xml.getLocalName().equals("genus") ? Taxon.Type.GENUS :
                    xml.getLocalName().equals("species") ? Taxon.Type.SPECIES :
                    xml.getLocalName().equals("subspecies") ? Taxon.Type.SUBSPECIES : null;

                // plant found
                if (level >= 1 && taxonType != null)
                {
                    Taxon parent = parsedTaxons.lastElement();

                    // recreate name by replacing abbreviated form of genus
                    String scientific = xml.getAttributeValue("", "scientific");
                    if (taxonType == Plant.Type.SPECIES && parent.getType() == Plant.Type.GENUS)
                        scientific = parent.getName() + scientific.substring(2);
                    if (taxonType == Plant.Type.SUBSPECIES)
                        scientific = parent.getName().substring(0, parent.getName().indexOf(' ')) + scientific.substring(2);

                    int id = Integer.parseInt(xml.getAttributeValue("", "id"));
                    Taxon taxon = null;
                    if (parent instanceof Plant)
                        taxon = new Plant(taxonType, id, scientific, (Plant)parent);
                    else if (parent instanceof Animal)
                        taxon = new Animal(taxonType, id, scientific, (Animal)parent);
                    parsedTaxons.push(taxon);
                    String english = xml.getAttributeValue("", "name");
                    taxon.setTranslation("en", english);
                    continue;
                }

                // elements inside a plant
                if (parsedTaxons.size() > 1)
                {
                    Taxon lastTaxon = parsedTaxons.lastElement();
                    if (level >= 2 && lastTaxon instanceof Plant)
                    {
                        loadPlant(speciesList, xml, level, (Plant)lastTaxon);
                        continue;
                    }
                }

                // additional found
                if (level == 1 && xml.getLocalName().equals("animals"))
                {
                    parsedTaxons.push(Animal.getKingdom());
                    continue;
                }

                // TODO: refactory items ( maybe to not be plant )
                if (level == 2 && xml.getLocalName().equals("item"))
                {
                    int id = Integer.parseInt(xml.getAttributeValue("", "id"));
                    String english = xml.getAttributeValue("", "name");
                    Plant item = new Plant(Plant.Type.ITEM, id, english, items);
                    item.setTranslation("en", english);
                    speciesList.addTaxon(item);
                    continue;
                }

                // translations
                if (level == 1 && xml.getLocalName().equals("translation"))
                {
                    translation = xml.getAttributeValue("http://www.w3.org/XML/1998/namespace", "lang");
                    // log.fine("loading translation " + translation);
                    continue;
                }
                if (level == 2 && translation != null && xml.getLocalName().equals("name"))
                {
                    int id = Integer.parseInt(xml.getAttributeValue("", "id"));
                    xml.next();
                    speciesList.setTranslation(id, translation, xml.getText());
                    continue;
                }

                // images
                if (level == 2 && xml.getLocalName().equals("image"))
                {
                    int id = Integer.parseInt(xml.getAttributeValue("", "id"));
                    Plant plant = speciesList.getPlant(id);
                    if (plant != null)
                    {
                        xml.next();
                        plant.setImage(xml.getText());
                    }
                    continue;
                }

                // sources
                if (level == 2 && xml.getLocalName().equals("source"))
                {
                    ++currentSource;
                    int id = Integer.parseInt(xml.getAttributeValue("", "id"));
                    if (id != currentSource)
                        log.severe("Source id must be increase always by 1");
                    speciesList.reserveSource(id);
                    PropertySource source = speciesList.getSource(id);
                    source.name = xml.getAttributeValue("", "name");
                    source.url = xml.getAttributeValue("", "url");
                }
            }

            // replace placeholders
            for (Plant plant : speciesList.getPlants())
                for (Companion companion : plant.getCompanions().get())
                {
                    companion.plant = speciesList.getPlant(companion.plant.getId());
                    if (!companion.animals.isEmpty())
                    {
                        TreeSet<Animal> animals = new TreeSet<>();
                        for (Animal a : companion.animals)
                            animals.add(speciesList.getAnimal(a.getId()));
                        companion.animals = animals;
                    }
                }
        }
        catch (XmlException ex)
        {
            throw new IOException(ex);
        }

        log.info("Species list loaded");

        // save(speciesList, new DataOutputStream(new FileOutputStream (new File("e:\\Tibi\\garden\\resources\\species1.xml"))));
        return speciesList;
    }

    private void loadPlant(PlantList speciesList, XmlReader xml, int level, Plant parsedPlant) {
        if (xml.getLocalName().equals("lifetime"))
        {
            String repetition = xml.getAttributeValue("", "repetition");
            String gap = xml.getAttributeValue("", "gap");
            parsedPlant.lifetime.set(
                    deserializeEnum(Lifetime.Value.class, xml.getAttributeValue("", "type")),
                    repetition != null ? Integer.parseInt(repetition) : null,
                    gap != null ? Integer.parseInt(gap) : null);
            loadSourceList(xml, speciesList, parsedPlant.lifetime.references);
        }

        else if (xml.getLocalName().equals("nutritional-needs"))
        {
            parsedPlant.setNutritionalNeeds(new NutritionalNeeds(
                deserializeEnum(NutritionalNeeds.Type.class, xml.getAttributeValue("", "value"))));
            loadSourceList(xml, speciesList, parsedPlant.getNutritionalNeeds().references);
        }

        else if (xml.getLocalName().equals("root"))
        {
            try { parsedPlant.setRootDeepness(RootDeepness.parseString(xml.getAttributeValue("", "deepness"))); }
            catch (Exception ex) { log.warning(ex.toString()); }
            loadSourceList(xml, speciesList, parsedPlant.getRootDeepness().references);
        }

        else if (xml.getLocalName().equals("weed"))
        {
            parsedPlant.setWeedControl(new WeedControl(
                deserializeEnum(WeedControl.Type.class, xml.getAttributeValue("", "control"))));
            loadSourceList(xml, speciesList, parsedPlant.getWeedControl().references);
        }

        else if (xml.getLocalName().equals("companion"))
        {
            Companion.Type type = deserializeEnum(Companion.Type.class, xml.getAttributeValue("", "type"));
            ReferenceList sourceRef = loadSourceList(xml, speciesList);
            String additional = xml.getAttributeValue("", "additional");
            for (String id : xml.getAttributeValue("", "id").split(" "))
            {
                TreeSet<Animal> animals = new TreeSet<>();
                TreeSet<Companion.Improve> improve = new TreeSet<>();
                if (type.compareTo(Companion.Type.IMPROVE) == 0 || (type.compareTo(Companion.Type.INHIBIT)==0 && additional!=null))
                {
                    for (String s : additional.split(" "))
                        improve.add(deserializeEnum(Companion.Improve.class, s));
                }
                else if (type.withAnimals())
                {
                    for (String s : additional.split(" "))
                        animals.add(new Animal(Integer.parseInt(s)));
                }
                parsedPlant.getCompanions().add(new Plant(Integer.parseInt(id)), type, animals, improve, sourceRef);
            }
        } else if (xml.getLocalName().equals("img"))
        {
        	//TODO: update taxon so we can give it a resource path instead of an encoded string.
        	try {
        		parsedPlant.setImage(new String(java.util.Base64.getEncoder().encode(Resources.openPic("species_xml/"+xml.getAttributeValue("", "path")).readAllBytes())));
        	}
        	catch(Exception e){
        		log.severe("Unable to parse image from species.xml for id: "+parsedPlant.getId());
        		e.printStackTrace();
        		//throw new Error(e);
        	}
        }
    }

    private void loadSourceList(XmlReader xml, PlantList speciesList, ReferenceList references)
    {
        String sources = xml.getAttributeValue("", "source");
        if (sources == null)
            return;
        for (String source : sources.split(" "))
        {
            String[] tmp = source.split("/");
            PropertySource s = speciesList.reserveSource(Integer.parseInt(tmp[0]));
            references.add(new Reference(s, tmp.length == 1 ? null : tmp[1]));
        }
    }

    private ReferenceList loadSourceList(XmlReader xml, PlantList speciesList)
    {
        String sources = xml.getAttributeValue("", "source");
        if (sources == null)
            return null;
        ReferenceList ref = new ReferenceList();
        for (String source : sources.split(" "))
        {
            String[] tmp = source.split("/");
            PropertySource s = speciesList.reserveSource(Integer.parseInt(tmp[0]));
            ref.add(new Reference(s, tmp.length == 1 ? null : tmp[1]));
        }
        return ref;
    }

    int indent = 0;
    void writeStartElement(XmlWriter xml, String element) throws XmlException
    {
        xml.writeCharacters("\n");
        for (int i = 0; i < indent; ++i)
            xml.writeCharacters("    ");
        ++indent;
        xml.writeStartElement(element);
    }

    void writeEmptyElement(XmlWriter xml, String element) throws XmlException
    {
        xml.writeCharacters("\n");
        for (int i = 0; i < indent; ++i)
            xml.writeCharacters("    ");
        xml.writeEmptyElement(element);
    }

    void writeEndElement(XmlWriter xml) throws XmlException
    {
        xml.writeCharacters("\n");
        --indent;
        for (int i = 0; i < indent; ++i)
            xml.writeCharacters("    ");
        xml.writeEndElement();
    }

    public void save(PlantList speciesList, DataOutputStream out) throws IOException
    {
        try
        {
            // initialize document
            XmlWriter xml = new XmlWriter(out, "UTF-8", "1.0");
            writeStartElement(xml, "plants");
            xml.setDefaultNamespace(namespace);
            xml.writeDefaultNamespace();
            xml.writeAttribute("version", Integer.toString(SPECIES_FILE_VERSION));

            // write plants
            for (Plant plant : speciesList.getPlants())
            {
                if (plant.getId() != 7200 && plant.getType() == Taxon.Type.FAMILY) // TODO: refactory this
                    saveTaxon(xml, plant);
            }

            // write items
            boolean hasItems = false;
            for (Plant s : speciesList.getPlants())
            {
                if (!s.isItem())
                    continue;
                if (!hasItems)
                {
                    writeStartElement(xml, "items");
                    hasItems = true;
                }
                writeEmptyElement(xml, "item");
                xml.writeAttribute("id", Integer.toString(s.getId()));

                String english = s.getTranslation("en");
                if (english != null)
                    xml.writeAttribute("name", english);
            }
            if (hasItems)
                writeEndElement(xml);

            // write animals
            writeStartElement(xml, "animals");
            for (Animal animal : speciesList.getAnimals())
            {
                if (animal.getType() == Taxon.Type.FAMILY)
                    saveTaxon(xml, animal);
            }
            writeEndElement(xml);

            // write translations
            for (String language : speciesList.getTranslations())
            {
                if (language.compareTo("en") == 0)
                    continue;
                writeStartElement(xml, "translation");
                xml.writeAttribute("xml:lang", language);

                // write translations
                for (int type = 0; type < 2; ++type)
                    for (Plant s : speciesList.getPlants())
                    {
                        String t = s.getTranslation(language);
                        if (t == null)
                            continue;
                        if (type == 0 && s.getType() != Plant.Type.FAMILY)
                            continue;
                        if (type != 0 && s.getType() == Plant.Type.FAMILY)
                            continue;
                        // if (s.isItem() && type != 2)
                        //    continue;
                        writeStartElement(xml, "name");
                        xml.writeAttribute("id", Integer.toString(s.getId()));
                        xml.writeCharacters(t);
                        xml.writeEndElement();
                        --indent;
                    }
                for (Animal a : speciesList.getAnimals())
                {
                    String t = a.getTranslation(language);
                    if (t == null)
                        continue;
                    writeStartElement(xml, "name");
                    xml.writeAttribute("id", Integer.toString(a.getId()));
                    xml.writeCharacters(t);
                    xml.writeEndElement();
                    --indent;
                }

                writeEndElement(xml);
            }

            // write images
            writeStartElement(xml, "images");
            for (Plant s : speciesList.getPlants())
            {
                if (s.getImageAsString() == null)
                    continue;
                writeStartElement(xml, "image");
                xml.writeAttribute("id", Integer.toString(s.getId()));
                // xml.writeAttribute("type", s.getImageType());
                xml.writeCharacters(s.getImageAsString());
                xml.writeEndElement();
                --indent;
            }
            writeEndElement(xml);

            // write sources
            writeStartElement(xml, "resources");
            int i = 1;
            while (true)
            {
                PropertySource source = speciesList.getSource(i);
                if (source == null)
                    break;
                writeEmptyElement(xml, "source");
                xml.writeAttribute("id", Integer.toString(i));
                xml.writeAttribute("name", source.name);
                if (source.url != null && !source.url.isEmpty())
                    xml.writeAttribute("url", source.url);
                ++i;
            }
            writeEndElement(xml);

            writeEndElement(xml);
            xml.flush();
            xml.close();
        }
        catch (XmlException ex)
        {
            throw new IOException(ex);
        }
    }

    private <T extends Taxon> void saveTaxon(XmlWriter xml, Taxon<T> t) throws XmlException
    {
        boolean emptyElement = t.getChildren().isEmpty();
        if (t instanceof Plant)
        {
            Plant s = (Plant)t;
            emptyElement &= !(s.hasNutritionalNeeds() || s.lifetime.isDefined() ||
                    s.hasRootDeepness() || s.getCompanions().isDefined());
        }

        if (emptyElement)
            writeEmptyElement(xml, serializeEnum(t.getType()));
        else
            writeStartElement(xml, serializeEnum(t.getType()));

        xml.writeAttribute("id", Integer.toString(t.getId()));
        String scientific = t.getName();
        if (t.getType() == Plant.Type.SPECIES && t.getTaxonParent().getType() == Plant.Type.GENUS ||
            t.getType() == Plant.Type.SUBSPECIES)
            scientific = scientific.substring(0, 1) + "." + scientific.substring(scientific.indexOf(' '));
        xml.writeAttribute("scientific", scientific);

        String speciesName = t.getTranslation("en");
        if (speciesName != null)
            xml.writeAttribute("name", speciesName);

        if (t instanceof Plant)
            savePlant(xml, (Plant)t);

        for (Taxon child : t.getChildren())
            saveTaxon(xml, child);

        if (!emptyElement)
            writeEndElement(xml);
    }

    private void savePlant(XmlWriter xml, Plant s) throws XmlException
    {
        if (s.lifetime.isDefined())
        {
            writeEmptyElement(xml, "lifetime");
            xml.writeAttribute("type", serializeEnum(s.lifetime.get()));
            if (s.lifetime.hasRepetitionYears())
                xml.writeAttribute("repetition", Integer.toString(s.lifetime.getRepetitionYears()));
            if (s.lifetime.hasRepetitionGap())
                xml.writeAttribute("gap", Integer.toString(s.lifetime.getRepetitionGap()));
            serializeSource(xml, s.lifetime.references);
        }

        if (s.hasNutritionalNeeds())
        {
            writeEmptyElement(xml, "nutritional-needs");
            xml.writeAttribute("value", serializeEnum(s.getNutritionalNeeds().type));
            serializeSource(xml, s.getNutritionalNeeds().references);
        }

        if (s.hasRootDeepness())
        {
            writeEmptyElement(xml, "root");
            xml.writeAttribute("deepness", s.getRootDeepness().toString());
            serializeSource(xml, s.getRootDeepness().references);
        }

        if (s.hasWeedControl())
        {
            writeEmptyElement(xml, "weed");
            xml.writeAttribute("control", serializeEnum(s.getWeedControl().type));
            serializeSource(xml, s.getWeedControl().references);
        }

        saveCompanions(xml, s);
    }

    private void saveCompanions(XmlWriter xml, Plant s) throws XmlException
    {
        ArrayList<Companion> save = new ArrayList<>();
        for (Companion c : s.getCompanions().get())
            save.add(c);

        // companions are saved sorted by type
        for (Companion.Type type : Companion.Type.values())
        {
            while (true)
            {
                ArrayList<Companion> toSave = new ArrayList<>();
                ReferenceList sourceRef = null;
                TreeSet<Animal> animals = new TreeSet<>();
                TreeSet<Companion.Improve> improve = new TreeSet<>();

                // search all the companions that have same source reference list
                for (Companion companion : save)
                {
                    if (companion.type != type)
                        continue;
                    if (toSave.isEmpty())
                    {
                        sourceRef = companion.references;
                        animals = companion.animals;
                        improve = companion.improve;
                    }
                    int ret = companion.references.compareTo(sourceRef);
                    if (ret == 0)
                    {
                        if (companion.animals.equals(animals) && companion.improve.equals(improve))
                            toSave.add(companion);
                    }
                    else if (ret < 0)
                    {
                        // save companions ordered by reference list
                        toSave.clear();
                        toSave.add(companion);
                        sourceRef = companion.references;
                        animals = companion.animals;
                        improve = companion.improve;
                    }
                }
                if (toSave.isEmpty())
                    break;

                Set<Plant> ids = new HashSet<>();
                for (Companion c : toSave)
                {
                    save.remove(c);
                    ids.add(c.plant);
                }
                writeEmptyElement(xml, "companion");
                xml.writeAttribute("type", serializeEnum(type));
                xml.writeAttribute("id", serializeIds(ids));
                if (!animals.isEmpty())
                {
                    StringBuilder tmp = new StringBuilder();
                    for (Animal a : animals)
                    {
                        if (tmp.length() != 0)
                            tmp.append(" ");
                        tmp.append(a.getId());
                    }
                    xml.writeAttribute("additional", tmp.toString());
                }
                if (!improve.isEmpty())
                {
                    StringBuilder tmp = new StringBuilder();
                    for (Companion.Improve i : improve)
                    {
                        if (tmp.length() != 0)
                            tmp.append(" ");
                        tmp.append(serializeEnum(i));
                    }
                    xml.writeAttribute("additional", tmp.toString());
                }
                serializeSource(xml, sourceRef);
            }
        }
    }

    private void serializeSource(XmlWriter xml, Reference ref) throws XmlException
    {
        if (ref != null)
        {
            xml.writeAttribute("source", Integer.toString(ref.source.id));
            if (ref.page != null)
                xml.writeAttribute("page", ref.page);
        }
    }

    private void serializeSource(XmlWriter xml, ReferenceList references) throws XmlException
    {
        StringBuilder s = new StringBuilder();
        for (Reference ref : references)
        {
            if (s.length() != 0)
                s.append(' ');
            s.append(ref.source.id);
            if (ref.page != null)
            {
                s.append('/');
                s.append(ref.page);
            }
        }
        if (s.length() != 0)
            xml.writeAttribute("source", s.toString());
    }
}
