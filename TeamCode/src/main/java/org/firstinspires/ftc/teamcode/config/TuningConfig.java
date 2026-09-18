package org.firstinspires.ftc.teamcode.config;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

/**
 * Live-tunable constants, editable from the Panels dashboard (192.168.43.1:8001)
 * without redeploying code. Placeholder values only until tuned against real hardware.
 */
@Configurable
public class TuningConfig {

    // Drivetrain
    public static double DRIVE_SPEED_MULTIPLIER = 1.0;

    // Which way the Control/Expansion Hub is physically mounted on the robot - the
    // built-in IMU needs this to report correct heading. Not Panels-editable (enum
    // constants, not primitives); set these directly to match your hub's mounting.
    public static RevHubOrientationOnRobot.LogoFacingDirection HUB_LOGO_FACING_DIRECTION =
            RevHubOrientationOnRobot.LogoFacingDirection.UP;
    public static RevHubOrientationOnRobot.UsbFacingDirection HUB_USB_FACING_DIRECTION =
            RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;

    // Catapult - launch kinematics not yet finalized, tune against real hardware
    public static double CATAPULT_FIRE_POWER = 1.0;
    public static int CATAPULT_FIRE_POSITION = 1500;
    public static int CATAPULT_READY_POSITION = 0;
    public static int CATAPULT_TOLERANCE = 20;
    // Leader motor current draw above which it's treated as stalled and cut - placeholder,
    // set from the GoBilda Yellow Jacket's free/stall current spec once tuned.
    public static double CATAPULT_STALL_CURRENT_AMPS = 5.0;

    // Flywheel - velocity PIDF + feedforward, all placeholders pending tuning against the
    // actual REV planetary motor (its free speed/encoder CPR differ from the old catapult
    // motors, so these can't be copied from another mechanism's constants).
    public static double FLYWHEEL_TARGET_VELOCITY = 1800.0; // ticks/sec
    public static double FLYWHEEL_KP = 0.0005;
    public static double FLYWHEEL_KI = 0.0;
    public static double FLYWHEEL_KD = 0.0;
    public static double FLYWHEEL_KV = 0.00015;
    public static double FLYWHEEL_KA = 0.0;
    public static double FLYWHEEL_KS = 0.0;
    public static double FLYWHEEL_VELOCITY_TOLERANCE = 50.0; // ticks/sec

    // Feeder - timed push into the flywheel, no position feedback
    public static double FEEDER_EXTEND_POSITION = 1.0;
    public static double FEEDER_RETRACT_POSITION = 0.0;
    public static double FEEDER_EXTEND_SECONDS = 0.25;
}
