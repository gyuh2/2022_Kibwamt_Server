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

    public int newSignupUser(Map<String, String> users) throws SQLException {
        // DB에 user 레코드를 새로 추가 하고, 앱에 성공여부 (1/0)으로 알리기
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
            return 0;
        }
        statement.close();
        connection.close();

        return 1;
    }

    public Map<String,String> getUserData() { //회원 정보 가져오기
        Map<String, String> Userdata= new HashMap<>();
        // 'Users Table'에서 참조해서 Userdata 변수에 저장하여 return
        return Userdata;
    }

    public int setUserData(Map<String, String> users){
        //회원정보 변경 및 등록하기
        // DB에 user변수 값으로 저장하고, 앱에 성공여부 (1/0)으로 알리기
        return 1;
    }

    public int WithdrawUserData(Map<String, String> users){
        //회원 탈퇴하기
        // DB에서 passwd 가져와서 확인하고 해당 레코드 정보 삭제하기.
        return 1;
    }

    public boolean checkId() throws SQLException {
        //id.charAt(0);
        id = id.replaceAll("[\"]", "");
        System.out.println(id);

        boolean result = false;
        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        PreparedStatement pstmt = null;

        String sql = "select id from Users where id=?;";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, id);

        ResultSet rs = pstmt.executeQuery();

        while(rs.next()){
            result = true;
        } // end of while();
        System.out.printf("결과" + result);
        return result;
    }
}
