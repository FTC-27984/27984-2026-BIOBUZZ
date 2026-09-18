package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.config.TuningConfig;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.MotorEx;

/**
 * Flywheel shooter: single REV planetary-geared motor spinning a hooded, fixed-angle
 * flywheel. The hood shapes the exit trajectory mechanically, so it isn't modeled here -
 * only wheel velocity is closed-loop controlled.
 *
 * Unlike the old catapult (drive-to-position, then stop), a flywheel must hold a target
 * velocity continuously. The controller runs every periodic() cycle off whatever
 * targetVelocity was last set, instead of living inside a single command's update loop -
 * that way spin-up persists across command boundaries (e.g. while a feed command runs).
 */
public class FlywheelSubsystem implements Subsystem {

    public static final FlywheelSubsystem INSTANCE = new FlywheelSubsystem();

    private final MotorEx flywheel = new MotorEx("flywheel");

    private double targetVelocity = 0.0;
    private ControlSystem controller = buildController();

    private FlywheelSubsystem() {
    }

    // Rebuilt from TuningConfig each cycle (see periodic()) so PIDF/FF edits made live
    // on the Panels dashboard take effect immediately instead of only at startup.
    private static ControlSystem buildController() {
        return ControlSystem.builder()
                .velPid(TuningConfig.FLYWHEEL_KP, TuningConfig.FLYWHEEL_KI, TuningConfig.FLYWHEEL_KD)
                .basicFF(TuningConfig.FLYWHEEL_KV, TuningConfig.FLYWHEEL_KA, TuningConfig.FLYWHEEL_KS)
                .build();
    }

    @Override
    public void initialize() {
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void periodic() {
        controller = buildController();
        controller.setGoal(new KineticState(targetVelocity));
        double output = controller.calculate(new KineticState(flywheel.getVelocity()));
        flywheel.setPower(Math.max(-1.0, Math.min(1.0, output)));

        ActiveOpMode.telemetry().addData("Flywheel target vel", targetVelocity);
        ActiveOpMode.telemetry().addData("Flywheel actual vel", flywheel.getVelocity());
        ActiveOpMode.telemetry().addData("Flywheel at speed", isAtSpeed());
        ActiveOpMode.telemetry().update();
    }

    // Feeder checks this before pushing a game piece in, so shots aren't taken off-speed.
    public boolean isAtSpeed() {
        return targetVelocity != 0.0
                && controller.isWithinTolerance(new KineticState(TuningConfig.FLYWHEEL_VELOCITY_TOLERANCE));
    }

    public final Command spinUp = new LambdaCommand()
            .setStart(() -> targetVelocity = TuningConfig.FLYWHEEL_TARGET_VELOCITY)
            .setIsDone(() -> true)
            .requires(this)
            .named("Spin Up Flywheel");

    public final Command stop = new LambdaCommand()
            .setStart(() -> {
                targetVelocity = 0.0;
                flywheel.setPower(0.0);
            })
            .setIsDone(() -> true)
            .requires(this)
            .named("Stop Flywheel");
}
