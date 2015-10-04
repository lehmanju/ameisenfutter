package de.lehmanju.ameisenfutter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Platform;

public class Simulator
{
    DrawArea area;
    int mitteX, mitteY;
    Ameise[] amArray;
    private Set<Change> changes;
    Speicher speicher;

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

    public Simulator(Speicher sp, DrawArea ar)
    {
        speicher = sp;
        area = ar;
        initialize();
    }

    private void initialize()
    {
        amArray = new Ameise[speicher.ameisen];
        mitteX = (int) Math.floor(speicher.groesseX / 2);
        mitteY = (int) Math.floor(speicher.groesseY / 2);
        Platform.runLater(new Runnable()
        {

            @Override
            public void run()
            {
                area.drawNest(mitteX, mitteY);
                area.drawAnt(mitteX, mitteY, true);
            }

        });
        for (int i = 0; i < speicher.futterStellen; i++)
        {
            int zX;
            int zY;
            do
            {
                zX = (int) Math.floor((Math.random() * speicher.groesseX));
                zY = (int) Math.floor((Math.random() * speicher.groesseY));
            } while (containsFutter(zX, zY));
            speicher.futterVerteilung[zX][zY] = speicher.portionen;
            final int ZX = zX;
            final int ZY = zY;
            Platform.runLater(new Runnable()
            {

                @Override
                public void run()
                {
                    area.drawFutter(ZX, ZY, true);
                }
            });
        }
        speicher.amVerteilung[mitteX][mitteY] = speicher.ameisen;
        for (int i = 0; i < speicher.ameisen; i++)
        {
            amArray[i] = new Ameise();
            amArray[i].x = mitteX;
            amArray[i].y = mitteY;
            amArray[i].futter = false;
        }
    }

    public Set<Change> simulate(int iterations)
    {
        changes = new HashSet<Change>();
        for (int count = 0; count < iterations; count++)
        {
            for (int aN = 0; aN < speicher.ameisen; aN++)
            {
                Ameise cA = amArray[aN];
                if (cA.futter) //Futter im Inventar, im Nest?
                {
                    if (cA.x == mitteX && cA.y == mitteY)
                    {
                        cA.futter = false;
                    } else
                    {
                        speicher.pheromone[cA.x][cA.y]++;
                        changes.add(new Change('P', cA.x, cA.y));
                        toNest(cA);
                    }
                } else
                {
                    if (speicher.futterVerteilung[cA.x][cA.y] > 0)
                    {
                        cA.futter = true;
                        speicher.futterVerteilung[cA.x][cA.y]--;
                        Change aen = new Change('F', cA.x, cA.y);
                        changes.add(aen);
                    } else
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
        }       
        return changes;
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
            if (speicher.pheromone[ar[0] + a.x][ar[1] + a.y] > 0)
                notNull = true;
            if (speicher.pheromone[ar[0] + a.x][ar[1] + a.y] > maxPhero)
            {
                maxPhero = speicher.pheromone[ar[0] + a.x][ar[1] + a.y];
                xyD = ar;
            }
        }
        if (notNull)
            setAPos(a, xyD[0], xyD[1]);
        return notNull;
    }

    private void toNest(Ameise a)
    {
        setAPos(a, mitteX - a.x, mitteY - a.y);
    }

    protected void setAPos(Ameise a, int dx, int dy)
    {
        speicher.amVerteilung[a.x][a.y]--;//Ameise von aktueller Position "subtrahieren"
        Change ae1 = new Change('A', a.x, a.y);
        a.x += dx;
        a.y += dy;
        speicher.amVerteilung[a.x][a.y]++;//Ameise bei neuer Position hinzufügen
        Change ae2 = new Change('A', a.x, a.y);
        changes.add(ae1);
        changes.add(ae2);
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

    public boolean containsFutter(int x, int y)
    {
        return speicher.futterVerteilung[x][y] > 0;
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
            if ((dX + a.x) < speicher.groesseX && (dY + a.y) < speicher.groesseY && (dX + a.x) >= 0 && (dY + a.y) >= 0)
                tempL.add(new int[] { dX, dY });
        }
        return tempL;
    }
}
