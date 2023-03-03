package org.bitbuckets.cubeCone;

import org.bitbuckets.OperatorInput;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;

public class gamePeiceSetup implements ISetup<GamePiece>{

    final OperatorInput input;

    public gamePeiceSetup(OperatorInput input) {
        this.input = input;
    }

    @Override
    public GamePiece build(IProcess self) {
        GamePiece gamePiece = new GamePiece(input);
        return gamePiece;
    }
}
