package org.sourceforge.kga.gui;

import java.util.Collection;
import java.util.Set;

import org.sourceforge.kga.translation.Translation;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public interface CentralWindowProvider {
	
	public Translation.Key getName();
	
	public Node getLeftPane();

	public Node getMainPane();
	
	public Node getToolbar();
	
	public void onHide();
	
	public void onShow();
	
	public Printable getPrintTask();
		

}
