package baekkoji.smarthomeserver.controller;

import baekkoji.smarthomeserver.dto.Sensor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@ResponseBody
@RequestMapping("/sensor")
public class SensorController {

    static char status; // 현재 상태

    @PostMapping("/set-data")
    public @ResponseBody Map<String,Object> setData(@RequestBody Sensor sensor) {
        System.out.println(sensor); //콘솔 출력
        Map<String,Object> retVal = new HashMap<String,Object>();
        //status = sensor.ChangeStatus(); //데이터 처리

        retVal.put("result","success!!");
        return retVal;
    }
}





