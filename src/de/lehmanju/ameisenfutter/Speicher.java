package de.lehmanju.ameisenfutter;

import java.util.HashSet;
import java.util.Set;

public class Speicher {
	int groesseX, groesseY, nestX, nestY;
	int ameisen;
	int futterStellen;
	int portionen;
	long steps;
	int futterNest;
	int[][] amVerteilung;// x, y, anzahl
	int[][] futterVerteilung; // [index], [0] = x-Koord., [1] = y-Koord. , [2] = Anzahl Futter
	int[][] pheromone; // x,y,Staerke
	Set<XYPoint> phero = new HashSet<>();
	int timeout;
	int iterations;

	public Speicher() {
		new Speicher(5, 100, 50, 500, 500);
	}

	public Speicher(int futter, int ameisen, int portionen, int groesseX, int groesseY) {
		int nestX = (int) Math.floor(groesseX / 2);
		int nestY = (int) Math.floor(groesseY / 2);
		this.groesseX = groesseX;
		this.groesseY = groesseY;
		this.futterStellen = futter;
		this.ameisen = ameisen;
		this.portionen = portionen;
		this.nestX = nestX;
		this.nestY = nestY;
		steps = 0;
		futterNest = 0;
		this.timeout = 1000;
		this.iterations = 1;
		amVerteilung = new int[this.groesseX][this.groesseY];
		futterVerteilung = new int[groesseX][groesseX];
		pheromone = new int[this.groesseX][this.groesseY];
	}

	public Speicher(int futter, int ameisen, int portionen, int groesseX, int groesseY, int nestX, int nestY,
			int timeout, int iterations) {
		this.groesseX = groesseX;
		this.groesseY = groesseY;
		this.futterStellen = futter;
		this.ameisen = ameisen;
		this.portionen = portionen;
		this.nestX = nestX;
		this.nestY = nestY;
		steps = 0;
		futterNest = 0;
		this.timeout = timeout;
		this.iterations = iterations;
		amVerteilung = new int[this.groesseX][this.groesseY];
		futterVerteilung = new int[groesseX][groesseX];
		pheromone = new int[this.groesseX][this.groesseY];
	}
}
