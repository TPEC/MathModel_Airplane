package core;

public class User {
    private int id;
    private int from;
    private int to;

    public User(int id, int from, int to) {
        this.id = id;
        this.from = from;
        this.to = to;
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
}
