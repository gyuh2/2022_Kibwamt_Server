package baekkoji.smarthomeserver.controller;

import baekkoji.smarthomeserver.dto.Sensor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@ResponseBody
@RequestMapping("/sensor")
public class SensorController {

    static int status; // 현재 상태

    @PostMapping("/set-data")
    public @ResponseBody int setData(@RequestBody Sensor sensor) {
        System.out.println(sensor); //콘솔 출력
        status = sensor.ChangeStatus(); //데이터 처리한 값 저장.
        return status; // 아두이노 request에 대한 응답.
    }
}





