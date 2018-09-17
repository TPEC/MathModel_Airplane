package core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static solution.Model.MINUTE45;

public class Gate {
    public static final int TYPE_I = 1;
    public static final int TYPE_D = 2;

    public static final int AREA_CENTER = 1;
    public static final int AREA_NORTH = 0;
    public static final int AREA_SOUTH = 2;
    public static final int AREA_EAST = 3;

    private int typeArrive;
    private int typeLeave;
    private boolean nw;
    private boolean ts;
    private int id;
    private int area;

    private final List<Plane> planes = new ArrayList<>();
    private double score = 0;
    private long timeCount = 0;

    public Gate(int typeArrive, int typeLeave, boolean nw, boolean ts, int id, String area) {
        this.typeArrive = typeArrive;
        this.typeLeave = typeLeave;
        this.nw = nw;
        this.ts = ts;
        this.id = id;
        switch (area) {
            case "Center":
                this.area = AREA_CENTER;
                break;
            case "North":
                this.area = AREA_NORTH;
                break;
            case "South":
                this.area = AREA_SOUTH;
                break;
            default:
                this.area = AREA_EAST;
                break;
        }
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

    public boolean isTs() {
        return ts;
    }

    public void setTs(boolean ts) {
        this.ts = ts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Plane getLastPlane() {
        return planes.isEmpty() ? null : planes.get(planes.size() - 1);
    }

    public boolean canSetIn(Plane plane) {
        for (Plane p : planes) {
            if ((plane.getTimeArrive() >= p.getTimeArrive() && plane.getTimeArrive() < p.getTimeLeave())
                    || (plane.getTimeLeave() > p.getTimeArrive() && plane.getTimeLeave() <= p.getTimeLeave())) {
                return false;
            }
        }
        return true;
    }

    public Plane getPlaneIn(Plane plane) {
        return getPlaneIn(plane.getTimeLeave() - MINUTE45, plane.getTimeLeave());
    }

    public Plane getPlaneIn(long from, long to) {
        for (Plane p : planes) {
            if (p.getTimeArrive() < to) {
                if (p.getTimeLeave() > from) {
                    return p;
                }
            } else {
                break;
            }
        }
        return null;
    }

    public void setPlane(Plane plane) {
        planes.add(plane);
        planes.sort(Comparator.comparingLong(Plane::getTimeLeave));
        plane.setGate(this);
        long beg = Math.max(plane.getTimeArrive(), 1516377600000L);
        long end = Math.min(plane.getTimeLeave(), 1516464000000L);
        timeCount += end - beg;
    }

    public void removePlane(Plane plane) {
        if (planes.remove(plane)) {
            plane.setGate(null);
            long beg = Math.max(plane.getTimeArrive(), 1516377600000L);
            long end = Math.min(plane.getTimeLeave(), 1516464000000L);
            timeCount -= end - beg;
        }
    }

    public int getPlaneCount() {
        return planes.size();
    }

    public long getTimeCount() {
        return timeCount;
    }

    public void init() {
        this.planes.clear();
        this.score = 0;
        this.timeCount = 0;
    }
}
