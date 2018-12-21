package org.usfirst.frc.team2073.robot.svc.camera;

import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import com.team2073.common.svc.camera.CameraMessageParser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.domain.CameraMessage;

public class CameraMessageParserArucoCubeImpl implements CameraMessageParser<CameraMessage>, SmartDashboardAware {
    private static final String ARUCO_ID_JSON_KEY = "ArID";
    private static final String ARUCO_ALIGN_JSON_KEY = "ArAlign";
    private static final String ARUCO_DISTANCE_JSON_KEY = "ArDist";
    private static final String CUBE_ALIGN_JSON_KEY = "CbAlign";
    private static final String CUBE_DISTANCE_JSON_KEY = "CbDist";
    private static final String CUBE_TRACK_JSON_KEY = "CbTrk";
    private static final String TIMER_JSON_KEY = "Timer";
    private String rawMsg = "Not Yet Recived";

    public CameraMessageParserArucoCubeImpl(SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        smartDashboardAwareRegistry.registerInstance(this);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public CameraMessage parseMsg(String msg) {
        try {
            this.rawMsg = msg;
            if (msg == null) {
                logger.trace("Null JSON String received.");
                return new CameraMessage();
            }

            if (!msg.startsWith("{")) {
                logger.trace("Non JSON String received: [{}]", msg);
                return new CameraMessage();
            }

            logger.trace("Parsing camera message: [{}]", msg);

            JSONObject jsonObject = new JSONObject(msg);
            CameraMessage message = new CameraMessage();
            message.setArAlign(jsonObject.getDouble(ARUCO_ALIGN_JSON_KEY));
            message.setArDist(jsonObject.getDouble(ARUCO_DISTANCE_JSON_KEY));
            message.setArID(jsonObject.getInt(ARUCO_ID_JSON_KEY));
            message.setCbAlign(jsonObject.getDouble(CUBE_ALIGN_JSON_KEY));
            message.setCbDist(jsonObject.getDouble(CUBE_DISTANCE_JSON_KEY));
            message.setCbTrk(jsonObject.getBoolean(CUBE_TRACK_JSON_KEY));
            logger.trace("CameraMessage: [{}].", message.toString());
            return message;
        } catch (JSONException e) {
            logger.error("Could not parse camera message: [{}]", msg);
            SmartDashboard.putString("JSON object error", msg);

            return new CameraMessage();
        }
    }

    @Override
    public void updateSmartDashboard() {
        SmartDashboard.putString("CameraMessage", rawMsg);
    }

    @Override
    public void readSmartDashboard() {
    }
}
