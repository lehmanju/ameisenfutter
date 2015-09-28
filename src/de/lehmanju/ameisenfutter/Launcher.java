package de.lehmanju.ameisenfutter;

import javafx.stage.Stage;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TitledPane;

public class Launcher extends Application
{
    TitledPane p;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        p = FXMLLoader.load(this.getClass().getResource("/Fenster.fxml"));
        DrawArea area = new DrawArea(stage, p);
        Task<Void> backgroundSim = new Task<Void>()
        {

            @Override
            protected Void call() throws Exception
            {
                Simulator sim = new Simulator(area);
                sim.startSimulation(1000);
                return null;
            }

        };
        Thread th = new Thread(backgroundSim);
        th.setDaemon(true);
        th.start();
    }

}
