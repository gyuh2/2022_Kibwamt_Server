package baekkoji.smarthomeserver.controller;

import baekkoji.smarthomeserver.dto.ControlData;
import baekkoji.smarthomeserver.dto.HomeDataInfo;
import baekkoji.smarthomeserver.dto.Users;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")

public class AppApiController {

    public static Users person = new Users(); //회원정보 클래스 생성
    static HomeDataInfo home = new HomeDataInfo();


    @PostMapping ("/users/idCheck") // 앱 -> 서버 : 회원가입 아이디 중복체크
    public @ResponseBody boolean checkId(@RequestBody String id) throws SQLException{
        person.setId(id);
        boolean result = person.checkId();
        System.out.println("결과 " + result);
        return result;
    }

    @PostMapping ("/users/signUp") // 앱 -> 서버 : 회원가입 요청
    public @ResponseBody String newUser(@RequestBody Map<String,String> users) throws SQLException{
        String result = person.newSignupUser(users);
        return result;
    }

    @PostMapping("/users/setUsers") // 앱 -> 서버 : 회원정보 수정
    public @ResponseBody int setUser(@RequestBody Map<String,String> users) throws SQLException{
        int result = person.setUserData(users); // 앱에서 받은 데이터로 DB 수정
        return result; //앱에게 수정 여부 반환
    }

    @GetMapping("/users/getUsers") // 앱 -> 서버 : 회원정보 참조
    public @ResponseBody Map<String,String> sendData() throws SQLException{
        Map<String, String> Userdata = new HashMap<>();
        Userdata = person.getUserData(); 
        return Userdata; // 앱에 회원 정보 반환
    }
    
    @GetMapping("/home/getDatas") // 앱 -> 서버 : 홈데이터 요청
    public @ResponseBody Map<String,Float> getHomeData() throws SQLException {
        Map<String, Float> HomeData = new HashMap<>();
        HomeData = home.getHomeDataInfo();
        home.toString();
        return HomeData; // 앱에 홈 데이터 반환
    }

    @GetMapping("/main/getDatas") // 앱 -> 서버 : 메인 페이지 정보
    public @ResponseBody Map<String,Float> getMainData() throws SQLException {
        Map<String, Float> MainData = new HashMap<>();
        MainData = home.getMainDataInfo();
        return MainData;
    }

    @PostMapping("/home/Control") // 앱 -> 서버 : 기기 제어 요청
    public @ResponseBody String ControlHome(@RequestBody ControlData controlData) throws SQLException{
        String result = controlData.setControlData(); // 앱에서 받은 데이터 DB에 저장.
        return result; //앱에 응답여부 반환 (ok)
    }
}