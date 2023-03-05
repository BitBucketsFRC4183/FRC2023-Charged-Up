package org.bitbuckets.arm;

import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.cubeCone.GamePiece;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;


public class ArmSubsystemSetup implements ISetup<ArmSubsystem> {

    final OperatorInput input;
    final AutoSubsystem autoSubsystem;
    final ISetup<ArmControl> armControlSetup;
    final GamePiece gamePiece;

    public ArmSubsystemSetup(OperatorInput input, AutoSubsystem autoSubsystem, ISetup<ArmControl> armControlSetup, GamePiece gamePiece) {
        this.input = input;
        this.autoSubsystem = autoSubsystem;
        this.armControlSetup = armControlSetup;
        this.gamePiece = gamePiece;
    }

    @Override
    public ArmSubsystem build(IProcess self) {
        return new ArmSubsystem(
                input,
                self.childSetup("arm-ctrl", armControlSetup),
                autoSubsystem,
                self.getDebuggable()
        );
    }
}
