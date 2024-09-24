package org.sourceforge.kga.gui.tableRecords;

import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.translation.Translation;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.util.StringConverter;

public class  TreeDisplayable<T> extends StringConverter<TreeDisplayable<?>>{

	public interface nodeGenerator<T>{
		public Node getDisplayNode(T myData);
		public void delete(T myData);
	}
	
	public static abstract class unDeleteableNodeGenerator <T> implements nodeGenerator<T> {

		public void delete(T myData) {
			
		};
	}


	public static enum actionButtons {//IMPORTANT: /ensure you add any tnew options to the 'toString' option below to ensure proper localizability.
		Add
	};
	public T data;
	nodeGenerator<T> generator;
	public boolean selectable;
	
	public boolean deleteable;

	public TreeDisplayable(T myData, nodeGenerator<T> gen, boolean selectable, boolean deletable) {
		data=myData;
		generator = gen;
		this.selectable = selectable;
		this.deleteable=deletable;
	}

	public Node getDisplayNode() {
		return generator.getDisplayNode(data);
	}

	public String toString() {
		if(data instanceof Translation.Key) {
			return Translation.getCurrent().translate((Translation.Key)data);
		}
		else if (data instanceof SeedList) {
			return ((SeedList)data).getName();
		}
		else if (data instanceof actionButtons) {
			actionButtons ab = (actionButtons)data;
			switch (ab) {
			case Add: return Translation.getCurrent().add();
			default: return data.toString() + "WARNING: NOT LOCALIZABLE";
			}
		}
		else {
			return data.toString();
		}
	}
	
	public void delete() {
		generator.delete(data);
	}

	public boolean isRenamable() {
		return data instanceof SeedList;
	}

	public boolean isItalic() {
		return data instanceof actionButtons;
	}

	public void rename(String newName) {
		if (data instanceof SeedList) {
			((SeedList)data).setName(newName);
		}
	}

	public void applyStyling(TreeCell cell) {
		if(isRenamable()) {
			cell.setEditable(true);
		}
		if(isItalic())
			cell.setStyle("-fx-font-style: italic; ");
		else if (deleteable) {
			cell.setStyle("-fx-content-display:right;");	
		}
	}

	@Override
	public String toString(TreeDisplayable<?> object) {
		return object.toString();
	}

	@Override
	public TreeDisplayable<T> fromString(String string) {
		rename(string);
		return this;
	}

}
