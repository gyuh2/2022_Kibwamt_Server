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
{//느려서 죄삼다

    public static void main(String[] args) throws SQLException {

        String url = "jdbc:mysql://database-baekkoji.ccp9kadfy1fx.ap-northeast-2.rds.amazonaws.com:3306/smarthome";
        String userName = "admin";
        //admin - 환경변수로 해야함. 보안상 문제로. application.properties에 정의해야함.
        // application.properties 파일은 git에 push 하면 안됨
        String password = "baekkoji";
        //baekkoji

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        //insert가 되는지 test한 코드로 주석을 제거하고 1번만 실행하여 잘 반영 되었는지 확인만 해주세요.//
        //statement.executeUpdate("insert into users(Name) values('gyuhee')");
        ResultSet resultSet = statement.executeQuery("select * from HomeDataInfo");

        while(resultSet.next()) {
            String name = resultSet.getString("temp");
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


