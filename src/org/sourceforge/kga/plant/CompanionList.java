package org.sourceforge.kga.plant;

import java.util.*;

import org.sourceforge.kga.Animal;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Taxon;

public class CompanionList
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(CompanionList.class.getName());

    ArrayList<Companion> companions = new ArrayList<>();
    ArrayList<Companion> inheritedCache = null;
    Plant plant = null;

    public CompanionList(Plant plant)
    {
        this.plant = plant;
    }

    private static Companion findCompanion(
        ArrayList<Companion> companions, Plant plant, Companion.Type type,
        TreeSet<Animal> animals, TreeSet<Companion.Improve> improve)
    {
        for (Companion companion : companions)
            if (companion.plant.getId() == plant.getId() &&
                companion.type.compareTo(type) == 0 &&
                companion.animals.equals(animals) &&
                companion.improve.equals(improve))
            {
                return companion;
            }
        return null;
    }

    static private Companion add(
        ArrayList<Companion> companions, Plant plant, Companion.Type type,
        TreeSet<Animal> animals, TreeSet<Companion.Improve> improve, Reference sourceRef)
    {
        Companion found = findCompanion(companions, plant, type, animals, improve);
        if (found != null)
        {
            // add a new source to an already existing companion
            found.references.add(sourceRef);
            return found;
        }

        Companion newCompanion = new Companion();
        newCompanion.plant = plant;
        newCompanion.type = type;
        newCompanion.animals = animals;
        newCompanion.improve = improve;
        newCompanion.references.add(sourceRef);
        companions.add(newCompanion);
        return newCompanion;
    }

    private static Companion add(
        ArrayList<Companion> companions, Plant plant, Companion.Type type,
        TreeSet<Animal> animals, TreeSet<Companion.Improve> improve, ReferenceList sourceRef)
    {
        Companion found = add(companions, plant, type, animals, improve, (Reference)null);
        for (Reference ref : sourceRef)
            found.references.add(ref);
        return found;
    }

    public Companion add(
            Plant plant, Companion.Type type,
            TreeSet<Animal> animals, TreeSet<Companion.Improve> improve, Reference sourceRef)
    {
        cleanUpInheritedCache();
        return add(companions, plant, type, animals, improve, sourceRef);
    }

    public Companion add(
        Plant plant, Companion.Type type,
        TreeSet<Animal> animals, TreeSet<Companion.Improve> improve, ReferenceList sourceRef)
    {
        cleanUpInheritedCache();
        return add(companions, plant, type, animals, improve, sourceRef);
    }

    // Remove the cache list of companions
    private void cleanUpInheritedCache()
    {
        inheritedCache = null;
        for (Taxon child : plant.getChildren())
            ((Plant)child).getCompanions().cleanUpInheritedCache();
    }

    public boolean isDefined()
    {
        return companions.size() != 0;
    }

    public Collection<Companion> get()
    {
        return companions;
    }

    public Collection<Companion> getInherited()
    {
        if (inheritedCache != null)
            return inheritedCache;

        inheritedCache = new ArrayList<>();
        Plant plant = this.plant;
        while (plant != null)
        {
            for (Companion companion : plant.getCompanions().get())
            {
                add(inheritedCache, companion.plant, companion.type, companion.animals, companion.improve, companion.references);
            }
            plant = plant.getParent();
        }
        Collections.sort(inheritedCache, new CompanionComparatorByParent());
        return inheritedCache;
    }
    
    public void logConflicts(Companion companion, StringBuilder conflict)
    {
        for (Companion c : companions)
        {
            if (c == companion)
                continue;
            if (companion.type.isBeneficial() != c.type.isBeneficial() &&
                (c.plant == companion.plant || c.plant.isParentOf(companion.plant)))
            {
                conflict.append("Conflict found: ");
                conflict.append(plant.getName());
                conflict.append(" (");
                conflict.append(plant.getId());
                conflict.append("): ");
                conflict.append(companion.plant.getName());
                conflict.append(" (");
                conflict.append(companion.plant.getId());
                conflict.append(") / ");
                conflict.append(c.plant.getName());
                conflict.append(" (");
                conflict.append(c.plant.getId());
                conflict.append(")\n");
            }
        }
    }
    
    public void logConflicts(StringBuilder conflict)
    {
        for (Companion c : companions)
            logConflicts(c, conflict);
    }

    private class CompanionComparatorByParent implements Comparator<Companion>
    {
        @Override
        public int compare(Companion o1, Companion o2)
        {
            if (o1.plant.getType() != o2.plant.getType())
                return o2.plant.getType().ordinal() - o1.plant.getType().ordinal();
            return o1.plant.getId() - o2.plant.getId();
        }
    }
}
