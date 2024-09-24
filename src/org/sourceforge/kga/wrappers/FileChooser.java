package org.sourceforge.kga.wrappers;

import javafx.stage.Window;
import org.sourceforge.kga.Garden;

import java.io.File;

/**
 * Created by Tiberius on 8/17/2017.
 */
public class FileChooser
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    public enum FileType
    {
        GARDEN,
        SEEDS,
        PROJECT
    }
    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();

    public FileChooser(String initialDirectory, FileType fileType)
    {
        if (!initialDirectory.isEmpty())
            fileChooser.setInitialDirectory(new File(initialDirectory));

        javafx.stage.FileChooser.ExtensionFilter extension = null;
        switch (fileType)
        {

            case GARDEN:
                extension = new javafx.stage.FileChooser.ExtensionFilter("Garden file", "*.kga");
                break;
            case SEEDS:
                extension = new javafx.stage.FileChooser.ExtensionFilter("Seed file", "*.seed");
                break;
            case PROJECT:
                extension = new javafx.stage.FileChooser.ExtensionFilter("KitchenGardenAid file", "*.kga");
                break;
        }

        fileChooser.getExtensionFilters().addAll(
            extension, new javafx.stage.FileChooser.ExtensionFilter("All files", "*.*"));
    }

    public File showOpenDialog(Window window)
    {
        return fileChooser.showOpenDialog(window);
    }

    public File showSaveDialog(Window window)
    {
        File file = fileChooser.showSaveDialog(window);
        if (file != null)
        {
            String extension = fileChooser.getExtensionFilters().get(0).getExtensions().get(0).substring(1);
            log.warning(extension);
            log.warning(file.getPath());
            if (!file.getPath().endsWith(extension))
                file = new File(file.getPath() + extension);
        }
        return file;
    }
}
