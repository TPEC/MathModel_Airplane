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
        Set<Plane> prePlanes = new HashSet<>();
        for (int epoch = 0; epoch < 10; epoch++) {
            System.out.println("--EPOCH:" + epoch + "--");
            prePlanes.addAll(waiting);
            List<Plane> prePlanesSorted = new ArrayList<>(prePlanes);
            prePlanesSorted.sort(Comparator.comparingLong(Plane::getTimeLeave));

            System.out.println("P:" + prePlanesSorted.size());

            init();

            for (Plane p : prePlanesSorted) {
                List<Gate> gs = getAvailableGates(p);
                if (gs.isEmpty()) {
                    addBuffer(p);
                    continue;
                }
                for (Gate g : gs) {
                    g.setScore(score(g, p));
                }
                gs.sort((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));

                setPlaneInGate(p, gs.get(0));
            }


            for (int i = 0; i < planeSize; i++) {
                if (new Date(planes[i].getTimeArrive()).getDate() != 20 && new Date(planes[i].getTimeLeave()).getDate() != 20) {
                    continue;
                }
                if (prePlanes.contains(planes[i])) {
                    continue;
                }


                List<Gate> gs = getAvailableGates(planes[i]);
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

//            for (int i = 0; i < gateSize; i++) {
//                System.out.println("Gate:" + i + "\t" + gates[i].getPlaneCount());
//            }

            System.out.println(waiting.size());
        }
    }

    @Override
    void setPlaneInGate(Plane plane, Gate gate) {
//        long cutTime = getCutTime(plane, gate);
//        plane.setTimeActive(plane.getTimeArrive() + cutTime);
        gate.setPlane(plane);
    }

    private long getCutTime(Plane plane, Gate gate) {
        long cutTime = 0;
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

    @Override
    List<Gate> getAvailableGates(Plane plane) {
        List<Gate> l = new ArrayList<>();
        for (int i = 0; i < gateSize; i++) {
            if (gates[i].getPlaneIn(plane) == null) {
                if (checkPlaneGate(plane, gates[i])) {
                    l.add(gates[i]);
                }
            }
        }
        return l;
    }
}
