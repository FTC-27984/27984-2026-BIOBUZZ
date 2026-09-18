package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commands.MecanumDriveCommand;
import org.firstinspires.ftc.teamcode.components.LogoStreamComponent;
import org.firstinspires.ftc.teamcode.subsystems.DrivetrainSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem;

import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

@TeleOp(name = "Main TeleOp")
public class MainTeleOp extends NextFTCOpMode {

    public MainTeleOp() {
        addComponents(
                BulkReadComponent.INSTANCE,
                LogoStreamComponent.INSTANCE,
                new SubsystemComponent(DrivetrainSubsystem.INSTANCE, FlywheelSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
        );
    }

    private boolean lastSpinButton = false;
    private boolean lastFeedButton = false;
    private boolean flywheelSpinning = false;

    @Override
    public void onStartButtonPressed() {
        CommandManager.INSTANCE.scheduleCommand(new MecanumDriveCommand());
    }

    @Override
    public void onUpdate() {
        boolean spinNow = gamepad2.a;
        if (spinNow && !lastSpinButton) {
            flywheelSpinning = !flywheelSpinning;
            CommandManager.INSTANCE.scheduleCommand(
                    flywheelSpinning ? FlywheelSubsystem.INSTANCE.spinUp : FlywheelSubsystem.INSTANCE.stop);
        }
        lastSpinButton = spinNow;

        boolean feedNow = gamepad2.b;
        if (feedNow && !lastFeedButton && FlywheelSubsystem.INSTANCE.isAtSpeed()) {
            CommandManager.INSTANCE.scheduleCommand(FeederSubsystem.INSTANCE.feed);
        }
        lastFeedButton = feedNow;
    }
}
