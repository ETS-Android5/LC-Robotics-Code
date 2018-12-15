/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

/**
 * This file illustrates the concept of driving a path based on encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forwards, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backwards for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This methods assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="EncoderTest", group="Autonomous")
public class EncoderAuto extends LinearOpMode {

    /* Declare OpMode members. */
    private ElapsedTime     runtime = new ElapsedTime();

    static final double     turnConstant = 0.45;
    static final double     reductionConstant = 0.2;
    static final double     BIG_ENCODER_COUNTS_PER_MOTOR_REV    = 30 ;
    static final double     COUNTS_PER_MOTOR_REV    = 30;    // eg: TETRIX Motor Encoder
    static final double     COUNTS_PER_INCH         = 3.5;
    static final double     DRIVE_SPEED             = 0.2;
    static final double     TURN_SPEED              = 0.5;

    static final int sleep = 2000;

    // motor controllers
    DcMotorController wheelController;
    // motors
    DcMotor motorRight;
    DcMotor motorLeft;

    @Override
    public void runOpMode() {

        motorRight = hardwareMap.dcMotor.get("motorRight");
        motorLeft = hardwareMap.dcMotor.get("motorLeft");


        motorLeft.setDirection(REVERSE);
        motorRight.setDirection(REVERSE);

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();

        telemetry.addData("Mode Right", motorRight.getMode());
        telemetry.addData("Mode Left", motorLeft.getMode());
        telemetry.update();
        sleep(sleep);   // optional pause after each move

        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        telemetry.addData("Mode Right", motorRight.getMode());
        telemetry.addData("Mode Left", motorLeft.getMode());
        telemetry.update();
        sleep(sleep);   // optional pause after each move

        motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Mode Right", motorRight.getMode());
        telemetry.addData("Mode Left", motorLeft.getMode());
        telemetry.update();
        sleep(sleep);   // optional pause after each move

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0",  "Starting at %7d :%7d",
                          motorRight.getCurrentPosition(),
                motorLeft.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        encoderTurn(DRIVE_SPEED,  -135, 15);
        encoderDrive(DRIVE_SPEED,  33,  33, 15);  // S1: Forward 47 Inches with 5 Sec timeout
        encoderTurn(DRIVE_SPEED,  90, 15);
        encoderDrive(DRIVE_SPEED,  16,  16, 15);  // S1: Forward 47 Inches with 5 Sec timeout

        sleep(2000);     // pause

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        boolean leftStop = false;
        boolean rightStop = false;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {


            // Determine new target position, and pass to motor controller
            newLeftTarget = motorLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = motorRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);

            // reset the timeout time and start motion.
            runtime.reset();
            motorRight.setPower(Math.abs(0.5));
            motorLeft.setPower(Math.abs(0.5));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.*/
            while (opModeIsActive() &&
                   (runtime.seconds() < timeoutS) &&
                   (!leftStop || !rightStop)) {

                //Stop right motor if it's finished.
                if(motorRight.getCurrentPosition() >= newRightTarget) {

                    motorRight.setPower(0);
                    rightStop = true;

                }

                    //Stop left motor if it's finished.
                if ((Math.abs((motorLeft.getCurrentPosition()))) >= newLeftTarget) {

                        motorLeft.setPower(0);
                        leftStop = true;

                    }



                // Display it for the driver.
                telemetry.addData("Path1 Right, Left",  "Running to %7d :%7d", newRightTarget,  newLeftTarget);
                telemetry.addData("Status Right, Left",  "Running at %7d :%7d",

                                            motorRight.getCurrentPosition(),
                        (Math.abs((motorLeft.getCurrentPosition()))));

                telemetry.addData("Mode Right", motorRight.getMode());
                telemetry.addData("Mode Left", motorLeft.getMode());
                telemetry.addData("Motor Right", motorRight.isBusy());
                telemetry.addData("Motor Left", motorLeft.isBusy());
                telemetry.update();
            }

            // Stop all motion;
            motorRight.setPower(0);
            motorLeft.setPower(0);

            telemetry.addData("Final position Left: ", motorLeft.getCurrentPosition());
            telemetry.addData("Final position Right: ", motorRight.getCurrentPosition());
            telemetry.update();
              sleep(2000);   // pause after each move
        }
    }

    public void encoderTurn(double speed,
                             double angle,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        boolean leftStop = false;
        boolean rightStop = false;

        int angleToInches = (int)((angle/360)*(38.3)*turnConstant); // Needs to be calibrated.

        // Ensure that the opmode is still active
        if (opModeIsActive()) {


            if(angle > 0) {

                // Determine new target position, and pass to motor controller
                newLeftTarget = (int)(Math.abs(motorLeft.getCurrentPosition()) - (angleToInches*COUNTS_PER_INCH));
                newRightTarget = (int)(Math.abs(motorRight.getCurrentPosition()) + (angleToInches*COUNTS_PER_INCH));

            } else {

                // Determine new target position, and pass to motor controller
                newLeftTarget = (int)(Math.abs(motorLeft.getCurrentPosition()) + (angleToInches*COUNTS_PER_INCH));
                newRightTarget = (int)(Math.abs(motorRight.getCurrentPosition()) - (angleToInches*COUNTS_PER_INCH));

            }

            // reset the timeout time and start motion.
            runtime.reset();

            if(angle > 0) {

                motorRight.setPower(Math.abs(0.5));
                motorLeft.setPower((-0.5));

            } else {

                motorRight.setPower((-0.5));
                motorLeft.setPower(Math.abs(0.5));

            }

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.*/
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (!leftStop || !rightStop)) {

                if(angle > 0) {

                    //Stop right motor if it's finished.
                    if (Math.abs(motorRight.getCurrentPosition()) >= Math.abs(newRightTarget)) {

                        motorRight.setPower(0);
                        rightStop = true;

                    }

                    //Stop left motor if it's finished.
                    if ((Math.abs((motorLeft.getCurrentPosition()))) <= Math.abs(newLeftTarget)) {

                        motorLeft.setPower(0);
                        leftStop = true;

                    }

                } else {

                    //Stop right motor if it's finished.
                    if (Math.abs(motorRight.getCurrentPosition()) <= Math.abs(newRightTarget)) {

                        motorRight.setPower(0);
                        rightStop = true;

                    }

                    //Stop left motor if it's finished.
                    if ((Math.abs((motorLeft.getCurrentPosition()))) >= Math.abs(newLeftTarget)) {

                        motorLeft.setPower(0);
                        leftStop = true;

                    }

                }

            sleep(2000);

                // Display it for the driver.
                telemetry.addData("Turn Right, Left",  "Running to %7d :%7d", newRightTarget,  newLeftTarget);
                telemetry.addData("Status Right, Left",  "Running at %7d :%7d",

                        motorRight.getCurrentPosition(),
                        (Math.abs((motorLeft.getCurrentPosition()))));

                telemetry.addData("Mode Right", motorRight.getMode());
                telemetry.addData("Mode Left", motorLeft.getMode());
                telemetry.addData("Motor Right", motorRight.isBusy());
                telemetry.addData("Motor Left", motorLeft.isBusy());
                telemetry.update();
            }


            // Stop all motion;
            motorRight.setPower(0);
            motorLeft.setPower(0);

            sleep(2000);

            telemetry.addData("Final position Left: ", motorLeft.getCurrentPosition());
            telemetry.addData("Final position Right: ", motorRight.getCurrentPosition());
            telemetry.update();
        }
    }

}