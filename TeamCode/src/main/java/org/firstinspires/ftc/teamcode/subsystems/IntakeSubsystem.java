package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.config.TuningConfig;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.impl.VoltageCompensatingMotor;

public class IntakeSubsystem implements Subsystem {

    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();

    private final MotorEx intakeMotor = new MotorEx("intake");

    private IntakeSubsystem() {
    }

    @Override
    public void initialize() {
        // Keeps rollers locked when power drops to 0
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.reverse();
    }

    @Override
    public void periodic() {
        ActiveOpMode.telemetry().addData("Intake Power", intakeMotor.getPower());
    }

    public void setIntakePower(double power) {
        intakeMotor.setPower(power);
    }

    // --- NextFTC Command Definitions ---

    /** Spin intake forward to pull game elements in */
    public Command intake = new LambdaCommand()
            .setStart(() -> setIntakePower(1.0))
            .setStop((interrupted) -> setIntakePower(0.0))
            .requires(this)
            .named("Intake In");

    /** Spin intake in reverse to eject game elements */
    public final Command outtake = new LambdaCommand()
            .setStart(() -> setIntakePower(-1.0))
            .setStop((interrupted) -> setIntakePower(0.0))
            .requires(this)
            .named("Outtake");

    /** Stop the intake motor */
    public final Command stop = new LambdaCommand()
            .setStart(() -> setIntakePower(0.0))
            .setIsDone(() -> true)
            .requires(this)
            .named("Stop Intake");
}