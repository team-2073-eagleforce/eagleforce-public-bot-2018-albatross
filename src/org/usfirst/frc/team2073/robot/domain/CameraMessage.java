package org.usfirst.frc.team2073.robot.domain;

public class CameraMessage {

    public static final String REQUEST_MESSAGE = "4\n";
    private int ArID = -2;
    private boolean CbTrk;
    private double ArAlign = -2;
    private double ArDist = -2;
    private double CbAlign = -2;
    private double CbDist = -2;
    private double Timer = -2;

    public int getArID() {
        return ArID;
    }

    public void setArID(int arID) {
        ArID = arID;
    }

    public boolean isCbTrk() {
        return CbTrk;
    }

    public void setCbTrk(boolean cbTrk) {
        CbTrk = cbTrk;
    }

    public double getArAlign() {
        return ArAlign;
    }

    public void setArAlign(double arAlign) {
        ArAlign = arAlign;
    }

    public double getArDist() {
        return ArDist;
    }

    public void setArDist(double arDist) {
        ArDist = arDist;
    }

    public double getCbAlign() {
        return CbAlign;
    }

    public void setCbAlign(double cbAlign) {
        CbAlign = cbAlign;
    }

    public double getCbDist() {
        return CbDist;
    }

    public void setCbDist(double cbDist) {
        CbDist = cbDist;
    }

    public double getTimer() {
        return Timer;
    }

    public void setTimer(double timer) {
        Timer = timer;
    }

    @Override
    public String toString() {
        return "CameraMessage [ArID=" + ArID + ", CbTrk=" + CbTrk + ", ArAlign=" + ArAlign + ", ArDist=" + ArDist
                + ", CbAlign=" + CbAlign + ", CbDist=" + CbDist + ", Timer=" + Timer + "]";
    }
}
