package baekkoji.smarthomeserver.dto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class Sensor
{
    private double temp; //실내 온도
    private double humid; //실내 습도
    private double pm; //실내 미세먼지
    private double API_PM; //실외 미세먼지
    private double API_PMGrade; //실외 미세먼지 등급
    private double API_temp; //실외 기온
    private double API_humid; //실외 습도

    public int ChangeStatus()
    { // 이 결과값 1이 client에서 서버에 request보내고 받는 응답이야!
        // 자 여기서 데이터 분석을 해봅시다!
        //실내외 온도 차이 15도 이상, 습도 60% 이상, 미세먼지 농도 81 ㎍/㎥ 이상)
        //API 참조해야해!
        if(temp-API_temp>=15 || humid>=60){
            //실내,외 온도 차이가 15도 이상이거나 실내 습도 60%이상일 경우
            //환기팬 작동
        }
        if(API_PMGrade>=3.0){
            //실외 미세먼지 등급이 나쁨 이상일 경우
            //미세먼지 농도 수치값 + 경고 알림 앱으로 전송해야함.
        }
        if(pm>=75.0){
            //실내 미세먼지 농도가 75 이상일 경우
            //
            //환기팬 작동
        }
        return 1;
    }

    public void APIData(String day)
    {
        StringBuffer Tempresult = new StringBuffer();
        try{
            StringBuilder urlBuilder_tmp = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/" +
                    "getUltraSrtNcst?serviceKey=Ovk4W7VO%2By140bj6hI2mVl5IAMamS%2BpIhGUfFnxWbnYbXNXMSSsCjVH2G6YTQSGmEf0%2BlGhlAt0Hz6x00dl5Pw%3D%3D" +
                    "&pageNo=1&numOfRows=100&dataType=JSON&base_date=20220729&base_time=1200&nx=60&ny=127");
            //날짜랑 시간 어떻게 해야할지 의논해야함. 공공데이터 업로드가 어떤 기준으로 되는지 알아보고 매개변수로 받든. 현재시간을 입력하든 알아서.
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
            try{
                JsonNode jsonNode = objectMapper.readTree(String.valueOf(Tempresult));
                this.setAPI_humid(jsonNode.get("response").get("body").get("items").get("item").get(1).get("obsrValue").asDouble());
                this.setAPI_temp(jsonNode.get("response").get("body").get("items").get("item").get(3).get("obsrValue").asDouble());
                System.out.println("\nAPI_humid: " + this.getAPI_humid()+"% , API_temp: " + this.getAPI_temp());
            }catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        //실외 미세먼지 농도랑 등급 참조
        StringBuffer PMresult = new StringBuffer();
        try
        {
            //종로구를 매개변수로 받아야함. 사용자별로 거주지가 상이하기 때문이다. 추후에 의논예정.
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty");
            urlBuilder.append("?" + URLEncoder.encode("stationName", "UTF-8") + "=" + URLEncoder.encode("종로구", "UTF-8"));
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
                this.setAPI_PMGrade(jsonNode.get("response").get("body").get("items").get(0).get("pm10Grade").asDouble());
                System.out.println("API_PM: " + this.getAPI_PM()+ " , API_PMGrade: " + this.getAPI_PMGrade());
            }catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}