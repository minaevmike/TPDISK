package com.example.mike.tpdisk;

/**
 * Created by Mike on 26.10.2014.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mike on 29.09.2014.
 */
public class Connector {
    public static String getByUrl(String url){
        Map<String, String> h = new HashMap<String, String>();
        return getByUrl(url, h);
    }

    public static String getByUrl(String surl, Map<String, String> headers){
        String answer = null;
        try {
            URL url = new URL(surl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            for(Map.Entry<String, String> entry:headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream in = connection.getInputStream();
                answer = handleInputStream(in);
            }
            connection.disconnect();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return answer;
    }

    private static String handleInputStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String result = "", line = "";
        while ((line = reader.readLine()) != null) {
            result += line;
        }
        return result;
    }
}
