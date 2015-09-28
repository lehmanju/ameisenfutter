package de.lehmanju.ameisenfutter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Platform;

public class Simulator
{
    DrawArea area;
    int mitteX, mitteY, groesseX = 500, groesseY = 500;
    int ameisen = 100;
    int futterStellen = 5;
    int portionen = 50;
    Ameise[] amArray;
    int[][] amVerteilung;//x, y, anzahl
    int[][] futterVerteilung; //[index], [0] = x-Koord., [1] = y-Koord. , [2] = Anzahl Futter
    int[][] pheromone; //x,y,Stärke

    protected class Ameise
    {
        int x, y;
        boolean futter;
        int lastDir;
    }

    public Simulator(DrawArea area)
    {
        this.area = area;
        initialize();
    }

    public Simulator(DrawArea area, int futter, int ameisen, int portProFutter, int groesseX, int groesseY)
    {
        this.ameisen = ameisen;
        this.futterStellen = futter;
        portionen = portProFutter;
        this.area = area;
        this.groesseX = groesseX;
        this.groesseY = groesseY;
        initialize();
    }

    private void initialize()
    {
        amArray = new Ameise[ameisen];
        amVerteilung = new int[groesseX][groesseY];
        pheromone = new int[groesseX][groesseY];
        futterVerteilung = new int[futterStellen][3];
        mitteX = (int) Math.floor(groesseX / 2);
        mitteY = (int) Math.floor(groesseY / 2);
        Platform.runLater(new Runnable()
        {

            @Override
            public void run()
            {
                area.drawNest(mitteX, mitteY);
            }

        });
        for (int i = 0; i < futterStellen; i++)
        {
            boolean vorhanden = false;
            final int zX = (int) Math.floor((Math.random() * groesseX));
            final int zY = (int) Math.floor((Math.random() * groesseY));
            for (int n = 0; n < futterStellen; n++)
            {
                if (futterVerteilung[n][0] == zX && futterVerteilung[n][1] == zX)
                    vorhanden = true;
            }
            if (vorhanden)
                continue;
            futterVerteilung[i][0] = zX;
            futterVerteilung[i][1] = zY;
            futterVerteilung[i][2] = portionen;
            Platform.runLater(new Runnable()
            {

                @Override
                public void run()
                {
                    area.drawFutter(zX, zY, portionen);
                }

            });
        }
        Platform.runLater(new Runnable()
        {

            @Override
            public void run()
            {
                area.drawAnt(mitteX, mitteY, ameisen);
            }

        });        
        amVerteilung[mitteX][mitteY] = ameisen;
        for (int i = 0; i < ameisen; i++)
        {
            amArray[i] = new Ameise();
            amArray[i].x = mitteX;
            amArray[i].y = mitteY;
            amArray[i].futter = false;
        }
    }

    public void startSimulation(int timeIntervall)
    {
        while (true)
        {
            for (int aN = 0; aN < ameisen; aN++)
            {
                Ameise cA = amArray[aN];
                if (cA.futter) //Futter im Inventar, im Nest?
                {
                    if (cA.x == mitteX && cA.y == mitteY)
                    {
                        cA.futter = false;
                    } else
                    {
                        pheromone[cA.x][cA.y]++;
                        toNest(cA);
                    }
                } else
                {
                    if (futterGefunden(cA))
                        continue;
                    else
                    {
                        if (nextPPos(cA))
                            continue;
                        else
                        {
                            List<int[]> availDirections = getAvailableDirections(cA);
                            int rD = ThreadLocalRandom.current().nextInt(0, availDirections.size());
                            setAPos(cA, availDirections.get(rD)[0], availDirections.get(rD)[1]);
                        }
                    }
                }
            }
            updateScreen();
            try
            {
                Thread.sleep(timeIntervall);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }
    }

    private boolean futterGefunden(Ameise a)
    {
        for (int n = 0; n < futterStellen; n++)
        {
            if (futterVerteilung[n][0] == a.x && futterVerteilung[n][1] == a.y && futterVerteilung[n][2] > 0)
            {
                a.futter = true;
                futterVerteilung[n][2]--;
                return true;
            }
        }
        return false;
    }

    private boolean nextPPos(Ameise a)
    {
        List<int[]> availDirections = getAvailableDirections(a);
        int[] xyD = { 0, 0 };
        int maxPhero = 0;
        boolean notNull = false;
        for (int in = 0; in < availDirections.size(); in++)
        {
            int[] ar = availDirections.get(in);
            if (pheromone[ar[0] + a.x][ar[1] + a.y] > 0)
                notNull = true;
            if (pheromone[ar[0] + a.x][ar[1] + a.y] > maxPhero)
            {
                maxPhero = pheromone[ar[0] + a.x][ar[1] + a.y];
                xyD = ar;
            }
        }
        if (notNull)
        {
            setAPos(a, xyD[0], xyD[1]);
        }
        return notNull;
    }

    private void toNest(Ameise a)
    {
        setAPos(a, mitteX - a.x, mitteY - a.y);
    }

    public void updateScreen()
    {
        for (int a = 0; a < groesseX; a++)
            for (int b = 0; b < groesseY; b++)
            {
                final int rA = a;
                final int rB = b;
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        area.drawAnt(rA, rB, amVerteilung[rA][rB]);
                        area.drawPheromone(rA, rB, pheromone[rA][rB]);
                    }

                });
            }
        for (int i = 0; i < futterStellen; i++)
        {
            final int index = i;
            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    area.drawFutter(futterVerteilung[index][0], futterVerteilung[index][1], futterVerteilung[index][2]);
                }

            });
        }

    }

    protected void setAPos(Ameise a, int dx, int dy)
    {
        amVerteilung[a.x][a.y]--;//Ameise von aktueller Position "subtrahieren"
        a.x += dx;
        a.y += dy;
        amVerteilung[a.x][a.y]++;//Ameise bei neuer Position hinzufügen
    }

    protected void setAPos(Ameise a, int direction)
    {
        switch (direction)
        {
        case 0:
            setAPos(a, 0, 1);
            break;
        case 1:
            setAPos(a, -1, 0);
            break;
        case 2:
            setAPos(a, 0, -1);
            break;
        case 3:
            setAPos(a, 1, 0);
            break;
        }
    }

    public List<int[]> getAvailableDirections(Ameise a)
    {
        ArrayList<int[]> tempL = new ArrayList<>();
        int dX = 0;
        int dY = 0;
        for (int i = 0; i < 4; i++)
        {
            switch (i)
            {
            case 0:
                dX = 0;
                dY = 1;
                break;
            case 1:
                dX = -1;
                dY = 0;
                break;
            case 2:
                dX = 0;
                dY = -1;
                break;
            case 3:
                dX = 1;
                dY = 0;
                break;
            }
            if ((dX + a.x) < groesseX && (dY + a.y) < groesseY && (dX + a.x) >= 0 && (dY + a.y) >= 0)
                tempL.add(new int[] { dX, dY });
        }
        return tempL;
    }
}
