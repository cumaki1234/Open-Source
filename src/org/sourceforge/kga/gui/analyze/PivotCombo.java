package org.sourceforge.kga.gui.analyze;

import java.util.HashMap;
import java.util.Map;

import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.Query;
import org.sourceforge.kga.analyze.QueryField;
import org.sourceforge.kga.gui.analyze.AnalysisPane.QueryTypeDependantHolder;
import org.sourceforge.kga.translation.Translation;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class PivotCombo <T> extends ComboBox<FXField<T,?>>{
	
	DelayedQueryTable toPivot;
	
	public PivotCombo(DelayedQueryTable toPivot, QueryTypeDependantHolder <T> holder) {
		super.getItems().addAll(holder.lastQuery.getAggregateBy());
		super.getSelectionModel().select(holder.lastQuery.getPivotBy());
		super.getSelectionModel().selectedItemProperty().addListener((f,old,n)->{
			holder.lastQuery = holder.lastQuery.repivotBy(n);
			toPivot.updateQuery(holder.lastQuery);
		});
		Map<String,FXField<T,?>> fieldFromName = new HashMap<>();
		super.setConverter(new StringConverter<FXField<T,?>>() {

			@Override
			public String toString(FXField<T,?> object) {
				String name = Translation.getCurrent().translate(object.getFieldName());
				fieldFromName.put(name, object);
				return name;
			}

			@Override
			public FXField<T,?> fromString(String string) {
				return fieldFromName.get(string);
			}
			
		});
	}

}
