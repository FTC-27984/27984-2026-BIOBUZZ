package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.config.TuningConfig;

import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.MotorEx;

/**
 * Standard 4-wheel mecanum drivetrain. Config names must match the Driver Station
 * robot configuration: frontLeft, frontRight, backLeft, backRight, and the Pinpoint
 * odometry computer as "pinpoint" (any I2C port except port 0).
 *
 * Owns hardware + mixing only. Per-loop drive policy (reading gamepad1) lives in
 * MecanumDriveCommand, not here - periodic() is telemetry-only per NextFTC convention.
 */
public class DrivetrainSubsystem implements Subsystem {

    public static final DrivetrainSubsystem INSTANCE = new DrivetrainSubsystem();

    private final MotorEx frontLeft = new MotorEx("frontLeft");
    private final MotorEx frontRight = new MotorEx("frontRight");
    private final MotorEx backLeft = new MotorEx("backLeft");
    private final MotorEx backRight = new MotorEx("backRight");

    private GoBildaPinpointDriver pinpoint;

    private DrivetrainSubsystem() {
    }

    @Override
    public void initialize() {
        // Right side reversed so positive power drives all wheels forward.
        // Confirm against physical mounting once wired.
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pinpoint = ActiveOpMode.getHardwareMap().get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setOffsets(TuningConfig.PINPOINT_X_OFFSET_MM, TuningConfig.PINPOINT_Y_OFFSET_MM);
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);
        // Robot must be stationary here - this runs during onInit(), before start is pressed.
        pinpoint.resetPosAndIMU();
    }

    @Override
    public void periodic() {
        pinpoint.update();

        ActiveOpMode.getTelemetry().addData("FL power", frontLeft.getPower());
        ActiveOpMode.getTelemetry().addData("FR power", frontRight.getPower());
        ActiveOpMode.getTelemetry().addData("BL power", backLeft.getPower());
        ActiveOpMode.getTelemetry().addData("BR power", backRight.getPower());
        ActiveOpMode.getTelemetry().addData("Heading (deg)", Math.toDegrees(pinpoint.getHeading()));
        ActiveOpMode.getTelemetry().update();
    }

    /** Robot heading in radians, counterclockwise positive (Pinpoint/IMU convention). */
    public double getHeadingRadians() {
        return pinpoint.getHeading();
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
