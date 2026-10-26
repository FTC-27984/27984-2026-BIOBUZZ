package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commands.MecanumDriveCommand;
import org.firstinspires.ftc.teamcode.subsystems.DrivetrainSubsystem;

import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

/**
 * Drivetrain-only teleop for testing and tuning the mecanum drive and IMU heading,
 * without the flywheel, feeder, or logo stream. Controls are the same as MainTeleOp:
 * gamepad1 sticks drive field-centric, left bumper holds robot-centric, A zeroes heading.
 */
@TeleOp(name = "Drivetrain TeleOp")
public class DrivetrainTeleOp extends NextFTCOpMode {

    public DrivetrainTeleOp() {
        addComponents(
                BulkReadComponent.INSTANCE,
                new SubsystemComponent(DrivetrainSubsystem.INSTANCE)
        );
    }

    @Override
    public void onStartButtonPressed() {
        CommandManager.INSTANCE.scheduleCommand(new MecanumDriveCommand());
    }
}
