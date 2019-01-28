package lc101.liftoff.gradeit.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Registrar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private String userName;
    @NotNull
    private String password;
    //Constructors****************************
    public Registrar() {
    }
    public Registrar(@NotNull String userName, @NotNull String password) {
        this.userName = userName;
        this.password = password;
    }
    //Getters and setters*********************
    public int getId() {
        return id;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
