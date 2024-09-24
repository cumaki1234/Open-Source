package org.sourceforge.kga.gui.localization;

import org.sourceforge.kga.translation.Translation;

import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class LocalizedTableView<S> extends TableView<S> {
	
	public LocalizedTableView() {
		super();
		setPlaceholder(new Text(Translation.getCurrent().getTablePlaceholder()));
	}

}
