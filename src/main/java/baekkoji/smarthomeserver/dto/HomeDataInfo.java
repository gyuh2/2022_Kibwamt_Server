package baekkoji.smarthomeserver.dto;

import lombok.Data;
import org.json.JSONObject;

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

    public Map<String,Float> getHomeDataInfo() throws SQLException {
        Map<String, Float> HomeData= new HashMap<>();
        // 'HomeDataInfo Table'에서 참조해서 HomeData 변수에 저장하여 return

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from HomeDataInfo where id='chayoung';");
        //임의로 id 변경가능.

        while(resultSet.next()) {
            float pm = resultSet.getFloat("pm");
            float API_PMGrade =resultSet.getFloat("API_PMGrade");

            HomeData.put("temp", resultSet.getFloat("temp"));
            HomeData.put("humid", resultSet.getFloat("humid"));
            HomeData.put("pm", resultSet.getFloat("pm"));
            HomeData.put("pmGrade", (float) resultSet.getInt("pmGrade"));
            HomeData.put("API_temp", resultSet.getFloat("API_temp"));
            HomeData.put("API_humid", resultSet.getFloat("API_humid"));
            HomeData.put("API_PM", resultSet.getFloat("API_PM"));
            HomeData.put("API_PMGrade", (float) resultSet.getInt("API_PMGrade"));

            if(API_PMGrade>=3){
                // 실외 미세먼지 경고 알림.
                HomeData.put("API_pmWarn", 1.0F);
            }
        }
        resultSet.close();
        statement.close();
        connection.close();
        return HomeData;
    }


}
