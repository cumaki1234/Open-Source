package org.sourceforge.kga.plant;

import java.util.ArrayList;

public class Reference implements Comparable<Reference>
{
    public Reference(PropertySource source, String page)
    {
        this.source = source;
        this.page = page;
    }

    public PropertySource source;
    public String page;

    @Override
    public int compareTo(Reference o)
    {
        if (source == o.source)
        {
            if (page == null && o.page == null)
                return 0;
            if (page == null && o.page != null)
                return -1;
            if (page != null && o.page == null)
                return 1;
            return page.compareTo(o.page);
        }
        return source.id < o.source.id ? -1 : 1;
    }
};
