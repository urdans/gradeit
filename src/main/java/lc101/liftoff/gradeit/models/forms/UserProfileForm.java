package lc101.liftoff.gradeit.models.forms;

import lc101.liftoff.gradeit.models.Teacher;
import lc101.liftoff.gradeit.models.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class UserProfileForm {
    private int id;

    @Size(min=2, message = "First name size must be >= 2")
    private String firstName;

    @Size(min=2, message = "Last name size must be >= 2")
    private String lastName;

    @Email(message = "Email address should be valid")
    private String email;

	@Size(min=2, message = "User name size must be >= 2")
    private String userName;

/*	@Size(min=2, message = "Password size must be >= 1")
	private String currentPassword;*/

    @Size(min=1, message = "Password size must be >= 1")
    private String password;

    @Size(min=1, message = "Password size must be >= 1")
    private String password2;

	private String address;
    private String phoneNumber;

	public UserProfileForm() {
	}

	public UserProfileForm(Teacher teacher) {
		setUserData(teacher);
	}

	public void setUserData(User user){
		id = user.getId();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		email = user.getEmail();
		userName = user.getUserName();
		phoneNumber = user.getPhoneNumber();
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
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
}
