package org.sourceforge.kga.prefs;

/**
 * Created by tidu8815 on 21/09/2018.
 */
public class EntryBoolean extends Entry<Boolean>
{
    boolean defaultValue;

    public EntryBoolean(boolean defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public Boolean get()
    {
        return node.getBoolean(key, defaultValue);
    }

    @Override
    public void set(Boolean value)
    {
        node.putBoolean(key, value);
    }
}