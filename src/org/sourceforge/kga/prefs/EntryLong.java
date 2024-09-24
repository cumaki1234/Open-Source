package org.sourceforge.kga.prefs;

/**
 * Created by tidu8815 on 21/09/2018.
 */
public class EntryLong extends Entry<Long>
{
    long defaultValue;

    public EntryLong(long defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public Long get()
    {
        return node.getLong(key, defaultValue);
    }

    @Override
    public void set(Long value)
    {
        node.putLong(key, value);
    }
}