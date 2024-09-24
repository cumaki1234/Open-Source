package org.sourceforge.kga.plant;

import org.sourceforge.kga.translation.Translation;

public class NutritionalNeeds
{
    public enum Type { SOIL_IMPROVER, LOW, HIGH }

    public Type type;

    public NutritionalNeeds(Type type) { this.type = type; }

    public ReferenceList references = new ReferenceList();

    public String translate()
    {
        Translation t = Translation.getCurrent();
        switch (type)
        {
        case HIGH:
            return t.nutritional_needs_high();
        case LOW:
            return t.nutritional_needs_low();
        case SOIL_IMPROVER:
            return t.nutritional_needs_soil_improver();
        }
        return "";
    }
}