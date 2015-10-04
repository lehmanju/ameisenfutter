package de.lehmanju.ameisenfutter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DrawArea implements EventHandler<MouseEvent>
{
    Pane rootPane;
    TitledPane tPane;
    int width = 20;
    ImageView[] views;
    Map<XYPoint, ImageView> futterPos;
    Map<XYPoint, Integer> ameisenPos;
    Queue<Integer> unusedViews;
    Map<XYPoint, ImageView> pheromonPos;
    Image ameise = new Image("Ameise.png");
    Image futter = new Image("Futter.png");
    Image pheromon = new Image("Pheromon.png");
    Speicher speicher;

    protected class XYPoint
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

    public DrawArea(TitledPane p, Speicher sp)
    {
        rootPane = new Pane();
        tPane = p;
        speicher = sp;
        futterPos = new HashMap<>(speicher.futterStellen + 1, 1);
        ameisenPos = new HashMap<>(speicher.ameisen + 1, 1);
        pheromonPos = new HashMap<>(speicher.ameisen + 1);
        views = new ImageView[speicher.ameisen];
        unusedViews = new LinkedList<>();
        Canvas background = new Canvas(speicher.groesseX * (width - 1) + 1, speicher.groesseY * (width - 1) + 1);
        GraphicsContext gc = background.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.GREEN);
        gc.fill();
        for (int i = 0; i < speicher.groesseX; i++)
        {
            for (int n = 0; n < speicher.groesseY; n++)
            {
                gc.strokeRect(i * (width - 1), n * (width - 1), width, width);
            }
        }
        rootPane.getChildren().add(background);
        rootPane.setCache(true);
        rootPane.addEventHandler(MouseEvent.MOUSE_MOVED, this);
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(rootPane);
        tPane.setContent(scroll);
        for (int i = 0; i < speicher.ameisen; i++)
        {
            views[i] = new ImageView();
            views[i].setImage(ameise);
            unusedViews.add(i);
        }

    }

    public void drawNest(int x, int y)
    {
        Image nest = new Image("Nest.png");
        ImageView view = new ImageView();
        view.setImage(nest);
        view.setX(x * (width - 1) + 1);
        view.setY(y * (width - 1) + 1);
        rootPane.getChildren().add(view);
    }

    public void drawAnt(int x, int y, boolean draw)
    {
        XYPoint xy = new XYPoint(x, y);
        if (ameisenPos.containsKey(xy))
        {
            if (!draw)
            {
                int index = ameisenPos.get(xy);
                ameisenPos.remove(xy);
                rootPane.getChildren().remove(views[index]);
                unusedViews.add(index);
            }
        } else
        {
            if (!draw)
                return;
            int index = unusedViews.poll();
            ameisenPos.put(xy, index);
            ImageView v = views[index];
            v.relocate(x * (width - 1) + 1, y * (width - 1) + 1);
            rootPane.getChildren().add(v);
        }
    }

    public void drawFutter(int x, int y, boolean draw)
    {
        XYPoint xy = new XYPoint(x, y);
        if (futterPos.containsKey(xy))
        {
            if (!draw)
            {
                rootPane.getChildren().remove(futterPos.get(xy));
                futterPos.remove(xy);
            }
        } else
        {
            if (!draw)
                return;
            ImageView v = new ImageView();
            futterPos.put(xy, v);
            v.setImage(futter);
            v.relocate(x * (width - 1) + 1, y * (width - 1) + 1);
            rootPane.getChildren().add(v);
        }
    }

    public void drawPheromone(int x, int y, boolean draw)
    {
        XYPoint xy = new XYPoint(x, y);
        if (pheromonPos.containsKey(xy))
        {
            if (!draw)
            {
                rootPane.getChildren().remove(pheromonPos.get(xy));
                pheromonPos.remove(xy);
            }
        } else
        {
            if (!draw)
                return;
            ImageView v = new ImageView();
            pheromonPos.put(xy, v);
            v.setImage(pheromon);
            v.relocate(x * (width - 1) + 1, y * (width - 1) + 1);
            rootPane.getChildren().add(v);
        }
    }

    @Override
    public void handle(MouseEvent event)
    {
        int x = (int) Math.floor(event.getX() / (width - 1));
        int y = (int) Math.floor(event.getY() / (width - 1));
        if (x >= speicher.groesseX || y >= speicher.groesseY)
            return;
        tPane.setText("(X:" + (x + 1) + ";Y:" + (y + 1) + ") Ameisen: " + speicher.amVerteilung[x][y] + "; Futter: " + speicher.futterVerteilung[x][y] + "; Pheromone: " + speicher.pheromone[x][y]);
    }

}
