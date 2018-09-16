package solution;

import core.Gate;
import core.Plane;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class EnumModel extends Model {
    private static final double k_type[][] = new double[][]{
            {0, 0, 0, 0},
            {0, 10, 10, 2},
            {0, 10, 10, 2},
            {0, 2, 2, 1},
    };

    private boolean check(long time) {
        boolean flag = false;
        for (int j = 0; j < gateSize; j++) {
            if (gates[j].getPlane() != null) {
                Plane p = gates[j].getPlane();
                if (gates[j].getPlane().getTimeLeave() <= time) {
                    for (int k = 0; k < waiting.size(); k++) {
                        if (checkPlaneGate(waiting.get(k), gates[j])) {
                            if (p.getTimeLeave() + 45L * 60L * 1000L < waiting.get(k).getTimeLeave()) {
//                                System.out.println("MOVE\t" + waiting.get(k).getId());
                                setPlaneInGate(waiting.get(k), gates[j]);
                                waiting.remove(k);
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            } else {
                for (int k = 0; k < waiting.size(); k++) {
                    if (checkPlaneGate(waiting.get(k), gates[j])) {
//                        System.out.println("MOVE\t" + waiting.get(k).getId());
                        setPlaneInGate(waiting.get(k), gates[j]);
                        waiting.remove(k);
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    @Override
    public void run() {
        for (int i = 0; i < planeSize; i++) {
            while (check(planes[i].getTimeArrive())) ;

            if (new Date(planes[i].getTimeArrive()).getDate() != 20 && new Date(planes[i].getTimeLeave()).getDate() != 20) {
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

            boolean flag = false;
            for (int j = 0; j < gs.size(); j++) {
                List<Plane> pu = getPlanesAfterPlane_C(planes[i], gs.get(j));
                if (!pu.isEmpty()) {
//                    System.out.println("NX");
                    addBuffer(planes[i]);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                setPlaneInGate(planes[i], gs.get(0));
            }
        }

        for (int i = 0; i < gateSize; i++) {
            System.out.println("Gate:" + i + "\t" + gates[i].getPlaneCount() + "\t" + gates[i].getTimeCount());
        }

        System.out.println(waiting.size());
    }

    @Override
    double score(Gate gate, Plane plane) {
        double s = 1;
        if (gate.getPlaneCount() > 0) {
            s *= 10;
        }
        s *= k_type[gate.getTypeArrive()][gate.getTypeLeave()];
        List<Plane> pu = getPlanesAfterPlane_U(plane, gate);
        s *= 10.0 / (pu.size() + 1);
//        List<Plane> pn = getPlanesAfterPlane_N(plane, gate);
//        if (!pn.isEmpty()) {
//            double k = 60.0 * 3600.0 * 1000.0 / (pn.get(0).getTimeArrive() - plane.getTypeLeave() + 1);
//            System.out.println(k);
//            s *= k;
//        }
        s += new Random().nextDouble() * 0.01;
        return s;
    }
}
