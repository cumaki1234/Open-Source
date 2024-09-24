package org.sourceforge.kga;

import java.util.Collection;

public class Point
{
    public int x;
    public int y;

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Point(Point p)
    {
        this.x = p.x;
        this.y = p.y;
    }
    
    public Point()
    {
        x = 0;
        y = 0;
    }

    public void translate(int dx, int dy)
    {
        x += dx;
        y += dy;
    }

    public boolean equals(Object obj)
    {
         if (!(obj instanceof Point))
           return false;
         Point p = (Point)obj;
         return x == p.x && y == p.y;
    }
    
    public int hashCode()
    {
         // Talk about a fun time reverse engineering this one!
         long l = java.lang.Double.doubleToLongBits((double)y);
         l = l * 31 ^ java.lang.Double.doubleToLongBits((double)x);
         return (int) ((l >> 32) ^ l);
    }
    
    public String toString()
    {
        return getClass().getName() + "[x=" + x + ",y=" + y + ']';
    }
    
    public boolean instersectsAtSize(Point other, int mySize, int otherSize) {
    	return (rangesOverlap(x,x+mySize,other.x,other.x+otherSize)) &&
    			(rangesOverlap(y,y+mySize,other.y,other.y+otherSize));
    }
    
    private static boolean rangesOverlap(int start1, int end1, int start2, int end2) {
    	if(start1<=start2&&end1>=start2) {
    		return true;
    	}
    	if(start1>=start2&& start1<=end2) {
    		return true;
    	}
    	return false;
    }

}
