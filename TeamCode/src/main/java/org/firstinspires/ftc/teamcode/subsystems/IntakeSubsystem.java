package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.config.TuningConfig;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.controllable.Controllable;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.impl.VoltageCompensatingMotor;

public class IntakeSubsystem implements Subsystem {

    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();

    private final MotorEx intakeMotor = new MotorEx("intake");

    private Controllable intake;

    private IMU imu;
    private IntakeSubsystem() {
    }

    @Override
    public void initialize() {
        // Keeps rollers locked when power drops to 0
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake = new VoltageCompensatingMotor(intakeMotor);

        imu = ActiveOpMode.hardwareMap().get(IMU.class, "imu");
        // MUST match how the Control/Expansion Hub is physically mounted on the robot -
        // these are placeholders (see TuningConfig) and will give wrong heading if unset.
        RevHubOrientationOnRobot orientation = new RevHubOrientationOnRobot(
                TuningConfig.HUB_LOGO_FACING_DIRECTION,
                TuningConfig.HUB_USB_FACING_DIRECTION);
        imu.initialize(new IMU.Parameters(orientation));
    }

    @Override
    public void periodic() {
        ActiveOpMode.telemetry().addData("Intake Power", intakeMotor.getPower());
    }

    void setIntakePower(double power){
            intakeMotor.setPower(power);
    }
}
