package lc101.liftoff.gradeit.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`group`")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Size(min=1, message = "Group name size must be >= 1")
    private String name;

    @OneToMany(mappedBy = "group")
    private List<Student> students = new ArrayList<>();

    //Constructors****************************
    public Group() {
    }

    public Group(@NotNull String name) {
        this.name = name;
    }

    //Getters and setters*********************
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
