package org.bitbuckets.rgb;

import com.ctre.phoenix.led.CANdle;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.debug.IDebuggable;

public class RgbSubsystem implements HasLoop {

    final CANdle candle;

    final OperatorInput rgbInput;



    RgbFSM state = RgbFSM.DEFAULT;

    public RgbFSM state() {
        return state;
    }

    final IDebuggable debuggable;

    final OperatorInput operatorInput;



    public RgbSubsystem(CANdle candle, OperatorInput rgbInput, IDebuggable debuggable, OperatorInput operatorInput) {
        this.candle = candle;
        this.rgbInput = rgbInput;
        this.debuggable = debuggable;
        this.operatorInput = operatorInput;
    }

    @Override
    public void loop() {
        candle.setLEDs(0,255,0);
        if(!rgbInput.isCube()) {
            candle.setLEDs(0, 255, 255);
        }
        if(rgbInput.isCube()) {
            candle.setLEDs(255, 0, 255);
        }
    }
}
