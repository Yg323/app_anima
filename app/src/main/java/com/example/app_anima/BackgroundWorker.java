package com.example.app_anima;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class BackgroundWorker {
    public String postRequestHandler(String requestUrl, HashMap<String, String> requestedDataParams) throws UnsupportedEncodingException {
        URL url;

        StringBuilder stringBuilder = new StringBuilder();

        try {
            url = new URL(requestUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(Constant.POST_METHOD);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            StringBuilder url_string = new StringBuilder();

            boolean ampersand = false;
            for (Map.Entry<String, String> params : requestedDataParams.entrySet()) {
                if (ampersand)
                    url_string.append("&");
                else ampersand = true;

                url_string.append(URLEncoder.encode(params.getKey(), "UTF-8"));
                url_string.append("=");
                url_string.append(URLEncoder.encode(params.getValue(), "UTF-8"));
            }
            Log.d("Final Url===", url_string.toString());

            OutputStream outputStream = connection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(url_string.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            Log.d("Response===", connection.getResponseMessage());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String result;

                while ((result = bufferedReader.readLine()) != null) {
                    stringBuilder.append(result);
                }

                Log.d("Result===", result);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
