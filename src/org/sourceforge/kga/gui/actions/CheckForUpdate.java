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

import java.awt.Desktop;
import java.io.StringWriter;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamResult;

import org.sourceforge.kga.prefs.Preferences;
import org.w3c.dom.*;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.sourceforge.kga.*;
import org.sourceforge.kga.translation.*;


public class CheckForUpdate
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    static String rssFeed =
        "https://sourceforge.net/projects/kitchengarden2/rss?limit=20";


    static private String getStringFromDoc(org.w3c.dom.Document doc)
    {
        try
        {
           DOMSource domSource = new DOMSource(doc);
           StringWriter writer = new StringWriter();
           StreamResult result = new StreamResult(writer);
           TransformerFactory tf = TransformerFactory.newInstance();
           Transformer transformer = tf.newTransformer();
           transformer.transform(domSource, result);
           writer.flush();
           return writer.toString();
        }
        catch (TransformerException ex)
        {
           ex.printStackTrace();
           return null;
        }
    }
    
    static public void GetLatestVersion(StringBuffer title, StringBuffer link) throws Exception
    {

        log.info("Get latest version from " + rssFeed);

        // download document from sourceforge file feed
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(rssFeed);

        // parse the RSS feed
        Element root = document.getDocumentElement();
        // log.info(getStringFromDoc(document));
        NodeList channels = root.getElementsByTagName("channel");
        if (channels.getLength() != 1)
            throw new Exception("Can not parse RSS document from sourceforge: channel ( expected: 1; found: " + Integer.toString(channels.getLength()) + " )");
        Element channel = (Element)channels.item(0);

        NodeList items = channel.getElementsByTagName("item");
        if (items.getLength() == 0)
            throw new Exception("Can not parse RSS document from sourceforge: item");
        Element item = (Element)items.item(0);

        NodeList titles = item.getElementsByTagName("title");
        if (titles.getLength() != 1)
            throw new Exception("Can not parse RSS document from sourceforge: title");
        Element titleNode = (Element)titles.item(0);

        NodeList links = item.getElementsByTagName("link");
        if (links.getLength() != 1)
            throw new Exception("Can not parse RSS document from sourceforge: link");
        Element linkNode = (Element)links.item(0);

        // save the time
        Date now = new Date();
        Preferences.check_for_update.time.set(now.getTime());
        log.info("Set last check time " + now.toString());

        title.replace(0, title.length(), ((CDATASection)titleNode.getFirstChild()).getData());
        link.replace(0, title.length(), ((Text)linkNode.getFirstChild()).getNodeValue());
    }

    static public boolean AutomaticallyCheck()
    {
        // automatically checking is disabled
        if (!Preferences.check_for_update.automatically.get())
        {
            log.info("Automatically check for new version is disabled by user");
            return false;
        }

        // don't check more often than one month
        Date now = new Date();
        Date time = new Date(Preferences.check_for_update.time.get());
        log.info("Last check time " + time.toString());
        if (time.getTime() + 30L * 24 * 3600 * 1000 > now.getTime())
        {
            log.info("Don't check earlier than 30 days");
            return false;
        }

        return true;
    }
    
    public static void performAutomaticCheckIfNeeded() {
    	if(AutomaticallyCheck()) {
            StringBuffer title = new StringBuffer(), link = new StringBuffer();
            try {
            GetLatestVersion(title, link);
            if (title.indexOf(Version.value) == -1)
            {
            	new CheckForUpdate().manualCheck();
            }
            }
            catch (Exception e) {
            	e.printStackTrace();
            	Alert a = new Alert(AlertType.ERROR,"An error occurred when checking for updates: "+e.toString());
            	a.showAndWait();
            }
    		
    	}
    }

    public void manualCheck()
    {
        Translation t = Translation.getCurrent();
        try
        {
            StringBuffer title = new StringBuffer(), link = new StringBuffer();
            GetLatestVersion(title, link);
            final java.net.URI uri = new java.net.URI(link.toString());

            BorderPane outer = new BorderPane();
            
            VBox panel = new VBox();
            outer.setCenter(panel);
            Label appHeader = new Label("Kitchen garden aid");
            appHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
            outer.setTop(appHeader);
            BorderPane.setMargin(appHeader,  new Insets(5));
            BorderPane.setMargin(panel,  new Insets(5));

            Button download = null;
            System.out.println("my version: "+Version.value);
            String myVersion=""+Version.value;
            if (title.indexOf(myVersion) == -1)
            {
            	Label nvaLab = new Label(t.new_version_available()+":"+title);
            	nvaLab.setStyle("-fx-font-weight: bold");
                panel.getChildren().add(nvaLab);

            	if (Desktop.isDesktopSupported())
            	{
            		download  = new Button(t.go_to_download());
            		download.setOnAction(w->{

            			Desktop desktop = Desktop.getDesktop();
            			try
            			{
            				desktop.browse(uri);
            			}
            			catch (Exception ex)
            			{}
            		});
            	}
            }
            else {

                Label noNew = new Label(t.no_new_version_available() + ":" + title);
                noNew.setStyle("-fx-font-weight: bold");
                panel.getChildren().add(noNew);
                

            }

            CheckBox checkAuto = new CheckBox(t.automatically_check());
            checkAuto.setSelected(Preferences.check_for_update.automatically.get());
            checkAuto.setOnAction(w->{Preferences.check_for_update.automatically.set(checkAuto.isSelected());});


            panel.getChildren().add(checkAuto);
            BorderPane buttons = new BorderPane();
            BorderPane.setMargin(buttons,  new Insets(5));
            outer.setBottom(buttons);
            if (download != null)
            	buttons.setLeft(download);
            Scene scene = new Scene(outer);
            Stage stage = new Stage();
            stage.setScene(scene);
            Button closer = new Button("OK");
            closer.setOnAction(w->{
            	stage.hide();
            });
            buttons.setRight(closer);
            stage.showAndWait();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Alert download  = new Alert(AlertType.ERROR, ex.toString(), ButtonType.OK);
            download.showAndWait();
            
        }
    }
}
