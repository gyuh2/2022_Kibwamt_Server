package baekkoji.smarthomeserver.controller;

import baekkoji.smarthomeserver.dto.ControlData;
import baekkoji.smarthomeserver.dto.Sensor;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@ResponseBody
@RequestMapping("/sensor")
public class IoTController {

    static String status; // 자동제어값

    @PostMapping("/set-data")
    public @ResponseBody String setSensorData(@RequestBody Sensor sensor) throws SQLException { //아두이노 -> 서버 (센서값 전달)
        sensor.getAPIData(); // 공공데이터 참조하여 멤버 변수에 저장.
        sensor.setDataAll(); // 데이터 DB 저장.
        status = sensor.AutoControl(); // 자동제어 분석 및 DB 저장.
        System.out.println(sensor); // 실내,실외 데이터 출력
        System.out.println(status); // 자동제어값 출력
        return status; // 아두이노에 자동제어
    }

    @GetMapping("/get-control-data")
    public @ResponseBody String getControlData() throws SQLException { // 아두이노 -> 서버 (원격제어 데이터 참조)
        String result = "";
        ControlData controlData = new ControlData();
        result = controlData.getControlData();
        return result;
    }
}





