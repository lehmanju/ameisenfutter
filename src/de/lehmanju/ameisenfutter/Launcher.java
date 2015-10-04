package de.lehmanju.ameisenfutter;

import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Set;

import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class Launcher extends Application implements EventHandler<WorkerStateEvent>
{
    Speicher sp;
    DrawArea area;
    SimulationService simService;
    @FXML
    TitledPane titledPane;
    @FXML
    AnchorPane root;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/Fenster.fxml"));
        loader.setController(this);
        loader.load();
        sp = new Speicher(5, 100, 50, 500, 500);
        area = new DrawArea(titledPane, sp);
        Scene scene = new Scene(root, 800, 700);
        stage.setScene(scene);
        stage.show();
        Simulator sim = new Simulator(sp, area);
        simService = new SimulationService(sim);
        simService.setOnSucceeded(this);
        simService.setPeriod(new Duration(2000));
        simService.setIterations(100);
        simService.start();
    }

    @Override
    public void handle(WorkerStateEvent wsevent)
    {
        //System.out.println("Task beendet");
        Set<Change> changes = simService.getValue();
        for (Change ch : changes)
        {
            switch (ch.type)
            {
            case 'A':
            {
                if (sp.amVerteilung[ch.x][ch.y] > 0)
                    area.drawAnt(ch.x, ch.y, true);
                else
                    area.drawAnt(ch.x, ch.y, false);
                break;
            }
            case 'P':
            {
                if (sp.pheromone[ch.x][ch.y] > 0)
                    area.drawPheromone(ch.x, ch.y, true);
                else
                    area.drawPheromone(ch.x, ch.y, false);
                break;
            }
            case 'F':
            {
                if (sp.futterVerteilung[ch.x][ch.y] > 0)
                    area.drawFutter(ch.x, ch.y, true);
                else
                    area.drawFutter(ch.x, ch.y, false);
                break;
            }
            }
        }
        //simService.restart();
    }

    @FXML
    public void startSimulation()
    {

    }

    @FXML
    public void pauseSimulation()
    {

    }

    @FXML
    public void endSimulation()
    {
        simService.cancel();
    }

    @FXML
    public void changeSettings()
    {

    }
}
