package org.sourceforge.kga.plant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ReferenceList implements Iterable<Reference>, Comparable<ReferenceList>
{
    private ArrayList<Reference> sourceRef = new ArrayList<>();

    public void add(Reference ref)
    {
        if (ref == null)
            return;
        for (Reference r : sourceRef)
            if (r.compareTo(ref) == 0)
                return;
        sourceRef.add(ref);
        Collections.sort(sourceRef);
    }

    public void add(ReferenceList references)
    {
        if (references == null || references.sourceRef.isEmpty())
            return;
        for (Reference ref : references.sourceRef)
        {
            boolean found = false;
            for (Reference r : sourceRef)
                if (r.compareTo(ref) == 0)
                {
                    found = true;
                    break;
                }
            if (!found)
                sourceRef.add(ref);
        }
        Collections.sort(sourceRef);
    }

    @Override
    public Iterator<Reference> iterator()
    {
        return sourceRef.iterator();
    }

    @Override
    public int compareTo(ReferenceList o)
    {
        for (int i = 0; i < sourceRef.size() && i < o.sourceRef.size(); ++i)
        {
            int ret = sourceRef.get(i).compareTo(o.sourceRef.get(i));
            if (ret != 0)
                return ret;
        }
        return sourceRef.size() - o.sourceRef.size();
    }
}
