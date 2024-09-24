package org.sourceforge.kga.plant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SourceList
{
    public static int add(List<PropertySource> sources, PropertySource source)
    {
        if (source == null)
            return -1;
            
        for (int i = 0; i < sources.size(); ++i)
            if (sources.get(i).id == source.id)
                return i;
                
        sources.add(source);
        return sources.size() - 1;
    }
}
