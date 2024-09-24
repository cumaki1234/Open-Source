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


import javafx.event.*;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ZoomEvent;
import org.sourceforge.kga.*;
import org.sourceforge.kga.gui.gardenplan.EditableGarden.Operation;
import org.sourceforge.kga.translation.*;

import java.util.List;

/**
 * @(#)GardenView.java
 *
 *
 * @author Tiberius Duluman
 * @version 1.00 2011/6/15
 */
public class GardenController implements EventHandler<MouseEvent>
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    private GardenCanvas view;

    public GardenController()
    {}

    public void setView(GardenCanvas view)
    {
        /* TODO: ScrollPane pane = view.getScrollPane();

        pane.addEventHandler(MouseEvent.ANY, this);
        pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        pane.addEventFilter(ZoomEvent.ANY, new EventHandler<ZoomEvent>()
        {
            @Override
            public void handle(ZoomEvent event)
            {
                log.info(event.toString());
            }
        }); */
        /* TODO: view.addMouseListener(this);
        view.addMouseMotionListener(this);
        view.addMouseWheelListener(this); */
        this.view = view;
        view.addEventHandler(MouseEvent.ANY, this);
    }

    @Override
    public void handle(MouseEvent event)
    {
        if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED))
            mouseClicked(event);
        if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED))
            mouseDragged(event);
        if (event.getEventType().equals(MouseEvent.MOUSE_EXITED))
            mouseExited(event);
        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED))
            mouseMoved(event);
        if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED))
            mousePressed(event);
        if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED))
            mouseReleased(event);
    }

    private void mouseClicked(MouseEvent e)
    {
        // picker tool, nothing to do when control is not pressed
        if (view.getGarden().getOperation() != EditableGarden.Operation.PickPlant)
            return;

        // no plant under the cursor
        Point grid = view.getGarden().imageToGrid(e.getX(), e.getY());
        List<TaxonVariety<Plant>> species = view.getGarden().getSpeciesNoPreview(view.getYear(), grid);
        if (species == null || species.size() == 0)
            return;

        // remove preview
        view.getGarden().removePreview();

        // only one plant under the cursor
        if (species.size() == 1)
        {
            if (view.getGarden().getSelectedPlant() != species.get(0))
                view.getGarden().setSelectedPlant(species.get(0));
            return;
        }

        // more plant under the cursor, select first one or the one after current selected one
        int i;
        for (i = 0; i < species.size(); ++i)
            if (view.getGarden().getSelectedPlant() == species.get(i))
            {
                ++i;
                break;
            }
        view.getGarden().setSelectedPlant(species.get(i % species.size()));
        view.getGarden().setOperation(EditableGarden.Operation.AddPlant);
    }

    Point lastPosition = null;

    private void mouseDragged(MouseEvent e)
    {
        log.info("mouseDragged " + Double.toString(e.getX()) + "," + Double.toString(e.getY()));
        // don't drag and drop on margins
        Point grid = view.getGarden().imageToGrid(e.getX(), e.getY());
        Rectangle bounds = view.getGarden().getBounds();

        if (grid.x < bounds.x || grid.y < bounds.y ||
            grid.x > bounds.x + bounds.width - 1 ||
            grid.y > bounds.y + bounds.height - 1)
        {
            return;
        }
        if (lastPosition != null && grid.x == lastPosition.x &&  grid.y == lastPosition.y)
            return;

        lastPosition = grid;
        mousePressed(e);
    }

    private void mouseExited(MouseEvent e)
    {
        view.getGarden().removePreview();
        lastPosition = null;
    }

    private void mouseMoved(MouseEvent e)
    {
        Point grid = view.getGarden().imageToGrid(e.getX(), e.getY());
        if (lastPosition != null && grid.x == lastPosition.x &&  grid.y == lastPosition.y)
            return;
        lastPosition = grid;
        if (view.getGarden().getSelectedPlant() != null && view.getGarden().getOperation() == EditableGarden.Operation.AddPlant)
        {
            view.getGarden().addPreview(view.getYear(), lastPosition, view.getGarden().getSelectedPlant());
        }
    }

    // user clicked the garden view
    private void mousePressed(MouseEvent e)
    {
        // picker tool is handled by mouseClicked
        if (view.getGarden().getOperation() == EditableGarden.Operation.PickPlant)
            return;

        // remove preview
        view.getGarden().removePreview();

        // convert mouse position to grid position
        Point grid = view.getGarden().imageToGrid(e.getX(), e.getY());
        lastPosition = grid;
        Rectangle bounds = view.getGarden().getBounds();
        boolean onMargin = grid.x < bounds.x || grid.y < bounds.y ||
            grid.x > bounds.x + bounds.width - 1 ||
            grid.y > bounds.y + bounds.height - 1;

        // remove plant
        if (view.getGarden().getOperation() == EditableGarden.Operation.DeletePlant)
        {
            // if square contains selected plant, remove only that specie,
            // otherwise clear all plant from square
            view.getGarden().removePlant(view.getYear(), grid, view.getGarden().getSelectedPlant());
        }
        else if (view.getGarden().getSelectedPlant() != null)
        {
            // add square to garden grid
            view.getGarden().addPlant(view.getYear(), grid, view.getGarden().getSelectedPlant());
            log.info("Adding square with "
                    + view.getGarden().getSelectedPlant()
                    + " at grid " + Integer.toString(grid.x) + "x" + Integer.toString(grid.y));

            // when adding plant on margin,
            // readd preview on empty space that is automatically added
            if (onMargin)
            {
                Point2D p2 = view.screenToLocal(e.getScreenX(), e.getScreenY());
                lastPosition = view.getGarden().imageToGrid(p2.getX(), p2.getY());
                view.getGarden().addPreview(view.getYear(), lastPosition, view.getGarden().getSelectedPlant());
                log.fine("Adding preview mouse " + Double.toString(e.getX()) + "x" + Double.toString(e.getY()) + " "
                        + view.getGarden().getSelectedPlant()
                        + " at grid " + Integer.toString(lastPosition.x) + "x" + Integer.toString(lastPosition.y));
            }
        }
        else
        {
            // user clicked view, but no plant selected
            // TODO: JOptionPane.showMessageDialog(null, Translation.getCurrent().select_species_first());
        }
    }

    private void mouseReleased(MouseEvent e)
    {
        lastPosition = null;
    }

    // if user holds CTRL down and move mouse wheel, zoom is changed
    private void mouseWheelMoved(MouseEvent e)
    {
        /* TODO:
        if (e.isControlDown())
            view.getGarden().setZoomFactor(
                view.getGarden().getZoomFactor() - 5 * e.getWheelRotation());
        else
            view.getParent().dispatchEvent(e); */
    }
}
