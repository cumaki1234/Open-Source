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

package org.sourceforge.kga.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.imageio.*;
import java.io.*;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import org.sourceforge.kga.translation.*;

public class Export
{
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Export.class.getName());


    public void export(Window win, ExportableImage gui)
    {
    	FileChooser f = new FileChooser();
    	String[] writerFormats = ImageIO.getWriterFormatNames();
    	for(String curr : writerFormats){
    		f.getExtensionFilters().add(new FileChooser.ExtensionFilter(curr,curr));
    	}

    	File selectedFile = f.showSaveDialog(win);
    	if (selectedFile != null) {
    		String selectedFormat=f.getSelectedExtensionFilter().getExtensions().get(0);
    		if (!selectedFile.getName().endsWith(selectedFormat)) {
    			selectedFile = new File(
    					selectedFile.getParent(),
    					selectedFile.getName() + "." + selectedFormat);
    		}
    		log.info("Export into format " + selectedFormat + " to " + selectedFile.getPath());



        	Node garden = gui.getNodeToExport();
        	Image exported = garden.snapshot(null,  null);

            try
            {
            	BufferedImage img = new BufferedImage((int)exported.getWidth(),(int)exported.getHeight(),BufferedImage.TYPE_INT_RGB);
            	if (ImageIO.write(SwingFXUtils.fromFXImage(exported,img), selectedFormat, selectedFile))
                	log.info("Export succeed");
            	else
            		throw new IOException("No appropriate writer found");
            }
            catch (IOException ex)
            {
                Translation t = Translation.getCurrent();
                ex.printStackTrace();
                Alert a = new Alert(AlertType.ERROR, t.error_saving_file()+":"+ex.toString());
                a.showAndWait();
            }
    	}

    }
}
