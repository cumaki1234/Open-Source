package org.sourceforge.kga;

import javafx.scene.image.Image;
import org.sourceforge.kga.io.SerializableSpecies;
import org.sourceforge.kga.rules.Rule;
import org.sourceforge.kga.translation.TranslationList;
import org.xml.sax.SAXParseException;

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.prefs.Preferences;

/**
 * Created by tidu8815 on 1/22/2016.
 */
public class Resources
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Resources.class.getName());

    private static PlantList plantList = null;
    private static TranslationList translationList = null;

    static Image cursorArrow;
    static Image cursorAdd;
    static Image cursorDelete;
    static Image cursorPicker;
    static Image folder;
    static Image file;
    static Image filter;
    static Image edit;
    static Image snapshot;

    public static void load()
    {
        log.info("Loading resources");
        Rule.GOOD = new Image(Resources.openFile("pics/good.svg"));
        Rule.BAD  = new Image(Resources.openFile("pics/bad.svg"));
        Rule.EQUAL  = new Image(Resources.openFile("pics/equal.svg"));
        //Rule.GOOD_ARROWS[0][0] = new Image(Resources.openFile("pics/good_arrow_nw.svg"));
        //Rule.GOOD_ARROWS[0][1] = new Image(Resources.openFile("pics/good_arrow_n.svg"));
        //Rule.GOOD_ARROWS[0][2] = new Image(Resources.openFile("pics/good_arrow_ne.svg"));
        // Rule.GOOD_ARROWS[1][0] = new Image(Resources.openFile("pics/good_arrow_w.svg"));
        // Rule.GOOD_ARROWS[1][2] = new Image(Resources.openFile("pics/good_arrow_e.svg"));
        //Rule.GOOD_ARROWS[2][0] = new Image(Resources.openFile("pics/good_arrow_sw.svg"));
        //Rule.GOOD_ARROWS[2][1] = new Image(Resources.openFile("pics/good_arrow_s.svg"));
        //Rule.GOOD_ARROWS[2][2] = new Image(Resources.openFile("pics/good_arrow_se.svg"));
        //Rule.BAD_ARROWS[0][0] = new Image(Resources.openFile("pics/bad_arrow_nw.svg"));
        //Rule.BAD_ARROWS[0][1] = new Image(Resources.openFile("pics/bad_arrow_n.svg"));
        //Rule.BAD_ARROWS[0][2] = new Image(Resources.openFile("pics/bad_arrow_ne.svg"));
        //Rule.BAD_ARROWS[1][0] = new Image(Resources.openFile("pics/bad_arrow_w.svg"));
        //Rule.BAD_ARROWS[1][2] = new Image(Resources.openFile("pics/bad_arrow_e.svg"));
        //Rule.BAD_ARROWS[2][0] = new Image(Resources.openFile("pics/bad_arrow_sw.svg"));
        //Rule.BAD_ARROWS[2][1] = new Image(Resources.openFile("pics/bad_arrow_s.svg"));
        //Rule.BAD_ARROWS[2][2] = new Image(Resources.openFile("pics/bad_arrow_se.svg"));

        cursorArrow = new Image(Resources.openFile("pics/cursor.svg"));
        cursorAdd = new Image(Resources.openFile("pics/good.svg"));
        cursorDelete = new Image(Resources.openFile("pics/delete.svg"));
        cursorPicker = new Image(Resources.openFile("pics/picker.svg"));
        folder = new Image(Resources.openFile("pics/folder.svg"));
        file = new Image(Resources.openFile("pics/file.svg"));
        filter = new Image(Resources.openFile("pics/filter.svg"));
        edit = new Image(Resources.openFile("pics/edit.svg"));
        snapshot = new Image(Resources.openFile("pics/snapshot.svg"));

        // loading translations
        translationList = new TranslationList();
        translationList.load("translation");


        // set current translation


        // loading system plant
        try
        {
            SerializableSpecies serializableSpecies = new SerializableSpecies(openFile("species.xml"));
            plantList = serializableSpecies.getSpeciesList();

            StringBuilder conflicts = new StringBuilder();
            for (Plant plant : plantList.getPlants())
                plant.getCompanions().logConflicts(conflicts);
            log.warning(conflicts.toString());
        }
        catch (Exception ex)
        {
            log.severe(ex.toString());
            ex.printStackTrace();
            plantList = new PlantList();
        }
    }

    public static PlantList plantList()
    {
        return plantList;
    }

    public static TranslationList translations() { return translationList; }

    public static InputStream openPic(String name)
    {
    	return openFile("pics/"+name);
    }
    
    public static InputStream openFile(String name)
        {
        log.fine("Open resource file " + name);
        String path = "resources/" + name;
        InputStream stream = Resources.class.getResourceAsStream("/" + path);
        if (stream == null)
        {
            File file = new File(path);
            if (file.exists())
            {
                log.fine("Not found in application resources; open file");
                try
                {
                    stream = new FileInputStream(file);
                }
                catch (FileNotFoundException e)
                {
                    log.severe(e.toString());
                }
            }
        }
        if (stream == null)
            log.severe("Cannot load resource: " + path);
        return stream;
    }

    public static List<String> getResourcesInPath(String path)
    {
        log.fine("Get resources in path: " + path);
        ArrayList<String> result = new ArrayList<>();
        try
        {
            // TODO: find a way to work on adroid; using only a folder result in null url
            URL url = Resources.class.getResource("/resources/" + path + "/en.xml");
            URI uri = url == null ? null : url.toURI();
            if (url == null || uri == null || !uri.getScheme().equals("jar"))
            {
                log.fine("Not found in application resources; looking for folder");
                File folder = new File("resources/" + path);
                if (folder.exists())
                {
                    for (File file : folder.listFiles())
                    {
                        log.finest("Resource found in folder: " + path + "/" + file.getName());
                        result.add(file.getName());
                    }
                }
                else
                {
                    log.severe("Cannot list resources in path " + path);
                }
            }
            else
            {
                log.fine(uri.toString());
                String jarPath = uri.toString();
                jarPath = jarPath.substring(jarPath.indexOf("/"), jarPath.indexOf("!"));
                JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
                Enumeration<JarEntry> entries = jar.entries();
                String prefix = "resources/" + path + "/";
                while (entries.hasMoreElements())
                {
                    String name = entries.nextElement().getName();
                    if (name.startsWith(prefix))
                    {
                        name = name.substring(prefix.length());
                        if (!name.isEmpty())
                        {
                            log.info(name);
                            result.add(name);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            log.severe(e.getMessage());
        }
        return result;
    }

    public static Properties loadPropertiesFromFile(String filePath)
    {
        log.info("Loading properties from file " + filePath);
        try
        {
            Properties properties = new Properties();
            properties.loadFromXML(Resources.openFile(filePath));
            return properties;
        }
        catch (InvalidPropertiesFormatException e)
        {
        	if(e.getCause()instanceof SAXParseException) {
        		SAXParseException cause = (SAXParseException)e.getCause();
        		log.info("cause.getColumnNumber(): " + cause.getColumnNumber() +
        				" cause.getLineNumber(): " + cause.getLineNumber() +
        				" cause.getPublicId(): " + cause.getPublicId() +
        				" cause.getSystemId(): " + cause.getSystemId());
        		e.printStackTrace();
        	}
        	else {
        		System.out.println(e.getCause().getMessage());
        		e.getCause().printStackTrace();
        	}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Image cursorArrow() { return cursorArrow; }
    public static Image cursorAdd() { return cursorAdd; }
    public static Image cursorDelete() { return cursorDelete; }
    public static Image cursorPicker() { return cursorPicker; }
    public static Image folder() { return folder; }
    public static Image file() { return file; }
    public static Image filter() { return filter; }
    public static Image edit() { return edit; }
    public static Image snapshot() { return snapshot; }
    
    public static int icon_size=1;

    public static Image applicationIcon()
    {
        return plantList().getPlant(40).getImage();
    }
}
