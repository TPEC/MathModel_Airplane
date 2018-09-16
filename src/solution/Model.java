package solution;

import core.Gate;
import core.Plane;
import core.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public abstract class Model {
    public static final long MINUTE45 = 45L * 60L * 1000L;

    static final int gateSize;
    static final Gate[] gates;
    static final int[][] timeFlow;
    static final int[][] timeMetro;
    static final int[][] timeWalk;

    static final int planeSize;
    static final Plane[] planes;

    static final int userSize;
    static final User[] users;

    final List<Plane> waiting = new ArrayList<>();

    static {
        gateSize = 69;
        gates = new Gate[gateSize];
        timeFlow = new int[gateSize][gateSize];
        timeMetro = new int[gateSize][gateSize];
        timeWalk = new int[gateSize][gateSize];

        try {
            BufferedReader br = new BufferedReader(new FileReader("D:\\Develop\\Java\\Airplane\\Gates.csv"));
            br.readLine();
            for (int i = 0; i < gateSize; i++) {
                String[] l = br.readLine().split(",");
                gates[i] = new Gate(Integer.valueOf(l[3]),
                        Integer.valueOf(l[4]),
                        l[5].equals("1"),
                        l[1].equals("T"), i, l[2]);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        planeSize = 753;
        planes = new Plane[planeSize];
        try {
            BufferedReader br = new BufferedReader(new FileReader("D:\\Develop\\Java\\Airplane\\Pucks.csv"));
            br.readLine();
            for (int i = 0; i < planeSize; i++) {
                String[] l = br.readLine().split(",");
                planes[i] = new Plane(i,
                        Long.valueOf(l[13]) * 1000L,
                        Long.valueOf(l[15]) * 1000L,
                        Integer.valueOf(l[16]),
                        Integer.valueOf(l[17]),
                        l[5].equals("1"));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        userSize = 4315;
        users = new User[userSize];
        try {
            BufferedReader br = new BufferedReader(new FileReader("D:\\Develop\\Java\\Airplane\\DataS.csv"));
            for (int i = 0; i < userSize; i++) {
                String[] l = br.readLine().split(",");
                if (l[0].charAt(0) == '\uFEFF') {
                    l[0] = l[0].substring(1);
                }
                users[i] = new User(i, Integer.valueOf(l[0]), Integer.valueOf(l[1]));

                if (isInDate20(planes[users[i].getFrom()]) && isInDate20(planes[users[i].getTo()])) {
                    planes[users[i].getFrom()].getUserDn().add(users[i]);
                    planes[users[i].getTo()].getUserUp().add(users[i]);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Plane p : planes) {
            if (!isInDate20(p)) {
                continue;
            }
            System.out.println("ID:" + p.getId() + "\t" + p.getUserDn().size() + "\t" + p.getUserUp().size());
        }
    }

    static boolean isInDate20(Plane plane) {
        return new Date(plane.getTimeArrive()).getDate() == 20 || new Date(plane.getTimeLeave()).getDate() == 20;
    }

    void init() {
        for (Gate g : gates) {
            g.init();
        }
        for (Plane p : planes) {
            p.init();
        }
        waiting.clear();
    }


    public abstract void run();

    void addBuffer(Plane plane) {
        waiting.add(plane);
        waiting.sort(Comparator.comparingLong(Plane::getTimeLeave));
//        System.out.println("BUFFER\t" + plane.getId());
    }

    abstract double score(Gate gate, Plane plane);

    List<Plane> getPlanesAfterPlane_U(Plane plane, Gate gate) {
        List<Plane> l = new ArrayList<>();
        for (int i = plane.getId() + 1; i < planeSize; i++) {
            if (planes[i].getTimeArrive() < plane.getTypeLeave() && checkPlaneGate(plane, gate)) {
                l.add(planes[i]);
            }
        }
        return l;
    }

    List<Plane> getPlanesAfterPlane_C(Plane plane, Gate gate) {
        List<Plane> l = new ArrayList<>();
        for (int i = plane.getId() + 1; i < planeSize; i++) {
            if (planes[i].getTimeArrive() >= plane.getTimeLeave()) {
                break;
            }
            if (planes[i].getTimeLeave() + 45L * 60L * 1000L <= plane.getTimeLeave() && checkPlaneGate(planes[i], gate)) {
                l.add(planes[i]);
            }
        }
        return l;
    }

    List<Plane> getPlanesAfterPlane_N(Plane plane, Gate gate) {
        List<Plane> l = new ArrayList<>();
        for (int i = plane.getId() + 1; i < planeSize; i++) {
            if (planes[i].getTimeArrive() >= plane.getTypeLeave() && checkPlaneGate(plane, gate)) {
                l.add(planes[i]);
            }
        }
        return l;
    }

    static boolean checkPlaneGate(Plane plane, Gate gate) {
        return gate.isNw() == plane.isNw() && (gate.getTypeArrive() & plane.getTypeArrive()) > 0 && (gate.getTypeLeave() & plane.getTypeLeave()) > 0;
    }

    abstract List<Gate> getAvailableGates(Plane plane);

    void setPlaneInGate(Plane plane, Gate gate) {
        gate.setPlane(plane);
//        System.out.println(plane.getId() + "\t" + gate.getId()
//                    + "\t" + formatDate(plane.getTimeArrive())
//                    + "\t" + formatDate(plane.getTimeLeave()));
        System.out.println(plane.getId() + "," + gate.getId()
                + "," + (plane.getTimeArrive() - 1516377600000L) / 60000L
                + "," + (plane.getTimeLeave() - 1516377600000L) / 60000L);
    }

    static int getTimeTotal(int from, int to) {
        return timeFlow[from][to] + timeMetro[from][to] + timeWalk[from][to];
    }
}
