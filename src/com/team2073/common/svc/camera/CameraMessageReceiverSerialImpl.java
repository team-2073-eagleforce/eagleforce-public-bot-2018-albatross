package com.team2073.common.svc.camera;

import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.ctx.SerialPortProvider;
import org.usfirst.frc.team2073.robot.domain.CameraMessage;
import org.usfirst.frc.team2073.robot.util.CheckedProviderUtils;

public class CameraMessageReceiverSerialImpl implements CameraMessageReceiver, SmartDashboardAware {
    public static final String STATE_SMARTDASHBOARD_KEY = "Camera Message Receiver State";
    private String stateSmartdashboardKey = STATE_SMARTDASHBOARD_KEY;
    private static final double CIRCUIT_BREAKER_MULTIPLIER = 2;
    private static final double CIRCUIT_BREAKER_MAX_DELAY = 1;
    private static final double DEFAULT_DELAY = .03;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private State state = State.CONSTRUCTING;
    private SerialPort serialPort;

    enum State {
        CONSTRUCTING,
        NO_SERIAL_PORT_DEFINED,
        RECEIVING_MESSAGE,
        WAITING
    }

    public CameraMessageReceiverSerialImpl(SerialPort serialPort) {
        if (serialPort == null) {
            state = State.NO_SERIAL_PORT_DEFINED;
            logger.error("Camera serial port cannot be null. No Camera messages will be received!");
            return;
        }

        this.serialPort = serialPort;
        state = State.WAITING;
    }

    public CameraMessageReceiverSerialImpl(SerialPortProvider serialPortProvider, SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        this(CheckedProviderUtils.getOrNull(serialPortProvider));
        smartDashboardAwareRegistry.registerInstance(this);
    }

    // CameraMessageReceiver methods
    // ====================================================================================================
    @Override
    public String receiveMsg() {
        int attempts = 1;
        double circuitBreakerDelay = DEFAULT_DELAY;
        if (serialPort == null)
            return "";
        state = State.RECEIVING_MESSAGE;
        serialPort.writeString(CameraMessage.REQUEST_MESSAGE);
        String json = serialPort.readString();
        while (json == null || json.isEmpty() || json.equals("OK")) {
            attempts++;
            Timer.delay(circuitBreakerDelay);
            json = serialPort.readString();
            circuitBreakerDelay = Math.min(circuitBreakerDelay * CIRCUIT_BREAKER_MULTIPLIER, CIRCUIT_BREAKER_MAX_DELAY);
            logger.trace("TEMP: Circuit Breaker Delay [{}].", circuitBreakerDelay);
        }
        Timer.delay(DEFAULT_DELAY);
        logger.trace("JSON Receive Attempts [{}]. Total delay [{}]. JSON: [{}]", attempts, circuitBreakerDelay, json);
        state = State.WAITING;
        return json == null ? "" : json;
    }

    // SmartDashboardAware methods
    // ====================================================================================================
    @Override
    public void updateSmartDashboard() {
        SmartDashboard.putString(stateSmartdashboardKey, state.toString());
    }

    @Override
    public void readSmartDashboard() {
    }

    // Getters/Setters
    // ====================================================================================================
    public String getStateSmartdashboardKey() {
        return stateSmartdashboardKey;
    }

    public void setStateSmartdashboardKey(String stateSmartdashboardKey) {
        this.stateSmartdashboardKey = stateSmartdashboardKey;
    }
}
