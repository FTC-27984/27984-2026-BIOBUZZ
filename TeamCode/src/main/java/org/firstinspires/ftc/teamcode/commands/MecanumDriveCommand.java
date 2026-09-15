package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.DrivetrainSubsystem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.ftc.ActiveOpMode;

/**
 * Continuously drives the mecanum drivetrain from gamepad1 sticks, field-centric
 * (stick forward = away from driver, regardless of robot heading) using Pinpoint's
 * heading. Holding left bumper falls back to robot-centric, e.g. if heading drifts
 * or Pinpoint isn't calibrated. Scheduled once in onStartButtonPressed() and runs
 * for the whole teleop period (never finishes).
 *
 * NOTE: assumes ActiveOpMode exposes gamepad1 the same way it exposes telemetry/
 * hardwareMap - verify on first compile; fall back to dev.nextftc.ftc.Gamepads if not.
 */
public class MecanumDriveCommand extends Command {

    public MecanumDriveCommand() {
        requires(DrivetrainSubsystem.INSTANCE);
        setInterruptible(true);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void update() {
        double forward = -ActiveOpMode.getGamepad1().left_stick_y;
        double strafe = ActiveOpMode.getGamepad1().left_stick_x;
        double turn = ActiveOpMode.getGamepad1().right_stick_x;

        if (ActiveOpMode.getGamepad1().left_bumper) {
            DrivetrainSubsystem.INSTANCE.setDrivePowers(forward, strafe, turn);
            return;
        }

        // Field-centric transform: rotate the stick vector by -heading so "forward"
        // always means "away from the driver." Same polar-coordinate approach as the
        // FTC SDK's own RobotTeleopMecanumFieldRelativeDrive sample, using Pinpoint's
        // heading instead of the IMU.
        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);
        theta = AngleUnit.normalizeRadians(theta - DrivetrainSubsystem.INSTANCE.getHeadingRadians());

        double fieldForward = r * Math.sin(theta);
        double fieldStrafe = r * Math.cos(theta);

        DrivetrainSubsystem.INSTANCE.setDrivePowers(fieldForward, fieldStrafe, turn);
    }

    @Override
    public void stop(boolean interrupted) {
        DrivetrainSubsystem.INSTANCE.stop();
    }
}
