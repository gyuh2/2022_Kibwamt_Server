package baekkoji.smarthomeserver.controller;

import baekkoji.smarthomeserver.dto.Sensor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    static int status; // 현재 상태

    @PostMapping("/set-data")
    public int setData(@RequestBody Sensor sensor) {
        System.out.println(sensor); //콘솔 출력
        status = sensor.ChangeStatus();
        return status;//Client에게 주는 값
    }
}





