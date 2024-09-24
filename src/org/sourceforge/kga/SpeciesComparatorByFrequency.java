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


/**
 * Compare to plant by their frequency defined in a map
 */
public class SpeciesComparatorByFrequency implements java.util.Comparator<TaxonVariety<Plant>>
{
    java.util.Map<TaxonVariety<Plant>, Integer> f;
    boolean asc;
    static TaxonVarietyComparatorByName nameComparator = new TaxonVarietyComparatorByName();

    public SpeciesComparatorByFrequency(java.util.Map<TaxonVariety<Plant>, Integer> f)
    {
        this.f = f;
        this.asc = false;
    }

    public void setAscending(boolean asc)
    {
        this.asc = asc;
    }

    public boolean getAscending()
    {
        return asc;
    }

    @Override
    public int compare(TaxonVariety<Plant> o1, TaxonVariety<Plant> o2)
    {
        Integer f1 = f.get(o1);
        Integer f2 = f.get(o2);
        if (f1 == f2)
            return nameComparator.compare(o1, o2);
        if (f1 == null)
            return 1;
        if (f2 == null)
            return -1;
        return asc ?  f2 - f1 : f1 - f2;
    }
}