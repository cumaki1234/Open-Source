package org.sourceforge.kga.analyze;

import java.util.Map.Entry;

import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.gardenplan.GardenView;
import org.sourceforge.kga.translation.Translation;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class SortablePlant implements Comparable<SortablePlant>{
	public Plant p;
	
	public SortablePlant(Plant p) {
		this.p=p;
	}

	@Override
	public int compareTo(SortablePlant o) {
		return Translation.getCurrent().translate(p).compareTo(Translation.getCurrent().translate(o.p));
	}
	
	@Override
	public boolean equals(Object other) {
		if(other==null || ! (other instanceof SortablePlant)) {
			return false;
		}
		return p.equals(((SortablePlant)other).p);
	}

	@Override
	public int hashCode() {
		return p.hashCode();
	}
	
	@Override
	public String toString() {
		return Translation.getCurrent().translate(p);
	}
	

	 private static ImageView getImageForPlant(Plant entry) {
		 ImageView imageView = entry.createImageview(GardenView.PLANT_SIZE);
       return imageView;
	 }
	 
	public static <T> Callback<TableColumn<T, SortablePlant>, TableCell<T, SortablePlant>> getCellFactory() {

		return t->new TableCell<T, SortablePlant>() {	
				@Override
				public void updateItem(SortablePlant entry, boolean empty) {
					super.updateItem(entry, empty);
					if (empty) {
						setGraphic(null);
						setText(null);
					} else {
						setGraphic(getImageForPlant(entry.p));
						setText(Translation.getCurrent().translate(entry.p));
					}
				}
			};
		}

}