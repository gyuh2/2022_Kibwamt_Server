package baekkoji.smarthomeserver.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class user {

    private String id;
    private String passwd;
    private String name;
    private String address;
    private String addressDetail;
    private String authPoint;

    public Map<String,String> Userdata() {
        Map<String, String> Userdata= new HashMap<>();
        //DB에서 참조해서 보내야 함.
        Userdata.put("id", "sally");
        Userdata.put("passwd", "1234");
        Userdata.put("name", "chaeyoung");
        Userdata.put("address", "seoul");
        Userdata.put("adrressDetail", "jongno-gu");
        Userdata.put("authPoint", "0987");
        return Userdata;
    }
}
