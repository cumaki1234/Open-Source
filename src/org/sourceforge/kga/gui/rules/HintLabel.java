package org.sourceforge.kga.gui.rules;

import java.util.Set;
import java.util.TreeSet;

import org.sourceforge.kga.rules.Hint;
import org.sourceforge.kga.translation.Translation;

import javafx.scene.control.Label;

public class HintLabel extends Label {
	
	public HintLabel(Hint hint) {
		super(getText(hint));
		setStyle(hint.getValue() == Hint.Value.GOOD ?"-fx-text-fill: lightgreen;":"-fx-text-fill: orange;");
	}
	
	private static String getText(Hint hint) {
		Translation t = Translation.getCurrent();
		if (hint.getValue() == Hint.Value.TIP)
			return "TODO: not yet supported";

		StringBuilder toolTip = new StringBuilder();

		if (hint.isRotation())
		{
			toolTip.append(t.translate(hint.getCurrentPlant()));
			if (hint.getCurrentPlant() != hint.getNeighborPlant())
			{
				toolTip.append(" ").append(t.rotation_after()).append(" ").append(t.translate(hint.getNeighborPlant()));
			}
		}
		else
		{
			toolTip.append(t.translate(hint.getCurrentPlant())).append(" ");
			toolTip.append(hint.getCompanion().type.isBeneficial() ? t.companion_helped_by()  : t.companion_dislike());
			//if (hint.getCompanion().plant != hint.getNeighborPlant())
			//    toolTip.append(" ").append(hint.getCompanion().plant.getName()).append('(');
			toolTip.append(" ").append(t.translate(hint.getNeighborPlant()));
		}
		return toolTip.toString();
		
		
	}
	
	public void setSources(Set<Integer> sourceIndexes) {

		StringBuilder indexes = new StringBuilder();
		for (Integer index : sourceIndexes)
		{
			if (indexes.length() != 0)
				indexes.append(",");
			indexes.append("[");
			indexes.append(index + 1);
			indexes.append("]");
		}
		this.setText(getText()+indexes);
	}

}
