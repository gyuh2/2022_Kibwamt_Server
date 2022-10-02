package baekkoji.smarthomeserver;

import baekkoji.smarthomeserver.dto.Users;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;

@SpringBootApplication
public class SmarthomeServerApplication
{

    public static void main(String[] args) throws SQLException {
        SpringApplication.run(SmarthomeServerApplication.class, args);
        Users u = new Users();
        u.checkId("chaeyoung");
    }
}


