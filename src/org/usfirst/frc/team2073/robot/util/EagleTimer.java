package org.usfirst.frc.team2073.robot.util;

public class EagleTimer {
    private long startTime;
    private TimerState state;

    public EagleTimer() {
        state = TimerState.DOING_NOTHING;
    }

    enum TimerState {
        RESET, STARTED, DOING_NOTHING
    }

    public void startTimer() {
        startTime = System.currentTimeMillis();
        state = TimerState.STARTED;
    }

    public boolean hasWaited(long timePassedInMiliseconds) {
        long currTime = System.currentTimeMillis();
        return startTime + timePassedInMiliseconds < currTime && state != TimerState.RESET;

    }

    public void reset() {
        state = TimerState.RESET;
    }
}
