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

import java.util.ArrayList;


/**
 * This class store a book/site where plant information has been extracted
 */
public class PropertySource
{
    public PropertySource(int id, String name, String url)
    {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public int id;
    public String name;
    public String url;
}
