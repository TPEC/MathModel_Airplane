package solution;

import core.Gate;
import core.Plane;
import util.StringUtil;

import java.util.*;

public class CutModel extends Model {
    private static final double k_type[][][] = new double[][][]{
            {
                    {10, 10, 2},
                    {10, 10, 2},
                    {2, 2, 1}
            },
            {
                    {10, 10, 2},
                    {10, 10, 2},
                    {2, 2, 1}
            }
    };

    @Override
    public void run() {
        Arrays.sort(planes, Comparator.comparingLong(Plane::getTimeLeave));
        for (int i = 0; i < planeSize; i++) {
            if (new Date(planes[i].getTimeArrive()).getDate() != 20 && new Date(planes[i].getTimeLeave()).getDate() != 20) {
                continue;
            }

            List<Gate> gs = getAvailableGatesCut(planes[i]);
            if (gs.isEmpty()) {
                addBuffer(planes[i]);
                continue;
            }
            for (Gate g : gs) {
                g.setScore(score(g, planes[i]));
            }
            gs.sort((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));

            setPlaneInGate(planes[i], gs.get(0));
        }

        for (int i = 0; i < gateSize; i++) {
            System.out.println("Gate:" + i + "\t" + gates[i].getPlaneCount() + "\t" + gates[i].getTimeCount());
        }

        System.out.println(waiting.size());
    }

    @Override
    void setPlaneInGate(Plane plane, Gate gate) {
        long cutTime = getCutTime(plane, gate);
        plane.setTimeActive(plane.getTimeArrive() + cutTime);
        gate.setPlane(plane);
    }

    private long getCutTime(Plane plane, Gate gate) {
        long cutTime = 0;
        if (gate.getPlane() != null) {
            if (gate.getPlane().getTimeLeave() > plane.getTimeArrive()) {
                cutTime = gate.getPlane().getTimeLeave() - plane.getTimeArrive();
            }
        }
        return cutTime;
    }

    @Override
    double score(Gate gate, Plane plane) {
        double s = 1;
        double cutPct = (double) getCutTime(plane, gate) / (double) plane.getStayTime();

        if (gate.getPlaneCount() > 0) {
            s *= 2;
        }

        s *= 1.5 - cutPct;
//        s *= cutPct + 1;

        int nw = gate.isNw() ? 1 : 0;
        s *= k_type[nw][gate.getTypeArrive() - 1][gate.getTypeLeave() - 1];

        return s;
    }

    @Override
    void addBuffer(Plane plane) {
        super.addBuffer(plane);
        System.out.println("AB:" + plane.getId() + "\t" + StringUtil.formatDate(plane.getTimeLeave()));
    }

    private List<Gate> getAvailableGatesCut(Plane plane) {
        List<Gate> l = new ArrayList<>();
        for (int i = 0; i < gateSize; i++) {
            if (gates[i].getPlane() == null) {
                if (checkPlaneGate(plane, gates[i])) {
                    l.add(gates[i]);
                }
            } else {
                if (plane.getTimeLeave() - gates[i].getPlane().getTimeLeave() >= MINUTE45) {
                    if (checkPlaneGate(plane, gates[i])) {
                        l.add(gates[i]);
                    }
                }
            }
        }
        return l;
    }
}
