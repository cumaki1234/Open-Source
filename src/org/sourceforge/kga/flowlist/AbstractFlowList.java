package org.sourceforge.kga.flowlist;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractFlowList <T extends AbstractFlowList<T>> implements FlowListItem<T> {

	private Set<FlowList<T>> myLists;

	public AbstractFlowList() {
		myLists = new HashSet<FlowList<T>>();
	}

	@Override
	public final void addToList(FlowList<T> list) {
		myLists.add(list);
	}

	@Override
	public final void removeFromList(FlowList<T> list) {
		myLists.remove(list);
	}
	
	@SuppressWarnings("unchecked")
	protected T thisAsT() {
		return (T)this;
	}
	
	protected void markDirty() {
		for(FlowList<T> curr : myLists) {
			curr.markDirty(thisAsT());
		}
	}
}
