// Copyright (c) ORF 4450.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package Team4450.Robot24.commands.autonomous;

import static Team4450.Robot24.Constants.*;

import Team4450.Lib.LCD;
import Team4450.Lib.Util;
import Team4450.Robot24.RobotContainer;
import Team4450.Robot24.commands.autonomous.AutoDriveProfiled.Brakes;
import Team4450.Robot24.commands.autonomous.AutoDriveProfiled.StopMotors;
import Team4450.Robot24.subsystems.DriveBase;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

/**
 * This an autonomous command to drive from start straight out a specified distance in meters. The
 * distance is set by which starting pose is selected.
 */
public class DriveOut extends Command {
  private final DriveBase driveBase;

  private SequentialCommandGroup commands = null;
  private Command command = null;
  private Pose2d startingPose;
  private int startingPoseIndex;

  /**
   * Creates a new DriveOut autonomous command.
   *
   * @param driveBase DriveBase subsystem used by this command to drive the robot.
   * @param startingPose Start location pose.
   * @param startingPoseIndex The starting pose position 0-9.
   */
  public DriveOut(DriveBase driveBase, Pose2d startingPose, Integer startingPoseIndex) {
    Util.consoleLog("idx=%d", startingPoseIndex);

    this.driveBase = driveBase;

    this.startingPose = startingPose;

    this.startingPoseIndex = startingPoseIndex;

    // Use addRequirements() here to declare subsystem dependencies.
    // This command is requiring the driveBase for itself and all
    // commands added to the command list. If any command in the
    // list also requires the drive base it will cause this command
    // to be interrupted.
    addRequirements(this.driveBase);
  }

  /**
   * Called when the command is initially scheduled. (non-Javadoc)
   *
   * @see edu.wpi.first.wpilibj2.command.Command#initialize()
   */
  @Override
  public void initialize() {
    Util.consoleLog();

    // driveBase.setMotorSafety(false); // Turn off watchdog.

    LCD.printLine(
        LCD_1,
        "Mode: Auto - DriveOut - All=%s, Location=%d, FMS=%b, msg=%s",
        alliance.name(),
        location,
        DriverStation.isFMSAttached(),
        gameMessage);

    SmartDashboard.putBoolean("Autonomous Active", true);

    // Set heading tracking to initial angle (0 is robot pointed down the field) so
    // NavX class can track which way the robot is pointed all during the match.
    RobotContainer.navx.setHeading(startingPose.getRotation().getDegrees());

    // Target heading should be the same.
    RobotContainer.navx.setTargetHeading(startingPose.getRotation().getDegrees());

    // Reset odometry tracking with initial x,y position and heading (set above) specific to
    // this
    // auto routine. Robot must be placed in same starting location each time for pose tracking
    // to work.
    driveBase.resetOdometry(startingPose);

    double distance = 3.75; // meters.

    // Since a typical autonomous program consists of multiple actions, which are commands
    // in this style of programming, we will create a list of commands for the actions to
    // be taken in this auto program and add them to a sequential command list to be
    // executed one after the other until done at run time.

    commands = new SequentialCommandGroup();

    // First action is to drive forward distance meters and stop.

    command = new AutoDriveProfiled(driveBase, distance, StopMotors.stop, Brakes.off);

    commands.addCommands(command);

    // Launch autonomous command sequence.

    commands.schedule();
  }

  /**
   * Called every time the scheduler runs while the command is scheduled. In this model, this
   * command just idles while the Command Group we created runs on its own executing the steps
   * (commands) of this Auto program.
   */
  @Override
  public void execute() {}

  /** Called when the command ends or is interrupted. */
  @Override
  public void end(boolean interrupted) {
    Util.consoleLog("interrupted=%b", interrupted);

    driveBase.drive(0, 0, 0, false);

    Util.consoleLog(
        "final heading=%.2f  Radians=%.2f",
        RobotContainer.navx.getHeading(), RobotContainer.navx.getHeadingR());
    Util.consoleLog("end ---------------------------------------------------------------");

    SmartDashboard.putBoolean("Autonomous Active", false);
  }

  /**
   * Returns true when this command should end. That should be when all the commands in the command
   * list have finished.
   */
  @Override
  public boolean isFinished() {
    // Note: commands.isFinished() will not work to detect the end of the command list
    // due to how FIRST coded the SquentialCommandGroup class.

    return !commands.isScheduled();
  }
}
