package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;

import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

/*
Controls:
rightBumper to toggle on and off
leftBumper to reverse intake
 */

@TeleOp(name = "Intake TeleOp")
public class IntakeTeleOp extends NextFTCOpMode {

    public IntakeTeleOp() {
        addComponents(
                BulkReadComponent.INSTANCE,
                new SubsystemComponent(IntakeSubsystem.INSTANCE)
        );
    }

    @Override
    public void onStartButtonPressed() {
        // Right bumper: on/off
        Gamepads.gamepad1().rightBumper()
                .whenBecomesTrue(IntakeSubsystem.INSTANCE.toggle());

        // Left bumper: change direction (stays "on" if it was on)
        Gamepads.gamepad1().leftBumper()
                .whenBecomesTrue(IntakeSubsystem.INSTANCE.reverse());
    }
}