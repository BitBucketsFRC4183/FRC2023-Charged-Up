package org.bitbuckets.lib;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import edu.wpi.first.wpilibj.util.WPILibVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SwapSetup<T> implements ISetup<T> {

    //do stuff based on the robot

    final ISetup<T> useOnAppa;
    final ISetup<T> useOnNew;
    final ISetup<T> useOnSim;

    public SwapSetup(ISetup<T> useOnAppa, ISetup<T> useOnNew, ISetup<T> useOnSim) {
        this.useOnAppa = useOnAppa;
        this.useOnNew = useOnNew;
        this.useOnSim = useOnSim;
    }

    @Override
    public T build(IProcess self) {
        //TODO this will break


        if (self.isReal()) {
            ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "iwgetid -r");
            builder.redirectErrorStream(true);

            try {
                Process p = builder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();

                while (line != null) {
                    line = reader.readLine();

                    if (line.contains("appa")) {
                        return useOnAppa.build(self);
                    }
                }

                return useOnNew.build(self);


            } catch (IOException e) {
                throw new IllegalStateException("something broke when trying to figure out the robot");
            }

        } else {
            return useOnSim.build(self);
        }
    }
}
