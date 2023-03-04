package org.bitbuckets.cubeCone;

import org.bitbuckets.OperatorInput;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;

public class GamePieceSetup implements ISetup<GamePiece>{

    final OperatorInput input;

    public GamePieceSetup(OperatorInput input) {
        this.input = input;
    }

    @Override
    public GamePiece build(IProcess self) {
        return new GamePiece(input);
    }
}
