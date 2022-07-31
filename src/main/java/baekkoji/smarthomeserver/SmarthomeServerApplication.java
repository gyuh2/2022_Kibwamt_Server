package baekkoji.smarthomeserver;

import baekkoji.smarthomeserver.dto.Sensor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
public class SmarthomeServerApplication
{

    public static void main(String[] args) throws SQLException {

        String url = "jdbc:mysql://localhost:3306/SmartHome";
        String userName = "root";
        String password = "1234";

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        //insert가 되는지 test한 코드로 주석을 제거하고 1번만 실행하여 잘 반영 되었는지 확인만 해주세요.//
        //statement.executeUpdate("insert into users(Name) values('junyoung')");
        ResultSet resultSet = statement.executeQuery("select * from users");

        while(resultSet.next()) {
            String name = resultSet.getString("Name");
            System.out.println(name);
        }

        resultSet.close();
        statement.close();
        connection.close();

        SpringApplication.run(SmarthomeServerApplication.class, args);
        Sensor sensor = new Sensor();

        String day_join; //오늘 날짜 저장
        Date today = new Date();
        SimpleDateFormat today_format = new SimpleDateFormat("yyyyMMdd");
        day_join = today_format.format(today);
        sensor.APIData(day_join);
    }
}


