package org.sourceforge.kga.prefs;

/**
 * Created by tidu8815 on 25/09/2018.
 */
public class EntryWindowBounds
{
    public void initialize(java.util.prefs.Preferences node, String key)
    {
        x.initialize(node.node(key), "x");
        y.initialize(node.node(key), "y");
        w.initialize(node.node(key), "w");
        h.initialize(node.node(key), "h");
    }

    public EntryDouble x = new EntryDouble(Double.NaN);
    public EntryDouble y = new EntryDouble(Double.NaN);
    public EntryDouble w = new EntryDouble(Double.NaN);
    public EntryDouble h = new EntryDouble(Double.NaN);
}
