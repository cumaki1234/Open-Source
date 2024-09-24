package org.sourceforge.kga.gui.tableRecords.soilNutrition;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import org.sourceforge.kga.flowlist.FlowList;
import org.sourceforge.kga.flowlist.FlowListItem;
import org.sourceforge.kga.flowlist.FlowListRecordRow;
import org.sourceforge.kga.io.SaveableRecordRow;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;


public class SoilNutritionEntry implements FlowListRecordRow<SoilNutritionEntry> {

	//static enum fertilization_level{depleted,deficient,adequate,surplus,large_surplus};
	static enum fertilization_level_type{PPM};//bands,lbsPerAcre,
	
	static final int VERSION=1;
	
	private Double nitrogen;
	private Double phosphorus;
	private Double potassium;
	private Double calcium;
	private Double magnesium;
	private Double zinc;
	private Double PH;
	private String comment;
	private LocalDate date;
	
	
	
	//fertilization_level_type fertType;
	
	private Set<FlowList<SoilNutritionEntry>> myLists;
	
	public Double getNitrogen() {
		return nitrogen;
	}

	public void setNitrogen(Double nitrogen) {
		this.nitrogen = nitrogen;
		markDirty();
	}

	public Double getPhosphorus() {
		return phosphorus;
	}

	public void setPhosphorus(Double phosphorus) {
		this.phosphorus = phosphorus;
		markDirty();
	}

	public Double getPotassium() {
		return potassium;
	}

	public void setPotassium(Double potassium) {
		this.potassium = potassium;
		markDirty();
	}

	public Double getCalcium() {
		return calcium;
	}

	public void setCalcium(Double calcium) {
		this.calcium = calcium;
		markDirty();
	}

	public Double getMagnesium() {
		return magnesium;
	}

	public void setMagnesium(Double magnesium) {
		this.magnesium = magnesium;
		markDirty();
	}

	public Double getZinc() {
		return zinc;
	}

	public void setZinc(Double zinc) {
		this.zinc = zinc;
		markDirty();
	}

	public Double getPH() {
		return PH;
	}

	public void setPH(Double pH) {
		PH = pH;
		markDirty();
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
		markDirty();
	}

	public void setComment(String comment) {
		this.comment = comment;
		markDirty();
	}
	
	private void markDirty() {
		for(FlowList<SoilNutritionEntry> curr : myLists) {
			curr.markDirty(this);
		}
	}

	public SoilNutritionEntry() {
		nitrogen=phosphorus=potassium=calcium=magnesium=zinc=null;
		//fertType=fertilization_level_type.PPM;
		myLists = new HashSet<FlowList<SoilNutritionEntry>>();
	}
	
	public String getComment() {
		return comment;
	}
	
	public String fertNumberToString(Double number) {
		if(number==null) {
			return "";
		}/*else if(fertType==fertilization_level_type.bands) {
			return fertilization_level.values()[(int)number].name();
		}*/
		else {
			return number.toString();
		}
	}
	
	public Double stringTofertNumber(String number) {
		if(number==null || number.length()==0) {
			return null;
		}/*else if(fertType==fertilization_level_type.bands) {
			return fertilization_level.valueOf(number).ordinal();
		}*/
		else {
			return Double.parseDouble(number);
		}
	}
	
	public static interface typeConverter {
		public Double convert(Double in);
	}
	
	public static class LbsPerAcreToPPM implements typeConverter{

		@Override
		public Double convert(Double in) {return (in==null)?null:(double)(0.5*(double)in);}
		
	}
	
	public static class PPMToLbsPerAcre implements typeConverter{

