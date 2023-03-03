package org.bitbuckets.rgb;

import com.ctre.phoenix.led.CANdle;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.cubeCone.GamePiece;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.debug.IDebuggable;

public class RgbSubsystem implements HasLoop {

    final CANdle candle;

    final OperatorInput rgbInput;


    public GamePiece gamePiece;



    final IDebuggable debuggable;



    public RgbSubsystem(CANdle candle, OperatorInput rgbInput, IDebuggable debuggable) {
        this.candle = candle;
        this.rgbInput = rgbInput;
        this.debuggable = debuggable;
    }

    @Override
    public void loop() {
        candle.setLEDs(0,255,0);
        if(gamePiece.isCone()) {
            candle.setLEDs(0, 255, 255);
        }
        if(!gamePiece.isCone()) {
            candle.setLEDs(255, 0, 255);
        }
    }
}
