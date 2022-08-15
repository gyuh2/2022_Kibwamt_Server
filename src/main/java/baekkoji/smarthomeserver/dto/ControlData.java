package baekkoji.smarthomeserver.dto;

import lombok.Data;

import java.sql.*;

@Data
public class ControlData {

    private int angle=0;
    private int ac_temp=0;
    private int heater_temp=0;
    private boolean windowUp = false;
    private boolean heater = false;
    private boolean ac = false;
    private boolean airCleaner = false;
    private boolean airOut = false;

    String url = "jdbc:mysql://database-baekkoji.ccp9kadfy1fx.ap-northeast-2.rds.amazonaws.com:3306/smarthome";
    String userName = "admin";
    String password = "baekkoji";

    public String setControlData() throws SQLException {
        String result = "" ;

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        String sql = "update ControlData set ";

        if(windowUp==true) { //window 제어
            sql+= "windowUp=" + windowUp + ", angle=" + angle;
        }
        if(heater==true) {
            sql+= "heater=" + heater + ", heater_temp=" + heater_temp;
        }
        if(ac==true) {
            sql += "ac=" + ac + ", ac_temp=" + ac_temp;
        }
        if(airCleaner==true) {
            sql += "airCleaner=" + airCleaner;
        }
        if(airOut==true) {
            sql += "airOut=" + airOut;
        }

        sql += "where id=?;";
        pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, "chayoung"); //id 임의로

        pstmt.executeUpdate();

        resultSet.close();
        statement.close();
        connection.close();

        result = "ok";
        return result;
    }
}
