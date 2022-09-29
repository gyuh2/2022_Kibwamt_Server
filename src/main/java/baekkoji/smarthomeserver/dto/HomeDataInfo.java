package baekkoji.smarthomeserver.dto;

import lombok.Data;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        ResultSet resultSet = statement.executeQuery("select * from HomeDataInfo where id='baekkoji';");
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

    // App의 Main 페이지 업데이트를 위한 공공데이터 참조.
    public Map<String, Float> getMainDataInfo() throws SQLException {

        Map<String, Float> MainData= new HashMap<>();
        MainData = this.getHomeDataInfo();

        // 현재날씨, 강수확률
        String key = "Ovk4W7VO%2By140bj6hI2mVl5IAMamS%2BpIhGUfFnxWbnYbXNXMSSsCjVH2G6YTQSGmEf0%2BlGhlAt0Hz6x00dl5Pw%3D%3D";//OpenAPI인증키

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sd = new SimpleDateFormat("HH");

        String day = sdf.format(date);
        String time = sd.format(date);

        int pop = 0;
        int sky = 0;

        StringBuffer Tempresult = new StringBuffer();
        StringBuilder urlBuilder_tmp = new StringBuilder();

        urlBuilder_tmp = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/" +"getVilageFcst?serviceKey=" + key + "&pageNo=1&numOfRows=100&dataType=JSON&base_date=" + day + "&base_time=0500&nx=60&ny=127");

        try {
            URL url = new URL(urlBuilder_tmp.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            }else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line;
            while ((line = rd.readLine()) != null) {
                Tempresult.append(line + "\n");
            }
            rd.close();
            conn.disconnect();
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(String.valueOf(Tempresult));
            pop = jsonNode.get("response").get("body").get("items").get("item").get(5).get("fcstValue").asInt();
            sky = (jsonNode.get("response").get("body").get("items").get("item").get(7).get("fcstValue").asInt());
            MainData.put("pop",(float)pop);
            MainData.put("sky",(float)sky);
            System.out.println(pop +", " + sky);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return MainData;
    }
}
