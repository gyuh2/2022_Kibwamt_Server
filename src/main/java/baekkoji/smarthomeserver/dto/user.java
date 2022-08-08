package baekkoji.smarthomeserver.dto;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class user {

    private String id;
    private String passwd;
    private String name;
    private String address;
    private String addressDetail;
    private String authPoint;

    public Map<String,String> Userdata() {
        Map<String, String> Userdata= new HashMap<>();
        Userdata.put("id", "sally");
        Userdata.put("passwd", "1234");
        Userdata.put("name", "chayoung");
        Userdata.put("address", "seoul");
        Userdata.put("adrressDetail", "jon");
        Userdata.put("authPoint", "0987");
        return Userdata;
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
