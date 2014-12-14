package com.example.mike.tpdisk.Service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.mike.tpdisk.Credentials;
import com.example.mike.tpdisk.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UrlService extends IntentService {
    private static  int id = 0;
    private String TAG = "IntentService";
    public static final String ACTION_GET_URI = "com.example.mike.tpdisk.action.ACTION_GET_URI";
    public static final String ACTION_SEND_RESULT = "com.example.mike.tpdisk.action.ACTION_SEND_RESULT";
    public static final String ACTION_SEND_PROGRESS = "com.example.mike.tpdisk.action.ACTION_SEND_PROGRESS";

    public static final String PARAM_URL = "com.example.mike.tpdisk.extra.PARAM_URL";
    public static final String PARAM_RESULT = "com.example.mike.tpdisk.extra.PARAM_RESULT";
    public static final String PARAM_FOLDER = "com.example.mike.tpdisk.extra.PARAM_FOLDER";
    public static final String PARAM_MESSENGER = "com.example.mike.tpdisk.extra.PARAM_MESSENGER";
    public static final String IS_FORCE= "com.example.mike.tpdisk.extra.IS_FORCE";

    public static final String PROGRESS_RESULT = "com.example.mike.tpdisk.extra.PROGRESS_RESULT";

    // TODO: Customize helper method
    public static void startActionFoo(Context context, String url) {
        Intent intent = new Intent(context, UrlService.class);
        intent.setAction(ACTION_GET_URI);
        intent.putExtra(PARAM_URL, url);
        context.startService(intent);
    }

    public UrlService() {
        super("UrlService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_URI.equals(action)) {
                final String url = intent.getStringExtra(PARAM_FOLDER);
                boolean is_force = intent.getBooleanExtra(IS_FORCE, false);
                String folder = handleGetFolder(this, url, is_force);
                Messenger messenger = (Messenger)intent.getExtras().get(PARAM_MESSENGER);
                Message message = Message.obtain();
                message.obj = folder;
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                throw new UnsupportedOperationException("Wrong intent action");
            }
        }
    }

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String [] p = param.split("=");
            if(p.length > 1)
                map.put(p[0], p[1]);
        }
        return map;
    }

    private String handleGetFolder(Context context, String folder, boolean is_force){
        Processor processor = new Processor();
        return processor.getFileInstanceByPath(context, folder, is_force);
    }

    private void handleActionGetUri(String url) {
        Log.d(TAG, "handleActionGetUri");
        Log.d(TAG, hashCode() + " loadInBackground start");
        /*HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "OAuth " + Credentials.getToken());
        Connector connector = new Connector();
        connector.setHeader(headers);
        connector.setUrl(url);
        String answer = connector.getByUrl();
        SendResult(answer);*/
        id++;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        try {
            URL u = new URL(url);
            Log.d(TAG, u.getQuery());
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "OAuth " + Credentials.getToken());
            connection.connect();

            File cardRoot = Environment.getExternalStorageDirectory();

            File folder = new File(cardRoot, "tpyadisk");
            folder.mkdirs();

            Map<String, String> map = getQueryMap(u.getQuery());
            String fileName = map.get("filename");
            int size = Integer.parseInt(map.get("fsize"));

            File file = new File(folder, fileName);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            InputStream inputStream = connection.getInputStream();

            builder.setContentTitle("TPYADISK").setContentText("Downloading " + fileName).setSmallIcon(R.drawable.ic_launcher);
            byte [] buffer = new byte[1024 * 32];
            Log.d(TAG, Integer.toString(connection.getContentLength()));
            int bufferLength = 0, readed = 0;
            while ( (bufferLength = inputStream.read(buffer)) > 0){
                fileOutputStream.write(buffer, 0, bufferLength);
                readed += bufferLength;
                builder.setProgress(size, readed, false);
                manager.notify(id, builder.build());
            }
            fileOutputStream.close();
            connection.disconnect();
            builder.setContentText("File downloaded to " + file.getPath()).setProgress(0, 0, false).
            setStyle(new NotificationCompat.BigTextStyle().bigText("File downloaded to " + file.getPath()));
            Intent clickNotifyIntent = new Intent();
            clickNotifyIntent.setAction(Intent.ACTION_VIEW);
            //clickNotifyIntent.setDataAndType(Uri.fromFile(file), map.get("media_type") + "/*");
            clickNotifyIntent.setData(Uri.fromFile(file));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, clickNotifyIntent,0);
            builder.setContentIntent(pendingIntent);
            manager.notify(id, builder.build());


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SendResult(String result) {
        Log.d(TAG, "SendResult");
        Intent localIntent = new Intent(ACTION_SEND_RESULT).putExtra(PARAM_RESULT, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    @Override
    public void onDestroy() {

        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
