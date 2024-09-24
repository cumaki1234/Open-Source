package org.sourceforge.kga.prefs;


import org.sourceforge.kga.Garden;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by tidu8815 on 19/09/2018.
 */
public class Preferences
{
    static String rootPath = "/org/sourceforge/kga_javafx";
    static java.util.prefs.Preferences rootNode = java.util.prefs.Preferences.userRoot().node(rootPath);
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    public static void initialize()
    {
        log.info("initialize preferences");
        initialize(Preferences.class, rootNode);
    }

    private static void initialize(Class<?> cls,  java.util.prefs.Preferences parent)
    {
        for (Class<?> innerClass : cls.getDeclaredClasses())
        {
            initialize(innerClass, parent.node(innerClass.getSimpleName()));
        }
        for (Field field : cls.getDeclaredFields())
        {
            if (Modifier.isPublic(field.getModifiers()))
            {
                try
                {
                    Method initMethod = field.getType().getMethod("initialize", java.util.prefs.Preferences.class, String.class);
                    initMethod.invoke(field.get(null), parent, field.getName());
                }
                catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class gui
    {
        public static EntryString parameter = new EntryString("");
        public static class mainWindow
        {
            public static EntryRecentFile recentFile = new EntryRecentFile();
            public static EntryInteger selectedFamily = new EntryInteger(-1);
            public static EntryString selectedTag = new EntryString("");
            public static EntryDouble dividerPosition = new EntryDouble(0.25);
            public static EntryWindowBounds windowBounds = new EntryWindowBounds();
        };
        public static class seedManager
        {
            public static EntryRecentFile recentFile = new EntryRecentFile();
            public static class autogenerate
            {
                public static EntryString skip = new EntryString("");
                public static EntryWindowBounds windowBounds = new EntryWindowBounds();
            }
        };
        public static class importWindow
        {
            public static EntryString lastPathImportCsv = new EntryString("");
            public static EntryString lastSpeciesFilePath = new EntryString("");
        }
        public static class debugWindow
        {
            public static EntryWindowBounds windowBounds = new EntryWindowBounds();
        }
        public static class tagWindow
        {
            public static EntryDouble dividerPosition = new EntryDouble(0.25);
            public static EntryWindowBounds windowBounds = new EntryWindowBounds();
        }
        public static class renameTagWindow
        {
            public static EntryWindowBounds windowBounds = new EntryWindowBounds();
        }
        public static class selectPlantsTagWindow
        {
            public static EntryWindowBounds windowBounds = new EntryWindowBounds();
        }
        public static class plantEditorWindow
        {
            public static EntryDouble dividerPosition = new EntryDouble(0.25);
            public static EntryWindowBounds windowBounds = new EntryWindowBounds();
        }
    }

    public static class translation
    {
        public static class custom
        {
            public static EntryString list = new EntryString("");
        }
        public static EntryString language = new EntryString("");
    }

    public static class check_for_update
    {
        public static EntryLong time = new EntryLong(0);
        public static EntryBoolean automatically = new EntryBoolean(true);
    }
}
