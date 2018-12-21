package org.usfirst.frc.team2073.robot.conf;

public abstract class AppConstants {
    public abstract class Controllers {
        public abstract class PowerStick {
            public static final int PORT = 1;

            public abstract class ButtonPorts {
                public static final int LEFT = 4;
                public static final int CENTER = 3;
                public static final int BOTTOM = 2;
                public static final int TRIGGER = 1;
                public static final int RIGHT = 5;
            }
        }

        public abstract class DriveWheel {
            public static final int PORT = 0;

            public abstract class ButtonPorts {
                public static final int LPADDLE = 1;
                public static final int RPADDLE = 3;
            }
        }

        public abstract class Xbox {
            public static final int PORT = 2;

            public abstract class ButtonPorts {
                public static final int A = 1;
                public static final int B = 2;
                public static final int X = 3;
                public static final int Y = 4;
                public static final int L1 = 5;
                public static final int R1 = 6;
                public static final int BACK = 7;
                public static final int START = 8;
            }

            public abstract class Axes {
                public static final int LEFT_X = 0;
                public static final int LEFT_Y = 1;
                public static final int LEFT_TRIGGER = 2;
                public static final int RIGHT_TRIGGER = 3;
                public static final int RIGHT_X = 4;
                public static final int RIGHT_Y = 5;

            }
        }
    }

    public abstract class RobotPorts {

        // Drivetrain
        public static final int DRIVE_LEFT_MOTOR = 0;
        public static final int DRIVE_LEFT_MOTOR_SLAVE = 0;
        public static final int DRIVE_RIGHT_MOTOR = 1;
        public static final int DRIVE_RIGHT_MOTOR_SLAVE = 1;
        public static final int DRIVE_SOLENOID = 1;


        //PTO
        public static final int TALONTACH_PTO = 3;
        public static final int PTO_SOLENOID = 0;


        // Intake
        public static final int INTAKE_PIVOT_LEFT = 3;
        public static final int INTAKE_PIVOT_RIGHT = 2;
        public static final int INTAKE_SIDE_LEFT = 2;
        public static final int INTAKE_SIDE_RIGHT = 1;
        public static final int INTAKE_BOTTOM = 0;
        public static final int LEFT_PIVOT_LIMIT = 8;
        public static final int RIGHT_PIVOT_LIMIT = 9;


        // Shooter
        public static final int SHOOTER_PIVOT = 4;
        public static final int SHOOTER_LEFT = 2;
        public static final int SHOOTER_RIGHT = 3;
        public static final int SHOOTER_PIVOT_LIMIT = 1;
        public static final int SHOOTER_HOLDING_SENSOR = 0;


        // Elevator
        public static final int ELEVATOR_MASTER = 5;
        public static final int ELEVATOR_SLAVE = 5;
        public static final int ELEVATOR_MAX = 5;
        public static final int ELEVATOR_MIN = 4;
        public static final int ELEVATOR_BRAKE_SOLENOID = 2;
        public static final int PIGEON = 0;


    }

    public abstract class DashboardKeys {
        public static final String INVERSE = "Inverse";
        public static final String SENSE = "Sense";
        public static final String RPM = "RPM";
        public static final String SET_F = "Set F";
        public static final String DRIVETRAIN_P = "Drive P";
        public static final String DRIVETRAIN_I = "Drive I";
        public static final String DRIVETRAIN_D = "Drive D";
        public static final String FGAIN = "Fgain";
        public static final String RIGHT_DRIVE_F_GAIN = "Right Drive F Gain";
        public static final String LEFT_DRIVE_F_GAIN = "Left Drive F Gain";
        public static final String INTAKE_P_GAIN = "Intake P";
        public static final String INTAKE_I_GAIN = "Intake I";
        public static final String INTAKE_D_GAIN = "Intake D";
        public static final String CAMERA_P = "Camera P";
        public static final String CAMERA_I = "Camera I";
        public static final String CAMERA_D = "Camera D";
    }

    public abstract class Defaults {
        public static final double DRIVETRAIN_FGAIN = 0.;
        public static final double LEFT_DRIVE_F_GAIN = 0;
        public static final double RIGHT_DRIVE_F_GAIN = 0;
        public static final boolean LEFT_MOTOR_DEFAULT_DIRECTION = false;
        public static final boolean RIGHT_MOTOR_DEFAULT_DIRECTION = false;
        public static final boolean LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION = false;
        public static final boolean RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION = false;
        public static final double DRIVETRAIN_P_GAIN = 1.675;
        public static final double DRIVETRAIN_I_GAIN = 0;
        public static final double DRIVETRAIN_D_GAIN = 20;
        public static final int MINIMUM_POINTS_TO_RUN = 40;
        public static final double INTAKE_P_GAIN = 0.4;
        public static final double INTAKE_I_GAIN = 0.0005;
        public static final double INTAKE_D_GAIN = 10;
        public static final double INTAKE_P_GAIN_ZEROING = 0.2;
        public static final double INTAKE_I_GAIN_ZEROING = 0;
        public static final double INTAKE_D_GAIN_ZEROING = 30;

    }

