package baekkoji.smarthomeserver;

import baekkoji.smarthomeserver.dto.Sensor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
public class SmarthomeServerApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(SmarthomeServerApplication.class, args);
        Sensor sensor = new Sensor();
        
        String day_join=""; // 오늘날짜 저장할 변수
        Date today = new Date();
        SimpleDateFormat today_format = new SimpleDateFormat("yyyyMMdd");
        day_join = today_format.format(today);
        sensor.APIData(day_join);
    }
}


