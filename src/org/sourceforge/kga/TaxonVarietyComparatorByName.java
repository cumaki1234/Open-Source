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


package org.sourceforge.kga;

import org.sourceforge.kga.translation.*;


public class TaxonVarietyComparatorByName implements java.util.Comparator<TaxonVariety<Plant>>
{
    @Override
    public int compare(TaxonVariety<Plant> o1, TaxonVariety<Plant> o2)
    {
        Translation t = Translation.getCurrent();
        if (o1.isItem() == o2.isItem())
            return t.getCollator().compare(t.translate(o1), t.translate(o2));
        return o1.isItem() ? 1 : -1;
    }
}
