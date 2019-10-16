package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * The class for the LCR 2019-20 robot.
 *
 * If more components are added, please add them to this class!
 *
 * @author Noah Simon
 */
public class HardwareLilPanini extends Robot {

    // All of the components we will need (e.g. motors, servos, sensors...) that are attached to the robot

    public DcMotorController frontController;

    public DcMotorController rearController;

//    public DcMotor motorFrontLeft;
//
//    public DcMotor motorFrontRight;
//
//    public DcMotor motorBackLeft;
//
//    public DcMotor motorBackRight;

    public HardwareLilPanini(OpMode opMode) {
        super(opMode);
    }

    @Override
    public void init(HardwareMap hardwareMap) {
//        motorFrontLeft = registerMotor("motorFrontLeft", DcMotorSimple.Direction.REVERSE, DcMotor.RunMode.RUN_USING_ENCODER);
//        motorFrontRight = registerMotor("motorFrontRight", DcMotorSimple.Direction.FORWARD, DcMotor.RunMode.RUN_USING_ENCODER);
//        motorBackLeft = registerMotor("motorBackLeft", DcMotorSimple.Direction.REVERSE, DcMotor.RunMode.RUN_USING_ENCODER);
//        motorBackRight = registerMotor("motorBackRight", DcMotorSimple.Direction.FORWARD, DcMotor.RunMode.RUN_USING_ENCODER);

        frontController = hardwareMap.get(DcMotorController.class, "frontController");
        rearController = hardwareMap.get(DcMotorController.class, "rearController");
        frontController.setMotorMode(1, DcMotor.RunMode.RUN_USING_ENCODER); // RIGHT
        frontController.setMotorMode(2, DcMotor.RunMode.RUN_USING_ENCODER); // LEFT
        rearController.setMotorMode(1, DcMotor.RunMode.RUN_USING_ENCODER); // LEFT
        rearController.setMotorMode(2, DcMotor.RunMode.RUN_USING_ENCODER); // RIGHT

    }

    @Override
    public void drive(double speed, double dist, double timeout) {

    }

    public void strafe(HorizontalDirection direction, double speed, double dist, double timeout) {
        int correctDirection;
        if (direction == HorizontalDirection.LEFT) {
            correctDirection = 1;
        } else {
            correctDirection = -1;
        }
        frontController.setMotorPower(1, -speed * correctDirection); // Move forward
        frontController.setMotorPower(2, -speed * correctDirection); // Move backward
        rearController.setMotorPower(1, speed * correctDirection); // Move forward
        rearController.setMotorPower(2, speed * correctDirection); // Move backward

        // DO STUFF HERE

        frontController.setMotorPower(1, 0);
        frontController.setMotorPower(2, 0);
        rearController.setMotorPower(1, 0);
        rearController.setMotorPower(2, 0);
    }

    @Override
    public void turn(double speed, double dist, double timeout) {

    }

    public enum HorizontalDirection {
        RIGHT,
        LEFT;
    }

    public class TeleOpUtil {

    }

    public class AutonomousUtil {
        public void drive(double speed, double dist, double timeout) {
            frontController.setMotorPower(1, speed);
            frontController.setMotorPower(2, speed);
            rearController.setMotorPower(1, speed);
            rearController.setMotorPower(2, speed);
            ((LinearOpMode) opMode).sleep((long)timeout);
            frontController.setMotorPower(1, 0);
            frontController.setMotorPower(2, 0);
            rearController.setMotorPower(1, 0);
            rearController.setMotorPower(2, 0);
        }

    }
}
