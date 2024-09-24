package org.sourceforge.kga.flowlist;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;


public class FlowList <T extends FlowListItem<T>> implements Collection<T>, Publisher<T> {
	Collection<T> backingList;
	Set<FlowListSubscription>subscriptions;
	
	public void markDirty(T item) {
		for (FlowListSubscription curr : subscriptions) {
			curr.s.onNext(item);
		}
	}
	
	public FlowList(Collection<T> backingList) {
		this.backingList=backingList;
		subscriptions= new HashSet<FlowListSubscription>();
	}

	@Override
	public void subscribe(Subscriber<? super T> subscriber) {
		subscriber.onSubscribe(new FlowListSubscription(subscriber));
		
	}

	@Override
	public int size() {
		return backingList.size();
	}

	@Override
	public boolean isEmpty() {
		return backingList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return backingList.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableCollection(backingList).iterator();
	}

	@Override
	public Object[] toArray() {
		return backingList.toArray();
	}

	@Override
	public <o> o[] toArray(o[] a) {
		return backingList.toArray(a);
	}

	@Override
	public boolean add(T e) {
		boolean added = this.backingList.add(e);
		if(added) {
			e.addToList(this);
			markDirty(e);
		}
		return added;
	}

	@Override
	public boolean remove(Object o) {
		T asT=(T)o;
		boolean removed = this.backingList.remove(asT);
		if(removed) {
			asT.removeFromList(this);
			markDirty(asT);
		}
		return removed;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return backingList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for(T curr : c) {
			changed |=add(curr);
		}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for(Object curr : c) {
			changed |=remove(curr);
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		for (T curr :backingList){
			remove(curr);
		}
		
	}
	
	private class FlowListSubscription implements Subscription{
		Subscriber s;
		
		public FlowListSubscription(Subscriber s){
			subscriptions.add(this);	
			this.s=s;
		}
		

		@Override
		public void request(long n) {
		}

		@Override
		public void cancel() {
			subscriptions.remove(this);			
		}
		
	}

}
