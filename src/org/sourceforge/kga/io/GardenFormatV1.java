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

/**
 * This is where loading and saving garden takes place.
 * The kga file format is made of Integers.
 * It look like this:
 * 1, Constant FIELD_START
 * 2, Version of kga file
 * After that it is optional fields.
 * If field is FIELD_SQUARE:
 *  1, year
 *  2, grid x
 *  3, grid y
 *  4, FIELD_ADDRESS
 *  5, address of plant
 *  6, eventually repeating step 4 and 5 again if square has several plant
 */
class GardenFormatV1 implements GardenFormat
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    private static final int FIELD_START = 438351867;
    private static final int FIELD_SQUARE = 869725394;
    private static final int FIELD_ADDRESS = 495410209;
    private static final int KGA_FILE_VERSION = 1;

    public void load(Garden garden, InputStream stream) throws IOException, InvalidFormatException
    {
        DataInputStream in = new DataInputStream(new BufferedInputStream(stream));
        if (in.readInt() != FIELD_START)
            throw new InvalidFormatException();
        if (in.readInt() != KGA_FILE_VERSION)
            throw new InvalidFormatException();

        int year = 0, gridx = 0, gridy = 0;
        while (true)
        {
            int field;
            try
            {
                field = in.readInt();
            }
            catch (EOFException e)
            {
                log.info("Reading complete");
                break;
            }

            if (field == FIELD_SQUARE)
            {
                year = in.readInt();
                gridx = in.readInt();
                gridy = in.readInt();
            }
            else if (field == FIELD_ADDRESS)
            {
                int species = in.readInt();

                // use only one path instead of vertical, horizontal and crossing
                if (species == 117 || species == 119)
                    species = 116;

                garden.addPlant(year, new Point(gridx, gridy), Resources.plantList().getVariety(Resources.plantList().getPlant(species),null));
            }
        }

        log.info("Garden loaded");
    }

    public void save(Garden garden, DataOutputStream out) throws IOException
    {
        out.writeInt(FIELD_START);
        out.writeInt(KGA_FILE_VERSION);

        for (Map.Entry<Integer, HashMap<Point, java.util.List<TaxonVariety<Plant>>>> yearMap : garden.getAllSquares().entrySet())
            for (Map.Entry<Point, java.util.List<TaxonVariety<Plant>>> s : yearMap.getValue().entrySet())
            {
                if (s.getValue().size() == 0)
                    continue;
                out.writeInt(FIELD_SQUARE);
                out.writeInt(yearMap.getKey());
                out.writeInt(s.getKey().x);
                out.writeInt(s.getKey().y);
                for (TaxonVariety<Plant> plant : s.getValue())
                {
                    out.writeInt(FIELD_ADDRESS);
                    out.writeInt(plant.getId());
                }
            }
    }
}
