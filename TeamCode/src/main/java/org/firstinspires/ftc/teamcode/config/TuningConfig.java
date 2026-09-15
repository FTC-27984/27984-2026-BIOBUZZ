package org.firstinspires.ftc.teamcode.config;

import com.bylazar.configurables.annotations.Configurable;

/**
 * Live-tunable constants, editable from the Panels dashboard (192.168.43.1:8001)
 * without redeploying code. Placeholder values only until tuned against real hardware.
 */
@Configurable
public class TuningConfig {

    // Drivetrain
    public static double DRIVE_SPEED_MULTIPLIER = 1.0;

    // Pinpoint odometry offsets (mm), relative to tracking center - measure per
    // POD_OFFSETS.md and set for real before trusting field-centric driving
    public static double PINPOINT_X_OFFSET_MM = 0.0;
    public static double PINPOINT_Y_OFFSET_MM = 0.0;

    // Catapult - launch kinematics not yet finalized, tune against real hardware
    public static double CATAPULT_FIRE_POWER = 1.0;
    public static int CATAPULT_FIRE_POSITION = 1500;
    public static int CATAPULT_READY_POSITION = 0;
    public static int CATAPULT_TOLERANCE = 20;
}
