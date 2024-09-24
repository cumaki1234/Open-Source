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
import org.sourceforge.kga.*;

public class SerializableSpecies
{
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Plant.class.getName());

    /**
     * The file which user will save to. Null if not selected yet.
     */
    private transient File file = null;
    private PlantList speciesList = null;
    static SpeciesListFormatV1 v1 = new SpeciesListFormatV1();

    /**
     * Creating a new plant list
     */
    public SerializableSpecies()
    {
        speciesList = new PlantList();
    }

    /**
     * Load a plant list from the jar file
     * @param is a kga formated resource
     */
    public SerializableSpecies(InputStream is)
        throws IOException, InvalidFormatException
    {
        speciesList = loadFrom(is);
    }

    /**
     * Creating a new plant list and loads from file
     * @param file a kga formated file
     */
    public SerializableSpecies(File file)
        throws FileNotFoundException, IOException, InvalidFormatException
    {
        log.info("Loading species list from " + file.getAbsolutePath());
        speciesList = loadFrom(new FileInputStream(file));
        this.file = file;
    }


    public PlantList getSpeciesList()
    {
        return speciesList;
    }

    public File getFile()
    {
        return file;
    }

    /**
     * Loading set of squares from InputStream.
     * @param is the InputStream to load from
     * @return errorcode specified in SpeciesList
     */
    private PlantList loadFrom(InputStream is) throws IOException, InvalidFormatException
    {
        // read input stream into memory
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        byte[] buffer = new byte[65536];
        while (is.available() > 0)
        {
            int read = is.read(buffer, 0, 65536);
            tmp.write(buffer, 0, read);
        }

        ByteArrayInputStream speciesStream = new ByteArrayInputStream(tmp.toByteArray());
        try { return v1.load(speciesStream); }
        catch (InvalidFormatException ex) { throw ex; }

        // extension point for future file formats
        // try to load using another file format
    }

    /**
     * Tries to save plant list to the file which this plant was loaded from.
     * If plant list was created from scratch this method does nothing and returns false.
     * @return true if successful otherwise false
     */
    public boolean saveToFile() throws FileNotFoundException, IOException
    {
        if (file == null)
            return false;
        saveToFile(file);
        return true;
    }

    /**
     * Saving plant to file.
     * @param file the file to save the squares
     * @return true if successful
     */
    public void saveToFile(File file) throws FileNotFoundException, IOException
    {
        log.info("Saving to " + file.getAbsolutePath());
        DataOutputStream out = new DataOutputStream(new
            BufferedOutputStream(new FileOutputStream(file)));
        v1.save(speciesList, out);
        out.close();
        this.file = file;
    }
}
