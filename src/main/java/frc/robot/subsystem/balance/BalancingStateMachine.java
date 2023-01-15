package frc.robot.subsystem.balance;


//action to perform when state is entered
//class balanceSteps {
        //final Consumer<BalancerSubsystem> startAction;
        //wait before transitioning state (true = continue, false=wait)
        //final Function<BalancerSubsystem, Boolean> waitCondition;
        //action to perform when state is exited
        //final Consumer<BalancerSubsystem> endAction;
        //determines if step causes something on robot to move (true if it does)
        //final boolean runInStepByStep;


        //public balanceSteps(Consumer<BalancerSubsystem> startAction, Function<BalancerSubsystem, Boolean> waitCondition, Consumer<BalancerSubsystem> endAction, boolean runInStepByStep) {
            //this.startAction = startAction;
            //this.waitCondition = waitCondition;
            //this.endAction = endAction;
            //this.runInStepByStep = runInStepByStep;
        //}

    //}

public class BalancingStateMachine  {
    //set intake to non-extended state with method TBD
    public enum BalanceState {
        MOVING_TO_PREBALANCE{

        },
        IN_PREBALANCE,
        BALANCING,
        ENGAGED;


        private boolean isAtChargeStation = false;
    }

    BalanceState systemState = BalanceState.MOVING_TO_PREBALANCE;
    public synchronized void handle(double timestamp, boolean moveToStation) {
        BalanceState systemState = this.systemState;


        switch (systemState) {
            case IN_PREBALANCE:

            case BALANCING:

            case ENGAGED:


    }


}}
