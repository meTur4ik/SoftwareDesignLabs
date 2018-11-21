package instances;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Properties;

public class AppUser {
    public final String FIRST_NAME = "first_name";
    public final String LAST_NAME = "last_name";
    public final String EMAIL = "email";
    public final String PHONE = "phone";

    // properties

    private String first_name;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    private String last_name;

    private String phone_number;

    private String email;

    private String id;

    private String profile_image;

    // properties end

    public AppUser() {}

    public AppUser(String first_name, String last_name, String phone_number, String email,
                   String id, String profile_image) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.email = email;
        this.id = id;
        this.profile_image = profile_image;
    }

    public AppUser(Properties fileds){
        first_name = fileds.getProperty(FIRST_NAME);
        last_name = fileds.getProperty(LAST_NAME);
        email = fileds.getProperty(EMAIL);
        phone_number = fileds.getProperty(PHONE);
    }

    public Properties toProperties(){
        Properties props = new Properties();
        props.setProperty(FIRST_NAME, first_name);
        props.setProperty(LAST_NAME, last_name);
        props.setProperty(PHONE, phone_number);
        props.setProperty(EMAIL, email);

        return props;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                ", profile_image='" + profile_image + '\'' +
                '}';
    }
}
