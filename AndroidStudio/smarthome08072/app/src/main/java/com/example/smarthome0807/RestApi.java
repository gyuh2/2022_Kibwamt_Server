package com.example.smarthome0807;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestApi {
    @GET("api/boards")
    Call<Map<String,String>> getPosts();
}
