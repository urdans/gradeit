package lc101.liftoff.gradeit.models;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Student implements User { /*done makes this class implement User and refactor using User semantics*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Size(min=2, message = "First name size must be >= 2")
    private String firstName;

    @NotNull
    @Size(min=2, message = "Last name size must be >= 2")
    private String lastName;

    @Email(message = "Email address should be valid")
    private String email;

    private String userName;
    private String password;
    private String address;
    private String phoneNumber;

    @ColumnDefault("true") //This isn't working
    private boolean active = true;

    @ColumnDefault("false")
    private boolean confirmed = false;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    //Constructors****************************
    public Student() {
    }

    //this constructor is for the use of a registrar
    public Student(@NotNull String firstName, @NotNull String lastName, String email, String address,
                   String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    //Getters and setters*********************
    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isConfirmed() {
        return confirmed;
    }

    @Override
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
