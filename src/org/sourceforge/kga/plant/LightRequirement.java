package org.sourceforge.kga.plant;

import org.sourceforge.kga.translation.Translation;

public class LightRequirement
{
    public enum Type { FULL_SHADE, PARTIAL_SHADE, LIGHT_SHADE, PART_SUN, FULL_SUN }

    public Type type;

    public LightRequirement(Type type) { this.type = type; }

    public ReferenceList references = new ReferenceList();

    public String translate()
    {
        Translation t = Translation.getCurrent();
        switch (type)
        {
        case FULL_SHADE: // no direct sunlight
            return t.unknown();
        case PARTIAL_SHADE: // morning or afternoon sunlight
            return t.unknown();
        case LIGHT_SHADE: // under sparse foliage
            return t.unknown();
        case PART_SUN: // tolerate direct sunlight
            return t.unknown();
        case FULL_SUN: // direct sunlight
            return t.unknown();
        }
        return "";
    }
}