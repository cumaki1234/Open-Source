package org.sourceforge.kga.gui.tableRecords.soilNutrition;

import org.sourceforge.kga.translation.Translation;

public class NutrientRanges {
	Number verylow;
	Number low;
	Number medium;
	Number optimum;
	String name;
	
	public NutrientRanges(String rangeName, Number verylow, Number low, Number medium, Number optimum) {
		this.name=rangeName;
		this.verylow=verylow;
		this.low=low;
		this.medium=medium;
		this.optimum=optimum;
	}
	
	//nitrogen levels come from: http://cetehama.ucanr.edu/newsletters/Soil_Testing_Articles-by_Allan_Fulton39345.pdf
	//other nutrient levels come from: https://www.uaex.edu/publications/PDF/FSA-2118.pdf
	public static NutrientRanges [] getAll() {
		return new NutrientRanges[] {
				new NutrientRanges(Translation.getCurrent().nitrogen(),null,10,20,30),
				new NutrientRanges(Translation.getCurrent().phosphorus(),16,25,35,50),
				new NutrientRanges(Translation.getCurrent().potassium(),61,90,130,175),
				new NutrientRanges(Translation.getCurrent().calcium(),null,400,null,null),
				new NutrientRanges(Translation.getCurrent().magnesium(),null,30,null,null),
				new NutrientRanges(Translation.getCurrent().zinc(),1.6,3.0,4.0,8.0),
		};
	}

}
