package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.config.TuningConfig;

import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.controllable.Controllable;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.impl.VoltageCompensatingMotor;

/**
 * Standard 4-wheel mecanum drivetrain (Studica motors). Config names must match the
 * Driver Station robot configuration: frontLeft, frontRight, backLeft, backRight, and
 * "imu" for the Control Hub's built-in IMU (used for heading only - no odometry pods).
 *
 * Owns hardware + mixing only. Per-loop drive policy (reading gamepad1) lives in
 * MecanumDriveCommand, not here - periodic() is telemetry-only per NextFTC convention.
 */
public class DrivetrainSubsystem implements Subsystem {

    public static final DrivetrainSubsystem INSTANCE = new DrivetrainSubsystem();

    private final MotorEx frontLeftMotor = new MotorEx("frontLeft");
    private final MotorEx frontRightMotor = new MotorEx("frontRight");
    private final MotorEx backLeftMotor = new MotorEx("backLeft");
    private final MotorEx backRightMotor = new MotorEx("backRight");

    // Wrapped so commanded power is scaled to compensate for battery voltage sag -
    // without odometry to correct against, a consistent power-to-speed relationship
    // is what keeps IMU-only autos (time/power-based distance) from drifting further.
    private Controllable frontLeft;
    private Controllable frontRight;
    private Controllable backLeft;
    private Controllable backRight;

    private IMU imu;

    private DrivetrainSubsystem() {
    }

    @Override
    public void initialize() {
        // Right side reversed so positive power drives all wheels forward.
        // Confirm against physical mounting once wired.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft = new VoltageCompensatingMotor(frontLeftMotor);
        frontRight = new VoltageCompensatingMotor(frontRightMotor);
        backLeft = new VoltageCompensatingMotor(backLeftMotor);
        backRight = new VoltageCompensatingMotor(backRightMotor);

        imu = ActiveOpMode.getHardwareMap().get(IMU.class, "imu");
        // MUST match how the Control/Expansion Hub is physically mounted on the robot -
        // these are placeholders (see TuningConfig) and will give wrong heading if unset.
        RevHubOrientationOnRobot orientation = new RevHubOrientationOnRobot(
                TuningConfig.HUB_LOGO_FACING_DIRECTION,
                TuningConfig.HUB_USB_FACING_DIRECTION);
        imu.initialize(new IMU.Parameters(orientation));
    }

    @Override
    public void periodic() {
        ActiveOpMode.getTelemetry().addData("FL power", frontLeft.getPower());
        ActiveOpMode.getTelemetry().addData("FR power", frontRight.getPower());
        ActiveOpMode.getTelemetry().addData("BL power", backLeft.getPower());
        ActiveOpMode.getTelemetry().addData("BR power", backRight.getPower());
        ActiveOpMode.getTelemetry().addData("Heading (deg)", Math.toDegrees(getHeadingRadians()));
        ActiveOpMode.getTelemetry().update();
    }

    /** Robot heading in radians, counterclockwise positive. */
    public double getHeadingRadians() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    /** Zeroes heading to the robot's current orientation. */
    public void resetYaw() {
        imu.resetYaw();
    }

    /**
     * Mixes joystick-style inputs into wheel powers and writes them to the motors.
     *
     * @param forward -1.0 (backward) to 1.0 (forward)
     * @param strafe  -1.0 (left) to 1.0 (right)
     * @param turn    -1.0 (counterclockwise) to 1.0 (clockwise)
     */
    public void setDrivePowers(double forward, double strafe, double turn) {
        double fl = forward + strafe + turn;
        double fr = forward - strafe - turn;
        double bl = forward - strafe + turn;
        double br = forward + strafe - turn;

        double max = Math.max(1.0, Math.max(Math.max(Math.abs(fl), Math.abs(fr)), Math.max(Math.abs(bl), Math.abs(br))));

        double speed = TuningConfig.DRIVE_SPEED_MULTIPLIER;
        frontLeft.setPower((fl / max) * speed);
        frontRight.setPower((fr / max) * speed);
        backLeft.setPower((bl / max) * speed);
        backRight.setPower((br / max) * speed);
    }

    public void stop() {
        setDrivePowers(0.0, 0.0, 0.0);
    }
}
