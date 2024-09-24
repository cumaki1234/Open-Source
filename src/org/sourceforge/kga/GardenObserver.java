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

import org.sourceforge.kga.Point;
import org.sourceforge.kga.Rectangle;

/**
 * Observes when a garden changes. Please comment this.
 * @author Tiberius Duluman
 *
 */
public interface GardenObserver
{
    // an year has been addded
    void yearAdded(Garden sender, int year);

    // an year has been deleted
    void yearDeleted(Garden sender, int year);

    // an adjacent square has been changed, and hints related to this square may have been changed
    void hintsChanged(int year, Point grid);

    // plant has been added or removed from the square
    void plantsChanged(int year, Point grid);

    // the garden extended by adding a square outside current bounds
    void boundsChanged(Rectangle bounds);
}
