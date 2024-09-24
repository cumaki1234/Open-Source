package org.sourceforge.kga;

import java.util.Objects;

public class DatedPoint extends Point {
	
	int year;
	
	public DatedPoint(Point source, int year) {
		super(source);
		this.year=year;
	}

	public int getYear() {
		return year;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof DatedPoint) {
			DatedPoint o = (DatedPoint)other;
			return year==o.year && super.equals(o);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(year,x,y);
	}


}
