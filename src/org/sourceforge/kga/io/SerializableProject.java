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

public class SerializableProject
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    static GardenFormatV1 v1 = new GardenFormatV1();
    static GardenFormatXmlV1 xmlV1 = new GardenFormatXmlV1();
    static GardenFormatXmlV2 xmlV2 = new GardenFormatXmlV2();
    static ProjectFormatV1 prjV1 = new ProjectFormatV1();

    /**
     * Loading set of squares from InputStream.
     * @param is the InputStream to load from
     */
    static public void load(Project project, InputStream is)
            throws IOException, InvalidFormatException
    {
        // read input stream into memory
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        byte[] buffer = new byte[65536];
        while (is.available() > 0)
        {
            int read = is.read(buffer, 0, 65536);
            tmp.write(buffer, 0, read);
        }

        ByteArrayInputStream projectStream = new ByteArrayInputStream(tmp.toByteArray());
        try
        {
            prjV1.load(project, projectStream);
            return;
        }
        catch (InvalidFormatException ex)
        {}

        try
        {
            projectStream.reset();
            xmlV2.load(project.garden, projectStream);
            return;
        }
        catch (InvalidFormatException ex)
        {}

        try
        {
            projectStream.reset();
            xmlV1.load(project.garden, projectStream);
            return;
        }
        catch (InvalidFormatException ex)
        {}

        projectStream.reset();
        v1.load(project.garden, projectStream);

        // extension point for future file formats
        // try to load using another file format
    }

    /**
     * Saving garden to file.
     * @param file the file to save the squares
     * @return true if successful
     */
    static public void saveToFile(Project project, File file) throws FileNotFoundException, IOException
    {
        log.info("Saving to " + file.getAbsolutePath());
        DataOutputStream out = new DataOutputStream(new
                BufferedOutputStream(new FileOutputStream(file)));
        prjV1.save(project, out);
        out.close();
    }
}
