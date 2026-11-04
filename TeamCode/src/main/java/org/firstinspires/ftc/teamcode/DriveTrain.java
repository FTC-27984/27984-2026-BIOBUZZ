
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commands.MecanumDriveCommand;
import org.firstinspires.ftc.teamcode.subsystems.DrivetrainSubsystem;

import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

@TeleOp(name = "DriveTrain")
public class DriveTrain extends NextFTCOpMode {

    public DriveTrain() {
        addComponents(
                BulkReadComponent.INSTANCE,
                new SubsystemComponent(DrivetrainSubsystem.INSTANCE)
        );
    }

    @Override
    public void onStartButtonPressed() {
        CommandManager.INSTANCE.scheduleCommand(
                new MecanumDriveCommand()
        );
    }
}
