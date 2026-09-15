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
}
