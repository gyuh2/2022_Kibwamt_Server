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
        sensor.APIData(); //공공데이터 참조하여 멤버 변수에 저장.
        sensor.setDataAll(); //DB에 멤버변수 모두 저장
        status = sensor.ChangeStatus(); //데이터 분석하여 제어할 값 저장.
        System.out.println(sensor); //콘솔 출력
        System.out.println(status); //제어 Case 출력
        return status; // 아두이노 request에 대한 응답.
    }

    @GetMapping("/get-control-data")
    public @ResponseBody String getControlData() throws SQLException { //아두이노 -> 서버 (원격제어 데이터 참조)
        String result = "";
        ControlData controlData = new ControlData();
        result = controlData.getControlData();
        return result;
    }
}





