package frc.robot.subsystem.balance;



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
