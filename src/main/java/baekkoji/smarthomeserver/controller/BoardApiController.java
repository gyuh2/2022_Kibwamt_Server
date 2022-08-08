package baekkoji.smarthomeserver.controller;

import baekkoji.smarthomeserver.dto.Sensor;
import baekkoji.smarthomeserver.dto.user;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@ResponseBody
@RequestMapping(value="/url", method=RequestMethod.GET)

public class BoardApiController {

    static Map<String, String> Userdata= new HashMap<>();

    @GetMapping("/boards")
    public @ResponseBody Map<String,String> setData() {
        //System.out.println(person); //콘솔 출력
        user person = new user();
        Userdata = person.Userdata(); //데이터 처리한 값 저장.
        return Userdata; // 아두이노 request에 대한 응답.
    }
}
