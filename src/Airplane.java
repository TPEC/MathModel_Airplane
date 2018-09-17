import solution.GreedyModel;
import solution.Model;

import java.util.*;

public class Airplane {
    public static void main(String[] args) {
        GreedyModel model = new GreedyModel();
        model.run();

        int problem = Integer.valueOf(args[0]);
        int count = Integer.valueOf(args[1]);

        if (problem == 2) {
            Q2_new(model, count);
        } else if (problem == 3) {
            Q3_new(model, count);
        }
    }

    public static void switchGate(GreedyModel model, int q) {
        if (q == 2) {
            ScoreAndSgi best = Q2(model, false);
            ScoreAndSgi p = Q2(model, false);
            if (p.l < best.l) {
                best = p;
            }
            System.out.println("Best score:" + best.l);
        } else {
            ScoreAndSgi best = Q3(model, false);
            ScoreAndSgi p = Q3(model, false);
            if (p.d < best.d) {
                best = p;
            }
            System.out.println("Best score:" + best.d);
        }

    }

    public static void Q1() {
        Model model = new GreedyModel();
        model.run();
    }

    public static ScoreAndSgi Q2(GreedyModel model, boolean rnd) {
        ScoreAndSgi p = new ScoreAndSgi();
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            List<ScoreAndSgi> r = new ArrayList<>();
            model.beforeSwitch();
            int[] sgi = model.getSgi();
            long score = model.checkUser2().l;
            p.l = score;
            p.sgi = sgi;
            while (model.switchGate()) {
                int[] sgit = model.getSgi();
                long st = model.checkUser2().l;
                if (st < score) {
                    r.add(new ScoreAndSgi(st, Arrays.copyOf(sgit, sgit.length)));
                }
                model.setSgi(sgi);
            }
            r.sort(Comparator.comparingLong(o -> o.l));
            System.out.println("SIZE:" + r.size());
            if (!r.isEmpty()) {
                int index = 0;
                if (rnd) {
                    index = (int) (new Random().nextDouble() * r.size());
                }
                model.setSgi(r.get(index).sgi);
                System.out.println("Before:" + score + "\tAfter:" + r.get(index).l);
            } else {
                break;
            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println("TIME:" + (double) time / 1000.0);
        return p;
    }

    public static ScoreAndSgi Q3(GreedyModel model, boolean rnd) {
        ScoreAndSgi p = new ScoreAndSgi();
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            List<ScoreAndSgi> r = new ArrayList<>();
            model.beforeSwitch();
            int[] sgi = model.getSgi();
            double score = model.checkUser2().d;
            p.d = score;
            p.sgi = sgi;
            while (model.switchGate()) {
                int[] sgit = model.getSgi();
                double st = model.checkUser2().d;
                if (st < score) {
                    r.add(new ScoreAndSgi(st, Arrays.copyOf(sgit, sgit.length)));
                }
                model.setSgi(sgi);
            }
            r.sort(Comparator.comparingDouble(o -> o.d));
            System.out.println("SIZE:" + r.size());
            if (!r.isEmpty()) {
                int index = 0;
                if (rnd) {
                    index = (int) (new Random().nextDouble() * r.size());
                }
                model.setSgi(r.get(index).sgi);
                System.out.println("Before:" + score + "\tAfter:" + r.get(index).d);
            } else {
                break;
            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println("TIME:" + (double) time / 1000.0);
        return p;
    }

    public static class ScoreAndSgi {
        long l;
        double d;
        int[] sgi;

        public ScoreAndSgi() {

        }

        public ScoreAndSgi(long l, int[] sgi) {
            this.l = l;
            this.sgi = sgi;
        }

        public ScoreAndSgi(double d, int[] sgi) {
            this.d = d;
            this.sgi = sgi;
        }
    }

    public static void Q2_new(GreedyModel model, int count) {
        model.resetSwitch();
        for (int i = 0; i < count; i++) {
            if (!model.switchPlane_10(2)) {

            }
            ScoreAndSgi sas = Q2(model, false);
        }
    }

    public static void Q3_new(GreedyModel model, int count) {
        model.resetSwitch();
        Q3(model, false);
        for (int i = 0; i < count; i++) {
            System.out.println("=========EPOCH" + i);
            if (model.switchPlane_10(3)) {

            }
        }
    }
}
