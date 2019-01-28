package lc101.liftoff.gradeit.models;

import javax.persistence.*;

@Entity
public class Grouping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name="group_id", nullable = false)
    private Group group;
    @ManyToOne
    @JoinColumn(name="teacher_id")
    private Teacher teacher;
    @ManyToOne
    @JoinColumn(name="subject_id")
    private Subject subject;
    //Constructors****************************
    public Grouping() {
    }
    public Grouping(Group group) {
        this.group = group;
    }
    //Getters and setters*********************
    public int getId() {
        return id;
    }
    public Group getGroup() {
        return group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }
    public Teacher getTeacher() {
        return teacher;
    }
    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
    public Subject getSubject() {
        return subject;
    }
    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
