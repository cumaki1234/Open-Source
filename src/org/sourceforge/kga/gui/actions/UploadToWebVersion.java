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

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.Gui;
import org.sourceforge.kga.translation.Translation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;

/**
 * Creates and openFile a temporary html file that sends a POST request to smigo.org with all plant data.
 * Maximum number of plants is 10000 to prevent stack overflow.
 */
public class UploadToWebVersion extends KgaAction {
    private static final long serialVersionUID = 1L;
    private final String uploadUrl;

    public UploadToWebVersion(Gui gui) {
        this(gui, "http://smigo.org/plant/upload");
    }

    public UploadToWebVersion(Gui gui, String uploadUrl) {
        super(gui, Translation.getCurrent().action_upload_to_web());
        this.uploadUrl = uploadUrl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            final File file = getUploadFile();
            Desktop.getDesktop().open(file);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public File getUploadFile() throws IOException {
        final File file = Files.createTempFile("upload", ".html").toFile();
        final PrintWriter printWriter = new PrintWriter(file);
        printWriter.print("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head lang=\"en\">\n" +
                "<meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<form name=\"uploadform\" method=\"post\" action=\"" + uploadUrl + "\">\n");
        int i = 0;
        for (Map.Entry<Integer, HashMap<org.sourceforge.kga.Point, java.util.List<TaxonVariety<Plant>>>> yearMap : getGarden().getAllSquares().entrySet()) {
            for (Map.Entry<org.sourceforge.kga.Point, java.util.List<TaxonVariety<Plant>>> s : yearMap.getValue().entrySet()) {
                for (TaxonVariety<Plant> plant : s.getValue()) {
                    if (i < 10000) {
                        printWriter.print("<input type=\"hidden\" name=\"plants[" + i + "].speciesId\" value=\"" + plant.getId() + "\"/>");
                        printWriter.print("<input type=\"hidden\" name=\"plants[" + i + "].year\" value=\"" + yearMap.getKey() + "\"/>");
                        printWriter.print("<input type=\"hidden\" name=\"plants[" + i + "].x\" value=\"" + s.getKey().x + "\"/>");
                        printWriter.print("<input type=\"hidden\" name=\"plants[" + i + "].y\" value=\"" + s.getKey().y + "\"/>");
                        i++;
                    }
                }
            }
        }
        printWriter.print("</form>\n" +
                "<h1>Loading...</h1>\n" +
                "<script>\n" +
                "window.onload = function () {\n" +
                "console.log('a', document.getElementsByTagName('form'));\n" +
                "document.uploadform.submit();\n" +
                "}\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>");
        printWriter.flush();
        printWriter.close();
        return file;
    }
}