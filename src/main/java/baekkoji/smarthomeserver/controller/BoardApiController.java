package baekkoji.smarthomeserver.controller;

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

public class BoardApiController {

    public static user person = new user(); //회원정보 클래스 생성
    public static HomeDataInfo home = new HomeDataInfo(); //스마트홈 데이터 클래스 생성

    @GetMapping("/users/getUsers") // 앱이 서버에게 회원정보 참조 요청
    public @ResponseBody Map<String,String> sendData() throws SQLException{
        Map<String, String> Userdata = new HashMap<>();
        Userdata = person.getUserData(); 
        return Userdata; // 앱에 회원 정보 반환
    }

    @PostMapping("/users/setUsers") // 앱이 서버에게 회원정보 수정 요청
    public @ResponseBody int setUser(@RequestBody Map<String,String> user) throws SQLException{
        int result = person.setUserData(user); // 앱에서 받은 데이터로 DB 수정
        return result; //앱에게 수정 여부 반환
    }
    
    @GetMapping("/home/getDatas") // 앱이 서버에게 홈데이터 요청
    public @ResponseBody Map<String,Float> getData() throws SQLException {
        Map<String, Float> HomeData = new HashMap<>();
        HomeData = home.getHomeDataInfo();
        return HomeData; // 앱에 홈 데이터 반환
    }

    @PostMapping("/home/Control") // 앱이 서버에게 기기 제어 요청
    public @ResponseBody int ControlHome(@RequestBody Map<Integer,Boolean> controlData) throws SQLException{
        int result = home.controlHome(controlData); // 앱에서 받은 데이터로 DB 수정
        return result; //앱에게 수정 여부 반환
    }

}
