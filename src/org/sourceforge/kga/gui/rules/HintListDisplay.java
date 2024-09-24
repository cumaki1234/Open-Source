package org.sourceforge.kga.gui.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.sourceforge.kga.KitchenGardenAid;
import org.sourceforge.kga.plant.PropertySource;
import org.sourceforge.kga.plant.Reference;
import org.sourceforge.kga.plant.ReferenceList;
import org.sourceforge.kga.plant.SourceList;
import org.sourceforge.kga.rules.Hint;
import org.sourceforge.kga.translation.Translation;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HintListDisplay extends VBox {

	public HintListDisplay(Iterable<Hint> hints) {
        this.setStyle("-fx-font-size: " + 12);
        this.setMinHeight(USE_COMPUTED_SIZE);//USE_PREF_SIZE);
        this.setMaxHeight(USE_COMPUTED_SIZE);
		init(hints);
	}
	
	public void changeDisplay(Iterable<Hint> hints) {
		double oldH = this.getHeight();
		getChildren().clear();
		if(hints==null || !hints.iterator().hasNext()) {
			return;
		}
		init(hints);
	}
	
	private void init(Iterable<Hint> hints) {
		if(hints==null) {
			return;
		}

		Translation t = Translation.getCurrent();
		ArrayList<PropertySource> sources = new ArrayList<>();

		for (Hint hint : hints)
		{
			if (hint.getValue() == Hint.Value.TIP)
				continue; // TODO: not yet supported

			HintLabel hintLabel = new HintLabel(hint);

			getChildren().add(hintLabel);
			if(!hint.getDescription().isEmpty()) {
				getChildren().add(new IndentedVBoxLabel(hint.getDescription(),1,this));
			}

			TreeSet<Integer> sourceIndexes = new TreeSet<>();
			addHintReferences(sources, sourceIndexes, hint.getReferences());

			for (Hint detail : hint.getDetails())
			{
				if(!detail.getDescription().isEmpty()) {
					getChildren().add(new IndentedVBoxLabel(detail.getDescription(),1,this));
				}
				addHintReferences(sources, sourceIndexes, detail.getReferences());
			}

			hintLabel.setSources(sourceIndexes);
		}

		if (!sources.isEmpty()) {
			Label l = new Label(t.Sources());
			VBox.setMargin(l, new Insets(5,0,0,0));
			getChildren().add(l);
		}
		for (int i = 0; i < sources.size(); ++i)
		{
			PropertySource source = sources.get(i);
			Label l = new Label("["+(i+1)+"] "+source.name);
			getChildren().add(l);
			if (source.url != null && !source.url.isEmpty())
			{
				Hyperlink hlink = new Hyperlink(source.url);
				hlink.setWrapText(true);
				VBox.setMargin(hlink, IndentedVBoxLabel.getIndentingInsets(1));
				hlink.setOnAction(new EventHandler<ActionEvent>() {

        			@Override
        			public void handle(ActionEvent t) {
        				KitchenGardenAid.getInstance().getHostServices().showDocument(hlink.getText());
        			}
        		});
				getChildren().add(hlink);

			}
		} 
	}


	private static void addHintReferences(List<PropertySource> sources, TreeSet<Integer> sourceIndexes, ReferenceList references)
	{
		for (Reference ref : references)
		{
			sourceIndexes.add(SourceList.add(sources, ref.source));
		}
	}
}
