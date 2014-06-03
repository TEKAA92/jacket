package com.github.jacketapp.app;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.ContentHandler;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

/**
 * Created by hansrodtang on 28/05/14.
 */
class Message {
    public String Content;
    public int Code;

    Message(String message, int code){
        this.Content = message;
        this.Code = code;
    }
}

class ReadJSON extends AsyncTask<String, Void, Message> {
    JSONObject jsonObject;

    protected Message doInBackground(String... input) {

// Depends on your web service
        //httppost.setHeader("Content-type", "application/json");

        InputStream inputStream = null;
        String result = null;
        try {
            URL url = new URL(input[0]);
            InputStream stream = url.openStream();

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(stream));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            result =  sb.toString();

            try {
                jsonObject = new JSONObject(result);
                Message msg = new Message(jsonObject.getString("message"), jsonObject.getInt("code"));
                return msg;
            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            // Oops
        }
        finally {
            try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
        }

        return new Message("", 0);
    }

    protected void onPostExecute(Message msg) {
        super.onPostExecute(msg);
    }
}