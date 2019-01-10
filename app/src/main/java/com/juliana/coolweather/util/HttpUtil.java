package com.juliana.coolweather.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
/**
 * Created by JYUAN7 on 1/9/2019.
 */

public class HttpUtil {
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("mainActivity","sendHttpRequest address "+address);
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(address);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200){
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity,"utf-8");
                        if (listener!=null) {
                            listener.onFinish(response.toString());
                        }
                    }
                    /*URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    Log.i("mainActivity","url: "+url.toString());
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    if (connection.getResponseCode()==302) {
                        Log.i("mainActivity","response code is "+connection.getResponseCode());
                        String location = connection.getHeaderField("Location");
                        url = new URL(location);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        if (connection.getResponseCode()==200){
                            Log.i("mainActivity","response is ok");
                        }
                        else
                        {
                            Log.i("mainActivity","response code is " +connection.getResponseCode());
                        }
                    }
                    InputStream in = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);
                    BufferedReader reader = new BufferedReader(isr);
                    Log.i("mainActivity","reader: "+reader.readLine());
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine())!=null){
                        Log.i("mainActivity","line: "+line);
                        response.append(line);
                    }
                    if (listener!=null) {
                        listener.onFinish(response.toString());
                    }*/
                }catch (Exception e){
                    if (listener!=null) {
                        listener.onError(e);
                        Log.i("mainActivity", "error: " + e.getMessage());
                    }
                }/*finally {
                    if (listener!=null) {
                        connection.disconnect();
                    }
                }*/
            }
        }).start();
    }
}
