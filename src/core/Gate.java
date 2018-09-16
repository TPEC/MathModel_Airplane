package core;

public class Gate {
    public static final int TYPE_I = 1;
    public static final int TYPE_D = 2;

    public static final int AREA_CENTER = 0;
    public static final int AREA_NORTH = 1;
    public static final int AREA_SOUTH = 2;
    public static final int AREA_EAST = 3;

    private int typeArrive;
    private int typeLeave;
    private boolean nw;
    private boolean ts;
    private int id;
    private int area;

    private Plane plane = null;
    private double score = 0;
    private int planeCount = 0;
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

    public Plane getPlane(long time) {
        if (plane == null) {
            return null;
        }
        if (plane.getTimeLeave() <= time) {
            this.plane = null;
        }
        return plane;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
        planeCount++;
        long beg = Math.max(plane.getTimeArrive(), 1516377600000L);
        long end = Math.min(plane.getTimeLeave(), 1516464000000L);
        timeCount += (end - beg);
    }

    public int getPlaneCount() {
        return planeCount;
    }

    public void setPlaneCount(int planeCount) {
        this.planeCount = planeCount;
    }

    public long getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(long timeCount) {
        this.timeCount = timeCount;
    }
}
