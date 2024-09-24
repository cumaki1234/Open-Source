package org.sourceforge.kga.analyze;

import java.util.Collection;
import java.util.Collections;

import org.sourceforge.kga.translation.Translation.Key;

public abstract class QueryField <T,F extends QueryField<T,F,?>,K extends Comparable<K>>{

	public enum ALLOWED_AGGREGATIONS {SUM,NONE};
	ALLOWED_AGGREGATIONS myType;
	 boolean allowAggregateBy;
	
	public ALLOWED_AGGREGATIONS getAllowedAggregations() {
		return myType;
	}

	public Key getFieldName() {
		return fieldName;
	}

	Key fieldName;
	
	public QueryField(Key name, ALLOWED_AGGREGATIONS t, boolean allowAggregateBy) {
		myType=t;
		fieldName=name;
		this.allowAggregateBy=allowAggregateBy;
	}
	
	public boolean canAggregateBy() {
		return this.allowAggregateBy;
	}
	
	public Collection<F> includeGranularityWhenAggregating(){
		return Collections.EMPTY_SET;
	}

	public abstract K getValue(T point);
	
	public abstract T updateValueforPivot(T initialPoint,K pivotedBy);
	
	/**
	 * Only needs to be supported for fields that state they support the following aggregation types:
	 * SUM
	 * @param data
	 * @param field
	 * @return
	 */
	public double getNumericValue(T data) {
		throw new UnsupportedOperationException("Fields that support aggregation of numeric values must provide a numeric value");
	}
	
}
