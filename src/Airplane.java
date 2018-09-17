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
            ScoreAndSgi best = Q2(model);
            for (int i = 0; i < count; i++) {
                ScoreAndSgi p = Q2(model);
                if (p.l < best.l) {
                    best = p;
                }
                System.out.println("==========================" + i);
                System.out.println("Best score:" + best.l);
            }
            System.out.println("==========================");
            System.out.println("Best score:" + best.l);
            for (int i = 0; i < best.sgi.length; i++) {
                System.out.println(i + "\t" + best.sgi[i]);
            }
        } else if (problem == 3) {
            ScoreAndSgi best = Q3(model);
            for (int i = 0; i < count; i++) {
                ScoreAndSgi p = Q3(model);
                if (p.d < best.d) {
                    best = p;
                }
                System.out.println("==========================" + i);
                System.out.println("Best score:" + best.d);
            }
            System.out.println("==========================");
            System.out.println("Best score:" + best.d);
            for (int i = 0; i < best.sgi.length; i++) {
                System.out.println(i + "\t" + best.sgi[i]);
            }
        }
    }

    public static void Q1() {
        Model model = new GreedyModel();
        model.run();
    }

    public static ScoreAndSgi Q2(GreedyModel model) {
        ScoreAndSgi p = new ScoreAndSgi();
        model.resetSwitch();
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
                int index = (int) (new Random().nextDouble() * r.size());
                index = 0;
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

    public static ScoreAndSgi Q3(GreedyModel model) {
        ScoreAndSgi p = new ScoreAndSgi();
        model.resetSwitch();
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
                int index = (int) (new Random().nextDouble() * r.size());
                index = 0;
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

    static class ScoreAndSgi {
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
}
