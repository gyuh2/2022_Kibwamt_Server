package baekkoji.smarthomeserver.controller;

import baekkoji.smarthomeserver.dto.Sensor;
import baekkoji.smarthomeserver.dto.user;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/api")

public class BoardApiController {
    static JSONObject gsData;
    @Autowired
    private BoardApiController repository;

    @GetMapping("/boards")
    public @ResponseBody JSONObject setData(@RequestBody user person) {
        System.out.println(person); //콘솔 출력
        gsData = person.jsonData(); //데이터 처리한 값 저장.
        return gsData; // 아두이노 request에 대한 응답.
    }
}
