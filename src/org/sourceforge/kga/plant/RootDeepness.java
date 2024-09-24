/**
 * Kitchen garden aid is a planning tool for kitchengardeners.
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

package org.sourceforge.kga.plant;

import org.sourceforge.kga.Garden;


public class RootDeepness implements Comparable<RootDeepness>
{
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Garden.class.getName());

    public RootDeepness(int minimum, int maximum)
    {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static RootDeepness parseString(String rootDeepness) throws Exception
    {
        String[] deepness = rootDeepness.split("-");
        int minimum, maximum;
        if (deepness.length == 2)
        {
            minimum = Integer.parseInt(deepness[0]);
            maximum = Integer.parseInt(deepness[1]);
        }
        else if (deepness.length == 1)
        {
            minimum = maximum = Integer.parseInt(deepness[0]);
        }
        else
        {
            throw new Exception("Invalid root deepness: " + rootDeepness);
        }
        return new RootDeepness(minimum, maximum);
    }

    public String toString()
    {
        if (minimum != maximum)
        {
            return Integer.toString(minimum) + "-" + Integer.toString(maximum);
        }
        else
        {
            return Integer.toString(minimum);
        }
    }

    public int getMinimum() { return minimum; }
    public int getMaximum() { return maximum; }

    int minimum, maximum;
    public ReferenceList references = new ReferenceList();

    @Override
    public int compareTo(RootDeepness o)
    {
        return o.minimum == minimum && o.maximum == maximum ? 0 : 1;
    }
}