package baekkoji.smarthomeserver;

import baekkoji.smarthomeserver.dto.Sensor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
public class SmarthomeServerApplication
{

    private static String getServerip()
    {
        InetAddress local = null;
        try {
            local = InetAddress.getLocalHost();
        }catch(UnknownHostException e){
            e.printStackTrace();
        }
        if (local == null){
            return "";
        }
        else {
            return local.getHostAddress();
        }
    }

    public static void main(String[] args)
    {
        SpringApplication.run(SmarthomeServerApplication.class, args);
        Sensor sensor = new Sensor();
        String ipString = getServerip();
        System.out.println(ipString);
        String day_join; // 오늘날짜 저장할 변수
        Date today = new Date();
        SimpleDateFormat today_format = new SimpleDateFormat("yyyyMMdd");
        day_join = today_format.format(today);
        sensor.APIData(day_join);
    }
}


