package org.sourceforge.kga;

import org.sourceforge.kga.translation.Translation;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tiberius on 3/22/2018.
 */
public class SeedEntry implements Cloneable, Runnable
{
	private SeedList parent;
	
    private SeedEntry() {}

    public SeedEntry(PlantOrUnregistered plantOrUnregistered, String variety,
                    Quantity quantity, String comment, LocalDate validFrom,
                    LocalDate validTo, SeedList parent)
    {
        plant = plantOrUnregistered;
        this.variety = Resources.plantList().getVariety(plant.plant, variety);
        if(quantity!=null) {
        	this.qty = quantity.quantity;
        	this.qtyUnit=(quantity.unit==null)?"":quantity.unit;
        }
        else {
        	qty=0;
        	qtyUnit="";
        }
        this.comment = comment;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.parent=parent;
    }

    public void remove(LocalDate date)
    {
        if (date.compareTo(validFrom) < 0)
            throw new IllegalArgumentException("Invalid date " + date.toString() + " for entry with validFrom " + validFrom.toString());
        if (validTo != null && date.compareTo(validTo) > 0)
            throw new IllegalArgumentException("Invalid date " + date.toString() + " for entry with validTo " + validTo.toString());
        validTo = date;
    }

    public boolean isValid(LocalDate date)
    {
    	//date is null?
        return validFrom.compareTo(date) <= 0 && (validTo == null || date.compareTo(validTo) < 0);
    }

    public static class PlantOrUnregistered implements Comparable<PlantOrUnregistered>
    {
        public PlantOrUnregistered(Plant plant)
        {
            this.plant = plant;
            this.unregisteredPlant = null;
        }

        public PlantOrUnregistered(String plant)
        {
            this.plant = null;
            this.unregisteredPlant = plant;
        }

        public Plant plant;
        public String unregisteredPlant;
        
        public boolean equals(Object o) {
        	if (!(o instanceof PlantOrUnregistered)) {
        		return false;
        	}
        	PlantOrUnregistered other = (PlantOrUnregistered)o;
        	if (plant != null && other.plant!=null) {
        		return plant.equals(other.plant);
        	}
        	else if (unregisteredPlant!=null && other.unregisteredPlant!=null){
        		return unregisteredPlant.equals(other.unregisteredPlant);
        	}
        	else if (plant == null && other.plant==null&&unregisteredPlant==null&other.unregisteredPlant==null) {
        		return true;
        	}
        	else {
        		return false;
        	}
        }

		@Override
		public int compareTo(PlantOrUnregistered o) {
			String myName =(plant==null)?unregisteredPlant:Translation.getCurrent().translate(plant);
			String otherName = (o.plant==null)?o.unregisteredPlant:Translation.getCurrent().translate(o.plant);
			return myName.toUpperCase().compareTo(otherName.toUpperCase());
		}
     
    }

    public static class Quantity
    {
        public double quantity = 0;
        public String unit = null;
        public Quantity() {}
        public Quantity(double qty, String unit) {
        	quantity=qty;
        	if("null".equals(unit)) {
        		this.unit="";
        	}
        	else {
        		this.unit=(unit==null)?"":unit;
        	}
        }
    }

    public Object clone()
    {
        SeedEntry newEntry = new SeedEntry( );
        newEntry.validFrom = validFrom;
        newEntry.validTo = validTo;
        newEntry.plant = plant;
        newEntry.variety = variety;
        newEntry.qty = qty;
        newEntry.qtyUnit = qtyUnit;
        newEntry.comment = comment;
        newEntry.parent = parent;
        return newEntry;
    }

    public String getPlantName(boolean withVariety)
    {
        Translation t = Translation.getCurrent();
        String plant;
        if (getPlant().unregisteredPlant == null)
            plant = t.translate(getPlant().plant);
        else
            plant = getPlant().unregisteredPlant;
        if (withVariety && getVariety() != null && !getVariety().isEmpty())
            plant += " ( " + getVariety() + " )";
        return plant;
    }

    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public PlantOrUnregistered getPlant() { return plant; }
    public String getVariety() { return (variety==null)?"":variety.getVariety(); }
    public Quantity getQuantity() { return new Quantity(qty,qtyUnit); }
    
    public void setQtyUnit(String value) {
    	this.qtyUnit=(value==null)?"":value;
    }
    
    public void setQty(double value) {
    	this.qty=value;
    }
    
    public String getQtyUnit() {
    	return (qtyUnit==null)?"":qtyUnit;
    }
    
    public double getQty() {
    	return this.qty;
    }
    public String getComment() { return comment; }

    private LocalDate validFrom = null, validTo = null;
    private PlantOrUnregistered plant = null;
    private TaxonVariety variety = null;
    private double qty = 0;
    private String qtyUnit = "";
    private String comment = null;
    
    public void setVariety(String variety)//, LocalDate date)
    {
    	if(this.variety !=null&&this.variety.varietyEquals(variety))
    		return;
    	if(this.variety==null) {
    		variety="";
    	}
    	updateTaxonVariety(plant.plant,variety);
        parent.fireListChanged();
    }
    
    private void updateTaxonVariety(Plant plant, String variety) {
    	if (Resources.plantList().hasVariety(plant, variety) || this.variety==null||"".equals(this.variety.getVariety())) {
    		if (this.variety!=null) {
    			this.variety.removeOnChangeCallback(this);
    		}
    		this.variety=Resources.plantList().getVariety(plant, variety);
    		this.variety.addOnChangeCallback(this);
    	}
    	else
    		Resources.plantList().remapVariety(this.variety, variety, this.variety.getTaxon());    	
    }
    
    public void setComment(String comment)//, LocalDate date)
    {
        //if (!validFrom.equals(date))
        //    throw new IllegalArgumentException("Can not modify comment on a different date");
        if (compareNullStrings(this.comment, comment) == 0)
            return;
        this.comment = comment;
        parent.fireListChanged();
    }
    
    public void setQuantity(double val) {
    	if (qty!=val) {
    		qty=val;
    		parent.fireListChanged();
    	}
    }

    
    public void setQuantityUnits(String val) {

    	if (qtyUnit!=val) {
    		qtyUnit=(val==null)?"":val;
    		parent.fireListChanged();
    	}
    }
    
    public void setType(PlantOrUnregistered type) {
    	if (plant!=null && type!=null && !plant.equals(type)) {
    		if(variety==null) {
        		updateTaxonVariety(type.plant,"");    			
    		}else {
    			updateTaxonVariety(type.plant,variety.getVariety());
    		}
    	}
    	plant=type;
    	parent.fireListChanged();
    	
    }

    
    static private int compareNullStrings(String s1, String s2)
    {
        if (s1 == null && s2 == null)
            return 0;
        if (s1 == null && s2.isEmpty())
            return 0;
        if (s2 == null && s1.isEmpty())
            return 0;
        return s1 == null ? -1 : s2 == null ? 1 : s1.compareTo(s2);
    }
    
	@Override
	public void run() {
		parent.fireListChanged();
		
	}
    
}
