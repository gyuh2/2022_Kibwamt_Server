package com.example.smarthome0807;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private String url = "http://10.0.2.2:8081";
    private TextView textViewResult;
    private TextView textViewResult2;
    private TextView textViewResult3;
    private TextView textViewResult4;

    //인성씨 얼굴 보여주세요

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //res-layout-activity_main을 실행

        textViewResult = findViewById(R.id.textView);
        textViewResult2 = findViewById(R.id.textView2);
        textViewResult3 = findViewById(R.id.textView3);
        textViewResult4 = findViewById(R.id.textView4);

        Timer timer = new Timer();
        TimerTask tt = new TimerTask(){
            @Override
            public void run() {

                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit1 = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit1.create(JsonPlaceHolderApi.class);
                Call<Map<String,Float>> call = jsonPlaceHolderApi.getPosts();
                call.enqueue(new Callback<Map<String, Float>>() {
                    @Override
                    public void onResponse(Call<Map<String,Float>> call, Response<Map<String,Float>> response) {
                        //System.out.println(response.body().toString());
                        if (!response.isSuccessful()) {
                            textViewResult.setText("Code: " + response.code());
                            return;
                        }

                        Map<String,Float> posts = new HashMap<>();
                        posts = response.body();
                        String content = "";

                        content += "\n" + posts.get("temp") + "°C" + " / " + posts.get("humid") + "%";
                        textViewResult.append(content);

                        content = "";
                        content += "\n" + posts.get("API_temp") + "°C" + " / " + posts.get("API_humid") + "%";
                        textViewResult2.append(content);
                        content = "";
                        if(posts.get("pmGrade").intValue() == 1){//실내 미세먼지 등급
                            content += "\n" + "좋음 / " + posts.get("pm") + "㎍/㎥";
                        }else if(posts.get("pmGrade").intValue() == 2){
                            content += "\n" + "보통 / " + posts.get("pm") + "㎍/㎥";
                        }else if(posts.get("pmGrade").intValue() == 3){
                            content += "\n" + "나쁨 / " + posts.get("pm") + "㎍/㎥";
                        }else if(posts.get("pmGrade").intValue() == 4){
                            content += "\n" + "매우 나쁨 / " + posts.get("pm") + "㎍/㎥";
                        }else{
                            content += "\n" + "등급 산정 중 / " + posts.get("pm") + "㎍/㎥";
                        }
                        textViewResult3.append(content);
                        content = "";
                        if(posts.get("API_PMGrade").intValue() == 1){
                            content += "\n" + "좋음 / " + posts.get("API_PM") + "㎍/㎥";
                        }else if(posts.get("API_PMGrade").intValue() == 2){
                            content += "\n" + "보통 / " + posts.get("API_PM") + "㎍/㎥";
                        }else if(posts.get("API_PMGrade").intValue() == 3){
                            content += "\n" + "나쁨 / " + posts.get("API_PM") + "㎍/㎥";
                            showDusty(); //미세먼지 나쁨일 경우, 경고 알림 (상단바)
                        }else if(posts.get("API_PMGrade").intValue() == 4){
                            content += "\n" + "매우 나쁨 / " + posts.get("pm") + "㎍/㎥";
                            showDusty(); //미세먼지 나쁨일 경우, 경고 알림 (상단바)
                        }else{
                            content += "\n" + "등급 산정 중 / " + posts.get("API_PM") + "㎍/㎥";
                        }
                        textViewResult4.append(content);
                    }
                    //DB 읽어오는 구문 (실내외 온습도 및 미세먼지 정보)
                    @Override
                    public void onFailure(Call<Map<String, Float>> call, Throwable t) {
                        textViewResult.setText(t.getMessage());
                    }
                });
                textViewResult.setText("실내 온습도");
                textViewResult2.setText("실외 온습도");
                textViewResult3.setText("실내 미세먼지");
                textViewResult4.setText("실외 미세먼지");
            }
        };
        timer.schedule(tt,0,3000000);

    }//end of onCreate() Method



    //On 버튼 클릭 시 호출되는 함수
    public void windowController(View view) {
        //Onclick Method (JSON 데이터 송신)
        EditText editText = (EditText)findViewById(R.id.editTextNumber1);
        String text = editText.getText().toString().trim();
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        if(text.getBytes().length <= 0){
            String msg = "값을 입력해주십시오. (입력 범위 : 1°~90°)";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }else{
            int num = Integer.parseInt(editText.getText().toString());
            if (num>0 && num<=90) { //값이 null이 아니거나 1° ~ 90° 사이의 값일 때만 수행
                try {
                    controlDataInfo.setAngle(num);
                    controlDataInfo.setWindowUp(1);
                    //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

                    Retrofit retrofit2 = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    PostApi postApi = retrofit2.create(PostApi.class);
                    Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
                    call1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            System.out.println(response.message());

                            if (!response.isSuccessful()) {
                                //String i = "OK";
                                //editText.setText(i);
                                editText.setText(String.valueOf(response.code()));
                                //Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            ResponseBody result;
                            result = response.body();
                            if(result != null){
                                Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                                editText.setText(null);

                            }else{
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                //다시 입력 메시지 출력
                String msg = "다시 입력해주십시오. (입력 범위 : 1°~90°)";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        }
    }//end of windowController() Method

    public void heaterController(View view) {
        //Onclick Method (JSON 데이터 송신)
        EditText editText = (EditText)findViewById(R.id.editTextNumber2);
        String text = editText.getText().toString().trim();
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        if(text.getBytes().length <= 0){
            String msg = "값을 입력해주십시오. (입력 범위 : 25°C~)";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }else{
            int num = Integer.parseInt(editText.getText().toString());
            if (num>=25) { //값이 null이 아니거나 10°C~30°C 사이의 값일 때만 수행
                try {
                    controlDataInfo.setHeater_temp(num);
                    controlDataInfo.setHeater(1);
                    //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

                    Retrofit retrofit2 = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    PostApi postApi = retrofit2.create(PostApi.class);
                    Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
                    call1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            System.out.println(response.message());

                            if (!response.isSuccessful()) {
                                //String i = "OK";
                                //editText.setText(i);
                                editText.setText(String.valueOf(response.code()));
                                //Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            ResponseBody result;
                            result = response.body();
                            if(result != null){
                                Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                                editText.setText(null);
                            }else{
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                //다시 입력 메시지 출력
                String msg = "다시 입력해주십시오. (입력 범위 : 25°C~)";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        }
    }//end of heaterController() Method

    public void acController(View view) {
        //Onclick Method (JSON 데이터 송신)
        EditText editText = (EditText)findViewById(R.id.editTextNumber3);
        String text = editText.getText().toString().trim();
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        if(text.getBytes().length <= 0){
            String msg = "값을 입력해주십시오. (입력 범위 : 10°C~)";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }else{
            int num = Integer.parseInt(editText.getText().toString());
            if (num >= 10) { //값이 null이 아니거나 10°C~ 사이의 값일 때만 수행
                try {
                    controlDataInfo.setAc_temp(num);
                    controlDataInfo.setAc(1);
                    //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

                    Retrofit retrofit2 = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    PostApi postApi = retrofit2.create(PostApi.class);
                    Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
                    call1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            System.out.println(response.message());

                            if (!response.isSuccessful()) {
                                editText.setText(String.valueOf(response.code()));
                                //Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            ResponseBody result;
                            result = response.body();
                            if(result != null){
                                Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                                editText.setText(null);
                            }else{
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                //다시 입력 메시지 출력
                String msg = "다시 입력해주십시오. (입력 범위 : 10°C~)";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        }
    }//end of acController() Method

    public void AirController(View view) {
        //Onclick Method (JSON 데이터 송신)
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        try {
            controlDataInfo.setAirCleaner(1);
            //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi postApi = retrofit2.create(PostApi.class);
            Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
            call1.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.message());

                    if (!response.isSuccessful()) {
                        //Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    ResponseBody result;
                    result = response.body();
                    if(result != null){
                        Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of AirController() Method 공기

    public void fanController(View view) {
        //Onclick Method (JSON 데이터 송신)
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        try {
            controlDataInfo.setAirOut(1);
            //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi postApi = retrofit2.create(PostApi.class);
            Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
            call1.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.message());

                    if (!response.isSuccessful()) {
                        //Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    ResponseBody result;
                    result = response.body();
                    if(result != null){
                        Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of fanController() Method 환기

    public void doorController(View view) {
        //Onclick Method (JSON 데이터 송신)
        EditText editText = (EditText)findViewById(R.id.editText);
        String text = editText.getText().toString().trim();
        String num2 = editText.getText().toString();
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        if(text.getBytes().length <= 0){
            String msg = "값을 입력해주십시오.";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }else{
                try {
                    controlDataInfo.setDoor_passwd(num2);
                    controlDataInfo.setDoor(1);
                    //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

                    Retrofit retrofit2 = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    PostApi postApi = retrofit2.create(PostApi.class);
                    Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
                    call1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            System.out.println(response.message());

                            if (!response.isSuccessful()) {
                                //String i = "OK";
                                //editText.setText(i);
                                editText.setText(String.valueOf(response.code()));
                                Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            ResponseBody result;
                            result = response.body();
                            if(result != null){
                                Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                                editText.setText(null);

                            }else{
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }//end of doorController() Method



    //Off 버튼 클릭 시 호출되는 함수
    public void windowOff(View view) {
        //Onclick Method (JSON 데이터 송신)
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        try {
            controlDataInfo.setAngle(0);
            controlDataInfo.setWindowUp(0);
            //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi postApi = retrofit2.create(PostApi.class);
            Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
            call1.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.message());

                    if (!response.isSuccessful()) {
                        //Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    ResponseBody result;
                    result = response.body();
                    if(result != null){
                        Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of windowOff() Method

    public void heaterOff(View view) {
        //Onclick Method (JSON 데이터 송신)
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        try {
            controlDataInfo.setHeater(0);
            //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi postApi = retrofit2.create(PostApi.class);
            Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
            call1.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.message());

                    if (!response.isSuccessful()) {
                        //Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    ResponseBody result;
                    result = response.body();
                    if(result != null){
                        Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of heaterOff() Method

    public void acOff(View view) {
        //Onclick Method (JSON 데이터 송신)
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        try {
            controlDataInfo.setAc(0);
            //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi postApi = retrofit2.create(PostApi.class);
            Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
            call1.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.message());

                    if (!response.isSuccessful()) {
                        //Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    ResponseBody result;
                    result = response.body();
                    if(result != null){
                        Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of acOff() Method

    public void AirOff(View view) {
        //Onclick Method (JSON 데이터 송신)
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        try {
            controlDataInfo.setAirCleaner(0);
            //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi postApi = retrofit2.create(PostApi.class);
            Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
            call1.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.message());

                    if (!response.isSuccessful()) {
                        //Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    ResponseBody result;
                    result = response.body();
                    if(result != null){
                        Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of AirOff() Method 공기

    public void fanOff(View view) {
        //Onclick Method (JSON 데이터 송신)
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        try {
            controlDataInfo.setAirOut(0);
            //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi postApi = retrofit2.create(PostApi.class);
            Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
            call1.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.message());

                    if (!response.isSuccessful()) {
                        //Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    ResponseBody result;
                    result = response.body();
                    if(result != null){
                        Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of fanOff() Method 환기

    public void doorOff(View view) {
        //Onclick Method (JSON 데이터 송신)
        EditText editText = (EditText)findViewById(R.id.editText);
        String text = editText.getText().toString().trim();
        String num2 = editText.getText().toString();
        ControlDataInfo controlDataInfo = new ControlDataInfo();

        if(text.getBytes().length <= 0){
            String msg = "값을 입력해주십시오.";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }else{
            try {
                controlDataInfo.setDoor_passwd(num2);
                controlDataInfo.setDoor(0);
                //Toast.makeText(getApplicationContext(), controlDataInfo.toString(), Toast.LENGTH_LONG).show();

                Retrofit retrofit2 = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                PostApi postApi = retrofit2.create(PostApi.class);
                Call<ResponseBody> call1 = postApi.getControlResult(controlDataInfo);
                call1.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        System.out.println(response.message());

                        if (!response.isSuccessful()) {
                            editText.setText(String.valueOf(response.code()));
                            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ResponseBody result;
                        result = response.body();
                        if(result != null){
                            Toast.makeText(getApplicationContext(), "원격 제어 요청 완료되었습니다", Toast.LENGTH_LONG).show();
                            editText.setText(null);

                        }else{
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//end of doorOff() Method



    //미세먼지 경고 알림 수행 함수
    public void showDusty() {
        //미세먼지 경고 알림

        //알림(Notification)을 관리하는 관리자 객체를 운영체제(Context)로부터 소환하기
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Notification 객체를 생성해주는 건축가객체 생성(AlertDialog 와 비슷)
        NotificationCompat.Builder builder= null;

        //Oreo 버전(API26 버전)이상에서는 알림시에 NotificationChannel 이라는 개념이 필수 구성요소가 됨.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String channelID="channel_01"; //알림채널 식별자
            String channelName="MyChannel01"; //알림채널의 이름

            //알림채널 객체 만들기
            NotificationChannel channel= new NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_DEFAULT);
            //알림매니저에게 채널 객체의 생성을 요청
            notificationManager.createNotificationChannel(channel);
            //객체 생성
            builder=new NotificationCompat.Builder(this, channelID);

        }else{
            builder= new NotificationCompat.Builder(this, (Notification) null);
        }

        //원하는 알림의 설정작업
        builder.setSmallIcon(android.R.drawable.ic_menu_view);

        //알림창(확장 상태바)의 설정
        builder.setContentTitle("경고 알림");//알림창 제목
        builder.setContentText("미세먼지 수치가 나쁨입니다.");//알림창 내용

        Bitmap bm= BitmapFactory.decodeResource(getResources(), R.drawable.window);
        builder.setLargeIcon(bm); //매개변수가 Bitmap을 줘야한다.

        //건축가에게 알림 객체 생성하도록
        Notification notification=builder.build();

        //알림매니저에게 알림(Notify) 요청
        notificationManager.notify(1, notification);

    }//end of showDusty() Method

}