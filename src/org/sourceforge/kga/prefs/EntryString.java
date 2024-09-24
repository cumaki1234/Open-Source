package org.sourceforge.kga.prefs;

/**
 * Created by tidu8815 on 21/09/2018.
 */
public class EntryString extends Entry<String>
{
    String defaultValue;

    public EntryString(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @Override
    public String get()
    {
        return node.get(key, defaultValue);
    }

    @Override
    public void set(String value)
    {
        node.put(key, value);
    }
}
