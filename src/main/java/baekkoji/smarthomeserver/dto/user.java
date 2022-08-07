package baekkoji.smarthomeserver.dto;

import org.json.JSONObject;

public class user {

    private String id;
    private String passwd;
    private String name;
    private String address;
    private String addressDetail;
    private String authPoint;

    public JSONObject jsonData() {
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("id", "sally");
        jsonObject.put("passwd", "1234");
        jsonObject.put("name", "chayoung");
        jsonObject.put("address", "seoul");
        jsonObject.put("adrressDetail", "jon");
        jsonObject.put("authPoint", "0987");
        return jsonObject;
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
