package ro.pub.cs.pdsd.dblab07.app;

public class Profesor {

    private int id;
    private String name;

    public Profesor(int id, String name) {
        setId(id);
        setName(name);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return id + " " + name;
    }
}
