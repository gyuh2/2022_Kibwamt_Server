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

        String url = "jdbc:mysql://localhost:3307/SmartHome";
        String userName = "chaeyoung";
        String password = "1234";

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from users");

        resultSet.next();
        String name = resultSet.getString("name");
        System.out.println(name);

        resultSet.close();
        statement.close();
        connection.close();

        SpringApplication.run(SmarthomeServerApplication.class, args);
        Sensor sensor = new Sensor();
        String day_join; // 오늘날짜 저장할 변수
        Date today = new Date();
        SimpleDateFormat today_format = new SimpleDateFormat("yyyyMMdd");
        day_join = today_format.format(today);
        sensor.APIData(day_join);
    }
}


