package org.sourceforge.kga.prefs;

import java.util.prefs.BackingStoreException;

/**
 * Created by tidu8815 on 21/09/2018.
 */
public class Entry<TYPE>
{
    protected java.util.prefs.Preferences node;
    protected String key;

    public Entry()
    {
    }

    public void initialize(java.util.prefs.Preferences node, String key)
    {
        this.node = node;
        this.key = key;
    }

    public TYPE get()
    {
        return null;
    }

    public void set(TYPE value)
    {
    }

    public TYPE get(String key)
    {
        String tmpKey = this.key;
        this.key = key;
        TYPE tmpValue = get();
        this.key = tmpKey;
        return tmpValue;
    }

    public void set(String key, TYPE value)
    {
        String tmpKey = this.key;
        this.key = key;
        set(value);
        this.key = tmpKey;
    }

    public void remove()
    {
        node.remove(key);
    }

    public void remove(String key)
    {
        node.remove(key);
    }

    public java.util.prefs.Preferences node()
    {
        return node;
    }

    public String[] keys() throws BackingStoreException
    {
        return node.keys();
    }
}

