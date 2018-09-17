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

import static core.Gate.TYPE_D;

public abstract class Model {
    public static final long MINUTE45 = 45L * 60L * 1000L;
    public static final long MINUTE = 60L * 1000L;

    static final int gateSize;
    static final Gate[] gates;
    static final long[][] timeWalk;

    static final int planeSize;
    static final Plane[] planes;

    static final int userSize;
    static final List<User> users;

    final List<Plane> waiting = new ArrayList<>();

    static {
        String folder = "./";

        gateSize = 69;
        gates = new Gate[gateSize];
        timeWalk = new long[gateSize][gateSize];

        try {
            BufferedReader br = new BufferedReader(new FileReader(folder + "Gates.csv"));
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


        long t[][] = new long[][]{
                {10, 15, 20, 25, 20, 25, 25},
                {15, 10, 15, 20, 15, 20, 20},
                {20, 15, 10, 25, 20, 25, 25},
                {25, 20, 25, 10, 15, 20, 20},
                {20, 15, 20, 15, 10, 15, 15},
                {25, 20, 25, 20, 15, 10, 20},
                {25, 20, 25, 20, 15, 20, 10}
        };

        for (int i = 0; i < gateSize; i++) {
            Gate gi = gates[i];
            for (int j = 0; j < gateSize; j++) {
                Gate gj = gates[j];
                timeWalk[i][j] = t[(gi.isTs() ? 0 : 1) * 3 + gi.getArea()][(gj.isTs() ? 0 : 1) * 3 + gj.getArea()] * MINUTE;
            }
        }


        planeSize = 753;
        planes = new Plane[planeSize];
        try {
            BufferedReader br = new BufferedReader(new FileReader(folder + "Pucks.csv"));
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
        users = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(folder + "DataS.csv"));
            br.readLine();
            for (int i = 0; i < userSize; i++) {
                String[] l = br.readLine().split(",");
                if (l[0].charAt(0) == '\uFEFF') {
                    l[0] = l[0].substring(1);
                }
                int from = Integer.valueOf(l[1]);
                int to = Integer.valueOf(l[2]);
                boolean userflag = false;
                for (User user : users) {
                    if (user.getFrom() == from && user.getTo() == to) {
                        user.setAmount(user.getAmount() + Integer.valueOf(l[0]));
                        userflag = true;
                        break;
                    }
                }
                if (!userflag) {
                    User user = new User(users.size(), from, to, Integer.valueOf(l[0]));
                    users.add(user);
                    if (isInDate20(planes[user.getFrom()]) && isInDate20(planes[user.getTo()])) {
                        planes[user.getFrom()].getUserDn().add(user);
                        planes[user.getTo()].getUserUp().add(user);
                    }
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

    static long timeFlow(boolean ft, boolean tt, int fdi, int tdi) {
        boolean fd = fdi == TYPE_D;
        boolean td = tdi == TYPE_D;
        if (td && tt && fd && ft) {
            return 15 * MINUTE;
        } else if (td && tt && fd) {
            return 20 * MINUTE;
        } else if (td && tt && ft) {
            return 35 * MINUTE;
        } else if (td && tt) {
            return 40 * MINUTE;
        } else if (td && fd && ft) {
            return 20 * MINUTE;
        } else if (td && fd) {
            return 15 * MINUTE;
        } else if (td && ft) {
            return 40 * MINUTE;
        } else if (td) {
            return 35 * MINUTE;
        } else if (tt && fd && ft) {
            return 35 * MINUTE;
        } else if (tt && fd) {
            return 40 * MINUTE;
        } else if (tt && ft) {
            return 20 * MINUTE;
        } else if (tt) {
            return 30 * MINUTE;
        } else if (fd && ft) {
            return 40 * MINUTE;
        } else if (fd) {
            return 45 * MINUTE;
        } else if (ft) {
            return 30 * MINUTE;
        } else {
            return 20 * MINUTE;
        }
    }

    static long timeMetro(boolean ft, boolean tt, int fdi, int tdi) {
        boolean fd = fdi == TYPE_D;
        boolean td = tdi == TYPE_D;
        if (td && tt && fd && ft) {
            return 0;
        } else if (td && tt && fd) {
            return 8 * MINUTE;
        } else if (td && tt && ft) {
            return 0;
        } else if (td && tt) {
            return 8 * MINUTE;
        } else if (td && fd && ft) {
            return 8 * MINUTE;
        } else if (td && fd) {
            return 0;
        } else if (td && ft) {
            return 8 * MINUTE;
        } else if (td) {
            return 0;
        } else if (tt && fd && ft) {
            return 0;
        } else if (tt && fd) {
            return 8 * MINUTE;
        } else if (tt && ft) {
            return 0;
        } else if (tt) {
            return 8 * MINUTE;
        } else if (fd && ft) {
            return 8 * MINUTE;
        } else if (fd) {
            return 16 * MINUTE;
        } else if (ft) {
            return 8 * MINUTE;
        } else {
            return 0;
        }
    }

    static boolean checkPlaneGate(Plane plane, Gate gate) {
        return gate.isNw() == plane.isNw() && (gate.getTypeArrive() & plane.getTypeArrive()) > 0 && (gate.getTypeLeave() & plane.getTypeLeave()) > 0;
    }

    abstract List<Gate> getAvailableGates(Plane plane);

    void setPlaneInGate(Plane plane, Gate gate) {
        gate.setPlane(plane);
        plane.setGate(gate);
//        System.out.println(plane.getId() + "\t" + gate.getId()
//                    + "\t" + formatDate(plane.getTimeArrive())
//                    + "\t" + formatDate(plane.getTimeLeave()));
        System.out.println(plane.getId() + "," + gate.getId()
                + "," + (plane.getTimeArrive() - 1516377600000L) / 60000L
                + "," + (plane.getTimeLeave() - 1516377600000L) / 60000L);
    }
}
