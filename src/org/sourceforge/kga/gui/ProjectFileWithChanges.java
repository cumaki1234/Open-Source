package org.sourceforge.kga.gui;

import javafx.stage.Window;
import org.sourceforge.kga.*;
import org.sourceforge.kga.gui.gardenplan.EditableGarden;
import org.sourceforge.kga.io.SerializableGarden;
import org.sourceforge.kga.io.SerializableProject;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.wrappers.FileChooser;

import java.io.InputStream;
import java.util.Calendar;

import static org.sourceforge.kga.translation.Translation.Key.garden;


public class ProjectFileWithChanges extends FileWithChanges implements ProjectObserver
{
    Project project;

    public ProjectFileWithChanges(Window parent)
    {
        super(parent, Preferences.gui.mainWindow.recentFile, FileChooser.FileType.PROJECT);
    }

    public Project getProject()
    {
        return project;
    }

    public EditableGarden getGarden()
    {
        return project.garden;
    }

    private void setProject(Project newProject)
    {
        if (project != null)
            project.removeObserver(this);
        project = newProject;
        project.addObserver(this);
    }
    

    protected String getLastOpenPath() {
    	return prefs.lastGarden;
    }

    @Override
    protected void createObjects()
    {
        Project project = new Project();
        project.garden.addYear(Calendar.getInstance().get(Calendar.YEAR));
        setProject(project);
    }

    @Override
    protected void load(InputStream is) throws Exception
    {
        Project tmp = new Project();
        SerializableProject.load(tmp, is); // may throw

        setProject(tmp);
    }

    @Override
    protected void saveToFile() throws Exception
    {
        SerializableProject.saveToFile(project, file);
    }

    @Override
    public void projectChanged()
    {
        setUnsavedChanges(true);
    }
}
