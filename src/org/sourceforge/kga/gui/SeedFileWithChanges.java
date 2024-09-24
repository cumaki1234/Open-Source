package org.sourceforge.kga.gui;

import javafx.stage.Window;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.SeedCollection;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.io.InvalidFormatException;
import org.sourceforge.kga.io.SeedListFormat;
import org.sourceforge.kga.io.SeedListFormatV1;
import org.sourceforge.kga.io.SeedListFormatV2;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.wrappers.FileChooser;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

import java.io.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tiberius on 2/26/2016.
 */
public class SeedFileWithChanges extends FileWithChanges implements SeedList.Listener
{
    SeedCollection seedCollection;

    public SeedFileWithChanges(Window parent)
    {
        super(parent, Preferences.gui.seedManager.recentFile, FileChooser.FileType.SEEDS);
        seedCollection = new SeedCollection(this);
    }

    public SeedCollection getSeedCollection()
    {
    	return seedCollection;
    }

    public void setDate(LocalDate date)
    {
    	seedCollection.setDate(date);
    }

    
    public void importTo(SeedCollection c, boolean eraseExisting) {
    	open();
    	seedCollection.importTo(c, eraseExisting);
    }


    protected String getLastOpenPath() {
    	return prefs.lastSeed;
    }

    private void setLists(SeedCollection seedCollection)
    {
        for (SeedList seedList : seedCollection)
            seedList.removeListener(this);
        this.seedCollection = seedCollection;
    }
    

    
    public Set<LocalDate> getValidFromDates()
    {
        return seedCollection.getValidFromDates();
    }

    @Override
    public void createObjects()
    {
        setLists(new SeedCollection(this));
    }

    @Override
    public void load(InputStream is) throws Exception
    {
        SeedCollection seedCollection = new SeedCollection(this);
        XmlReader xml;
        SeedListFormat formatter;
        xml = new XmlReader(is);

        while (xml.hasNext())
        	if (xml.next() == XmlReader.START_ELEMENT)
        		break;
        if (xml.getLocalName().compareTo("seed-list") != 0)
        	throw new InvalidFormatException();
        int version = Integer.parseInt(xml.getAttributeValue("", "version"));
        if (version==SeedListFormatV1.SEED_FILE_VERSION)
        	formatter = new SeedListFormatV1();
        else if (version == SeedListFormatV2.SEED_FILE_VERSION)
        	formatter = new SeedListFormatV2();
        else throw new InvalidFormatException(); 

        seedCollection.deleteAllSeedLists();
        formatter.load(seedCollection, xml); // may throw

        setLists(seedCollection);
    }

    @Override
    public void saveToFile() throws Exception
    {
        SeedListFormatV2 xml = new SeedListFormatV2();
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        XmlWriter xmlWriter = new XmlWriter(out, "UTF-8", "1.0");
        xml.save(seedCollection, xmlWriter);
        xmlWriter.flush();
        xmlWriter.close();
        out.close();
    }

    @Override
    public void viewChanged()
    {

    }

    @Override
    public void listChanged()
    {
        super.unsavedChanges=true;
    }
}
