package de.lehmanju.ameisenfutter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SimulationService extends ScheduledService<List<GChange>> implements EventHandler<WorkerStateEvent>
{
    Speicher sp;
    Simulator sim;
    int width = 20;
    boolean paused = false;
    ImageView[] views;
    Map<XYPoint, ImageView> futterPos;
    Map<XYPoint, Integer> ameisenPos;
    Queue<Integer> unusedViews;
    Map<XYPoint, ImageView> pheromonPos;
    ChangeComparator comp;
    Image ameise = new Image("Ameise.png");
    Image futter = new Image("Futter.png");
    Image pheromon = new Image("Pheromon.png");
    Image nest = new Image("Nest.png");

    private class ChangeComparator implements Comparator<Change>
    {
        @Override
        public int compare(Change first, Change second)
        {
            if (sp.amVerteilung[first.x][first.y] == 0 && sp.amVerteilung[second.x][second.y] > 0)
                return -1;
            else if (sp.amVerteilung[first.x][first.y] > 0 && sp.amVerteilung[second.x][second.y] == 0)
                return 1;
            else
                return 0;
        }
    }

    @Override
    protected Task<List<GChange>> createTask()
    {
        Task<List<GChange>> t = new Task<List<GChange>>()
        {

            @Override
            protected List<GChange> call() throws Exception
            {
                List<Change> changes = new LinkedList<>(sim.simulate(sp.iterations, sp.timeout));
                Collections.sort(changes, comp);
                List<GChange> graphicCh = new LinkedList<>();
                for (Change ch : changes)
                {
                    GChange temp;
                    switch (ch.type)
                    {
                    case 'A':
                    {
                        temp = getAntChange(ch.x, ch.y, sp.amVerteilung[ch.x][ch.y] > 0);
                        if (temp != null)
                            graphicCh.add(temp);
                        break;
                    }
                    case 'P':
                    {
                        temp = getPhChange(ch.x, ch.y, sp.pheromone[ch.x][ch.y] > 0);
                        if (temp != null)
                            graphicCh.add(temp);
                        break;
                    }
                    case 'F':
                    {
                        temp = getFutterChange(ch.x, ch.y, sp.futterVerteilung[ch.x][ch.y] > 0);
                        if (temp != null)
                            graphicCh.add(temp);
                        break;
                    }
                    }
                }
                return graphicCh;
            }

        };
        t.setOnSucceeded(this);
        return t;
    }

    private GChange getAntChange(int x, int y, boolean draw)
    {
        XYPoint xy = new XYPoint(x, y);
        if (ameisenPos.containsKey(xy))
        {
            if (!draw)
            {
                int index = ameisenPos.get(xy);
                ameisenPos.remove(xy);
                unusedViews.add(index);
                return new GChange(views[index], draw);
            } else
                return null;
        } else
        {
            if (!draw)
                return null;
            int index = unusedViews.poll();
            ameisenPos.put(xy, index);
            ImageView v = views[index];
            v.relocate(x * (width - 1) + 1, y * (width - 1) + 1);
            return new GChange(v, draw);
        }
    }

    private GChange getPhChange(int x, int y, boolean draw)
    {
        XYPoint xy = new XYPoint(x, y);
        if (pheromonPos.containsKey(xy))
        {
            if (!draw)
            {
                ImageView v = pheromonPos.get(xy);
                pheromonPos.remove(xy);
                return new GChange(v, draw);
            } else
                return null;
        } else
        {
            if (!draw)
                return null;
            ImageView v = new ImageView();
            pheromonPos.put(xy, v);
            v.setImage(pheromon);
            v.relocate(x * (width - 1) + 1, y * (width - 1) + 1);
            return new GChange(v, draw);
        }
    }

    private GChange getFutterChange(int x, int y, boolean draw)
    {
        XYPoint xy = new XYPoint(x, y);
        if (futterPos.containsKey(xy))
        {
            if (!draw)
            {
                ImageView v = futterPos.get(xy);
                futterPos.remove(xy);
                return new GChange(v, draw);
            } else
                return null;
        } else
        {
            if (!draw)
                return null;
            ImageView v = new ImageView();
            futterPos.put(xy, v);
            v.setImage(futter);
            v.relocate(x * (width - 1) + 1, y * (width - 1) + 1);
            return new GChange(v, draw);
        }
    }

    public SimulationService(Simulator sim, Speicher speicher, int w)
    {
        sp = speicher;
        width = w;
        this.sim = sim;
        comp = new ChangeComparator();
        futterPos = new HashMap<>(speicher.futterStellen + 1, 1);
        ameisenPos = new HashMap<>(speicher.ameisen + 1, 1);
        pheromonPos = new HashMap<>(speicher.ameisen + 1);
        views = new ImageView[speicher.ameisen];
        unusedViews = new LinkedList<>();
        for (int i = 0; i < speicher.ameisen; i++)
        {
            views[i] = new ImageView();
            views[i].setImage(ameise);
            unusedViews.add(i);
        }
    }

    public List<GChange> getInitList()
    {
        List<GChange> initChanges = new LinkedList<>();
        ImageView n = new ImageView();
        n.setImage(nest);
        n.relocate(sp.nestX * (width - 1) + 1, sp.nestY * (width - 1) + 1);
        GChange nestChange = new GChange(n, true);
        GChange antChange = getAntChange(sp.nestX, sp.nestY, true);
        initChanges.add(nestChange);
        initChanges.add(antChange);
        sp.amVerteilung[sp.nestX][sp.nestY] = sp.ameisen;
        for (int i = 0; i < sp.futterStellen; i++)
        {
            int zX;
            int zY;
            do
            {
                zX = (int) Math.floor((Math.random() * sp.groesseX));
                zY = (int) Math.floor((Math.random() * sp.groesseY));
            } while (sp.futterVerteilung[zX][zY] > 0);
            sp.futterVerteilung[zX][zY] = sp.portionen;
            GChange futterCh = getFutterChange(zX, zY, true);
            initChanges.add(futterCh);
        }
        /*
         * sp.futterVerteilung[260][260] = sp.portionen; GChange gf =
         * getFutterChange(260, 260, true); initChanges.add(gf);
         */
        return initChanges;
    }

    public boolean setIterations(int it)
    {
        if (it > 0)
        {
            sp.iterations = it;
            return true;
        } else
            return false;
    }

    public void pause()
    {
        System.out.println("Pause vorgemerkt");
        paused = true;
    }

    @Override
    public void handle(WorkerStateEvent event)
    {
        if (paused)
        {
            paused = false;
            this.cancel();
            System.out.println("Pause ausgef�hrt");
        }
    }
}
