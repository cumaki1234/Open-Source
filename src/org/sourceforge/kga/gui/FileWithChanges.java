package org.sourceforge.kga.gui;

import javafx.scene.control.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

import org.sourceforge.kga.io.InvalidFormatException;
import org.sourceforge.kga.prefs.EntryRecentFile;
import org.sourceforge.kga.translation.Translation;
import org.sourceforge.kga.wrappers.FileChooser;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by Tiberius on 2/26/2016.
 */
public abstract class FileWithChanges
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(FileWithChanges.class.getName());

    File file;
    Window parent;
    FileChooser.FileType fileType;
    EntryRecentFile prefs = null;

    boolean unsavedChanges = false;

    public FileWithChanges(Window parent, EntryRecentFile prefs, FileChooser.FileType fileType)
    {
        this.parent = parent;
        this.prefs = prefs;
        this.fileType = fileType;
    }

    /////////////////////////////////////////////////////////////////////////
    // listener functions
    //
    public interface Listener
    {
        // new objects were created or another file has been opened
        void objectChanged();

        // unsaved changes flag has changed
        void unsavedChangesChanged();
    }

    ArrayList<Listener> listeners = new ArrayList<>();
    public void addListener(Listener listener)
    {
        listeners.add(listener);
    }

    public void removeListener(Listener listener)
    {
        listeners.remove(listener);
    }

    void notifyUnsavedChangesChanged()
    {
        for (Listener listener : listeners)
            listener.unsavedChangesChanged();
    }

    void notifyObjectChanged()
    {
        for (Listener listener : listeners)
            listener.objectChanged();
    }

    /////////////////////////////////////////////////////////////////////////
    //
    //
    public boolean hasUnsavedChanges()
    {
        return unsavedChanges;
    }

    protected void setUnsavedChanges(boolean unsavedChanges)
    {
        if (this.unsavedChanges != unsavedChanges)
        {
            this.unsavedChanges = unsavedChanges;
            notifyUnsavedChangesChanged();
        }
    }


    public void createNew()
    {
        log.info("FileWithChanged.createNew()");
        if (askToSave())
        {
            createObjects();
            file = null;
            unsavedChanges = false;
            notifyObjectChanged();
        }
    }
    
    protected abstract String getLastOpenPath();

    public boolean openLast()
    {
        // openFile last opened garden
        String lastOpened = getLastOpenPath();//prefs.lastGarden;
        log.info("Last opened file is " + lastOpened);
        if (lastOpened.isEmpty())
            return false;
        File f = new File(lastOpened);
        if (!f.exists())
            return false;
        return open(f, null);
    }

    public void open()
    {
        try
        {
            FileChooser f = new FileChooser(prefs.lastPath.get(), fileType);
            File selectedFile = f.showOpenDialog(parent.getScene().getWindow());
            if (selectedFile != null)
                open(selectedFile, null);
        }
        catch (Error error)
        {

        }
    }

    public boolean open(File file, InputStream is)
    {
        if (!askToSave())
            return false;

        try
        {
            if (file != null)
            {
                log.info("Loading objects from " + file.getAbsolutePath());
                load(new java.io.FileInputStream(file));
                this.file = file;
                unsavedChanges = false;
                saveToPreferences();
            }
            else
            {
                log.info("Loading object from input stream");
                load(is);
                this.file = null;
                unsavedChanges = true;
            }
            notifyObjectChanged();
        }
        catch (InvalidFormatException ex)
        {
            Translation t = Translation.getCurrent();
            ex.printStackTrace();
            displayAlert(Alert.AlertType.ERROR, t.error_loading_file(), t.invalid_file_format());
            return false;
        }
        catch (Exception ex)
        {
            Translation t = Translation.getCurrent();
            ex.printStackTrace();
            displayAlert(Alert.AlertType.ERROR, t.error_loading_file(), ex);
            return false;
        }
        return true;
    }

    /**
     * Ask user to save
     * @return true if user wants to continue the operation, false to abort it
     */
    public boolean askToSave()
    {
        if (!unsavedChanges)
            return true;

        Translation t = Translation.getCurrent();
        Optional<ButtonType> answer = displayAlert(
                Alert.AlertType.CONFIRMATION, t.action_exit(), t.do_you_want_to_save(),
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

        if (answer.isPresent() && answer.get() == ButtonType.YES)
            save(false);
        return answer.isPresent() &&
            (answer.get() == ButtonType.YES || answer.get() == ButtonType.NO);
    }

    public void save(boolean saveAs)
    {
        if (file == null || saveAs)
        {
            FileChooser f = new FileChooser(prefs.lastPath.get(), fileType);
            File selectedFile = f.showSaveDialog(parent.getScene().getWindow());
            if (selectedFile == null)
                return;

            file = selectedFile;
            saveToPreferences();
        }
        try
        {
            saveToFile();
            unsavedChanges = false;
            notifyUnsavedChangesChanged();
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
            Translation t = Translation.getCurrent();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(t.error_saving_file());
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
    }

    protected abstract void createObjects();

    protected abstract void load(InputStream is) throws Exception;

    protected abstract void saveToFile() throws Exception;

    public File getFile()
    {
        return file;
    }
    
    public void setFile(File f)
    {
        file=f;
    }

    private void saveToPreferences()
    {
        log.info("Set last path to  " + file.getParent());
        prefs.lastPath.set(file.getParent());
        prefs.lastOpened.set(file.toString());
        prefs.list.set(Long.toString((new Date()).getTime()), file.getAbsolutePath());
        //loadRecentFileList(menuRecentFiles);
    }

    Menu menuRecentFiles;
    public void loadRecentFileList(Menu menuRecentFiles) {
    	loadRecentFileList(menuRecentFiles, f->{open(f, null);});
    }
    
    public interface fileHandler{
    	public void onAction(File actioned);
    }
    
    public void loadRecentFileList(Menu menuRecentFiles, fileHandler handler)
    {
        this.menuRecentFiles = menuRecentFiles;
        try
        {
            // remove missing files
            for (String key : prefs.list.keys())
                if (!(new File(prefs.list.get(key)).exists()))
                    prefs.list.remove(key);

            // remove duplicated recent files
            String[] keys = prefs.list.keys();
            for (int i = 0; i < keys.length; ++i)
            {
                int j;
                for (j = i + 1; j < keys.length; ++j)
                    if (prefs.list.get(keys[i]).compareTo(prefs.list.get(keys[j])) == 0)
                        break;
                if (j < keys.length)
                {
                    log.info("Deleting from recent file list " + prefs.list.get(keys[i]));
                    prefs.list.remove(keys[i]);
                }
            }

            // remove older recent files
            keys = prefs.list.keys();
            for (int i = 0; i < keys.length - 7; ++i)
                prefs.list.remove(keys[i]);

            // load recent files
            menuRecentFiles.getItems().clear();
            
            for (String recentFile : prefs.list.keys())
            {
                final File file = new File(prefs.list.get(recentFile));
                if (!file.exists())
                    continue;
                MenuItem menuItem = new MenuItem(file.getAbsolutePath());
                menuItem.setOnAction(event -> {
                    handler.onAction(new File(file.getAbsolutePath()));
                });
                menuRecentFiles.getItems().add(0, menuItem);
            }
            menuRecentFiles.setVisible(!menuRecentFiles.getItems().isEmpty());
        }
        catch (BackingStoreException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    Optional<ButtonType> displayAlert(Alert.AlertType alertType, String title, Exception e, ButtonType... buttons)
    {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);

        log.log(Level.SEVERE,e.getMessage()+"\n"+sw.toString());
    	
    	return displayAlert(alertType,title,e.getMessage(),buttons);
    }
        Optional<ButtonType> displayAlert(Alert.AlertType alertType, String title, String text, ButtonType... buttons)
        {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(text);
        if (buttons.length > 0)
        {
            alert.getDialogPane().getButtonTypes().clear();
            alert.getDialogPane().getButtonTypes().addAll(buttons);
        }
        return alert.showAndWait();
    }
}
