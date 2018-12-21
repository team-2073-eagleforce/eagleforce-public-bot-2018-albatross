package org.usfirst.frc.team2073.robot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mycila.guice.ext.closeable.CloseableInjector;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;
import com.team2073.common.robot.AbstractRobotDelegate;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import com.team2073.common.util.ConversionUtil;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.commands.auto.center.CenterLeftSwitch;
import org.usfirst.frc.team2073.robot.commands.auto.right.RightLeftSwitchLeftScale;
import org.usfirst.frc.team2073.robot.ctx.*;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;
import org.usfirst.frc.team2073.robot.util.AutonomousChooser;
import org.usfirst.frc.team2073.robot.util.GameDataParser;
import org.usfirst.frc.team2073.robot.util.GameDataParser.Side;
import org.usfirst.frc.team2073.robot.util.GameDataParser.Target;
import org.usfirst.frc.team2073.robot.util.SetCameraModeService;
import org.usfirst.frc.team2073.robot.util.inject.InjectNamed;

import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Robot extends AbstractRobotDelegate {
    private static final String LOGGING_LEVEL_PREFIX = "log.";
    private static final String[] CONVENIENT_LOGGER_NAMES = {
            // Mainbot
            "", "commands", "subsystems", "util", "Robot",

            // Common
            "com.team2073.common.motionprofiling", "com.team2073.common.svc.camera",};
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    private final SendableChooser<Level> loggingLevelChooser = new SendableChooser<>();
    private final Preferences preferences = Preferences.getInstance();
    private final String defaultPackage = getClass().getPackage().getName();
    private final String defaultPackagePrefix = defaultPackage + ".";

    public enum Alignment {
        SELECT_ONE, LEFT, RIGHT, CENTER
    }

    private Side ourSwitch = Side.UNKNOWN;
    private Side scale = Side.UNKNOWN;

    private Command autonomousCommand;
    private SendableChooser<Alignment> alignmentChooser = new SendableChooser<>();
    @Inject
    private AutonomousChooser autonomousChooser;
    @Inject
    private CloseableInjector closeableInjector;
    @Inject
    private SmartDashboardAwareRegistry smartDashboardAwareRegistry;
    @Inject
    private CenterLeftSwitch redCenterAuto;
    @Inject
    private CommandFactory commandFactory;
    @Inject
    private SetCameraModeService setCameraModeService;
    @Inject
    private DrivetrainSubsystem drive;
    @Inject
    private IntakePivotSubsystem intake;
    @InjectNamed
    private DigitalInput leftPivotLimit;
    @Inject
    private ShooterSubsystem shooter;
    private boolean startedAutonCommand = false;
    @Inject
    private RightLeftSwitchLeftScale driveStraightAndZeroAuto;
    Servo cameraServo;

    @Override
    public void robotInit() {
        Injector injector = Guice.createInjector(new RobotMapModule(), new OperatorInterfaceModule(), new MiscModule(),
                new CloseableModule(), new Jsr250Module(), new CameraModule());

        injector.getInstance(OperatorInterface.class);
        injector.injectMembers(this);


        SmartDashboard.putData("Alignment", alignmentChooser);

        alignmentChooser.addDefault("Select One", Alignment.SELECT_ONE);
        alignmentChooser.addDefault("Center", Alignment.CENTER);
        alignmentChooser.addDefault("Left", Alignment.LEFT);
        alignmentChooser.addDefault("Right", Alignment.RIGHT);
        removeOldLoggingLevels();
        initLoggingLevels();
        drive.shiftLowGear();
    }

    @Override
    public void free() {
        closeableInjector.close();
    }

    @Deprecated
    private void removeOldLoggingLevels() {
        String oldLoggingLevelPrefix = "logging.level.";
        preferences.getKeys().stream().filter(key -> key.startsWith(oldLoggingLevelPrefix)).forEach(oldKey -> {
            String loggerName = oldKey.substring(oldLoggingLevelPrefix.length());
            if (loggerName.startsWith(defaultPackagePrefix)) {
                loggerName = loggerName.substring(defaultPackagePrefix.length());
            }
            String newKey = LOGGING_LEVEL_PREFIX + loggerName;
            if (!preferences.containsKey(newKey)) {
                String value = preferences.getString(oldKey, "");
                preferences.putString(newKey, value);
            }
            preferences.remove(oldKey);
        });
    }

    private void initLoggingLevels() {
        // Add chooser for default logging level
        loggingLevelChooser.addObject("All", Level.ALL);
        loggingLevelChooser.addObject("Trace", Level.TRACE);
        loggingLevelChooser.addObject("Debug", Level.DEBUG);
        loggingLevelChooser.addDefault("Info", Level.INFO);
        loggingLevelChooser.addObject("Warn", Level.WARN);
        loggingLevelChooser.addObject("Error", Level.ERROR);
        loggingLevelChooser.addObject("Off", Level.OFF);
        SmartDashboard.putData("Logging Level", loggingLevelChooser);

        // Add convenient logger names to preferences
        for (String loggerName : CONVENIENT_LOGGER_NAMES) {
            String key = LOGGING_LEVEL_PREFIX + loggerName;
            if (!preferences.containsKey(key)) {
                preferences.putString(key, "");
            }
        }
    }

    private boolean waitedForCamera = false;
    private boolean cameraInitialized = false;

    @Override
    public void robotPeriodic() {
        updateGameData();
        updateLoggerLevels();
        if (!waitedForCamera) {
            Timer.delay(1);
            waitedForCamera = true;
        }
        if (waitedForCamera && !cameraInitialized) {
            CameraServer.getInstance().startAutomaticCapture();
            cameraInitialized = true;
        }
        if (!startedAutonCommand) {
            autonomousCommand = autonomousChooser.autonomousSelector(alignmentChooser.getSelected(), ourSwitch, scale);
            logger.trace("Auton command: " + autonomousCommand);
        }

        logger.trace(DriverStation.Alliance.Red + " " +
                alignmentChooser.getSelected() + " " + ourSwitch + " " + scale);

        smartDashboardAwareRegistry.updateAll();
        smartDashboardAwareRegistry.readAll();
    }

    private void updateLoggerLevels() {
        // Set root (default) logger level based on selection
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(loggingLevelChooser.getSelected());

        // Reset all logger levels except root
        loggerContext.getLoggerList().stream().filter(logger -> !logger.getName().equals(Logger.ROOT_LOGGER_NAME))
                .forEach(logger -> logger.setLevel(null));

        // Set logger levels based on preferences
        preferences.getKeys().stream().filter(key -> key.startsWith(LOGGING_LEVEL_PREFIX)).forEach(key -> {
            // Convert preference value to logger level
            String levelString = preferences.getString(key, null);
            Level level = Level.toLevel(levelString, null);

            // Convert preference key to logger name
            String loggerName = key.substring(LOGGING_LEVEL_PREFIX.length());

            // Check if loggerName is a fully qualified class name or package name.
            boolean isValidLoggerName = false;
            try {
                boolean initializeClass = false;
                Class.forName(loggerName, initializeClass, getClass().getClassLoader());
                isValidLoggerName = true;
            } catch (ClassNotFoundException e) {
                String resourceName = loggerName.replace('.', '/');
                URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
                isValidLoggerName = resourceUrl != null;
            }
            if (!isValidLoggerName) {
                if (loggerName.isEmpty()) {
//					Handles empty logger name semicolon
                    loggerName = defaultPackage;
                } else {
                    loggerName = defaultPackagePrefix + loggerName;
                }

            }
            ch.qos.logback.classic.Logger logger = loggerContext.getLogger(loggerName);
            logger.setLevel(level);
        });
    }

    @Override
    public void disabledInit() {
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        startedAutonCommand = false;
    }

    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void autonomousInit() {
        drive.capVoltage();
        drive.enableBrakeMode();
    }

    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        if (autonomousCommand != null && !startedAutonCommand && isEnabled()) {
            autonomousCommand.start();
            System.out.println("Started Autonomous Command");
            startedAutonCommand = true;
        }
    }

    @Override
    public void teleopInit() {
        drive.uncapVoltage();
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
    }

    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void testPeriodic() {

    }

    private void updateGameData() {
        ourSwitch = GameDataParser.findSide(Target.OUR_SWITCH);
        scale = GameDataParser.findSide(Target.SCALE);
        Side opponentSwitch = GameDataParser.findSide(Target.OPPONENT_SWITCH);
        SmartDashboard.putString("Game data", ourSwitch + " " + scale + " " + opponentSwitch);
    }

    private void logDiagnostics() {
        Runtime rt = Runtime.getRuntime();
        String freeMem = ConversionUtil.humanReadableByteCount(rt.freeMemory());
        String totalMem = ConversionUtil.humanReadableByteCount(rt.totalMemory());
        String maxHeapSize = ConversionUtil.humanReadableByteCount(rt.maxMemory());
        System.out.println("Threads: " + Thread.activeCount());
        System.out
                .println(String.format("Memory: free=[%s] \t total=[%s] \t max=[%s]", freeMem, totalMem, maxHeapSize));
        logger.trace("Memory: free=[{}] \t total=[{}] \t max=[{}]", freeMem, totalMem, maxHeapSize);
    }

    private void collectDatGarbage() {

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            Runtime rt = Runtime.getRuntime();
            String freeMem = ConversionUtil.humanReadableByteCount(rt.freeMemory());
            String totalMem = ConversionUtil.humanReadableByteCount(rt.totalMemory());
            String maxHeapSize = ConversionUtil.humanReadableByteCount(rt.maxMemory());
            System.out.println(String.format("Memory Before: free=[%s] \t total=[%s] \t max=[%s]", freeMem, totalMem,
                    maxHeapSize));

            rt.gc();

            freeMem = ConversionUtil.humanReadableByteCount(rt.freeMemory());
            totalMem = ConversionUtil.humanReadableByteCount(rt.totalMemory());
            maxHeapSize = ConversionUtil.humanReadableByteCount(rt.maxMemory());
            System.out.println(
                    String.format("Memory After: free=[%s] \t total=[%s] \t max=[%s]", freeMem, totalMem, maxHeapSize));
        }, 30, 5, TimeUnit.SECONDS);
    }
}
