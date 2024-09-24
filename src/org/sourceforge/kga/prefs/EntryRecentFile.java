package org.sourceforge.kga.prefs;

import java.io.File;
import java.util.prefs.BackingStoreException;

/**
 * Created by tidu8815 on 24/09/2018.
 */
public class EntryRecentFile
{
    public void initialize(java.util.prefs.Preferences node, String key)
    {
        lastOpened.initialize(node.node(key), "lastOpened");
        lastPath.initialize(node.node(key), "lastPath");
        list.initialize(node.node(key).node("list"), "");
        try {
        for(String recentKey:list.keys()) {
        	String path = list.get(recentKey);
        	if(path.endsWith(".seed") && new File(path).exists()) {
        		lastSeed=(list.get(recentKey));        		
        	}
        	else if (path.endsWith(".kga") && new File(path).exists()) {
        		lastGarden=(list.get(recentKey));      		
        	}
        }
        }
        catch(BackingStoreException e) {
        	e.printStackTrace();
        }
        if(lastGarden==null)
        	lastGarden="";
        if(lastSeed==null)
        	lastSeed="";
    }

    public static EntryString lastOpened = new EntryString("");
    public static String lastSeed = null;
    public static String lastGarden = null;
    public static EntryString lastPath = new EntryString("");
    public static EntryString list = new EntryString("");
}
