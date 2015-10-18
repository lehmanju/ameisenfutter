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
        rootPane.setCache(true);
        tPane = p;
        speicher = sp;
        int canvaslaenge = (int) (Math.floor(1024 / (width - 1) / 10) * 10); //auf Zehner gerundet
        System.out.println(canvaslaenge);
        for (int n = 0; n < sp.groesseX / canvaslaenge; n++)
        {
            for (int i = 0; i < sp.groesseY / canvaslaenge; i++)
            {
                Canvas part = new Canvas(canvaslaenge * (width - 1) + 2, canvaslaenge * (width - 1) + 2);
                GraphicsContext gc = part.getGraphicsContext2D();
                gc.setFill(Color.WHITE);
                gc.setStroke(Color.GREEN);
                //gc.setLineWidth(1);
                for (int l = 0; l < canvaslaenge && l < (sp.groesseX - n * canvaslaenge); l++)
                {
                    for (int k = 0; k < canvaslaenge && k < (sp.groesseY - i * canvaslaenge); k++)
                    {
                        gc.strokeRect(l * (width - 1), k * (width - 1), width, width);
                    }
                }
                part.relocate(n * (canvaslaenge * (width - 1)), i * (canvaslaenge * (width - 1)));
                rootPane.getChildren().add(part);
            }
        }
        rootPane.addEventHandler(MouseEvent.MOUSE_MOVED, this);
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(rootPane);
        scroll.setHvalue(scroll.getHmax() / 2);
        scroll.setVvalue(scroll.getVmax() / 2);
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
        String text;
        if (x == speicher.nestX && y == speicher.nestY)
            text = "(X:" + (x + 1) + ";Y:" + (y + 1) + ") Ameisen: " + speicher.amVerteilung[x][y] + "; Futter: "
                    + speicher.futterNest + "; Pheromone: " + speicher.pheromone[x][y];
        else
            text = "(X:" + (x + 1) + ";Y:" + (y + 1) + ") Ameisen: " + speicher.amVerteilung[x][y] + "; Futter: "
                    + speicher.futterVerteilung[x][y] + "; Pheromone: " + speicher.pheromone[x][y];
        tPane.setText(text);
    }

    public void removeAll()
    {
        rootPane.getChildren().clear();
    }

}
