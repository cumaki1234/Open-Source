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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.sourceforge.kga.gui.Gui;
import org.sourceforge.kga.translation.*;


public class Review extends KgaAction
{
    private static final long serialVersionUID = 1L;

    public Review(Gui gui)
    {
        super(gui, Translation.getCurrent().action_review());
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        openPage();
    }

    public static void openPage()
    {
        try
        {
            Desktop.getDesktop().browse(
                new URI("https://sourceforge.net/projects/kitchengarden/reviews/new"));
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        catch (URISyntaxException e1)
        {
            e1.printStackTrace();
        }
    }
}
