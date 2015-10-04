package de.lehmanju.ameisenfutter;

import java.util.Set;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class SimulationService extends ScheduledService<Set<Change>>
{
    Simulator sim;
    int iterations;

    @Override
    protected Task<Set<Change>> createTask()
    {
        return new Task<Set<Change>>()
        {

            @Override
            protected Set<Change> call() throws Exception
            {
                return sim.simulate(iterations);
            }

        };
    }

    public SimulationService(Simulator sim)
    {
        this.sim = sim;
        iterations = 1;
    }

    public boolean setIterations(int it)
    {
        if (it > 0)
        {
            iterations = it;
            return true;
        } else
            return false;
    }

    public int getIterations()
    {
        return iterations;
    }

}
