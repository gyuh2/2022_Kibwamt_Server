package baekkoji.smarthomeserver.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class user {

    private Map id;
    private String passwd;
    private String name;
    private String address;
    private String addressDetail;
    private String authPoint;

    public Map<String,String> getUserData() { //회원정보 가져오기
        Map<String, String> Userdata= new HashMap<>();
        // 'Users Table'에서 참조해서 Userdata 변수에 저장하여 return
        Userdata.put("id", "sally");
        Userdata.put("passwd", "1234");
        Userdata.put("name", "chaeyoung");
        Userdata.put("address", "seoul");
        Userdata.put("adrressDetail", "jongno-gu");
        Userdata.put("authPoint", "0987");
        return Userdata;
    }
    public int setUserData(Map<String, String> user){ //회원정보 변경 및 등록하기
        // DB에 user변수 값으로 저장하고, 앱에 성공여부 (1/0)으로 알리기

        return 1;
    }
    public int WithdrawUserData(Map<String, String> user){ //회원 탈퇴하기
        // DB에서 passwd 가져와서 확인하고 해당 레코드 정보 삭제하기.
        
        return 1;
    }
}
