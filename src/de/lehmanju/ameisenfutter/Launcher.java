package de.lehmanju.ameisenfutter;

import java.util.List;

import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Launcher extends Application implements EventHandler<WorkerStateEvent>
{
    Speicher sp;
    DrawArea area;
    SimulationService simService;
    int width = 20;
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
        area = new DrawArea(titledPane, sp, width);
        Scene scene = new Scene(root, 800, 700);
        stage.setScene(scene);
        stage.show();
        //stage.setWidth(stage.getWidth() + 1);
        Simulator sim = new Simulator(sp);
        simService = new SimulationService(sim, sp, width);
        List<GChange> initCh = simService.getInitList();
        for (GChange ch : initCh)
            area.drawImage(ch.view, ch.draw);
        simService.setOnSucceeded(this);
        simService.setPeriod(new Duration(2000));
        simService.setIterations(10);
        //simService.start();
    }

    @Override
    public void handle(WorkerStateEvent wsevent)
    {
        List<GChange> changes = simService.getValue();
        for (GChange ch : changes)
        {
            area.drawImage(ch.view, ch.draw);
        }
    }

    @FXML
    public void startSimulation()
    {
        simService.restart();
    }

    @FXML
    public void pauseSimulation()
    {
        simService.pause();
    }

    @FXML
    public void resetSimulation()
    {
        simService.pause();
    }

    @FXML
    public void changeSettings()
    {

    }
}
