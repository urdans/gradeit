package lc101.liftoff.gradeit.models;

public interface User {
    int getId();
//    String getFirstName();
//    String getLastName();
    String getEmail();
    String getUserName();
    String getPassword();
    boolean isActive();
    boolean isConfirmed();
//    String getPhoneNumber();
}
