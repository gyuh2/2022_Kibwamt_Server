package baekkoji.smarthomeserver.dto;

import lombok.Data;
import java.sql.*;
@Data
public class ControlData {

    Users users = new Users();

    private String id = users.getId();
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
    private Boolean whetherCollectDoorPasswd() throws SQLException{
        String result = "";

        try{
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;
            String sql = "select door_passwd from ControlData where id=?";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                result = rs.getString("door_passwd");
            }
            rs.close();
            pstmt.close();
            connection.close();
         }catch (Exception e){
            System.out.println("whetherCollectDoorPasswd : "+e);
        }

        if(this.door_passwd.equals(result)){
            return true;
        }
        return false;
    }

    //매개변수에 따른 속성값 참조하여 분석
    private boolean gatvalue(String device) throws SQLException{
        int value = 2;

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;
            String sql = "select " + device + " from ControlData where id=?;";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                value = rs.getInt(device);
            }

            rs.close();
            pstmt.close();
            connection.close();
        }catch (Exception e) {
            System.out.println("gatvalue : "+e);
        }
        if(value==1){ // ON일 경우
            return false;
        }else {// OFF일 경우
            return true;
        }
    }

    // 각각 기기에 값이 3으로 저장되어 있는지 확인.
    public boolean isControlDevice(String device) throws SQLException{
        boolean result = false;

        Connection connection = DriverManager.getConnection(url, userName, password);
        PreparedStatement pstmt = null;

        String sql = "select " + device + " from ControlData where id =?";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, id);
        ResultSet rs = pstmt.executeQuery();

        if(rs.next()){
            if(rs.getInt(device)!=3){
                rs.close();
                pstmt.close();
                connection.close();
                result = true;
            }else {
                rs.close();
                pstmt.close();
                connection.close();
                result = false;
            }
        }
        System.out.println("result : "+result);
        return result;
    }

    // 원격제어 데이터 DB 저장.
    public String setControlData() throws SQLException {
        String result = "" ;
        //String id = "comehome";
        System.out.println("id는 " + id);

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            String sql = "update ControlData set ";

            if(windowUp==1 || windowUp==0) {
                if(isControlDevice("windowUp")) {
                    sql+= "windowUp=" + windowUp + ", angle=" + angle;
                    sql += " where id=?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, id); //id 임의로
                    int i = pstmt.executeUpdate();
                    if(i==1) {
                        result="ok";
                    }
                }
            }
            if(airCleaner==1 || airCleaner==0) {
                if(isControlDevice("airCleaner")) {
                    sql += "airCleaner=" + airCleaner;
                    sql += " where id=?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, id); //id 임의로
                    int i = pstmt.executeUpdate();
                    if(i==1) {
                        result="ok";
                    }
                }
            }
            if(airOut==1 || airOut==0) {
                if(isControlDevice("airOut")) {
                    sql += "airOut=" + airOut;
                    sql += " where id=?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, id); //id 임의로
                    int i = pstmt.executeUpdate();
                    if(i==1) {
                        result="ok";
                    }
                }
            }
            if(door==1 || door==0){
                if(isControlDevice("door")) { // 도어락 비밀번호 여부 확인
                    if(whetherCollectDoorPasswd()){
                        sql += "door=" + door;
                        sql += " where id=?";
                        pstmt = connection.prepareStatement(sql);
                        pstmt.setString(1, id); //id 임의로
                        int i = pstmt.executeUpdate();
                        if(i==1) {
                            result="ok";
                        }
                    }
                }
            }
            // heater 키면 에어컨 값 여부 확인
            if(heater==1 && gatvalue("ac")){ //히터 가동.
                if(isControlDevice("heater")) {
                    sql += "heater=" + heater + ", heater_temp=" + heater_temp;
                    sql += " where id=?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, id); //id 임의로
                    int i = pstmt.executeUpdate();
                    if(i==1) {
                        result="ok";
                    }
                }
            }

            // ac를 키면 히터 값 여부 확인
            if(ac==1 && gatvalue("heater")){ //에어컨 가동.
                if(isControlDevice("ac")) {
                    sql += "ac=" + ac + ", ac_temp=" + ac_temp;
                    sql += " where id=?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, id); //id 임의로
                    int i = pstmt.executeUpdate();
                    if(i==1) {
                        result="ok";
                    }
                }
            }
            if(heater==0) {
                if(isControlDevice("heater")) {
                    sql += "heater=" + heater + ", heater_temp=" + heater_temp;
                    sql += " where id=?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, id); //id 임의로
                    int i = pstmt.executeUpdate();
                    if(i==1) {
                        result="ok";
                    }
                }
            }
            if(ac==0) {
                if(isControlDevice("ac")) {
                    sql += "ac=" + ac + ", ac_temp=" + ac_temp;
                    sql += " where id=?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, id); //id 임의로
                    int i = pstmt.executeUpdate();
                    if(i==1) {
                        result="ok";
                    }
                }
            }
/*
            sql += " where id=?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id); //id 임의로
            int i = pstmt.executeUpdate();
            if(i==1) {
                result="ok";
            }
*/
            pstmt.close();
            connection.close();
        }catch (Exception e){
            System.out.println("setControlData : "+e);
        }
        return result;
    }

    // 아두이노가 원격제어 데이터 참조
    public String getControlData() throws SQLException {
        String result = "";

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            String sql= "select * from ControlData where id=?;";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,id);
            ResultSet resultSet = pstmt.executeQuery();

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
            pstmt.close();
            connection.close();
        }catch (Exception e){
            System.out.println("getControlData : "+e);
        }

        System.out.println(result);
        return result;
    }

}
