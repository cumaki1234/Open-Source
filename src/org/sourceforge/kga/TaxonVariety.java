package org.sourceforge.kga;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.JLabel;

import org.sourceforge.kga.gui.plants.PlantLabel;
import org.sourceforge.kga.translation.Translation;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class TaxonVariety <T extends Taxon> implements Comparable<TaxonVariety<T>> {

	private T taxon;
	private String variety;
	private Point size;

	public Point getSize() {
		return size;
	}

	public void setSize(Point size) {
		if(size==null) {
			size=Plant.LEGACY_DEFAULT_SIZE;
		}
		this.size = size;
	}

	TaxonVariety(T taxon, String variety){
		this.variety=variety.toUpperCase();
		this.taxon=taxon;
		notifyOnChange=new LinkedList<Runnable>();
		imageCache = new HashMap<Pair<Integer,TaxonVariety<?>>, Image>();
		size = Plant.LEGACY_DEFAULT_SIZE;
	}

	public String getVariety() {
		return variety;
	}

	public T getTaxon() {
		return taxon;
	}
	
	static Map<Pair<Integer,TaxonVariety<?>>, Image> imageCache;

	public Image getImage(int size) {
		Pair<Integer,TaxonVariety<?>> key = new Pair<Integer,TaxonVariety<?>>(size,this);
		Image cached = imageCache.get(key);
		if(cached==null) {
			Image image = taxon.getImage();
			if(isItem()) {
				return image;
			}

			// Create the Canvas
			Canvas canvas = new Canvas(size, size);
			// Get the graphics context of the canvas
			GraphicsContext gc = canvas.getGraphicsContext2D();

			double extraHeight=(variety==null||variety.length()==0)?0:gc.getFont().getSize();
			double finalHeight=image.getHeight()+extraHeight;
			int width,height;
			if(finalHeight>image.getWidth()) {
				height=(int)(size-extraHeight);
				width=(int)(((double)image.getWidth())/finalHeight*size);
			}
			else {
				width=size;   
				height=(int)(((double)finalHeight)/image.getWidth()*size);
			}

			// Set line width
			gc.setLineWidth(1.0);
			// Set fill color
			gc.setFill(Color.BLACK);

			gc.drawImage(image, 0, 0, width, height);


			if(variety!=null&&variety.length()>0) {
				gc.strokeText(variety, 0, size,size);

			}
			cached= canvas.snapshot(null,null);
			imageCache.put(key, cached);
		}
		return cached;

	}

	public boolean isItem() {
		return taxon.isItem();
	}
	
	public Set<Taxon> getChildren() {
		return taxon.getChildren();
	}
		
	public int getId() {
		return taxon.getId();
	}
	
	public Taxon getFamily() {
		return taxon.getFamily();
	}
	
	void updateAndNotify(T newTaxon, String newVariety) {
		taxon=newTaxon;
		variety=newVariety;
		for(Runnable curr:notifyOnChange) {
			curr.run();
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(taxon,variety.toUpperCase());
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TaxonVariety){
			TaxonVariety otherTax=(TaxonVariety)other;
			return taxon.equals(otherTax.taxon) && variety.equals(otherTax.variety);
		}
		return false;
	}
	
	public boolean varietyEquals(String variety) {
		return variety!=null&&this.variety.equals(variety.toUpperCase());
	}
    
    List<Runnable> notifyOnChange;
    
    public void addOnChangeCallback(Runnable r) {
    	notifyOnChange.add(r);
    }
    
    public void removeOnChangeCallback(Runnable r) {
    	notifyOnChange.remove(r);
    }

	@Override
	public int compareTo(TaxonVariety<T> o) {
		if(getTaxon().equals(o.getTaxon())) {
			return getVariety().toUpperCase().compareTo(o.getVariety().toUpperCase());
		}
		else {
			return Translation.getCurrent().translate(getTaxon()).compareTo(Translation.getCurrent().translate(o.getTaxon()));
		}
	}

}
