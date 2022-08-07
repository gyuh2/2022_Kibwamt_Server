package baekkoji.smarthomeserver.dto;

public class user {

    private String id;
    private String passwd;
    private String name;
    private String address;
    private String addressDetail;
    private String authPoint;

    public String jsonData() {
        return "{" +
                "\"id\":" + id +
                ", \"passwd\":" + passwd +
                ", \"name\":" + name +
                ", \"address\":" + address +
                ", \"addressDetail\":" + addressDetail +
                ", \"authPoint\":" + authPoint +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public void setAuthPoint(String authPoint) {
        this.authPoint = authPoint;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public String getAuthPoint() {
        return authPoint;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getId() {
        return id;
    }

    public String getPasswd() {
        return passwd;
    }
}
