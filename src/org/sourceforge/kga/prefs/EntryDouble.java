package org.sourceforge.kga.prefs;

/**
 * Created by tidu8815 on 25/09/2018.
 */
public class EntryDouble extends Entry<Double>
{
    double defaultValue;

    public EntryDouble(double defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public Double get()
    {
        return node.getDouble(key, defaultValue);
    }

    @Override
    public void set(Double value)
    {
        node.putDouble(key, value);
    }
}