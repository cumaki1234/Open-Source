package org.sourceforge.kga.flowlist;

public interface FlowListItem <T extends FlowListItem<T>> {
	
	public void addToList(FlowList <T> list);
	public void removeFromList(FlowList <T> list);

}
