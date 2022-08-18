package baekkoji.smarthomeserver.dto;

import lombok.Data;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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

    // ControlData Table에서 id에 해당되는 회원의 도어락 비밀번호를 가져온다.
    private String getDoorPasswd() throws SQLException{
        String result ="";

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        String sql = "select door_passwd from ControlData where id='baekkoji';";
        //System.out.println(sql);

        ResultSet rs = statement.executeQuery(sql);

        if(rs.next()){
            result = rs.getString("door_passwd");
        }

        rs.close();
        statement.close();
        connection.close();

        return result;
    }

    // ControlData Table에 앱에서 요청한 원격제어 데이터 저장하기.
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
                // 도어락 비밀번호 여부 확인
                sql += "door=" + door;
            }
        }

        sql += " where id=?";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, "baekkoji"); //id 임의로

        pstmt.executeUpdate();

        statement.close();
        connection.close();

        result = "ok";
        return result;
    }

    // 아두이노가 ControlData Table을 참조하여 원격제어 하기.
    public String getControlData() throws SQLException {
        String result = "";

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        PreparedStatement pstmt = null;

        String sql= "select * from ControlData where id='baekkoji';";
        ResultSet resultSet = statement.executeQuery(sql);

        if(resultSet.next()) {
            windowUp = resultSet.getInt("windowUp");
            heater = resultSet.getInt("heater");
            ac = resultSet.getInt("ac");
            airCleaner = resultSet.getInt("airCleaner");
            airOut = resultSet.getInt("airOut");
            door = resultSet.getInt("door");

            if(windowUp==1){
                int angle = resultSet.getInt("angle");
                result += "windowUp," + windowUp;
                result += ",angle," + angle;
            }
            if(windowUp==0 || windowUp==2){ //OFF
                result += ",windowUp,0,angle,0";
            }
            if(heater==1){
                int heater_temp = resultSet.getInt("heater_temp");
                result += ",heater," + heater;
                result += ",heater_temp," + heater_temp;
            }
            if(heater==0 || heater==2){ //OFF
                result += ",heater,0,heater_temp,0";
            }
            if(ac==1){
                int ac_temp = resultSet.getInt("ac_temp");
                result += ",ac," + ac;
                result += ",ac_temp," + ac_temp;
            }
            if(ac==0 || ac==2){ //OFF
                result += ",ac,0,ac_temp,0";
            }
            if(airCleaner==1){
                result += ",airCleaner," + airCleaner;
            }
            if(airCleaner==0 || airCleaner==2){
                result += ",airCleaner,0";
            }
            if(airOut==1){
                result += ",airOut," + airOut;
            }
            if(airOut==0 || airOut==2){
                result += ",airOut,0";
            }
            if(door==1){
                result += ",door," + door + ",";
            }
            if(door==0 || door==2){
                result += ",door,0,";
            }
        }
        resultSet.close();
        statement.close();
        connection.close();

        System.out.println(result);
        return result;
    }
}
