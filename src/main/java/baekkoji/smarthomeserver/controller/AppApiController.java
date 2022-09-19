package baekkoji.smarthomeserver.controller;

import baekkoji.smarthomeserver.dto.ControlData;
import baekkoji.smarthomeserver.dto.HomeDataInfo;
import baekkoji.smarthomeserver.dto.Sensor;
import baekkoji.smarthomeserver.dto.user;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")

public class AppApiController {
    //
    public static user person = new user(); //회원정보 클래스 생성
    static HomeDataInfo home = new HomeDataInfo();

    @GetMapping("/users/getUsers") // 앱 -> 서버 : 회원정보 참조 요청
    public @ResponseBody Map<String,String> sendData() throws SQLException{
        Map<String, String> Userdata = new HashMap<>();
        Userdata = person.getUserData(); 
        return Userdata; // 앱에 회원 정보 반환
    }

    @PostMapping("/users/setUsers") // 앱 -> 서버 : 회원정보 수정 요청
    public @ResponseBody int setUser(@RequestBody Map<String,String> user) throws SQLException{
        int result = person.setUserData(user); // 앱에서 받은 데이터로 DB 수정
        return result; //앱에게 수정 여부 반환
    }
    
    @GetMapping("/home/getDatas") // 앱 -> 서버 : 홈데이터 요청
    public @ResponseBody Map<String,Float> getData() throws SQLException {
        Map<String, Float> HomeData = new HashMap<>();
        HomeData = home.getHomeDataInfo();
        home.toString();
        return HomeData; // 앱에 홈 데이터 반환
    }

    @PostMapping("/home/Control") // 앱 -> 서버 : 기기 제어 요청
    public @ResponseBody String ControlHome(@RequestBody ControlData controlData) throws SQLException{
        String result = controlData.setControlData(); // 앱에서 받은 데이터 DB에 저장.
        return result; //앱에 응답여부 반환 (ok)
    }
}
