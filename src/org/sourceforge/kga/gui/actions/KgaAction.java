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

package org.sourceforge.kga.gui.actions;

import javax.swing.AbstractAction;

import org.sourceforge.kga.gui.*;
import org.sourceforge.kga.gui.gardenplan.EditableGarden;

public abstract class KgaAction extends AbstractAction
{
    private static final long serialVersionUID = 1L;
    private Gui gui;

    public KgaAction(Gui gui, String name)
    {
        this.gui = gui;
        putValue(NAME, name);
    }

    public Gui getGui()
    {
        return gui;
    }

    public EditableGarden getGarden()
    {
        return null; // TODO: gui.getGarden().getGarden();
    }
    /* TODO:
    public GardenView getGardenView(int year)
    {
        return gui.getGardenView(year);
    } */
}