    public abstract class Camera {
        public static final double FOLLOW_P = 0;
        public static final double FOLLOW_I = 0;
        public static final double FOLLOW_D = 0;

        public abstract class Mode {
            public static final int NONE = 0;
            public static final int CUBE = 1;
            public static final int ARUCO = 2;
        }
    }

    public abstract class Subsystems {


        public abstract class Drivetrain {
            public static final String NAME = "Drivetrain";
            public static final double WHEEL_CIRCUMFERENCE = 4 * Math.PI;
            public static final double AUTONOMOUS_MAX_VELOCITY_HIGH_GEAR = 5600;
            public static final double AUTONOMOUS_MAX_VELOCITY_LOW_GEAR = 3100.;
            public static final double AUTONOMOUS_MAX_ACCELERATION = 15;
            public static final double ROBOT_WIDTH = 25.5;
            public static final double HIGH_GEAR_RATIO = 4.89;
            public static final double LOW_GEAR_RATIO = 15.41;
            public static final double ENCODER_EDGES_PER_REVOLUTION = 4362;

            public abstract class ComponentNames {
                public static final String LEFT_MOTOR = "Left Motor";
                public static final String LEFT_MOTOR_SLAVE = "Left Motor Slave";
                public static final String RIGHT_MOTOR = "Right Motor";
                public static final String RIGHT_MOTOR_SLAVE = "Right Motor Slave";
                public static final String SOLENOID_1 = "Solenoid 1";
            }
        }

        public abstract class Intake {
            public static final String NAME = "Intake";
            public static final double PIVOT_MAX_VELOCITY = 830;
            public static final double PIVOT_MAX_ACCELERATION = 20;
            public static final double ENCODER_EDGES_PER_REVOLUTION = 4096;
            public static final double F_GAIN = .25;
            public static final double PIVOT_GEAR_RATIO = 48. / 25;
            public static final double MAX_PERCENT_OUT = .75;
        }

        public abstract class Shooter {
            public static final String NAME = "Shooter";
            public static final double PIVOT_MAX_VELOCITY = 4300;
            public static final double PIVOT_MAX_ACCELERATION = 40;
            public static final double PIVOT_F_GAIN = .25;
            public static final double PIVOT_P_GAIN = .0125;
            public static final double PIVOT_I_GAIN = 0.005;
            public static final double PIVOT_D_GAIN = 50;

            public static final double ENCODER_EDGES_PER_REVOLUTION = 2048;
            public static final double PIVOT_TO_ENCODER_RATIO = 1;
            public static final boolean PIVOT_DEFAULT_DIRECTION = true;
            public static final double PIVOT_HOLD_P = 6.5;
            public static final double PIVOT_HOLD_I = 0;
            public static final double PIVOT_HOLD_D = 12;

            public abstract class PivotAngles {
                public static final double INTAKE = -10;
                public static final double FRONT_FLAT = 10;
                public static final double FRONT_SHOOT = 45;
                public static final double BACK_SHOOT = 110;
                public static final double LOW_SCALE = 10;

            }

        }

        public abstract class Elevator {
            public static final String F_GAIN_KEY = "Elevator F Gain";
            public static final boolean MASTER_DEFAULT_DIRECTION = false;

            public static final double F_GAIN = 0;
            public static final double P_GAIN_DOWN = .8;
            public static final double I_GAIN_DOWN = 0;
            public static final double D_GAIN_DOWN = 70;

            public static final double P_GAIN_UP = 3.9;
            public static final double I_GAIN_UP = 0;
            public static final double D_GAIN_UP = 140;
            public static final double MAX_VELOCITY = 30000;
            public static final double MAX_ACCELERATION = 10;

            public static final boolean SLAVE_DEFAULT_DIRECTION = true;
            public static final double MAX_TRAVEL = 29;
            public static final double LOW_SCALE = 24;
            public static final double PIVOT_HEIGHT = MAX_TRAVEL - .001;
            public static final double SWITCH_HEIGHT = 11;
            public static final double BOTTOM_HEIGHT = 0;
            public static final String P_GAIN_KEY = "Elevator P Gain";
            public static final String I_GAIN_KEY = "Elevator I Gain";
            public static final String D_GAIN_KEY = "Elevator D Gain";
            public static final String NAME = "Elevator";
            public static final double ENCODER_EDGES_PER_INCH_OF_TRAVEL = 1350;

        }
    }

    public abstract class Diagnostics {
        public static final double UNSAFE_BATTERY_VOLTAGE = 8.0;
    }
}
