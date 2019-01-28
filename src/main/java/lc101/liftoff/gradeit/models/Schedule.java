package lc101.liftoff.gradeit.models;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private Date date;
    @NotNull
    @ColumnDefault("0.0")
    private double percentage;
    private String description;
    @ManyToOne
    @JoinColumn(name="grouping_id", nullable = false)
    private Grouping grouping;
    //Constructors****************************
    public Schedule() {
    }
    public Schedule(@NotNull Date date, @NotNull double percentage, String description, Grouping grouping) {
        this.date = date;
        this.percentage = percentage;
        this.description = description;
        this.grouping = grouping;
    }
    //Getters and setters*********************
    public int getId() {
        return id;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public double getPercentage() {
        return percentage;
    }
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Grouping getGrouping() {
        return grouping;
    }
    public void setGrouping(Grouping grouping) {
        this.grouping = grouping;
    }
}
