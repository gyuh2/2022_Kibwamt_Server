package baekkoji.smarthomeserver.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
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

    // homeData 참조
    public Map<String,String> getHomeDataInfo(String id) {
        id = id.replaceAll("[\"]", "");
        Map<String, String> HomeData= new HashMap<>();
        // 'HomeDataInfo Table'에서 참조해서 HomeData 변수에 저장하여 return

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            PreparedStatement pstmt = null;

            // 주소 참조 위한 코드
            String sql = "select address from Users where id=?;";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                HomeData.put("address",rs.getString("address"));
            }

            //주소 외에 HomeData 참조
            sql = "select * from HomeDataInfo where id=?;";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,id);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String pm = String.valueOf(rs.getFloat("pm"));
                float API_PMGrade = rs.getFloat("API_PMGrade");

                HomeData.put("temp", String.valueOf(rs.getFloat("temp")));
                HomeData.put("humid", String.valueOf(rs.getFloat("humid")));
                HomeData.put("pm", String.valueOf(rs.getFloat("pm")));
                HomeData.put("pmGrade", String.valueOf(rs.getInt("pmGrade")));
                HomeData.put("API_temp", String.valueOf(rs.getFloat("API_temp")));
                HomeData.put("API_humid", String.valueOf(rs.getFloat("API_humid")));
                HomeData.put("API_PM", String.valueOf(rs.getFloat("API_PM")));
                HomeData.put("API_PMGrade", String.valueOf(rs.getInt("API_PMGrade")));

                if(API_PMGrade>=3){
                    // 실외 미세먼지 경고 알림.
                    HomeData.put("API_pmWarn", "1.0F");
                }
            }
            rs.close();
            pstmt.close();
            connection.close();
        }catch (Exception e){
            System.out.println(e);
        }
        return HomeData;
    }

    // App의 Main 페이지 업데이트를 위한 공공데이터 참조.
    public Map<String, String> getMainDataInfo(String id) throws SQLException {

        Map<String, String> MainData= new HashMap<>();
        MainData = this.getHomeDataInfo(id);

        //현재날씨, 강수확률
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

        urlBuilder_tmp =
                new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/" +"getVilageFcst?serviceKey=" + key + "&pageNo=1&numOfRows=100&dataType=JSON&base_date=" + day + "&base_time=0500&nx=60&ny=127");

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
            sky = jsonNode.get("response").get("body").get("items").get("item").get(5).get("fcstValue").asInt();
            pop = (jsonNode.get("response").get("body").get("items").get("item").get(7).get("fcstValue").asInt());

            MainData.put("pop",String.valueOf(pop));
            MainData.put("sky",String.valueOf(sky));

            System.out.println(MainData.get("pop"));
            System.out.println(MainData.get("sky"));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return MainData;
    }


}
