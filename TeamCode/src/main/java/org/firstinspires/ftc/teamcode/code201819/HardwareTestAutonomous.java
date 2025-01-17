package org.firstinspires.ftc.teamcode.code201819;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.HardwareBobAlexanderIII;

/**
 * An autonomous program to test the features in {@link org.firstinspires.ftc.teamcode.hardware.HardwareBobAlexanderIII}.
 * @author Noah Simon
 */
@Autonomous(name="HardwareTestAutonomous", group="autonomous")
@Disabled
public class HardwareTestAutonomous extends LinearOpMode {
    private HardwareBobAlexanderIII robot = new HardwareBobAlexanderIII(this, 0.5);

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        waitForStart();

//        robot.moveMotor(robot.stronkboi, 0.5, 2000);
//        telemetry.addData("Status1", "Landed, On ground");
//        telemetry.update();

        robot.drive(30, 10);
        telemetry.addData("Status2", "Finished drive.");
        telemetry.update();

//        robot.turn(90, 5);
//        telemetry.addData("Status", "Finished turn.");
//        telemetry.update();

        robot.drive(false, 15, 5);
        telemetry.addData("Status3", "Finished reverse.");
        telemetry.update();

        robot.autoSpin(0.5, 3);
        telemetry.addData("Status4", "Finished spin.");
        telemetry.update();

        robot.idolArm.setPosition(1);
        telemetry.addData("Status5", "Finished idol raise");
        telemetry.update();


    }
}