		@Override
		public Double convert(Double in) {return (in==null)?null:(double)(2*(double)in);}
		
	}
	/*
	public void changeFertilizationType(fertilization_level_type newType) {
		boolean toFertBands = false;
		if (newType!=fertType) {
			if(fertType==fertilization_level_type.bands) {
				fertBandsToPPM(); 
				fertType=fertilization_level_type.PPM;
			}
			if(newType==fertilization_level_type.bands) {
				newType=fertilization_level_type.PPM;
				toFertBands=true;
			}
			if (newType!=fertType) {
				typeConverter converter =(fertType==fertilization_level_type.lbsPerAcre)?new LbsPerAcreToPPM():new PPMToLbsPerAcre();
				nitrogen = converter.convert(nitrogen);
				phosphorus = converter.convert(phosphorus);
				potassium = converter.convert(potassium);
				calcium = converter.convert(calcium);
				magnesium = converter.convert(magnesium);
				zinc = converter.convert(zinc);
			}
			if(toFertBands) {
				ppmToFertBands();
			}
		}
	}
*/
/*	public void ppmToFertBands() {
		 // bands based on https://www.dekalbasgrowdeltapine.com/en-us/agronomy/reading-interpreting-soil-test.html
		if(phosphorus<9) {
			phosphorus=(double)fertilization_level.depleted.ordinal();
		}else if(phosphorus<16) {
			phosphorus=(double)fertilization_level.deficient.ordinal();
		}else if(phosphorus<21) {
			phosphorus=(double)fertilization_level.adequate.ordinal();
		}else if(phosphorus<31) {
			phosphorus=(double)fertilization_level.surplus.ordinal();
		}else {
			phosphorus=(double)fertilization_level.large_surplus.ordinal();
		}

		if(potassium<120) {
			potassium=(double)fertilization_level.depleted.ordinal();
		}else if(potassium<161) {
			potassium=(double)fertilization_level.deficient.ordinal();
		}else if(potassium<201) {
			potassium=(double)fertilization_level.adequate.ordinal();
		}else if(potassium<240) {
			potassium=(double)fertilization_level.surplus.ordinal();
		}else {
			potassium=(double)fertilization_level.large_surplus.ordinal();
		}

		if(potassium<120) {
			potassium=(double)fertilization_level.depleted.ordinal();
		}else if(potassium<161) {
			potassium=(double)fertilization_level.deficient.ordinal();
		}else if(potassium<201) {
			potassium=(double)fertilization_level.adequate.ordinal();
		}else if(potassium<240) {
			potassium=(double)fertilization_level.surplus.ordinal();
		}else {
			potassium=(double)fertilization_level.large_surplus.ordinal();
		}


	}
	
	public void fertBandsToPPM() {
		
	}*/

	@Override
	public void save(XmlWriter writer) throws XmlException{
		writer.writeStartElement("SoilNutritionEntry");
		if(nitrogen!=null)
			writer.writeDoubleAttribute("n", nitrogen);
		if(phosphorus!=null)
			writer.writeDoubleAttribute("p", phosphorus);
		if(potassium!=null)
			writer.writeDoubleAttribute("k", potassium);
		if(calcium!=null)
			writer.writeDoubleAttribute("ca", calcium);
		if(magnesium!=null)
			writer.writeDoubleAttribute("mg", magnesium);
		if(zinc!=null)
			writer.writeDoubleAttribute("zn", zinc);
		if(PH!=null)
			writer.writeDoubleAttribute("ph", PH);
		if(date!=null)
			writer.writeAttribute("date", DateTimeFormatter.BASIC_ISO_DATE.format(this.date));
		if(comment!=null&&comment.length()>0) {
			writer.writeAttribute("comment", comment);
		}
		writer.writeEndElement();
	}

	@Override
	public void load(XmlReader xml, int version) {
		if(version!=1) {
			throw new Error("Unknown version");
		}
		
		nitrogen=xml.getDoubleAttributeValue("n");
		phosphorus=xml.getDoubleAttributeValue("p");
		potassium=xml.getDoubleAttributeValue("k");
		calcium=xml.getDoubleAttributeValue("ca");
		magnesium=xml.getDoubleAttributeValue("mg");
		zinc=xml.getDoubleAttributeValue("zn");
		PH=xml.getDoubleAttributeValue("ph");
		comment = xml.getAttributeValue("", "comment");
		String dateAsString=xml.getAttributeValue("", "date");
		if(dateAsString!=null)
			date = LocalDate.parse(dateAsString, DateTimeFormatter.BASIC_ISO_DATE);
		if(comment==null)
			comment="";
		
		
	}

	@Override
	public void addToList(FlowList list) {
		myLists.add(list);
	}

	@Override
	public void removeFromList(FlowList list) {
		myLists.remove(list);
	}

}
