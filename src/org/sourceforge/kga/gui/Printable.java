package org.sourceforge.kga.gui;

import java.util.Collection;

import javafx.print.PageLayout;
import javafx.scene.Node;

public interface Printable {
	
	public interface pageGenerator{
		public Node getPage();
	}
	
	public Collection<pageGenerator> getPrintTasks(PageLayout pf);

}
