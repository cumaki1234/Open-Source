package org.sourceforge.kga.prefs;

/**
 * Created by tidu8815 on 21/09/2018.
 */
public class EntryInteger extends Entry<Integer>
{
    int defaultValue;

    public EntryInteger(int defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public Integer get()
    {
        return node.getInt(key, defaultValue);
    }

    @Override
    public void set(Integer value)
    {
        node.putInt(key, value);
    }
}