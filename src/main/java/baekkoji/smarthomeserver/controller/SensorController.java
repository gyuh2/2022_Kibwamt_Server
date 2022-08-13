package baekkoji.smarthomeserver.controller;

import baekkoji.smarthomeserver.dto.Sensor;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping("/sensor")
public class SensorController {

    static String status; // 현재 상태

    @PostMapping("/set-data")
    public @ResponseBody String setData(@RequestBody Sensor sensor) { //아두이노 -> 서버 (센서값 전달)
        sensor.APIData(); //공공데이터 참조
        //DB에 저장하는 코드 입력 changeStatus 내부에 해도 됨.
        //실내 미세먼지 등급은 값으로 가져오지 않으므로 임의로 설정해야함.
        status = sensor.ChangeStatus(); //데이터 처리한 값 저장.
        System.out.println(sensor); //콘솔 출력
        return status; // 아두이노 request에 대한 응답.
    }
}





