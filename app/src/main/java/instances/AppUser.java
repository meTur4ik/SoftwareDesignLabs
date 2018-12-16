package instances;

import java.util.Properties;

import static instances.UserConstants.EMAIL_STR;
import static instances.UserConstants.FIRST_NAME_STR;
import static instances.UserConstants.ID_STR;
import static instances.UserConstants.LAST_NAME_STR;
import static instances.UserConstants.PHONE_STR;
import static instances.UserConstants.PROFILE_IMAGE_STR;
import static instances.UserConstants.RSS_ADDRESS_STR;

public class AppUser {


    // properties

    private String first_name = "";
    private String last_name = "";
    private String phone_number = "";
    private String email = "";
    private String id = "";
    private String profile_image = "";
    private String rss_address = "";

    public String getRss_address() { return rss_address; }

    public void setRss_address(String rss_address) { this.rss_address = rss_address; }

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


    // properties end

    public AppUser() {}

    public AppUser(String first_name, String last_name, String phone_number, String email,
                   String id, String profile_image, String rss_address) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.email = email;
        this.id = id;
        this.profile_image = profile_image;
        this.rss_address = rss_address;
    }

    public AppUser(Properties fields){
        first_name = fields.getProperty(FIRST_NAME_STR);
        last_name = fields.getProperty(LAST_NAME_STR);
        email = fields.getProperty(EMAIL_STR);
        phone_number = fields.getProperty(PHONE_STR);
        rss_address = fields.getProperty(RSS_ADDRESS_STR);
        profile_image = fields.getProperty(PROFILE_IMAGE_STR);
        id = fields.getProperty(ID_STR);
    }

    public Properties toProperties(){
        Properties props = new Properties();
        props.setProperty(FIRST_NAME_STR, first_name);
        props.setProperty(LAST_NAME_STR, last_name);
        props.setProperty(PHONE_STR, phone_number);
        props.setProperty(EMAIL_STR, email);
        props.setProperty(RSS_ADDRESS_STR, rss_address);
        props.setProperty(PROFILE_IMAGE_STR, profile_image);
        props.setProperty(ID_STR, id);
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
                ", rss_address='" + rss_address + '\'' +
                '}';
    }
}
