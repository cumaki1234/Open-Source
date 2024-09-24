package org.sourceforge.kga;

import org.sourceforge.kga.plant.Tag;
import org.sourceforge.kga.translation.Iso639_1;
import org.sourceforge.kga.translation.Translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tiberius on 1/14/2017.
 */
public class PlantListFilter
{
    public interface Listener
    {
        void filteredPlantsChanged();
    }

    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(PlantListFilter.class.getName());

    private Collection<Plant> plants;
    private ArrayList<Plant> filteredPlants = new ArrayList<>();
    private Taxon filterByFamily;
    private String filterByName;
    private Tag filterByTag;

    public PlantListFilter(Collection<Plant> plants)
    {
        this.plants = plants;
        filterSpecies();
    }

    public void filterByFamily(Taxon family)
    {
        filterByFamily = family;
        filterSpecies();
    }

    public Taxon getFilterByFamily()
    {
        return filterByFamily;
    }

    public void filterByName(String name)
    {
        filterByName = name;
        filterSpecies();
    }

    public void filterByTag(Tag tag)
    {
        filterByTag = tag;
        filterSpecies();
    }

    private void filterSpecies()
    {
        log.info("filterSpecies");
        filteredPlants.clear();

        // add filtered plants to the container
        for (Plant plant : plants)
        {
            boolean ok = true;

            // check family
            if (filterByFamily != null && filterByFamily != plant.getFamily())
                ok = false;

            // check name
            else if (filterByName != null && filterByName.length() != 0 &&
                !Iso639_1.containsText(plant.getName(), filterByName) &&
                !Iso639_1.containsText(Translation.getCurrent().translate(plant), filterByName))
                ok = false;

            // check tag
            else if (filterByTag != null && !filterByTag.getSpecies().contains(plant))
                ok = false;

            // passed all filters
            if (ok)
                filteredPlants.add(plant);
        }

        notifyFilteredPlantsChanged();
    }

    public Collection<Plant> getPlants()
    {
        return plants;
    }

    public List<Plant> getFilteredPlants()
    {
        return filteredPlants;
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

    private void notifyFilteredPlantsChanged()
    {
        for (Listener listener : listeners)
            listener.filteredPlantsChanged();
    }
}
