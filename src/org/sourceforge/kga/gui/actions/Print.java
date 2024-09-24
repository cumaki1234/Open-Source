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

import java.awt.print.Book;
import java.util.Collection;

import org.sourceforge.kga.gui.*;
import org.sourceforge.kga.gui.Printable.pageGenerator;
import org.sourceforge.kga.gui.gardenplan.GardenTabPane;
import org.sourceforge.kga.gui.gardenplan.GardenView;
import org.sourceforge.kga.translation.*;

import javafx.geometry.Rectangle2D;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.stage.Window;


public class Print
{

	public void actionPerformed(Window parent,Printable view)
	{
		Book book = new Book();
		Collection<pageGenerator> pages = view.getPrintTasks(PrintSetup.printerJob.getJobSettings().getPageLayout());//Collection<Rectangle2D> pages = view.getPrintPageViewports(PrintSetup.printerJob.getJobSettings().getPageLayout());
		boolean doPrint = PrintSetup.printerJob.showPrintDialog(parent);
		if (doPrint)
		{
			for (Printable.pageGenerator page : pages) {
				PrintSetup.printerJob.printPage(page.getPage());//view.getPrintPageNodes(PrintSetup.printerJob.getJobSettings().getPageLayout(), page));
			}
			if(!PrintSetup.printerJob.endJob()) {    
				Translation t = Translation.getCurrent();
				Alert a = new Alert(Alert.AlertType.ERROR,t.error_print());
				a.showAndWait();
			}
			PrintSetup.printerJob = PrinterJob.createPrinterJob();
		}
	}
}
