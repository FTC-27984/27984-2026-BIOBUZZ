package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.DrivetrainSubsystem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.ftc.ActiveOpMode;

/**
 * Continuously drives the mecanum drivetrain from gamepad1 sticks, field-centric
 * (stick forward = away from driver, regardless of robot heading) using the Control
 * Hub's built-in IMU. Holding left bumper falls back to robot-centric, e.g. if
 * heading drifts. Heading is zeroed when the command starts, so the robot must face
 * away from the driver when START is pressed; pressing A re-zeroes heading to the
 * robot's current facing (same pattern as the FTC SDK's
 * RobotTeleopMecanumFieldRelativeDrive sample). Scheduled once in
 * onStartButtonPressed() and runs for the whole teleop period (never finishes).
 */
public class MecanumDriveCommand extends Command {

    private boolean lastResetYawButton = false;

    public MecanumDriveCommand() {
        requires(DrivetrainSubsystem.INSTANCE);
        setInterruptible(true);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void start() {
        // The IMU keeps its yaw reference from power-up or a previous OpMode (e.g. auto),
        // so without this "forward" could point anywhere until the driver presses A.
        DrivetrainSubsystem.INSTANCE.resetYaw();
    }

    @Override
    public void update() {
        boolean resetYawNow = ActiveOpMode.gamepad1().a;
        if (resetYawNow && !lastResetYawButton) {
            DrivetrainSubsystem.INSTANCE.resetYaw();
        }
        lastResetYawButton = resetYawNow;

        double forward = -ActiveOpMode.gamepad1().left_stick_y;
        double strafe = ActiveOpMode.gamepad1().left_stick_x;
        double turn = ActiveOpMode.gamepad1().right_stick_x;

        if (ActiveOpMode.gamepad1().left_bumper) {
            DrivetrainSubsystem.INSTANCE.setDrivePowers(forward, strafe, turn);
            return;
        }

        // Field-centric transform: rotate the stick vector by -heading so "forward"
        // always means "away from the driver." Same polar-coordinate approach as the
        // FTC SDK's own RobotTeleopMecanumFieldRelativeDrive sample.
        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);
        theta = AngleUnit.normalizeRadians(theta - DrivetrainSubsystem.INSTANCE.getHeadingRadians());

        // Result is the driver's stick vector expressed in the robot's own frame.
        double robotForward = r * Math.sin(theta);
        double robotStrafe = r * Math.cos(theta);

        DrivetrainSubsystem.INSTANCE.setDrivePowers(robotForward, robotStrafe, turn);
    }

    @Override
    public void stop(boolean interrupted) {
        DrivetrainSubsystem.INSTANCE.stop();
    }
}
