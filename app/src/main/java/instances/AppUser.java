package instances;

import java.util.Properties;

public class AppUser {
    public final String FIRST_NAME = "first_name";
    public final String LAST_NAME = "last_name";
    public final String EMAIL = "email";
    public final String PHONE = "phone";

    // properties

    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    // properties end

    public AppUser() {}

    public AppUser(Properties fileds){
        firstName = fileds.getProperty(FIRST_NAME);
        lastName = fileds.getProperty(LAST_NAME);
        email = fileds.getProperty(EMAIL);
        phoneNumber = fileds.getProperty(PHONE);
    }

    public Properties toProperties(){
        Properties props = new Properties();
        props.setProperty(FIRST_NAME, firstName);
        props.setProperty(LAST_NAME, lastName);
        props.setProperty(PHONE, phoneNumber);
        props.setProperty(EMAIL, email);

        return props;
    }
}
