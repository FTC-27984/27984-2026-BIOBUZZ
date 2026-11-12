package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.controllable.Controllable;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.impl.VoltageCompensatingMotor;

public class IntakeSubsystem implements Subsystem {

    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();

    private static final double INTAKE_POWER = 0.8;

    private final MotorEx intakeMotor = new MotorEx("intake");
    private Controllable intake;

    private boolean running = false;
    private boolean reversed = false;

    private IntakeSubsystem() {}

    @Override
    public void initialize() {
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake = new VoltageCompensatingMotor(intakeMotor);
        running = false;
        reversed = false;
    }

    @Override
    public void periodic() {
        ActiveOpMode.telemetry().addData("Intake Running", running);
        ActiveOpMode.telemetry().addData("Intake Reversed", reversed);
        ActiveOpMode.telemetry().addData("Intake Power", intakeMotor.getPower());
    }

    /** On if off, off if on. Keeps the current direction. */
    public Command toggle() {
        return new InstantCommand(() -> {
            running = !running;
            applyPower();
        });
    }

    /** Flips direction. If the intake is running it changes direction immediately and keeps running. */
    public Command reverse() {
        return new InstantCommand(() -> {
            reversed = !reversed;
            applyPower();
        });
    }

    private void applyPower() {
        double power = !running ? 0.0 : (reversed ? -INTAKE_POWER : INTAKE_POWER);
        intake.setPower(power);
    }
}