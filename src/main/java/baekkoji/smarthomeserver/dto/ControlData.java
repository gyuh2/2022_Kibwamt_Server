package baekkoji.smarthomeserver.dto;

import lombok.Data;

import java.sql.*;

@Data
public class ControlData {

    private String id;
    private int angle = 0;
    private int ac_temp = 0;
    private int heater_temp = 0;
    private int windowUp = 2;
    private int heater = 2;
    private int ac = 2;
    private int airCleaner = 2;
    private int airOut = 2;
    private int door = 2;
    private String door_passwd = ""; //app에서 넘어온 passwd

    String url = "jdbc:mysql://database-baekkoji.ccp9kadfy1fx.ap-northeast-2.rds.amazonaws.com:3306/smarthome";
    String userName = "admin";
    String password = "baekkoji";

    private String getDoorPasswd() throws SQLException{
        String result ="";

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        String sql = "select door_passwd from ControlData where id='chayoung';";
        System.out.println(sql);

        ResultSet rs = statement.executeQuery(sql);

        if(rs.next()){
            result = rs.getString("door_passwd");
        }

        rs.close();
        statement.close();
        connection.close();

        return result;
    }

    public String setControlData() throws SQLException {
        String result = "" ;

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        PreparedStatement pstmt = null;

        String sql = "update ControlData set ";

        if(windowUp==1 || windowUp==0) { //window 제어
            sql+= "windowUp=" + windowUp + ", angle=" + angle;
        }
        if(heater==1 || heater==0) {
            sql+= "heater=" + heater + ", heater_temp=" + heater_temp;
        }
        if(ac==1 || ac==0) {
            sql += "ac=" + ac + ", ac_temp=" + ac_temp;
        }
        if(airCleaner==1 || airCleaner==0) {
            sql += "airCleaner=" + airCleaner;
        }
        if(airOut==1 || airOut==0) {
            sql += "airOut=" + airOut;
        }
        if(door==1 || door==0){
            String collectPasswd = getDoorPasswd();
            if(door_passwd.equals(collectPasswd)){
                sql += "door=" + door;
            }
        }

        sql += " where id=?";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, "chayoung"); //id 임의로

        pstmt.executeUpdate();

        statement.close();
        connection.close();

        result = "ok";
        return result;
    }
}
