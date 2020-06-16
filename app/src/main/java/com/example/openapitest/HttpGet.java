package com.example.openapitest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HttpGet {

    //@GET("?role=xhl")
    //Call<ResponseBody> HttpGet();
    @GET("getCurrentRoom")
    Call<ResponseBody> HttpGet(@Query("accountName") String role);

}
