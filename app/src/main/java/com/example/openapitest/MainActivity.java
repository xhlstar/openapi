package com.example.openapitest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            posttest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void posttest() throws JSONException {

        JSONObject jsonbody =  new JSONObject();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonbody.toString() );
        HttpApiManager.getInstance().HttpPost().HttpPost(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        String string = response.body().string();
                    } else {

                        Log.e("reponse error", "what wrong");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

}
