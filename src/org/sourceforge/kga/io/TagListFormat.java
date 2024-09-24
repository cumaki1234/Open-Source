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


import org.sourceforge.kga.PlantList;
import org.sourceforge.kga.TagList;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

public interface TagListFormat
{
    void load(TagList tags, PlantList plants, XmlReader xml) throws XmlException;
    void save(TagList tags, XmlWriter xml) throws XmlException;
}
