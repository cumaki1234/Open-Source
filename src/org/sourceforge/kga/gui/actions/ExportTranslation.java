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

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import org.sourceforge.kga.*;
import org.sourceforge.kga.gui.rules.IndentedVBoxLabel;
import org.sourceforge.kga.translation.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExportTranslation
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Export.class.getName());


    public void showAndWait(Stage parent)
    {
        KitchenGardenAid.getInstance().getHostServices().showDocument("https://sourceforge.net/p/kitchengarden2/tickets/new/"); 
        

        try
        {
            ByteArrayOutputStream xmlStream = new ByteArrayOutputStream();
            XMLOutputFactory factory = XMLOutputFactory.newFactory();
            XMLStreamWriter xml = factory.createXMLStreamWriter(xmlStream, "UTF-8");
            xml.writeStartDocument("UTF-8", "1.0");
            xml.writeCharacters("\n");
            xml.writeStartElement("translations");
            xml.writeCharacters("\n");
            for (String language : Resources.translations().getLanguages())
            {
                Properties p = Resources.translations().get(language).getCustomTranslations();
                if (p.isEmpty())
                    continue;
                xml.writeStartElement("translation");
                xml.writeAttribute("xml:lang", language);
                xml.writeCharacters("\n");
                
                // save interface strings
                ArrayList<String> tmp = new ArrayList<String>();
                Translation.Key keys[] = Translation.Key.values();
                for (Object k : p.keySet())
                {
                    for (int i = 0; i < keys.length; ++i)
                        if (keys[i].name().compareTo(k.toString()) == 0)
                        {
                            tmp.add(k.toString());
                            break;
                        }
                }
                Collections.sort(tmp);
                for (String key : tmp)
                {
                    xml.writeStartElement("entry");
                    xml.writeAttribute("key", key);
                    xml.writeCharacters(p.get(key).toString());
                    xml.writeEndElement();
                    xml.writeCharacters("\n");
                }
                
                // save families from species XML
                ArrayList<Integer> tmp2 = new ArrayList<Integer>();
                ArrayList<Integer> tmp3 = new ArrayList<Integer>();
                ArrayList<Integer> tmp4 = new ArrayList<Integer>();
                for (Object k : p.keySet())
                {
                    boolean found = false;
                    for (Plant plant : Resources.plantList().getPlants())
                        if (k.toString().compareTo(plant.getName()) == 0)
                        {
                            (plant.getType() == Plant.Type.FAMILY ? tmp2 : tmp3).add(plant.getId());
                            found = true;
                            break;
                        }
                    if (found)
                        continue;
                    
                    for (Animal  animal : Resources.plantList().getAnimals())
                        if (k.toString().compareTo(animal.getName()) == 0)
                        {
                            tmp4.add(animal.getId());
                            found = true;
                            break;
                        }
                }
                Collections.sort(tmp2);
                Collections.sort(tmp3);
                Collections.sort(tmp4);
                for (int i = 0; i < 3; ++i)
                {
                    ArrayList<Integer> tmp5 = i == 0 ? tmp2 : i == 1 ? tmp3 : tmp4;
                    for (Integer id : tmp5)
                    {
                        Taxon taxon = i == 2 ?
                            Resources.plantList().getAnimal(id) :
                            Resources.plantList().getPlant(id);
                        xml.writeStartElement("name");
                        xml.writeAttribute("id", Integer.toString(id));
                        xml.writeCharacters(p.get(taxon.getName()).toString());
                        xml.writeEndElement();
                        xml.writeCharacters("\n");
                    }
                }

                xml.writeEndElement();
                xml.writeCharacters("\n");
            }
            xml.writeEndDocument();
            xml.flush();
            xml.close();
            log.info("Export translation succeed");
/*            
            email.append(java.net.URLEncoder.encode(xmlStream.toString("UTF-8"), "UTF-8").replace("+", "%20"));
            log.info(email.toString());
            if (Desktop.isDesktopSupported())
            {
                Desktop desktop = Desktop.getDesktop();
                try
                {
                    if (desktop.isSupported(Desktop.Action.MAIL))
                        desktop.browse(new java.net.URI(email.toString()));
                }
                catch (Exception ex)
                {
                    log.warning(ex.toString());
                }
            }
*/            
            TextArea ta = new TextArea();

            BorderPane bp = new BorderPane();
            VBox top = new VBox();
            bp.setTop(top);
            top.getChildren().add(new Label("Copy the content below into a new ticket at"));
            Hyperlink hlink = new Hyperlink("https://sourceforge.net/p/kitchengarden2/tickets/new");
			hlink.setWrapText(true);
			hlink.setOnAction(t->{KitchenGardenAid.getInstance().getHostServices().showDocument(hlink.getText());});
			top.getChildren().add(hlink);
            top.getChildren().add(new Label("with the title: 'Request Translation Update'"));
            BorderPane.setMargin(top, new Insets(5,5,10,5));
            ta.setWrapText(true);
            ta.setEditable(false);
            ta.setText(xmlStream.toString("UTF-8"));
            ScrollPane scroll = new ScrollPane(ta);
            BorderPane.setMargin(scroll, new Insets(5));

            BorderPane.setMargin(top, new Insets(5));
            bp.setCenter(scroll);
            Stage export = new Stage();
            
            export.setScene(new Scene(bp));
            export.initOwner(parent);
            export.initModality(Modality.APPLICATION_MODAL);
            export.showAndWait();
            
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	Alert a = new Alert(AlertType.ERROR,Translation.getCurrent().error_saving_file()+":"+ex.toString(),ButtonType.OK);
        }
    }
}
