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

    public int newSignupUser(Map<String, String> user) {
        // DB에 user 레코드를 새로 추가 하고, 앱에 성공여부 (1/0)으로 알리기
        return 1;
    }

    public Map<String,String> getUserData() { //회원 정보 가져오기
        Map<String, String> Userdata= new HashMap<>();
        // 'Users Table'에서 참조해서 Userdata 변수에 저장하여 return
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
