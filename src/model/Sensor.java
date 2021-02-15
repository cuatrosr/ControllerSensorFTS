package model;

public class Sensor {

    private int address;
    private int status;
    private boolean sync;

    public Sensor(int address, int status, boolean sync) {
        this.address = address;
        this.status = status;
        this.sync = sync;
    }

    public int getAddress() {
        return address;
    }

    public int getStatus() {
        return status;
    }

    public boolean getSync() {
        return sync;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }
}
