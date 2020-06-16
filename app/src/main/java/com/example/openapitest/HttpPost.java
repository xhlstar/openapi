package com.example.openapitest;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HttpPost {
    @POST("artemis/api/resource/v1/regions/root")
    Call<ResponseBody> HttpPost(@Body RequestBody params);
}
