package org.sourceforge.kga.gui.tableRecords.soilNutrition;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sourceforge.kga.Project;
import org.sourceforge.kga.gui.tableRecords.RecordList;
import org.sourceforge.kga.gui.tableRecords.RecordTable;
import org.sourceforge.kga.gui.tableRecords.SingleElementEntryRecords;
import org.sourceforge.kga.gui.tableRecords.TreeDisplayable;
import org.sourceforge.kga.translation.Translation;
import org.sourceforge.kga.translation.Translation.Key;

import javafx.scene.Node;
import javafx.scene.web.WebView;

public class SoilNutritionRecords extends SingleElementEntryRecords<SoilNutritionEntry> implements RecordList<List<SoilNutritionEntry>> {
	WebView notesArea;
	
	public SoilNutritionRecords(Project p) {
		super(new SoilNutritionProvider(p.getSoilNutritionEntries()));
		try {
		notesArea = new WebView();
		}
		catch(Throwable e) {
			Logger.getGlobal().log(Level.INFO, "Caught javaFX exception. Please upgrade javaFX to 12+. Disabling feature.");
			e.printStackTrace();
		}
		Translation t = Translation.getCurrent();
		String tableInfo = t.info_nutrient_ppm_ranges();//"Enter your nutrient levels in PPM in the above table. Here are soil-nutrient concentration ranges in PPM for reference. ";
		
		NutrientRanges [] nutrients = NutrientRanges.getAll();
		tableInfo +="<body style=\"background-color:#f3f3f3;\"><table style='border: 1px solid grey;' width=100%>";
		String header = "<tr>"+htmlCell(new String []{"Soil Test Level","Expected Yield Potential"});

		String vLow = "<tr>"+htmlCell(new String []{"Very Low","&lt;65%"});
		String low="<tr>"+htmlCell(new String []{"Low","65% - 85%"});
		String medium="<tr>"+htmlCell(new String []{"Medium","85% - 95%"});
		String optimum="<tr>"+htmlCell(new String []{"Optimum","100%"});
		String high="<tr>"+htmlCell(new String []{"High","100%"});
		String emptyCell = "<td/>";
		for (NutrientRanges curr : nutrients) {
			header += htmlCell(curr.name);
			if (curr.verylow==null) {
				vLow += emptyCell;
				low += (curr.low==null)?emptyCell:htmlCell("&lt;"+curr.low);
			}
			else {
				vLow += htmlCell("&lt;"+curr.verylow);
				low += htmlCell(curr.verylow+" - "+curr.low);
			}
			medium += (curr.medium==null)?emptyCell:htmlCell(curr.low+" - "+curr.medium);
			optimum += (curr.optimum==null)?emptyCell:htmlCell(curr.medium+" - "+curr.optimum);
			high += (curr.optimum==null)?emptyCell:htmlCell("&gt;"+curr.optimum);
		}
		tableInfo +=header+vLow+low+medium+optimum+high+"</table></body>";
		if(notesArea!=null) {
			notesArea.getEngine().loadContent(tableInfo);
			notesArea.setPrefHeight(180);
		}
	}

	private String htmlCell(String[] cells) {
		String all = "";
		for(String curr:cells){
			all+=htmlCell(curr);
		}
		return all;
	}
	
	private String htmlCell(String inCell) {
		return "<td>"+inCell+"</td>";
	}
	
	@Override
	public Key getType() {
		return Translation.Key.action_soil_nutrition;
	}
	
	@Override
	public TreeDisplayable<Key> getRootDisplayable() {
		return new TreeDisplayable<Translation.Key>(getType(),new TreeDisplayable.unDeleteableNodeGenerator<Translation.Key>() {

			public Node getDisplayNode(Translation.Key myData) {
				RecordTable<SoilNutritionEntry> toShow =  new RecordTable<SoilNutritionEntry>(p);
				if(notesArea!=null)
					toShow.addToLeftBottomScetion(notesArea);
				return toShow;
			}
		}, true, false);
	}

}
