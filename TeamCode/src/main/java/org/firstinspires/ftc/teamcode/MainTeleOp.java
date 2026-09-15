package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commands.MecanumDriveCommand;
import org.firstinspires.ftc.teamcode.subsystems.CatapultSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DrivetrainSubsystem;

import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

@TeleOp(name = "Main TeleOp")
public class MainTeleOp extends NextFTCOpMode {

    public MainTeleOp() {
        addComponents(
                BulkReadComponent.INSTANCE,
                new SubsystemComponent(DrivetrainSubsystem.INSTANCE, CatapultSubsystem.INSTANCE)
        );
    }

    private boolean lastFireButton = false;
    private boolean lastReadyButton = false;

    @Override
    public void onStartButtonPressed() {
        CommandManager.INSTANCE.scheduleCommand(new MecanumDriveCommand());
    }

    @Override
    public void onUpdate() {
        boolean fireNow = gamepad2.a;
        if (fireNow && !lastFireButton) {
            CommandManager.INSTANCE.scheduleCommand(CatapultSubsystem.INSTANCE.fire);
        }
        lastFireButton = fireNow;

        boolean readyNow = gamepad2.b;
        if (readyNow && !lastReadyButton) {
            CommandManager.INSTANCE.scheduleCommand(CatapultSubsystem.INSTANCE.ready);
        }
        lastReadyButton = readyNow;
    }
}
