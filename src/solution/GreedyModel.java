package solution;

import core.Gate;
import core.Plane;

import java.util.*;

public class GreedyModel extends Model {
    private final Set<Gate>[] gateList = new Set[18];
    private final List<Plane>[] planeList = new List[18];

    @Override
    public void run() {
        for (int i = 0; i < gateList.length; i++) {
            gateList[i] = new HashSet<>();
            planeList[i] = new ArrayList<>();
        }
        for (Gate g : gates) {
            gateList[getGateType(g)].add(g);
        }

        List<Plane> PLANE_A = new ArrayList<>();
        for (Plane p : planes) {
            if (!isInDate20(p)) {
                continue;
            }
            PLANE_A.add(p);
        }
        List<Plane> PLANE_P = new ArrayList<>();
        for (int epoch = 0; epoch < 1; epoch++) {
            System.out.println("---------------EPOCH" + epoch);
            init();
            List<Plane> PLANE_T = new ArrayList<>(PLANE_A);
            PLANE_T.removeAll(PLANE_P);

            System.out.println("-------------P" + PLANE_P.size());
            doOnce(PLANE_P);
            System.out.println("SIZE:" + waiting.size());
            System.out.println("-------------T" + PLANE_T.size());
            doOnce(PLANE_T);


            System.out.println("SIZE:" + waiting.size());
            for (Plane p : waiting) {
                System.out.println(p.getId() + "\t" + p.isNw() + "\t" + p.getTypeArrive() + "\t" + p.getTypeLeave());
            }

            PLANE_P.clear();
            PLANE_P.addAll(waiting);
            PLANE_P = new ArrayList<>(new HashSet<>(PLANE_P));
        }

        for (int i = 0; i < gateSize; i++) {
            System.out.println("Gate:" + i + "\t" + gates[i].getPlaneCount() + "\t" + (double) gates[i].getTimeCount() / (double) (24L * 3600L * 1000L));
        }
    }

    private void doOnce(List<Plane> pl) {
        for (List<Plane> aPlaneList : planeList) {
            aPlaneList.clear();
        }
        waiting.addAll(pl);
        for (Plane p : pl) {
            for (int i = 0; i < gateList.length; i++) {
                if (isPlaneInType(p, i)) {
                    planeList[i].add(p);
                }
            }
        }
        for (int nw = 0; nw < 2; nw++) {
            greedy(1, 1, nw);
            greedy(2, 2, nw);

            greedy(1, 2, nw);
            greedy(2, 1, nw);

            greedy(1, 3, nw);
            greedy(2, 3, nw);
            greedy(3, 1, nw);
            greedy(3, 2, nw);

            greedy(3, 3, nw);
        }
    }

    private void greedy(int ta, int tl, int nw) {
        int t = nw * 9 + (ta - 1) * 3 + (tl - 1);
        List<Plane> pl = planeList[t];
        Set<Gate> gl = gateList[t];
        pl.sort(Comparator.comparingLong(Plane::getTimeLeave));
        while (!pl.isEmpty()) {
            List<Gate> gas = getAvailableSortGates(pl.get(0), gl);
            if (gas.isEmpty()) {
                pl.remove(0);
            } else {
                setPlaneInGate(pl.get(0), gas.get(0));
            }
        }
    }

    private static int getGateType(Gate gate) {
        int nw = gate.isNw() ? 1 : 0;
        return nw * 9 + (gate.getTypeArrive() - 1) * 3 + gate.getTypeLeave() - 1;
    }

    private static boolean isPlaneInType(Plane plane, int type) {
        boolean nw = type / 9 == 1;
        int typeLeave = type % 3 + 1;
        int typeArrive = (type / 3) % 3 + 1;
        return nw == plane.isNw() && (typeArrive & plane.getTypeArrive()) > 0 && (typeLeave & plane.getTypeLeave()) > 0;
    }

    @Override
    double score(Gate gate, Plane plane) {
        double s = 1;
        Plane p = gate.getLastPlane();
        if (p == null) {
            s *= 0;
        } else {
            s *= (double) MINUTE45 / (double) (plane.getTimeArrive() - p.getTimeLeave() + 1);
        }
        return s;
    }

    @Override
    List<Gate> getAvailableGates(Plane plane) {
        return null;
    }

    private List<Gate> getAvailableSortGates(Plane plane, Set<Gate> gates) {
        List<Gate> r = new ArrayList<>();
        for (Gate g : gates) {
            Plane p = g.getLastPlane();
            if (p == null || p.getTimeLeave() <= plane.getTimeArrive()) {
                g.setScore(score(g, plane));
                r.add(g);
            }
        }
        r.sort(((o1, o2) -> Double.compare(o2.getScore(), o1.getScore())));
        return r;
    }

    @Override
    void setPlaneInGate(Plane plane, Gate gate) {
        super.setPlaneInGate(plane, gate);
        for (List<Plane> pl : planeList) {
            pl.remove(plane);
        }
        waiting.remove(plane);
    }

    @Override
    void init() {
        super.init();
        for (List<Plane> pl : planeList) {
            pl.clear();
        }
    }
}
