package core;

public class User {
    private int id;
    private int from;
    private int to;
    private int amount;

    private long timepp;
    private double timejz;

    public User(int id, int from, int to, int amount) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getTimepp() {
        return timepp;
    }

    public void setTimepp(long timepp) {
        this.timepp = timepp;
    }

    public void init() {
        this.timepp = 0;
        this.timejz = 0;
    }

    public double getTimejz() {
        return timejz;
    }

    public void setTimejz(double timejz) {
        this.timejz = timejz;
    }
}
