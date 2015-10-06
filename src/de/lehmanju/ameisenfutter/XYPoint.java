package de.lehmanju.ameisenfutter;

public class XYPoint
{
    int x;
    int y;

    public XYPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode()
    {
        return x * 15 + y * 15;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof XYPoint)
        {
            XYPoint p = (XYPoint) obj;
            if (p.x == x && p.y == y)
                return true;
        }
        return false;
    }
}
