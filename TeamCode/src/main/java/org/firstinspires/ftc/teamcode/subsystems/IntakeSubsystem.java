package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.Controllable;
import dev.nextftc.hardware.impl.MotorEx;

public class IntakeSubsystem implements Subsystem {

    private final MotorEx intakeMotor = new MotorEx("intake");

    private Controllable intake;

    private double headingRadians = 0.0;

    public void initialize() {

    }

    public void periodic() {

    }

    public void setIntakePower(double power) {

    }
}
