package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.config.TuningConfig;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.MotorEx;
import kotlin.Unit;

/**
 * Catapult shooter: two GoBilda Yellow Jacket motors rigidly coupled to one arm,
 * so they must always be driven identically. Modeled as leader/follower rather than
 * two independent position loops, so the pair can't fight each other through the
 * shared mechanical load - the follower just mirrors the leader's output power.
 *
 * Launch kinematics (single revolution vs. timed power burst) are NOT finalized.
 * fire/ready/stop is the stable command surface; only the internals below should
 * need to change once the mechanism is tuned on real hardware.
 */
public class CatapultSubsystem implements Subsystem {

    public static final CatapultSubsystem INSTANCE = new CatapultSubsystem();

    private final MotorEx leader = new MotorEx("catapultLeft");
    private final MotorEx follower = new MotorEx("catapultRight");

    private CatapultSubsystem() {
    }

    @Override
    public void initialize() {
        leader.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        follower.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void periodic() {
        ActiveOpMode.getTelemetry().addData("Catapult position", leader.getCurrentPosition());
        ActiveOpMode.getTelemetry().addData("Catapult power", leader.getPower());
        ActiveOpMode.getTelemetry().addData("Catapult current (A)", leader.getMotor().getCurrent(CurrentUnit.AMPS));
        ActiveOpMode.getTelemetry().update();
    }

    // Leader is rigidly coupled to the arm, so a jam shows up as a current spike on the
    // leader alone - cut power instead of holding against the stall.
    private boolean isStalled() {
        return leader.getMotor().getCurrent(CurrentUnit.AMPS) > TuningConfig.CATAPULT_STALL_CURRENT_AMPS;
    }

    // Suppliers (not captured values) so live edits from the Panels dashboard take
    // effect on the next fire/ready, not just whatever TuningConfig held at startup.
    private Command goToPosition(IntSupplier targetPosition, DoubleSupplier power, String name) {
        return new LambdaCommand()
                .setStart(() -> {
                    leader.setPower(power.getAsDouble());
                    return Unit.INSTANCE;
                })
                .setUpdate(() -> {
                    // Follower mirrors leader's actual output power every loop -
                    // open-loop, no independent position control of its own.
                    follower.setPower(leader.getPower());
                    return Unit.INSTANCE;
                })
                .setIsDone(() -> Math.abs(leader.getCurrentPosition() - targetPosition.getAsInt()) < TuningConfig.CATAPULT_TOLERANCE
                        || isStalled())
                .setStop((interrupted) -> {
                    leader.setPower(0.0);
                    follower.setPower(0.0);
                    return Unit.INSTANCE;
                })
                .requires(this)
                .named(name);
    }

    public final Command fire = goToPosition(
            () -> TuningConfig.CATAPULT_FIRE_POSITION,
            () -> TuningConfig.CATAPULT_FIRE_POWER,
            "Fire Catapult");

    public final Command ready = goToPosition(
            () -> TuningConfig.CATAPULT_READY_POSITION,
            () -> -TuningConfig.CATAPULT_FIRE_POWER,
            "Ready Catapult");

    public final Command stop = new LambdaCommand()
            .setStart(() -> {
                leader.setPower(0.0);
                follower.setPower(0.0);
                return Unit.INSTANCE;
            })
            .setIsDone(() -> true)
            .requires(this)
            .named("Stop Catapult");
}
