package org.usfirst.frc.team2073.robot.util;

import edu.wpi.first.wpilibj.DriverStation;

public class GameDataParser {

    public enum Side {
        LEFT,
        RIGHT,
        UNKNOWN
    }

    public enum Target {
        OUR_SWITCH(0),
        SCALE(1),
        OPPONENT_SWITCH(2);

        private int charPosition;

        Target(int charPosition) {
            this.charPosition = charPosition;
        }

        /**
         * Returns 0,1, or 2 which are indexes for our switch, scale, etc.
         *
         * @return integer
         */
        public int getCharPosition() {
            return this.charPosition;
        }
    }

    private static String getDataFromDriverStation() {
        return DriverStation.getInstance().getGameSpecificMessage();
    }


    /**
     * Essentially get values from DriveStation and returning LEFT, RIGHT, or UNKNOWN.
     * Pass in Target only.
     *
     * @param target
     * @return Side(enum)
     */
    public static Side findSide(Target target) {
        String data = getDataFromDriverStation();

        if (data == null || data == "" || data.length() < target.getCharPosition() + 1) {
            return Side.UNKNOWN;
        } else {
            return parseCharToSide(data.charAt(target.getCharPosition()));
        }
    }


    public static Side parseCharToSide(char side) {
        side = Character.toUpperCase(side);
        if (side == 'L') {
            return Side.LEFT;
        } else if (side == 'R') {
            return Side.RIGHT;
        } else {
            return Side.UNKNOWN;
        }
    }

}
