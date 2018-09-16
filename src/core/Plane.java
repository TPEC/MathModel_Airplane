package core;

import java.util.HashSet;
import java.util.Set;

public class Plane {
    private int id;
    private long timeArrive;
    private long timeLeave;
    private int typeArrive;
    private int typeLeave;
    private long timeActive;
    private boolean nw;

    private final Set<User> userUp = new HashSet<>();
    private final Set<User> userDn = new HashSet<>();

    public Plane(int id, long timeArrive, long timeLeave, int typeArrive, int typeLeave, boolean nw) {
        this.id = id;
        this.timeArrive = timeArrive;
//        this.timeArrive = timeLeave - 45L * 60L * 1000L;
        this.timeLeave = timeLeave;
        this.typeArrive = typeArrive;
        this.typeLeave = typeLeave;
        this.nw = nw;
    }

    public long getStayTime() {
        return this.timeLeave - this.timeArrive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeArrive() {
        return timeArrive;
    }

    public void setTimeArrive(long timeArrive) {
        this.timeArrive = timeArrive;
    }

    public long getTimeLeave() {
        return timeLeave;
    }

    public void setTimeLeave(long timeLeave) {
        this.timeLeave = timeLeave;
    }

    public int getTypeArrive() {
        return typeArrive;
    }

    public void setTypeArrive(int typeArrive) {
        this.typeArrive = typeArrive;
    }

    public int getTypeLeave() {
        return typeLeave;
    }

    public void setTypeLeave(int typeLeave) {
        this.typeLeave = typeLeave;
    }

    public boolean isNw() {
        return nw;
    }

    public void setNw(boolean nw) {
        this.nw = nw;
    }

    public long getTimeActive() {
        return timeActive;
    }

    public void setTimeActive(long timeActive) {
        this.timeActive = timeActive;
    }

    public void init() {
        this.timeActive = -1;
    }

    public Set<User> getUserUp() {
        return userUp;
    }

    public Set<User> getUserDn() {
        return userDn;
    }
}
