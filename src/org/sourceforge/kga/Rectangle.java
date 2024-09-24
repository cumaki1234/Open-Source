package org.sourceforge.kga;


public class Rectangle
{
    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(int x, int y)
    {
        return width > 0 && height > 0 &&
               x >= this.x && x < this.x + width &&
               y >= this.y && y < this.y + height;
    }

    public void add(Point p)
    {
        int minx = Math.min(x, p.x);
        int maxx = Math.max(x + width, p.x);
        int miny = Math.min(y, p.y);
        int maxy = Math.max(y + height, p.y);

        x = minx;
        y = miny;
        width = maxx - minx;
        height = maxy - miny;
    }

    public boolean contains(Point grid)
    {
        return contains(grid.x, grid.y);
    }

    public org.sourceforge.kga.Point getLocation()
    {
        return new Point(x, y);
    }
}
