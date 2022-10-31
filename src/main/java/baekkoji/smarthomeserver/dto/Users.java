package baekkoji.smarthomeserver.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import java.sql.*;

@Data
public class Users {
    private String id;
    private String passwd;
    private String name;
    private String address;
    private String addressDetail;
    private String authPoint;

    String url = "jdbc:mysql://database-baekkoji.ccp9kadfy1fx.ap-northeast-2.rds.amazonaws.com:3306/smarthome";
    String userName = "admin";
    String password = "baekkoji";

    // 로그인
    public String login(String id, String passwd) throws SQLException{

        String result = ""; //로그인이 성공하면 반환할 id
        
        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        PreparedStatement pstmt = null;

        String sql = "select id,passwd from Users where id=?;";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, id);

        ResultSet rs = pstmt.executeQuery();

        if(rs.next()){
            if(passwd.equals(rs.getString("passwd"))) {
                result = id;
            }// end of inner if();
        } // end of if();

        return result;
    }

    // id 중복  : done
    public boolean checkId(String id) throws SQLException {
        id = id.replaceAll("[\"]", "");

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        PreparedStatement pstmt = null;

        String sql = "select * from Users where id=?;";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, id);

        ResultSet rs = pstmt.executeQuery();

        if(rs.next()){ //중복된 아이디값을 가져왔을 경우
            return true;
        } // end of if();

        return false;
    }

    //회원가입 기능 : done
    public String newSignupUser(Map<String, String> users) throws SQLException {
        // DB에 user 레코드를 새로 추가 하고, 앱에 성공여부 알리기
        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        PreparedStatement pstmt = null;

        String sql = "insert into Users(id, passwd, name, address, addressDetail, authPoint) values(?,?,?,?,?,?)";

        try{
            pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, users.get("id"));
            pstmt.setString(2, users.get("passwd"));
            pstmt.setString(3, users.get("name"));
            pstmt.setString(4, users.get("address"));
            pstmt.setString(5, users.get("addressDetail"));
            pstmt.setString(6, users.get("authPoint"));

            pstmt.executeUpdate();

        }catch(Exception e){
            return "";
        }
        statement.close();
        connection.close();

        return "ok";
    }

    //회원 정보 가져오기 : done
    public Map<String,String> getUserData(String id) throws SQLException { //회원 정보 가져오기
        id = id.replaceAll("[\"]", "");

        Map<String, String> Userdata= new HashMap<>();

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        PreparedStatement pstmt = null;

        String sql = "select * from Users where id=?;";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, id);

        ResultSet rs = pstmt.executeQuery();

        if(rs.next()){
            Userdata.put("id",id);
            Userdata.put("passwd",rs.getString("passwd"));
            Userdata.put("name",rs.getString("name"));
            Userdata.put("address",rs.getString("address"));
            Userdata.put("addressDetail",rs.getString("addressDetail"));
        } // end of if();
        return Userdata;
    }

    //회원 정보 변경 : done
    public boolean setUserData(Map<String, String> users) throws SQLException {
        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        PreparedStatement pstmt = null;

        String sql = "update Users set passwd=?, name=?, address=?, addressDetail=? where id=?";

        try{
            pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, users.get("passwd"));
            pstmt.setString(2, users.get("name"));
            pstmt.setString(3, users.get("address"));
            pstmt.setString(4, users.get("addressDetail"));
            pstmt.setString(5, users.get("id"));
            pstmt.executeUpdate();
        }catch(Exception e){
            return false;
        }
        statement.close();
        connection.close();

        return true;
    }

    //회원 탈퇴하기 : done
    public boolean WithdrawUserData(String id) throws SQLException {
        // DB에서 passwd 가져와서 확인하고 해당 레코드 정보 삭제하기.
        id = id.replaceAll("[\"]", "");

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        PreparedStatement pstmt = null;

        String sql = "delete from HomeDataInfo where id=?";
        try{
            pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }catch(Exception e){
            return false;
        }

        sql = "delete from ControlData where id=?";
        try{
            pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }catch(Exception e){
            return false;
        }
        sql = "delete from Users where id=?";
        try{
            pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }catch(Exception e){
            return false;
        }
        statement.close();
        connection.close();

        return true;
    }
}
