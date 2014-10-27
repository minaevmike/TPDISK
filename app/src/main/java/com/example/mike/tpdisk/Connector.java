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
    public void setMethod(String method) {
        this.method = method;
    }

    private String method = null;

    public void setUrl(String url) {
        this.url = url;
    }

    private String url = null;

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    private Map<String, String> header = null;

    public  String getByUrl(){
        String answer = null;
        if (header == null){
            header = new HashMap<String, String>();
        }
        if (method == null){
            method = "GET";
        }
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod(method);
            for(Map.Entry<String, String> entry:header.entrySet()) {
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

    private  String handleInputStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String result = "", line = "";
        while ((line = reader.readLine()) != null) {
            result += line;
        }
        return result;
    }
}
