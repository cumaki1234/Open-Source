package org.sourceforge.kga.analyze;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Query<T, D extends QueryField<T,D,?>> {
	
	Collection<D> aggregateBy;
	Collection<D> toAggregate;
	QueryProvider<T,D> provider;
	D sortBy;
	D pivotBy;
	volatile boolean shutdown;
	
	public Collection<D> getAggregateBy() {
		return Collections.unmodifiableCollection(aggregateBy);
	}

	public Collection<D> getToAggregate() {
		return Collections.unmodifiableCollection(toAggregate);
	}

	public QueryProvider<T, D> getProvider() {
		return provider;
	}

	public D getSortBy() {
		return sortBy;
	}

	public D getPivotBy() {
		return pivotBy;
	}
	
	public void shutdownQuery() {
		shutdown=true;
	}
	
	public Query<T,D> repivotBy(D pivotField) {
		Query<T,D> repivotted = new Query<T,D>(toAggregate,aggregateBy,provider,sortBy,pivotField);
		return repivotted;
	}
	
	public Query<T,D> reSortBy(D sortBy) {
		Query<T,D> repivotted = new Query<T,D>(toAggregate,aggregateBy,provider,sortBy,pivotBy);
		return repivotted;
	}

	public Query(Collection<D> summarize,Collection<D> by, QueryProvider<T,D> provider, D sortBy, D pivotBy) {
		aggregateBy=by;
		toAggregate=summarize;
		this.provider=provider;
		this.sortBy=sortBy;
		if(pivotBy!=null) {
			assert aggregateBy.contains(pivotBy);
		}
		this.pivotBy=pivotBy;
		shutdown=false;
		
	}
	
	public Stream<T> computeDisplayedValues() {
		if(!shutdown)
			return provider.stream().parallel().unordered().map(point->new summaryAggregator(point,false)).distinct().map(sum->sum.myPoint);//new summarySorter(sum.myPoint,sortBy)).sorted().map(sor->sor.p);
		else
			return Stream.empty();
	}
	
	public <K extends Comparable<K>> List<K> computeSortedUniqueValues(QueryField<T,D,K> field){
		if(!shutdown) {
			Stream <K> kStream= provider.stream().unordered().map(t->field.getValue(t)).distinct();
			return kStream.sorted().collect(Collectors.toList());
		}
		else
			return Collections.emptyList();
	}
		
	public List<T> computeSortedDisplayedValues() {
		if(!shutdown) {
			return computeDisplayedValues().map(p->new summarySorter(p,sortBy)).sorted().map(sor->(T)sor.p).collect(Collectors.toList());
		}
		else
			return Collections.emptyList();
	}
	
	private Stream<T> filterToDisplayedValue(T displayedValue){
		Set<T> s = new HashSet<T>();
		s.add(displayedValue);
		return filterToDisplayedValue(s);/*
		if(!shutdown) {
			summaryAggregator dva = new summaryAggregator(displayedValue,true);
			return provider.stream().unordered().filter(t->dva.equals(new summaryAggregator(t,true)));
		}
		else
			return Stream.empty();*/
	}
	
	private Stream<T> filterToDisplayedValue(Set<T> displayedValue){
		if(!shutdown) {
			setSummaryAggregator dva = new setSummaryAggregator(displayedValue,true);
			return provider.stream().unordered().filter(t->dva.containsAny(new summaryAggregator(t,true)));
		}
		else
			return Stream.empty();
	}

	public Double computeAggregatedValue(T displayedValue, D measure) {
		Collection<?> intermed = filterToDisplayedValue(displayedValue).collect(Collectors.toList());
		return computeAggregatedValue(filterToDisplayedValue(displayedValue),measure);
	}
	
	private Double computeAggregatedValue(Stream<T> stream, D measure) {
		if(measure.myType==QueryField.ALLOWED_AGGREGATIONS.NONE) {
			return null;
		}
		return stream.mapToDouble(d->measure.getNumericValue(d)).sum();		
	}

	private class setSummaryAggregator{
		Set<summaryAggregator> aggs;
		public setSummaryAggregator(Set<T> points, boolean includePivot) {
			aggs = new HashSet<>();
			for(T t: points) {
				aggs.add(new summaryAggregator(t,includePivot));
			}
		}
		
		public boolean containsAny(summaryAggregator other) {
			return aggs.contains(other);
		}
	
	}
		private class summaryAggregator{
		T myPoint;
		 boolean includePivot;
		 Collection<D> toCheck;
		
		public summaryAggregator(T point, boolean includePivot) {
			myPoint=point;
			this.includePivot=includePivot;
			toCheck = new HashSet(aggregateBy);
			for(D curr : toAggregate) {
				toCheck.addAll(curr.includeGranularityWhenAggregating());
			}
		}
		
		public int hashCode(){
			int code = 0;
			for(D field : toCheck) {
				if(field!=pivotBy || includePivot)
					code=Objects.hash(code,field.getValue(myPoint).hashCode());
			}
			return code;
		}
		
		public boolean equals(Object other) {
			if(! (other instanceof Query.summaryAggregator)) {
				return false;
			}
			summaryAggregator o = (summaryAggregator)other;
			if(myPoint==null || o.myPoint==null) {
				return myPoint==null&&o.myPoint==null;
			}
			for(D field : toCheck) {
				if((field!=pivotBy ||includePivot)&& !field.getValue(myPoint).equals(field.getValue(o.myPoint))) {
					return false;
				}
			}
			return true;
		}
	}	
	
	private class summarySorter <K extends Comparable<K>> implements Comparable<summarySorter<K>>{
		
		T p;
		Comparable value;
		
		public summarySorter(T point, D sortBy) {
			p=point;
			if(aggregateBy.contains(sortBy)) {
				value=sortBy.getValue(point);
			}
			else {
				Set<T> filteredVals= new HashSet<T>();
				filteredVals.add(p);
				if (pivotBy!=null) {
					filteredVals.addAll(getAllRepivots(point,pivotBy));
				}
				value=computeAggregatedValue(filterToDisplayedValue(p),sortBy);
			}
		}
		
		private <K extends Comparable<K>> Collection<T> getAllRepivots(T initial, D pivot){
			Set<T> pivots= new HashSet<T>();
			QueryField<T,D,K> pivotBy = (QueryField<T, D, K>)pivot;
			List<K> values =computeSortedUniqueValues(pivotBy);
			for (K kurr : values) {
				pivots.add(pivotBy.updateValueforPivot(initial, kurr));
			}
			return pivots;
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Query<T,D>.summarySorter<K> o) {
			if(value==null && o.value==null)
				return 0;
			else {
				if(value==null) {
					return -1;
				}
				else if(o.value==null) {
					return 1;
				}
			}
			return value.compareTo(o.value);
		}
		
	}

}
