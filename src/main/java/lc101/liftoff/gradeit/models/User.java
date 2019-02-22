package lc101.liftoff.gradeit.models;

public interface User {
	int getId();

	default String getFirstName() {
		return "";
	}

	default String getLastName() {
		return "";
	}

	default String getEmail() {
		return null;
	}

	String getUserName();

	String getPassword();

	default String getAddress() {
		return "";
	}

	default String getPhoneNumber() {
		return "";
	}

	default boolean isActive() {
		return false;
	}

	default boolean isConfirmed() {
		return false;
	}

	void setUserName(String userName);

	void setPassword(String password);

	default void setConfirmed(boolean confirmed) {
	}
}
