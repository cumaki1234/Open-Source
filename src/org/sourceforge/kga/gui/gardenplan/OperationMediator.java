package org.sourceforge.kga.gui.gardenplan;

import org.sourceforge.kga.Garden;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.PlantListSelection;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.gardenplan.plantSelection.PlantDetailPanel;
import org.sourceforge.kga.gui.gardenplan.toolbar.Toolbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Author: tiberius.duluman
 * Date: 2/13/14
 */
public class OperationMediator implements Toolbox.Listener, EditableGardenObserver, PlantListSelection.Listener, PlantDetailPanel.Listener
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    Toolbox toolbox;
    PlantDetailPanel plantDetail;
    PlantListSelection plantSelection;
    EditableGarden garden;
    boolean handlingEvents = false;

    public OperationMediator(Toolbox toolbox, PlantDetailPanel plantDetail, PlantListSelection plantSelection)
    {
        this.toolbox = toolbox;
        this.plantDetail=plantDetail;
        this.plantSelection = plantSelection;

        // TODO: Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        toolbox.setListener(this);
        plantDetail.setListener(this);
        plantSelection.addListener(this);
        
    }

    public void setGarden(EditableGarden garden)
    {
        if (this.garden != null)
            this.garden.removeObserver(this);
        this.garden = garden;
        this.garden.addObserver(this);

        handlingEvents = true;
        if(plantSelection.getSelectedPlant()!=null)
        	garden.setSelectedPlant(Resources.plantList().getVariety(plantSelection.getSelectedPlant(),""));
        else {
        	garden.setSelectedPlant(null);
        }
        handlingEvents = false;

        garden.setOperation(toolbox.getOperation());
    }

    @Override
    public void gardenChanged(EditableGarden editableGarden) {}

    @Override
    public void zoomFactorChanged(EditableGarden editableGarden) {}

    /* TODO:
    // event from keyboard
    @Override
    public void eventDispatched(AWTEvent event)
    {
        if (handlingEvents || garden == null)
            return;
        handlingEvents = true;

        KeyEvent e = (KeyEvent)event;
        // log.info("eventDispatched " + e.toString());
        if (e.isControlDown())
        {
            garden.setOperation(EditableGarden.Operation.PickPlant);
            toolbox.setOperation(EditableGarden.Operation.PickPlant);
        }
        else if (e.isShiftDown())
        {
            garden.setOperation(EditableGarden.Operation.DeletePlant);
            toolbox.setOperation(EditableGarden.Operation.DeletePlant);
        }
        else if (e.getID() == KeyEvent.KEY_RELEASED)
        {
            garden.setOperation(EditableGarden.Operation.AddPlant);
            toolbox.setOperation(EditableGarden.Operation.AddPlant);
        }

        handlingEvents = false;
    }
    */

    // operation changed by toolbox
    @Override
    public void operationChanged(EditableGarden.Operation operation)
    {
        // log.info("operationChanged " + (operation == null ? "null": operation.toString()));
        if (handlingEvents || garden == null)
            return;
        handlingEvents = true;

        if (operation == null)
        {
            garden.setSelectedPlant(null);
            garden.setOperation(EditableGarden.Operation.AddPlant);
            plantSelection.unselectAll();
        }
        else
        {
            garden.setOperation(operation);
        }

        handlingEvents = false;
    }

    // operation changed by garden controller
    @Override
    public void operationChanged(EditableGarden editableGarden)
    {
       /* if (handlingEvents || garden == null)
            return;
        handlingEvents = true;

        toolbox.setOperation(editableGarden.getOperation());

        handlingEvents = false;*/

    }

    // selected plant changed by garden controller
    @Override
    public void previewSpeciesChanged(EditableGarden editableGarden, TaxonVariety<Plant> plant)
    {
        if (handlingEvents || garden == null)
            return;
        handlingEvents = true;

        plantSelection.selectPlant(plant.getTaxon(), true);
        toolbox.setPlantSelected(plant!=null);
        plantDetail.setSelectedPlant(plant);

        handlingEvents = false;
    }

    // plant changed by plant panel list
    @Override
    public void selectedPlantChanged(Plant plant)
    {
        if (handlingEvents || garden == null)
            return;
        handlingEvents = true;

        garden.setSelectedPlant(Resources.plantList().getVariety(plant,""));
        garden.setOperation(EditableGarden.Operation.AddPlant);
        toolbox.setPlantSelected(plant!=null);
        plantDetail.setSelectedPlant(Resources.plantList().getVariety(plant,""));

        handlingEvents = false;
    }
    
    @Override
    public void selectedPlantVarietyChanged(TaxonVariety<Plant> plant)
    {
        if (handlingEvents || garden == null)
            return;
        handlingEvents = true;
        garden.setSelectedPlant(plant);
        garden.setOperation(EditableGarden.Operation.AddPlant);
        toolbox.setPlantSelected(plant!=null);

        handlingEvents = false;
    }
}
