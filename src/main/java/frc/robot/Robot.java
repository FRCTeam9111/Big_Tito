// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
 
  private final CANSparkMax m_leadMotorleft = new CANSparkMax(3, MotorType.kBrushed);
  //private final CANSparkMax m_arm = new CANSparkMax(9, MotorType.kBrushless);
  private final CANSparkMax m_followMotorleft = new CANSparkMax(4, MotorType.kBrushed);
  private final CANSparkMax m_followMotorright = new CANSparkMax(1, MotorType.kBrushed);
  private final CANSparkMax m_rightArm = new CANSparkMax(5, MotorType.kBrushed);
  private final CANSparkMax m_leftArm = new CANSparkMax(6, MotorType.kBrushed);
  private final CANSparkMax m_leadMotorright = new CANSparkMax(2, MotorType.kBrushed);
  private final CANSparkMax m_Intake = new CANSparkMax(7, MotorType.kBrushed);
  //private final CANSparkMax m_liftMotor = new CANSparkMax(6, MotorType.kBrushless);
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leadMotorleft, m_leadMotorright);
  //Joystick controller
  private final Joystick m_controller = new Joystick(0);
  //Intake timer
  private final Timer m_timer = new Timer();

  //Camera Thread
  Thread m_visionThread;
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
   // Syncs the left side of the motors with one another
   m_followMotorleft.follow(m_leadMotorleft);

   // Syncs the right side of the motors with one another
   m_followMotorright.follow(m_leadMotorright);

   

   m_visionThread = new Thread(
        () -> {
          var camera = CameraServer.startAutomaticCapture();

          var cameraWidth = 640;
          var cameraHeight = 480;

          camera.setResolution(cameraWidth, cameraHeight);        
        });
    m_visionThread.setDaemon(true);
    m_visionThread.start(); 

  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
 
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    m_timer.reset();
    m_timer.start();
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    // joystick 
    m_robotDrive.arcadeDrive(-m_controller.getRawAxis(0) * .5, -m_controller.getRawAxis(1) * .8);
    //intake load
    if (m_controller.getRawButtonPressed(2)) {
      double time = m_timer.get();  // intake values might need to be inverted
      while (m_timer.get() - time < .5) {
        m_Intake.set(.25);
        }
      }
    //intake shoot
    if (m_controller.getRawButtonPressed(2)) {
      m_Intake.set(-.25);
    }
    //arm
    if (m_controller.getRawAxis(6)>0) { 
      m_leftArm.set(.25);
      m_rightArm.set(-.25);
    }
    //arm reverse
    if (m_controller.getRawAxis(6)<0) { 
      m_leftArm.set(-.25);
      m_rightArm.set(.25);
    }
  }

  @Override
  public void testInit() {
 
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
