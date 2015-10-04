package de.lehmanju.ameisenfutter;

public class Speicher
{
    int groesseX, groesseY;
    int ameisen;
    int futterStellen;
    int portionen;
    int[][] amVerteilung;//x, y, anzahl
    int[][] futterVerteilung; //[index], [0] = x-Koord., [1] = y-Koord. , [2] = Anzahl Futter
    int[][] pheromone; //x,y,Stärke

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
        amVerteilung = new int[this.groesseX][this.groesseY];
        futterVerteilung = new int[groesseX][groesseX];
        pheromone = new int[this.groesseX][this.groesseY];
    }
}
