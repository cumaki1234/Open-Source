package org.sourceforge.kga.gui;

import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.stage.*;
import org.sourceforge.kga.Garden;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.io.SeedListFormatV1;
import org.sourceforge.kga.io.SerializableGarden;
import org.sourceforge.kga.prefs.EntryWindowBounds;

import javax.swing.*;

// TODO: to be removed when everything is converted
public class Gui extends JComponent
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Gui.class.getName());

    public static void makeWindowBoundsPersistent(JDialog dialog, String name, boolean b)
    {

    }

    public static void makeWindowBoundsPersistent(JDialog dialog, EntryWindowBounds bounds, boolean b)
    {

    }

    public void resetGui()
    {


    }

    public FileWithChanges getGarden()
    {
        return null;
    }
/*
    private void parseParameters(SeedManagerSwing seedManager)
    {
        java.util.prefs.Preferences prefs = Resources.getPreferences("gui");
        String path = prefs.get("parameter", "");
        if (path.isEmpty())
            return;

    private void parseParameters(SeedManagerSwing seedManager)
    {
        java.util.prefs.Preferences prefs = ResourceLoader.getPreferences("gui");
        String path = prefs.get("parameter", "");
        if (path.isEmpty())
            return;

        boolean isGarden = false;
        boolean isSeed = false;
        try
        {
            Garden tmp = new Garden();
            SerializableGarden.load(tmp, new java.io.FileInputStream(path));
            isGarden = true;
        }
        catch (Exception ex1)
        {
            try
            {
                SeedList tmpInventory = new SeedList();
                SeedList tmpShoppingList = new SeedList();
                SeedListFormatV1 xml = new SeedListFormatV1();
                xml.load(tmpInventory, tmpShoppingList, new java.io.FileInputStream(path));
                isSeed = true;
            }
            catch (Exception ex2)
            {
            }
        }
*/

/* TODO:
            log.info("Setting current opened garden to " + path);
            prefs.put("lastOpened", path);
        }
        catch (Exception e1)
        {
                log.info("Setting current opened seed file to " + path);
                prefs.node("seedManager").put("lastOpened", path);
                SwingUtilities.invokeLater(new Runnable(){ public void run() {
                    seedManager.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
                } });
            }
            catch (Exception e2)
            {
                e2.printStackTrace();
            }
        } */
    //}
}