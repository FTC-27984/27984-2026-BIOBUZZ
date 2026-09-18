package org.firstinspires.ftc.teamcode.subsystems;

import org.firstinspires.ftc.teamcode.config.TuningConfig;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.SetPosition;

/**
 * Pushes a game piece into the flywheel. Didn't exist under the catapult design - a
 * flywheel only imparts velocity, it doesn't have its own "launch" stroke, so something
 * has to feed a piece into it. Timed extend/retract rather than position feedback since
 * this is a simple pusher, not a mechanism with its own sensor.
 */
public class FeederSubsystem implements Subsystem {

    public static final FeederSubsystem INSTANCE = new FeederSubsystem();

    private final ServoEx feeder = new ServoEx("feeder");

    private FeederSubsystem() {
    }

    public final Command feed = new SequentialGroup(
            new SetPosition(feeder, TuningConfig.FEEDER_EXTEND_POSITION).requires(this),
            new Delay(TuningConfig.FEEDER_EXTEND_SECONDS),
            new SetPosition(feeder, TuningConfig.FEEDER_RETRACT_POSITION).requires(this)
    );
}
