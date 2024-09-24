package org.sourceforge.kga.analyze;
import java.util.Collection;
import java.util.stream.Stream;

import org.sourceforge.kga.gui.ProjectFileWithChanges;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public interface QueryProvider <T, D extends QueryField<T,D,?>> {
	
	public Collection<D> getAvailableFields();
	
	
	public Stream<T> stream();
	
	public Query<T, D> getDefaultQuery();
		

}
