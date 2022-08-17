package baekkoji.smarthomeserver.controller;

import baekkoji.smarthomeserver.dto.ControlData;
import baekkoji.smarthomeserver.dto.Sensor;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@ResponseBody
@RequestMapping("/sensor")
public class IoTController {

    static String status; // 현재 상태

    @PostMapping("/set-data")
    public @ResponseBody String setSensorData(@RequestBody Sensor sensor) throws SQLException { //아두이노 -> 서버 (센서값 전달)
        sensor.APIData();
        sensor.setDataAll(); //DB에 저장하는 코드
        status = sensor.ChangeStatus(); //데이터 처리한 값 저장.
        System.out.println(sensor); //콘솔 출력
        System.out.println(status);
        return status; // 아두이노 request에 대한 응답.
    }

    @GetMapping("/get-control-data")
    public @ResponseBody Map<String,Integer> getControlData() throws SQLException { //아두이노 -> 서버 (원격제어 데이터 참조)
        Map<String,Integer> result = new HashMap<>();
        ControlData controlData = new ControlData();
        result = controlData.getControlData();
        return result;
    }
}





