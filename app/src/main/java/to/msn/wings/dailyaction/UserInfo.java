package to.msn.wings.dailyaction;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by kazuya on 2018/02/27.
 */

public class UserInfo extends RealmObject {
    @PrimaryKey
    private Long id;
    private String login_ID;
    private String password;
    private String name;
    private String picture_pass;
    private Double latitude;
    private Double longitude;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginID() {
        return login_ID;
    }

    public void setLoginId(String login_ID) {
        this.login_ID = login_ID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String  getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicturePass() {
        return picture_pass;
    }

    public void setPicturePass(String picture_pass) {
        this.picture_pass = picture_pass;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLogitude(Double logitude) {
        this.longitude = logitude;
    }
}
