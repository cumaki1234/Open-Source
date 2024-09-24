package org.sourceforge.kga.plant;


import org.sourceforge.kga.Plant;
import org.sourceforge.kga.translation.Translation;

public class Lifetime
{
    static public final int DEFAULT_REPETITION_GAP = 3;
    public enum Value { ANNUAL, BIENNIAL, PERENNIAL }

    Plant plant;
    Value value;
    Integer repetitionYears = null;
    Integer repetitionGap = null;
    public ReferenceList references = new ReferenceList();

    public Lifetime(Plant plant) { this.plant = plant; }

    public Value get()
    {
        if (value != null)
            return value;
        if (plant.getParent() != null)
            return plant.getParent().lifetime.get();
        return Value.ANNUAL;
    }

    public String translate()
    {
        Translation t = Translation.getCurrent();
        switch (get())
        {
            case ANNUAL:
                return t.lifetime_annual();
            case BIENNIAL:
                return t.lifetime_biennial();
            case PERENNIAL:
                return t.lifetime_perennial();
        }
        return "";
    }

    public int getRepetitionYears()
    {
        if (plant.isItem())
            return Integer.MAX_VALUE;
        if (value == null)
            return plant.getParent() != null ? plant.getParent().lifetime.getRepetitionYears() : 1;
        if (value == Value.ANNUAL)
            return 1;
        if (repetitionYears != null)
            return repetitionYears;
        if (value == Value.BIENNIAL)
            return 2;
        return Integer.MAX_VALUE; // Value.PERENNIAL
    }

    public int getRepetitionGap()
    {
        if (repetitionGap != null)
            return repetitionGap;
        if (plant.getParent() == null)
            return DEFAULT_REPETITION_GAP;
        return plant.getParent().lifetime.getRepetitionGap();
    }

    public boolean isDefined()          { return value != null; }
    public boolean hasRepetitionYears() { return plant.isItem() || value == null || value == Value.ANNUAL ? false : repetitionYears != null; }
    public boolean hasRepetitionGap()   { return repetitionGap != null; }

    public void set(Value lifetime, Integer repetitionYears, Integer repetitionGap)
    {
        this.value = lifetime;
        this.repetitionYears = repetitionYears;
        this.repetitionGap = repetitionGap;
    }

}
