package lc101.liftoff.gradeit.models;

public interface User {
    int getId();

    //    String getFirstName();
    //    String getLastName();
    default String getEmail() {
        return null;
    }

    String getUserName();

    String getPassword();

    default boolean isActive() {
        return false;
    }

    default boolean isConfirmed() {
        return false;
    }

    //    String getPhoneNumber();
    void setUserName(String userName);

    void setPassword(String password);

    default void setConfirmed(boolean confirmed) {}
}
