package org.sourceforge.kga.plant;

import org.sourceforge.kga.translation.Translation;

public class WeedControl
{
    public enum Type { WEEDY, CLEAR; }

    public Type type;

    public WeedControl(Type type) { this.type = type; }

    public ReferenceList references = new ReferenceList();

    public String translate()
    {
        Translation t = Translation.getCurrent();
        switch (type)
        {
        case WEEDY:
            return t.weed_control_weedy();
        case CLEAR:
            return t.weed_control_clear();
        }
        return "";
    }
}