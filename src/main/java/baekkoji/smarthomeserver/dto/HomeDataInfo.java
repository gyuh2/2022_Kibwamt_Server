package baekkoji.smarthomeserver.dto;

import lombok.Data;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Data
public class HomeDataInfo {
    private String id;
    private float temp;
    private float humid;
    private float pm;
    private int pmGrade;
    private float API_PM;
    private int API_PMGrade;
    private float API_temp;
    private float API_humid;

    String url = "jdbc:mysql://database-baekkoji.ccp9kadfy1fx.ap-northeast-2.rds.amazonaws.com:3306/smarthome";
    String userName = "admin";
    String password = "baekkoji";

    public Map<String,String> getHomeDataInfo() throws SQLException {
        Map<String, String> HomeData= new HashMap<>();
        // 'HomeDataInfo Table'에서 참조해서 HomeData 변수에 저장하여 return

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from Users");

        while(resultSet.next()) {
            int pm = Integer.getInteger(resultSet.getString("pm"));
            int API_PMGrade = Integer.getInteger(resultSet.getString("API_PMGrade"));

            HomeData.put("id", resultSet.getString("id"));
            HomeData.put("temp", resultSet.getString("temp"));
            HomeData.put("humid", resultSet.getString("humid"));
            HomeData.put("pm", resultSet.getString("pm"));
            HomeData.put("pmGrade", resultSet.getString("pmGrade"));
            HomeData.put("API_temp", resultSet.getString("API_temp"));
            HomeData.put("API_humid", resultSet.getString("API_humid"));
            HomeData.put("API_PM", resultSet.getString("API_PM"));
            HomeData.put("API_PMGrade", resultSet.getString("API_PMGrade"));

            if(pm>=71){ // 미세먼지 농도가 실내 75이상 / 실외 81이상 일 경우 경고 알림.
                // 실내 미세먼지 경고 알림.
                HomeData.put("pmWarn", "1");
            }
            if(API_PMGrade>=3){
                // 실외 미세먼지 경고 알림.
                HomeData.put("API_pmWarn", "1");
            }
        }
        resultSet.close();
        statement.close();
        connection.close();

        /*HomeData.put("id", "sally");
        HomeData.put("temp", "22.0");
        HomeData.put("humid", "65.0");
        HomeData.put("pm", "12.98");
        HomeData.put("pmGrade", "1");
        HomeData.put("API_PM", "14.0");
        HomeData.put("API_PMGrade", "1");
        HomeData.put("API_temp", "26.0");
        HomeData.put("API_humid", "90.0");*/
        return HomeData;
    }

    public int controlHome(Map<Integer,Boolean> controlData){
        int result = 0;

        return result;
    }
}
