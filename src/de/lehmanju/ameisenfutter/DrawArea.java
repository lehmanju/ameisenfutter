package de.lehmanju.ameisenfutter;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class DrawArea implements EventHandler<MouseEvent>
{
    Pane rootPane;
    TitledPane tPane;
    int width = 20;
    Speicher speicher;

    public DrawArea(TitledPane p, Speicher sp, int width)
    {
        this.width = width;
        rootPane = new Pane();
        tPane = p;
        speicher = sp;
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
    }

    public void drawImage(ImageView view, boolean draw)
    {
        if (draw)
            rootPane.getChildren().add(view);
        else
            rootPane.getChildren().remove(view);
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
