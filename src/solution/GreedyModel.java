package solution;

import core.DoubleLong;
import core.Gate;
import core.Plane;
import core.User;

import java.util.*;

public class GreedyModel extends Model {
    private final Set<Gate>[] gateList = new Set[18];
    private final List<Plane>[] planeList = new List[18];

    private int[] sgi = new int[gateSize];

    @Override
    public void run() {
        for (int i = 0; i < sgi.length; i++) {
            sgi[i] = i;
        }
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
        System.out.println("-------------T" + PLANE_A.size());
        doOnce(PLANE_A);


        System.out.println("SIZE:" + waiting.size());
        for (Plane p : waiting) {
            System.out.println(p.getId() + "\t" + p.isNw() + "\t" + p.getTypeArrive() + "\t" + p.getTypeLeave());
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

    public DoubleLong checkUser2() {
        long l = 0;
        double s = 0;
        for (User u : users) {
            if (!isInDate20(planes[u.getFrom()]) || !isInDate20(planes[u.getTo()])) {
                continue;
            }
            u.init();
            if (planes[u.getFrom()].getGate() == null || planes[u.getTo()].getGate() == null) {
                continue;
            }

            long maxt = planes[u.getTo()].getTimeLeave() - planes[u.getFrom()].getTimeArrive() - MINUTE45;
            long t = timeFlow(
                    getSGate(planes[u.getFrom()].getGate()).isTs(),
                    getSGate(planes[u.getTo()].getGate()).isTs(),
                    planes[u.getFrom()].getTypeArrive(),
                    planes[u.getTo()].getTypeLeave()
            );
            u.setTimepp(t <= maxt ? t : 360 * MINUTE);

            t += timeMetro(
                    getSGate(planes[u.getFrom()].getGate()).isTs(),
                    getSGate(planes[u.getTo()].getGate()).isTs(),
                    planes[u.getFrom()].getTypeArrive(),
                    planes[u.getTo()].getTypeLeave()
            );
            t += timeWalk
                    [getSGate(planes[u.getFrom()].getGate()).getTypeArrive()]
                    [getSGate(planes[u.getTo()].getGate()).getTypeLeave()];
            u.setTimejz((double) (t <= maxt ? t : 360 * MINUTE) / (double) maxt);

//            System.out.println("User:" + u.getId() + "\tFrom:" + u.getFrom() + "\tTo:" + u.getTo() + "\tAmount:" + u.getAmount() + "\t" + u.getTimepp() / MINUTE + "\t" + u.getTimejz());
            l += u.getTimepp() / MINUTE / 5 * u.getAmount();
            s += u.getTimejz() * u.getAmount();
        }
        return new DoubleLong(s, l);
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

    private Gate getSGate(Gate raw) {
        return gates[sgi[raw.getId()]];
    }

    public int[] getSgi() {
        return sgi;
    }

    public void setSgi(int[] sgi) {
        this.sgi = sgi;
    }

    private int swk;
    private int sw1;
    private int sw2;
    private List<Gate> gateListL = new ArrayList<>();

    public void beforeSwitch() {
        swk = 0;
        sw1 = 0;
        sw2 = 0;
    }

    public boolean switchGate() {
        int k = swk;
        swk++;
        for (Set<Gate> g : gateList) {
            if (g.size() >= 2) {
                int s2 = g.size() * (g.size() - 1) / 2;
                if (k < s2) {
                    if (k == 0) {
                        sw1 = 0;
                        sw2 = 0;
                        gateListL.clear();
                        gateListL.addAll(g);
                    }
                    sw2++;
                    if (sw2 >= g.size()) {
                        sw1++;
                        sw2 = sw1 + 1;
                    }
                    int c = sgi[gateListL.get(sw1).getId()];
                    sgi[gateListL.get(sw1).getId()] = sgi[gateListL.get(sw2).getId()];
                    sgi[gateListL.get(sw2).getId()] = c;
                    return true;
                } else {
                    k -= s2;
                }
            }
        }
        return false;
    }

    public void resetSwitch() {
        for (int i = 0; i < sgi.length; i++) {
            sgi[i] = i;
        }
    }

    private List<Gate> getPlaneSwitch1_0(Plane plane) {
        List<Gate> r = new ArrayList<>();
        for (Gate g : gates) {
            if (g.canSetIn(plane)) {
                r.add(g);
            }
        }
        return r;
    }

    private DoubleLong best = new DoubleLong();

    private Gate getBestGate(Plane plane, int problem) {
        List<Gate> gs = getPlaneSwitch1_0(plane);
        if (gs.isEmpty()) {
            return null;
        }
        best = checkUser2();
        Gate bestGate = null;
        Gate gOrigin = plane.getGate();
        gOrigin.removePlane(plane);
        for (Gate g : gs) {
            g.setPlane(plane);
            DoubleLong dl = checkUser2();
            g.removePlane(plane);
            if (problem == 2) {
                if (dl.l < best.l) {
                    best = dl;
                    bestGate = g;
                }
            } else {
                if (dl.d < best.d) {
                    best = dl;
                    bestGate = g;
                }
            }
        }
        gOrigin.setPlane(plane);
        return bestGate;
    }

    public boolean switchPlane_10(int problem) {
        boolean flag = false;
        for (int i = 0; i < planeSize; i++) {
            Plane p = planes[i];
            if (!isInDate20(p)) {
                continue;
            }
            if (p.getGate() == null) {
                continue;
            }
            if (p.getUserUp().size() == 0 && p.getUserDn().size() == 0) {
                continue;
            }
            Gate g = getBestGate(p, problem);
            if (g != null) {
                p.getGate().removePlane(p);
                g.setPlane(p);
                System.out.println("SWITCH plane" + p.getId() + " to gate" + sgi[g.getId()] + "\t" + best.d);
                i = 0;
                flag = true;
            }
        }
        return flag;
    }
}
