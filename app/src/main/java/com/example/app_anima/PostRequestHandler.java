package com.example.app_anima;

import android.os.AsyncTask;

import java.util.HashMap;

public class PostRequestHandler extends AsyncTask<Void, Void, String> {
    String url;
    HashMap<String, String> requestedParams;

    PostRequestHandler(String url, HashMap<String, String> params){
        this.url = url;
        this.requestedParams = params;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(Void... voids) {

        return null;
    }
}
