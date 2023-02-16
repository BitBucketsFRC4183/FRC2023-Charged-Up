package org.bitbuckets.lib.control;

class PIDCalculatorSetupTest {
/*
    @Test
    void pidCalculator_shouldWork() throws InterruptedException {
        assert HAL.initialize(500, 0);

        NetworkTable table = NetworkTableInstance.getDefault().getTable("RealOutputs");

        IdentityDriver id = new IdentityDriver();
        TuneableDriver driver = new TuneableDriver(table, id);
        LoopDriver loopDriver = new LoopDriver();

        ProcessPath path = new ProcessPath(
                0,
                Mockito.mock(),
                id,
                Mockito.mock(),
                loopDriver,
                driver,
                false
        );

        IPIDCalculator calculator = new PIDCalculatorSetup(
                new PIDConfig(1,0,0,0)
        ).build(path.addChild("calculator"));

        Assertions.assertTrue(table.getEntry("calculator/p").exists());
        Assertions.assertEquals(1.0, calculator.rawAccess(PIDController.class).getP(), "initial P value should be 1");

        table.getEntry("calculator/p").setDouble(2.0);

        Thread.sleep(1000);
        loopDriver.runPeriodic();
        Assertions.assertEquals(2.0, calculator.rawAccess(PIDController.class).getP(), "pid calculator should read 2");

    }*/
}