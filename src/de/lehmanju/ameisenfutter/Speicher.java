package de.lehmanju.ameisenfutter;

import java.util.HashSet;
import java.util.Set;

public class Speicher
{
    int groesseX, groesseY, mitteX, mitteY;
    int ameisen;
    int futterStellen;
    int portionen;
    long steps;
    int futterNest;
    int[][] amVerteilung;//x, y, anzahl
    int[][] futterVerteilung; //[index], [0] = x-Koord., [1] = y-Koord. , [2] = Anzahl Futter
    int[][] pheromone; //x,y,Stärke
    Set<XYPoint> phero = new HashSet<>();

    public Speicher()
    {
        new Speicher(5, 100, 50, 500, 500);
    }

    public Speicher(int futter, int ameisen, int portionen, int groesseX, int groesseY)
    {
        this.groesseX = groesseX;
        this.groesseY = groesseY;
        this.futterStellen = futter;
        this.ameisen = ameisen;
        this.portionen = portionen;
        mitteX = (int) Math.floor(groesseX / 2);
        mitteY = (int) Math.floor(groesseY / 2);
        steps = 0;
        futterNest = 0;
        amVerteilung = new int[this.groesseX][this.groesseY];
        futterVerteilung = new int[groesseX][groesseX];
        pheromone = new int[this.groesseX][this.groesseY];
    }
}
