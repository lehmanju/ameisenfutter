package de.lehmanju.ameisenfutter;

import java.util.HashMap;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DrawArea implements EventHandler<MouseEvent>
{
    Pane rootPane;
    TitledPane tPane;
    int width = 20;
    int zeilen = 500;
    int spalten = 500;
    int maxFutter = 5;
    int maxAmeisen = 100;
    Map<double[], ImageView> futterPos = new HashMap<>(maxFutter + 1, 1);
    Map<double[], ImageView> ameisenPos = new HashMap<>(maxAmeisen + 1, 1);
    Map<double[], ImageView> pheromonPos = new HashMap<>(maxAmeisen + 1);
    int[][] ameisenAnzahl = new int[zeilen][spalten];
    int[][] futterAnzahl = new int[zeilen][spalten];
    int[][] pheromonAnzahl = new int[zeilen][spalten];
    double[][] futterPositionen = new double[5][2];
    Image ameise = new Image("Ameise.png");
    Image futter = new Image("Futter.png");
    Image pheromon = new Image("Pheromon.png");

    public DrawArea(Stage stage, TitledPane p)
    {
        rootPane = new Pane();
        tPane = p;
        for (int i = 0; i < zeilen; i++)
        {
            for (int n = 0; n < spalten; n++)
            {
                Rectangle rect = new Rectangle();
                rect.setX(i * (width - 1));
                rect.setY(n * (width - 1));
                rect.setWidth(width);
                rect.setHeight(width);
                rect.setStroke(Color.GREEN);
                rect.setFill(Color.WHITE);
                //rect.addEventHandler(MouseEvent.MOUSE_MOVED, this);
                rootPane.getChildren().add(rect);
            }
        }
        rootPane.setCache(true);
        rootPane.addEventHandler(MouseEvent.MOUSE_MOVED, this);
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(rootPane);
        tPane.setContent(scroll);
        Scene scene = new Scene(tPane, 800, 700);
        stage.setScene(scene);
        stage.show();
    }

    public synchronized void drawNest(int x, int y)
    {
        Image nest = new Image("Nest.png");
        ImageView view = new ImageView();
        view.setImage(nest);
        view.setX(x * (width - 1) + 1);
        view.setY(y * (width - 1) + 1);
        rootPane.getChildren().add(view);
    }

    public synchronized void drawAnt(double x, double y, int quantity)
    {
        ameisenAnzahl[(int) x][(int) y] = quantity;
        double[] xy = new double[] { x, y };
        if (ameisenPos.containsKey(xy))
        {
            if (quantity == 0)
            {
                rootPane.getChildren().remove(ameisenPos.get(xy));
                ameisenPos.remove(xy);
            }
            ameisenAnzahl[(int) x][(int) y] = quantity;
        } else
        {
            if (quantity == 0)
                return;
            ImageView v = new ImageView();
            ameisenPos.put(xy, v);
            v.setImage(ameise);
            v.relocate(x * (width - 1) + 1, y * (width - 1) + 1);
            rootPane.getChildren().add(v);
        }
    }

    public synchronized void drawFutter(double x, double y, int quantity)
    {
        futterAnzahl[(int) x][(int) y] = quantity;
        double[] xy = new double[] { x, y };
        if (futterPos.containsKey(xy))
        {
            if (quantity == 0)
            {
                rootPane.getChildren().remove(futterPos.get(xy));
                futterPos.remove(xy);
            }
            futterAnzahl[(int) x][(int) y] = quantity;
        } else
        {
            if (quantity == 0)
                return;
            ImageView v = new ImageView();
            futterPos.put(xy, v);
            v.setImage(futter);
            v.relocate(x * (width - 1) + 1, y * (width - 1) + 1);
            rootPane.getChildren().add(v);
        }
    }

    public synchronized void drawPheromone(double x, double y, int quantity)
    {
        pheromonAnzahl[(int) x][(int) y] = quantity;
        double[] xy = new double[] { x, y };
        if (pheromonPos.containsKey(xy))
        {
            if (quantity == 0)
            {
                rootPane.getChildren().remove(pheromonPos.get(xy));
                pheromonPos.remove(xy);
            }
            pheromonAnzahl[(int) x][(int) y] = quantity;
        } else
        {
            if (quantity == 0)
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
        if (x >= zeilen || y >= spalten)
            return;
        tPane.setText("Ameisen: " + ameisenAnzahl[x][y] + "; Futter: " + futterAnzahl[x][y] + "; Pheromone: " + pheromonAnzahl[x][y]);
    }

}
