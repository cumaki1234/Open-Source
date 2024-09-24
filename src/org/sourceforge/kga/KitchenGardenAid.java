/**
 * Kitchen garden aid is a planning tool for kitchengardeners.
 * Copyright (C) 2010 Christian Nilsson
 *
 * This file is part of Kitchen garden aid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 * Email contact: tiberius.duluman@gmail.com; christian1195@gmail.com
 */

package org.sourceforge.kga;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.sourceforge.kga.gui.MainWindow;
import org.sourceforge.kga.gui.PersistWindowBounds;
import org.sourceforge.kga.gui.actions.CheckForUpdate;
import org.sourceforge.kga.gui.actions.Debug;
import org.sourceforge.kga.gui.actions.Language;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Iso639_1;
import org.sourceforge.kga.translation.Translation;

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * This is the class with the main method.
 * @author Christian Nilsson
 *
 */

public class KitchenGardenAid extends Application
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    
    private static KitchenGardenAid instance;


    public static KitchenGardenAid getInstance() {
    	return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
    	if(instance==null) {
    		instance=this;
    	}
        Debug.Handler.initialize();

        try
        {
        	SvgImageLoaderFactory.install();
            Resources.load();

            Translation translation = null;
            String currentTranslation = Translation.getCurrentFromPreferences();
            if (currentTranslation != null)
            {
                translation = Resources.translations().get(currentTranslation);
            }
            if (translation == null)
            {
                translation = Resources.translations().get("en");
                Translation.setCurrent(translation);
                Iso639_1.Language selected = new Language().showAndWait(null);
                translation = Resources.translations().get(selected.code);
            }
            Translation.setCurrent(translation);
            
            CheckForUpdate.performAutomaticCheckIfNeeded();

            MainWindow mainWindow = new MainWindow(primaryStage);
            primaryStage.show();
        }
        catch (Exception ex)
        {
            log.severe(ex.getMessage());
            ex.printStackTrace();
            primaryStage.setScene(new Scene(new ScrollPane(Debug.Handler.getInstance().label)));
            primaryStage.show();
        }
    }

    public static java.util.List<String> args;
    public KitchenGardenAid()
    {
        /* TODO:
        CheckForUpdate checkForUpdate = new CheckForUpdate(null);
        if (CheckForUpdate.AutomaticallyCheck())
        {
            checkForUpdate.actionPerformed(null);
        } */

    }


    public static void main(String[] args)
    {
        Preferences.initialize();
        if (args.length == 1)
            Preferences.gui.parameter.set(args[0]);
        else
            Preferences.gui.parameter.remove();

        launch(args);
    }
}
