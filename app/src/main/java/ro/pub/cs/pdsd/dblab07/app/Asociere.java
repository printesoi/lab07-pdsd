package ro.pub.cs.pdsd.dblab07.app;

public class Asociere {

    int profId;
    int courseId;

    public Asociere(int profId, int courseId) {
        setProfId(profId);
        setCourseId(courseId);
    }

    public void setProfId(int id) {
        this.profId = id;
    }

    public int getProfId() {
        return this.profId;
    }

    public void setCourseId(int id) {
        this.courseId = id;
    }

    public int getCourseId() {
        return this.courseId;
    }

    @Override
    public String toString() {
        return this.profId + " -> " + this.courseId;
    }
}
