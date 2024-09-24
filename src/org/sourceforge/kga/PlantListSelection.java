package org.sourceforge.kga;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by Tiberius on 1/14/2017.
 */
public class PlantListSelection
{
    public interface Listener
    {
        void selectedPlantChanged(Plant plant);
    }

    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(PlantListSelection.class.getName());

    private ArrayList<Plant> selectedPlants = new ArrayList<>();
    private boolean multipleSelection = false;

    public PlantListSelection()
    {}

    public boolean getMultipleSelection() { return multipleSelection; }

    public void setMultipleSelection(boolean b) { multipleSelection = b; }

    public List<Plant> getSelectedPlants()
    {
        return selectedPlants;
    }

    public Plant getSelectedPlant()
    {
        return selectedPlants.size() > 0 ? selectedPlants.get(0) : null;
    }

    public void selectAll(Collection<Plant> plants)
    {
        for (Plant plant : plants)
            selectPlant(plant, true);
    }

    public void unselectAll(Collection<Plant> plants)
    {
        for (Plant plant : plants)
            selectPlant(plant, false);
    }

    public void unselectAll()
    {
        if (selectedPlants.size() > 0)
        {
            selectedPlants.clear();
            if (!multipleSelection)
                notifySelectedPlantChanged(null);
        }
    }

    public void invertSelection(Collection<Plant> plants)
    {
        for (Plant plant : plants)
        {
            selectPlant(plant, !selectedPlants.contains(plant));
        }
    }

    public void selectPlant(Plant plant, boolean select)
    {
        if (select && selectedPlants.contains(plant) || !select && !selectedPlants.contains(plant))
            return;
        log.info("selectPlant " + plant.getName() + " " + Boolean.toString(select));

        if (!select)
            selectedPlants.remove(plant);
        if (!multipleSelection && selectedPlants.size() > 0)
            selectedPlants.clear();
        if (select)
            selectedPlants.add(plant);

        notifySelectedPlantChanged(multipleSelection || selectedPlants.size() == 0 ? null : selectedPlants.get(0));
    }

    public boolean isSelected(Plant plant)
    {
        return selectedPlants.contains(plant);
    }

    ArrayList<Listener> listeners = new ArrayList<>();

    public void addListener(Listener listener)
    {
        listeners.add(listener);
    }

    public void removeListener(Listener listener)
    {
        listeners.remove(listener);
    }

    private void notifySelectedPlantChanged(Plant plant)
    {
        for (Listener listener : listeners)
            listener.selectedPlantChanged(plant);
    }
}
