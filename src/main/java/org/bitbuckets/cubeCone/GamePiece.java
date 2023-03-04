package org.bitbuckets.cubeCone;

import org.bitbuckets.OperatorInput;
import org.bitbuckets.arm.ArmFSM;
import org.bitbuckets.lib.core.HasLoop;

public class GamePiece implements HasLoop {
    boolean isCone = true;

    final OperatorInput operatorInput;

    public GamePiece(OperatorInput operatorInput) {
        this.operatorInput = operatorInput;
    }

    public boolean isCone()
    {
        return isCone;
    }


    @Override
    public void loop() {
        if(operatorInput.conevscube())
        {
           isCone = false;
        }
        else {
            isCone = true;
        }
    }
}
