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

package org.sourceforge.kga.gui.gardenplan;


import org.sourceforge.kga.Plant;
import org.sourceforge.kga.TaxonVariety;

/**
 * Observes when a editable garden changes. Please comment this.
 * @author Tiberius Duluman
 *
 */
public interface EditableGardenObserver
{
    // something has changed in the garden
    public void gardenChanged(EditableGarden editableGarden);

    // the zoom factor has been changed by calling setZoom
    public void zoomFactorChanged(EditableGarden editableGarden);

    // current selected plant has been changed by calling setSelectedPlant
    public void previewSpeciesChanged(EditableGarden editableGarden, TaxonVariety<Plant> plant);

    //
    public void operationChanged(EditableGarden editableGarden);
}
