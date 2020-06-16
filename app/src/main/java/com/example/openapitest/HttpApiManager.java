package com.example.openapitest;

import android.util.Base64;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

public class HttpApiManager {


    private static final int    READ_TIME_OUT    = 15;
    private static final int    CONNECT_TIME_OUT = 15;
    private static final String BASE_URL         = "https://47.99.135.97:443/";

    private  Retrofit     mRetrofit;
    private  OkHttpClient mOkHttpClient;
    private  HttpPost   mHttpPost;

    public static HttpApiManager getInstance() {
        return Holder.instance;
    }

    private static class Holder{
        private static HttpApiManager   instance = new HttpApiManager();
    }

    private HttpApiManager() {
        this.initOkHttp();
        this.initRetrofit();
        this.initHttpPost();
    }

    public static String MD5(String strSrc) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] bt = new byte[0];
        try {
            bt = strSrc.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        md.update(bt);

        String strDes = bytes2Hex(md.digest()); // to HexString
        return strDes;
    }


    private static String bytes2Hex(byte[] bts) {
        StringBuffer des = new StringBuffer();
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }

    public  String Base64md5 ;
    public  String BaseSHA256;
    public  String currentTime;
    public String createSignature() throws UnsupportedEncodingException {

        final Calendar ca = Calendar.getInstance();
        System.out.println("current time:"+ca.getTime().toString());
        currentTime = ca.getTime().toString();

        JSONObject jsonbody = new JSONObject();
        String body = jsonbody.toString();
        Base64md5 = Constants.base64AndMD5(body.getBytes(Constants.ENCODING));
        String path = "/artemis/api/resource/v1/regions/root";
        String httpHeaders = "POST"+ "\n"
                +"*/*" + "\n"
                +Base64md5 + "\n"
                +HttpHeader.HTTP_HEADER_CONTENT_TYPE +"\n"
                +currentTime +"\n"
                +"x-ca-key:"+"27137055" +"\n"
                +path;
        System.out.println("httpheaders :"+httpHeaders);
        BaseSHA256 = HMACSHA256.sha256_HMAC(httpHeaders,"7Thl9Y2d8nehTalL7LaG");
//        BaseSHA256 = new String (Base64.encode(SHA256.getBytes("UTF-8"),Base64.NO_WRAP)).trim();
        System.out.println("xCASignature :"+BaseSHA256);
        return BaseSHA256;

    }


    private void initOkHttp() {

        final Calendar ca = Calendar.getInstance();
        try {
            String signature = createSignature();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(READ_TIME_OUT, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());

        builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request  = chain.request().newBuilder()
                            .addHeader("Accept", "*/*")
                            .addHeader("Content-MD5",Base64md5)
                            .addHeader("Content-Type","application/json")
                            .addHeader("Date",currentTime)
                            .addHeader("X-Ca-Key","27137055")
                            .addHeader("X-Ca-Signature",BaseSHA256)
                            .addHeader("X-Ca-Signature-Headers","x-ca-key")
                            .build();
                    return chain.proceed(request);
                }
        });


        this.mOkHttpClient = builder.build();
    }


    private void initRetrofit() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(BASE_URL);
        builder.client(this.mOkHttpClient);
        this.mRetrofit = builder.build();
    }
    private void initHttpPost() {
        this.mHttpPost = mRetrofit.create(HttpPost.class);
    }

    public HttpPost HttpPost() {
        return this.mHttpPost;
    }

    public static class SSLSocketClient {
        public static SSLSocketFactory getSSLSocketFactory() {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, getTrustManager(), new SecureRandom());
                return sslContext.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static TrustManager[] getTrustManager() {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }};
            return trustAllCerts;
        }

        public static HostnameVerifier getHostnameVerifier() {
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
            return hostnameVerifier;
        }
    }







}
