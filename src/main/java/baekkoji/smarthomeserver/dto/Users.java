package baekkoji.smarthomeserver.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import java.sql.*;

@Data
public class Users {
    private static String id;
    private String passwd;
    private String name;
    private String address;
    private String addressDetail;
    private String authPoint;

    public String getId() {
        return id;
    }

    String url = "jdbc:mysql://database-baekkoji.ccp9kadfy1fx.ap-northeast-2.rds.amazonaws.com:3306/smarthome";
    String userName = "admin";
    String password = "baekkoji";

    // 로그인
    public boolean login(String id, String passwd) throws SQLException{
        Boolean result = false; //로그인 성공 여부

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            String sql = "select id,passwd from Users where id=?;";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                if(passwd.equals(rs.getString("passwd"))) {
                    result = true;
                    this.id = id;
                }// end of inner if();
            } // end of if();

            connection.close();
            pstmt.close();
            rs.close();
        }catch (Exception e){
            System.out.println(e);
        }
        //System.out.println("id는 " + id + ", passwd는 "+ passwd + "결과 값은 " + result);
        return result;
    }

    // id 중복  : done
    public boolean checkId(String id) throws SQLException {
        id = id.replaceAll("[\"]", "");
        boolean result = false; // id 중복 여부

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            String sql = "select * from Users where id=?;";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){ //중복된 아이디값을 가져왔을 경우
                result = true;
            } // end of if();

            rs.close();
            connection.close();
            pstmt.close();
        }catch(Exception e){
            System.out.println(e);
        }
        return result;
    }

    //회원가입 기능 : done
    public String newSignupUser(Map<String, String> users) throws SQLException {
        // DB에 user 레코드를 새로 추가 하고, 앱에 성공여부 알리기

        try{
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            String sql = "insert into Users(id, passwd, name, address, addressDetail, authPoint) values(?,?,?,?,?,?)";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, users.get("id"));
            pstmt.setString(2, users.get("passwd"));
            pstmt.setString(3, users.get("name"));
            pstmt.setString(4, users.get("address"));
            pstmt.setString(5, users.get("addressDetail"));
            pstmt.setString(6, users.get("authPoint"));

            pstmt.executeUpdate();

            pstmt.close();
            connection.close();

            String door_passwd = "1234";
            newUserDatas(users.get("id"),door_passwd);
        }catch(Exception e){
            System.out.println(e);
            return "";
        }
        return "ok";
    }

    //회원 정보 가져오기 : done
    /*
    public Map<String,String> getUserData(String id) throws SQLException { //회원 정보 가져오기
        id = id.replaceAll("[\"]", "");

        Map<String, String> Userdata= new HashMap<>();

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            String sql = "select * from Users where id=?;";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                Userdata.put("id",rs.getString("id"));
                Userdata.put("passwd",rs.getString("passwd"));
                Userdata.put("name",rs.getString("name"));
                Userdata.put("address",rs.getString("address"));
                Userdata.put("addressDetail",rs.getString("addressDetail"));
            } // end of if();

            rs.close();
            pstmt.close();
            connection.close();
        }catch(Exception e){
            System.out.println(e);
        }
        return Userdata;
    }*/

    //회원 정보 반환 : done
    public Map<String,String> getUserData() throws SQLException { //회원 정보 가져오기

        Map<String, String> Userdata= new HashMap<>();

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            String sql = "select * from Users where id=?;";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                Userdata.put("id",rs.getString("id"));
                Userdata.put("passwd",rs.getString("passwd"));
                Userdata.put("name",rs.getString("name"));
                Userdata.put("address",rs.getString("address"));
                Userdata.put("addressDetail",rs.getString("addressDetail"));
            } // end of if();

            rs.close();
            pstmt.close();
            connection.close();
        }catch(Exception e){
            System.out.println(e);
        }
        return Userdata;
    }

    //회원 정보 변경 : done
    public boolean setUserData(Map<String, String> users) throws SQLException {
        boolean result = false;

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            String sql = "update Users set passwd=?, name=?, address=?, addressDetail=? where id=?";

            pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, users.get("passwd"));
            pstmt.setString(2, users.get("name"));
            pstmt.setString(3, users.get("address"));
            pstmt.setString(4, users.get("addressDetail"));
            pstmt.setString(5, users.get("id"));
            int i = pstmt.executeUpdate();

            if(i==1){
                result = true;
            }
            pstmt.close();
            connection.close();
        }catch(Exception e){
            System.out.println(e);
        }
        return result;
    }

    //회원 탈퇴하기 : done
    public boolean WithdrawUserData(String id, String passwd) throws SQLException {
        // DB에서 passwd 가져와서 확인하고 해당 레코드 정보 삭제하기.
        boolean result = false;

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            String sql = "select * from Users where id=?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                if(passwd.equals(rs.getString("passwd"))){
                    sql = "delete from HomeDataInfo where id=?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, id);
                    int i = pstmt.executeUpdate();

                    System.out.println("homedatainfo done");
                    sql = "delete from ControlData where id=?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, id);
                    i = pstmt.executeUpdate();

                    System.out.println("ControlData done");
                    sql = "delete from Users where id=?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, id);
                    i = pstmt.executeUpdate();

                    if(i==1) {
                        result = true;
                    }
                }
            }

            rs.close();
            pstmt.close();
            connection.close();
        }catch(Exception e){
            System.out.println(e);
        }
        return result;
    }

    // homedata table, control table insert하기.
    public void newUserDatas(String id,String door_passwd) throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            String sql = "insert into HomeDataInfo(id) values(?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.executeUpdate();

            sql = "insert into ControlData(id,door_passwd) values(?,?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2,door_passwd);
            pstmt.executeUpdate();

            pstmt.close();
            connection.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // 제어할 기기 삭제 및 추가
    public boolean editControlDevices(Map<String,String> datas) throws SQLException {
        boolean result = false;
        String method = datas.get("method"); // delete / add
        String device = datas.get("device"); // 기기명

        String id = "comehome";

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            String sql ="";
            if(method.equals("delete")) {
                sql = "update ControlData set ?=3 where id=?";         // 3의 값은 제어를 하지 않는다고 판단함.
                sql = "update ControlData set " + device + "=3 where id=?";
            }else if(method.equals("add")){
                //sql = "update ControlData set" + "device"+"=2 where id=?";        // 2의 값은 초기값.
                sql = "update ControlData set " + device + "=2 where id=?";
            }

            pstmt = connection.prepareStatement(sql);
            //pstmt.setString(1,device);
            pstmt.setString(1,id);
            int i = pstmt.executeUpdate();

            if(i==1) {
                result = true;
            }
            pstmt.close();
            connection.close();
        }catch (Exception e){
            System.out.println(e);
        }
        return result;
    }

}
