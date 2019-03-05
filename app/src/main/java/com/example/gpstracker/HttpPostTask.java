package com.example.gpstracker;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPostTask extends AsyncTask<String, String, String> {

    Context context;
    Location location;
//    TextView txtUnlock;
//    String dataToPost;
//    String room;
//    String unlockText = "Room unlocked!";
//    String lockText = "Room stays shut";

    public HttpPostTask(Context context, Location location) {
        this.context = context;
        this.location = location;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        //String urlString = params[0]; // URL to call
        //String data = params[1]; //data to post
        String urlString = "https://ptsv2.com/t/1mc22-1551179837/post" /*"http://52.56.153.134:8080/api/room/" + room + "/unlock"*/;
        String data = "TODO" /*dataToPost*/;
        OutputStream out = null;

        String response = "";
        String outputText = "";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("latitude", location.getLatitude());
            jsonParam.put("longitude", location.getLongitude());
//            jsonParam.put("studentId", dataToPost);

            Log.i("JSON", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            InputStream in = conn.getInputStream();
            InputStreamReader inReader = new InputStreamReader(in);

            int inputStreamData = inReader.read();
            while (inputStreamData != -1) {
                char currentData = (char) inputStreamData;
                inputStreamData = inReader.read();
                response += currentData;
            }

            //JSONObject jsonResponse = new JSONObject(response);
            /*boolean unlock = jsonResponse.getBoolean("unlock");
            if (unlock) {
                outputText = unlockText;
            } else {
                outputText = lockText;
            }*/

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG" , conn.getResponseMessage());

            response = conn.getResponseCode() + ": " + conn.getResponseMessage();

            conn.disconnect();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return outputText;
    }

    @Override
    protected void onPostExecute(String unlocked) {
        //Toast.makeText(context, "TODO", Toast.LENGTH_SHORT).show();
        /*(if (unlocked.equals(unlockText)) {
            txtUnlock.setTextColor(0xFF00FF00);
        } else {
            txtUnlock.setTextColor(0xFFFF0000);
        }
        txtUnlock.setText(unlocked);*/
    }
}