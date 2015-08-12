package com.bsreeinf.jobapp.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bsreeinf on 03/07/15.
 */
public class Server {
    public static JsonObject makeServerRequest(String requestMethod, String requestMode, JsonObject jsonToSend) {
        String serverResponseStream = null;
        try {
            String strURL = "";
            switch (requestMode) {
                case "init":
                    strURL = Commons.URL_INIT;
            }
            URL url = new URL(strURL);
            HttpURLConnection con = (HttpURLConnection) url
                    .openConnection();
            con.setRequestMethod(requestMethod);
            con.setDoOutput(true);
            if (jsonToSend != null) {
                con.setRequestProperty("Content-Type", "application/json");
                OutputStream os = con.getOutputStream();
                os.write(jsonToSend.toString().getBytes("UTF-8"));
                os.close();
            }
            serverResponseStream = readStream(con.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonParser().parse(serverResponseStream).getAsJsonObject();
    }

    private static String readStream(InputStream in) {
        String serverOutput = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                serverOutput += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return serverOutput;
    }
}
