package org.bitbuckets.rgb;

import com.ctre.phoenix.led.CANdle;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.log.IDebuggable;

public class RgbSubsystem implements HasLoop {

    final CANdle candle;




    RgbFSM state = RgbFSM.DEFAULT;

    public RgbFSM state() {
        return state;
    }

    final IDebuggable debuggable;

    final OperatorInput operatorInput;



    public RgbSubsystem(CANdle candle , IDebuggable debuggable, OperatorInput operatorInput) {
        this.candle = candle;
        this.debuggable = debuggable;
        this.operatorInput = operatorInput;
    }

    @Override
    public void loop() {

        // Wrote state machine to better comprehend user input systems,
        // but can also be used for extra spice during certain maneuvers ;)
        switch (state) {
            case DEFAULT:
                candle.setLEDs(0,255,0);
                if (operatorInput.conevscube()) {
                    candle.setLEDs(0, 255, 255);
                } else {
                    candle.setLEDs(255, 0, 255);
                }

                if (operatorInput.isAutoHeadingPressed() ) {
                    state = RgbFSM.AUTO_HEADING;
                }


        }


    }
}
