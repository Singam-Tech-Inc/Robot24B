// Copyright (c) ORF 4450.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package Team4450.Robot24.commands;

import Team4450.Lib.Util;
import Team4450.Robot24.subsystems.DriveBase;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Momentarily apply small rotation speed to steer wheels into rotation orientation, without
 * rotating the wheels, to park the robot on the charge station.
 */
public class ParkWheels extends Command {
  private final DriveBase driveBase;
  private double timeStamp;

  public ParkWheels(DriveBase driveBase) {
    Util.consoleLog();

    this.driveBase = driveBase;

    addRequirements(driveBase);
  }

  @Override
  public void initialize() {
    Util.consoleLog();

    timeStamp = Util.timeStamp();

    // driveBase.drive(0, 0, .03);
  }

  @Override
  public boolean isFinished() {
    if (Util.getElaspedTime(timeStamp) > .20) return true;
    else return false;
  }

  @Override
  public void end(boolean interrupted) {
    // driveBase.stop();

    Util.consoleLog("interrupted=%b", interrupted);
  }
}
