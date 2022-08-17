package baekkoji.smarthomeserver.dto;

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
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class Sensor
{
    private String id;
    private double temp; //실내 온도
    private double humid; //실내 습도
    private double pm; //실내 미세먼지
    private int pmGrade; // 실내 미세먼지 등급
    private double API_PM; //실외 미세먼지
    private int API_PMGrade; //실외 미세먼지 등급
    private double API_temp; //실외 기온
    private double API_humid; //실외 습도

    String url = "jdbc:mysql://database-baekkoji.ccp9kadfy1fx.ap-northeast-2.rds.amazonaws.com:3306/smarthome";
    String userName = "admin";
    String password = "baekkoji";

    public void setPmGrade(){
        //10, 10-15, 15-22.5 , 22.5-30
        if(pm >= 0.0 && pm < 10.0){
            this.setPmGrade(1);
        }else if(pm >= 10.0 && pm < 15.0){
            this.setPmGrade(2);
        }else if(pm >= 15.0 && pm < 22.5){
            this.setPmGrade(3);
        }else if(pm >= 22.5){
            this.setPmGrade(4);
        }else { // pm<0에 해당되는 값 (음수)
            this.setPmGrade(0);
        }
    }

    public void setDataAll() throws SQLException {
        if(API_PM==0 || API_PMGrade==0){return;} // 공공데이터 참조 오류날 경우 DB에 저장X
        setPmGrade(); //실내 미세먼지 판단 후 변수에 저장.

        Connection connection = DriverManager.getConnection(url, userName, password);
        Statement statement = connection.createStatement();
        PreparedStatement pstmt = null;

        //맨처음에는 insert하고, 그 후에는 update해야함
        String sql = "update HomeDataInfo set temp=?, humid=?, pm=?, pmGrade=?, API_temp=?, API_humid=?, API_PM=?, API_PMGrade=? where id=?;";
        pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setFloat(1, (float)this.temp);
        pstmt.setFloat(2, (float)this.humid);
        pstmt.setFloat(3, (float)this.pm);
        pstmt.setInt(4, this.pmGrade);
        pstmt.setFloat(5, (float)this.API_temp);
        pstmt.setFloat(6, (float)this.API_humid);
        pstmt.setFloat(7, (float)this.API_PM);
        pstmt.setInt(8, this.API_PMGrade);
        pstmt.setString(9, "chayoung"); //id 임의로

        pstmt.executeUpdate();

        statement.close();
        connection.close();
    }

    public String ChangeStatus()
    {
        //실내외 온도 차이 15도 이상, 습도 60% 이상, 실내 미세먼지 농도 15㎍/㎥ 이상, 실외는 81 이상)
        if( (Math.abs(temp-API_temp)>=15) || (humid>=60)){
            if(pm>=15.0){ //15 실내 청정기준이므로 15이상일 시 나쁨.
                return "1a"; //환기팬 on, 실링팬 on
            }else {
                return "2b"; //환기팬 on, 실링팬 off
            }
        }else {
            if (pm >= 15.0) {
                return "3c"; //환기팬 off, 실링팬 on
            } else {
                return "4c"; //환기팬 off, 실링팬 off
            }
        }
    }

    public void APIData()
    {
        String day; //오늘 날짜 저장
        Date today = new Date(); //날짜 형식
        SimpleDateFormat today_format = new SimpleDateFormat("yyyyMMdd");
        day = today_format.format(today); //날짜 형식에 맞게 저장.

        LocalTime now = LocalTime.now();
        int minute = now.getMinute(); //현재 분

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");
        String time = now.format(formatter);
        int hour = Integer.parseInt(time);

        StringBuffer Tempresult = new StringBuffer();
        StringBuilder urlBuilder_tmp = new StringBuilder();

        if(minute>=0 && minute<=40){
            // 초단기예보 //
            hour -= 1; //초딘기예보에서는 현재 시간에서 -1시간을 해야함.
            //time = String.valueOf(hour);
            time = String.format("%02d", hour);
            urlBuilder_tmp = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst?serviceKey=Ovk4W7VO%2By140bj6hI2mVl5IAMamS%2BpIhGUfFnxWbnYbXNXMSSsCjVH2G6YTQSGmEf0%2BlGhlAt0Hz6x00dl5Pw%3D%3D" +
                    "&numOfRows=100&pageNo=1&dataType=JSON&base_date="+day+"&base_time="+time+"00&nx=60&ny=127");
            //위도 경도도 나중에 DB에서 가져와야 함.
        }else {
            // 초단기실황 //
            // 8시 정보는 8시 30분에 생성되어 8시 40분에 API에 반영이 된다. 40분 보다 더 일찍 반영되는 경우도 있음.
            // ** 정시부터 40분전까지는 어떤 정보를 통해 데이터를 분석할지? 초단기예보 정보 활용하기로 함. 해결 완료.
            urlBuilder_tmp = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/" +
                    "getUltraSrtNcst?serviceKey=Ovk4W7VO%2By140bj6hI2mVl5IAMamS%2BpIhGUfFnxWbnYbXNXMSSsCjVH2G6YTQSGmEf0%2BlGhlAt0Hz6x00dl5Pw%3D%3D" +
                    "&pageNo=1&numOfRows=100&dataType=JSON&base_date=" + day + "&base_time=" + time + "00&nx=60&ny=127");
            //위도 경도도 나중에 DB에서 가져와야 함.
        }
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
            // 24번이 temp
            try{
                JsonNode jsonNode = objectMapper.readTree(String.valueOf(Tempresult));
                if(minute>=0 && minute<=40) {
                    this.setAPI_humid(jsonNode.get("response").get("body").get("items").get("item").get(30).get("fcstValue").asDouble());
                    this.setAPI_temp(jsonNode.get("response").get("body").get("items").get("item").get(24).get("fcstValue").asDouble());
                    System.out.println("\nAPI_humid: " + this.getAPI_humid() + "% , API_temp: " + this.getAPI_temp() + "℃");
                }else {
                    this.setAPI_humid(jsonNode.get("response").get("body").get("items").get("item").get(1).get("obsrValue").asDouble());
                    this.setAPI_temp(jsonNode.get("response").get("body").get("items").get("item").get(3).get("obsrValue").asDouble());
                    System.out.println("\nAPI_humid: " + this.getAPI_humid() + "% , API_temp: " + this.getAPI_temp() + "℃");
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        //실외 미세먼지 농도랑 등급 참조
        String address = getAddress();
        StringBuffer PMresult = new StringBuffer();
        try
        {
            // 측정소별 실시간 측정정보 조회 //
            // 종로구를 매개변수로 받아야함. 사용자별로 거주지가 상이하기 때문이다. 추후에 의논예정.
            //
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty");
            urlBuilder.append("?" + URLEncoder.encode("stationName", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("dataTerm", "UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&returnType=json");
            urlBuilder.append("&" + URLEncoder.encode("serviceKey", "UTF-8") + "=hlW83HNEAywzq1LaUYFqxBsWVBL2%2B6d2vmBpcPRptPQJCCJVyhpARwdu7eR7o%2BLOzlt1nCFhtmYPiP%2BDSv9cMw%3D%3D");

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
            {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else
            {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String line;
            while ((line = rd.readLine()) != null)
            {
                PMresult.append(line + "\n");
            }
            rd.close();
            conn.disconnect();

            ObjectMapper objectMapper = new ObjectMapper();
            try{
                JsonNode jsonNode = objectMapper.readTree(String.valueOf(PMresult));
                this.setAPI_PM((jsonNode.get("response").get("body").get("items").get(0).get("pm10Value").asDouble()));
                this.setAPI_PMGrade(jsonNode.get("response").get("body").get("items").get(0).get("pm10Grade").asInt());
                System.out.println("API_PM: " + this.getAPI_PM()+ " , API_PMGrade: " + this.getAPI_PMGrade());
            }catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private String getAddress() {

        return "종로구";
    }

}